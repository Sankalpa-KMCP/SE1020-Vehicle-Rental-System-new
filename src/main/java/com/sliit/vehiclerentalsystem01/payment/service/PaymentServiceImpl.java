package com.sliit.vehiclerentalsystem01.payment.service;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.payment.entity.CardPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.Payment;
import com.sliit.vehiclerentalsystem01.payment.entity.PaymentStatus;
import com.sliit.vehiclerentalsystem01.payment.repository.PaymentRepository;
import com.sliit.vehiclerentalsystem01.reservation.entity.Booking;
import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Concrete implementation of the PaymentService.
 * Contains the business logic for managing payments, interfacing between controllers and the repository.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationService reservationService;
    private final VehicleService vehicleService;

    /**
     * Constructor Injection ensures dependencies are provided when the service is created.
     * @Lazy is used here to avoid circular dependency issues during Spring context loading.
     */
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              @Lazy ReservationService reservationService,
                              @Lazy VehicleService vehicleService) {
        this.paymentRepository = paymentRepository;
        this.reservationService = reservationService;
        this.vehicleService = vehicleService;
    }

    /**
     * Checks all bookings. If a booking is confirmed or completed but has no payment record,
     * it automatically generates a "PENDING" payment.
     */
    private void syncPendingPayments() {
        List<Booking> allBookings = reservationService.findAll();
        List<Payment> allPayments = paymentRepository.findAll();
        
        // Collect IDs of bookings that already have a payment to prevent duplicates
        Set<String> paidOrPendingBookingIds = allPayments.stream()
                .map(Payment::getBookingId)
                .collect(Collectors.toSet());

        boolean changed = false;
        List<Payment> listToSave = new ArrayList<>(allPayments);

        for (Booking b : allBookings) {
            String status = b.getStatus() == null ? "" : b.getStatus().trim();
            if (("CONFIRMED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status))
                    && !paidOrPendingBookingIds.contains(b.getId())) {

                // Calculate total amount based on vehicle rate and booking duration
                double amount = vehicleService.findById(b.getVehicleId())
                        .map(v -> v.calculateRentalRate() * Math.max(1, b.getDurationDays()))
                        .orElse(0.0);

                // Create the pending payment record
                Payment p = new CardPayment("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                        b.getId(), amount, "PENDING");
                
                listToSave.add(p);
                paidOrPendingBookingIds.add(b.getId());
                changed = true;
            }
        }
        
        if (changed) {
            paymentRepository.saveAll(listToSave);
        }
    }

    /**
     * CRUD: READ
     */
    @Override
    public List<Payment> findAll() {
        syncPendingPayments(); // Ensure data is up to date before fetching
        return paymentRepository.findAll();
    }

    /**
     * CRUD: READ
     */
    @Override
    public Optional<Payment> findById(String id) {
        syncPendingPayments();
        return paymentRepository.findAll().stream().filter(p -> id.equals(p.getId())).findFirst();
    }

    /**
     * CRUD: CREATE / UPDATE
     * Updates an existing payment or saves a new one if it doesn't exist.
     */
    @Override
    public void save(Payment payment) {
        if (payment.getAmount() < 0) {
            throw new ValidationException("invalid_amount"); // Business rule validation
        }
        payment.setStatus(PaymentStatus.fromString(payment.getStatus()).name());
        
        if (payment.getId() == null || payment.getId().isBlank()) {
            payment.setId(UUID.randomUUID().toString()); // Generate unique ID for new payments
        }
        
        List<Payment> list = new ArrayList<>(paymentRepository.findAll());
        // Remove the older version of this payment (if it exists)
        list.removeIf(p -> payment.getId().equals(p.getId()));
        // Add the updated/new payment
        list.add(payment);
        
        paymentRepository.saveAll(list);
    }

    /**
     * CRUD: DELETE
     */
    @Override
    public void deleteById(String id) {
        List<Payment> list = paymentRepository.findAll().stream()
                .filter(p -> !id.equals(p.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        paymentRepository.saveAll(list);
    }

    /**
     * CRUD: READ
     * Returns the most recent payment for the booking. Assumes {@code payments.txt}
     * appends rows chronologically, so the last matching entry is the latest.
     */
    @Override
    public Optional<Payment> findLatestByBookingId(String bookingId) {
        syncPendingPayments();
        if (bookingId == null || bookingId.isBlank()) {
            return Optional.empty();
        }
        
        String key = bookingId.trim();
        List<Payment> all = paymentRepository.findAll();
        
        // Search backwards to quickly find the latest entry
        for (int i = all.size() - 1; i >= 0; i--) {
            Payment p = all.get(i);
            if (key.equals(p.getBookingId())) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    /**
     * CRUD: READ
     */
    @Override
    public boolean hasAnyPaymentForBooking(String bookingId) {
        syncPendingPayments();
        if (bookingId == null || bookingId.isBlank()) {
            return false;
        }
        String key = bookingId.trim();
        return paymentRepository.findAll().stream().anyMatch(p -> key.equals(p.getBookingId()));
    }

    /**
     * CRUD: READ
     */
    @Override
    public boolean hasPaidPaymentForBooking(String bookingId) {
        syncPendingPayments();
        if (bookingId == null || bookingId.isBlank()) {
            return false;
        }
        String key = bookingId.trim();
        return paymentRepository.findAll().stream()
                .filter(p -> key.equals(p.getBookingId()))
                .anyMatch(p -> PaymentStatus.fromString(p.getStatus()).isPaid());
    }
}