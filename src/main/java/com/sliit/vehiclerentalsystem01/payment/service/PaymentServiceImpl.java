package com.sliit.vehiclerentalsystem01.payment.service;

import com.sliit.vehiclerentalsystem01.core.config.CachingConfig;
import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.payment.entity.CardPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.Payment;
import com.sliit.vehiclerentalsystem01.payment.entity.PaymentStatus;
import com.sliit.vehiclerentalsystem01.payment.repository.PaymentRepository;
import com.sliit.vehiclerentalsystem01.reservation.entity.Booking;
import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
     * Run sync once at startup so confirmed bookings without payment records
     * are backfilled without doing a full scan on every read.
     */
    @PostConstruct
    public void syncOnStartup() {
        syncPendingPayments();
    }

    /**
     * Called only at startup or when a booking status transitions to CONFIRMED/COMPLETED.
     */
    public void triggerSync() {
        syncPendingPayments();
    }

    /**
     * CRUD: READ
     */
    @Override
    @Cacheable(CachingConfig.PAYMENTS_CACHE)
    public List<Payment> findAll() {
        return paymentRepository.findAll().stream()
                .filter(p -> !PaymentStatus.fromString(p.getStatus()).isDeleted())
                .collect(Collectors.toList());
    }

    /**
     * CRUD: READ
     */
    @Override
    public Optional<Payment> findById(String id) {
        return paymentRepository.findAll().stream()
                .filter(p -> id.equals(p.getId()) && !PaymentStatus.fromString(p.getStatus()).isDeleted())
                .findFirst();
    }

    /**
     * CRUD: CREATE / UPDATE
     * Updates an existing payment or saves a new one if it doesn't exist.
     */
    @Override
    @CacheEvict(value = CachingConfig.PAYMENTS_CACHE, allEntries = true)
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
    @CacheEvict(value = CachingConfig.PAYMENTS_CACHE, allEntries = true)
    public void deleteById(String id) {
        paymentRepository.findAll().stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst()
                .ifPresent(p -> {
                    p.setStatus("DELETED");
                    save(p);
                });
    }

    /**
     * CRUD: READ
     * Returns the most recent payment for the booking. Assumes {@code payments.txt}
     * appends rows chronologically, so the last matching entry is the latest.
     */
    @Override
    public Optional<Payment> findLatestByBookingId(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return Optional.empty();
        }
        String key = bookingId.trim();
        List<Payment> all = paymentRepository.findAll();
        for (int i = all.size() - 1; i >= 0; i--) {
            Payment p = all.get(i);
            if (key.equals(p.getBookingId()) && !PaymentStatus.fromString(p.getStatus()).isDeleted()) {
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
        if (bookingId == null || bookingId.isBlank()) {
            return false;
        }
        String key = bookingId.trim();
        return paymentRepository.findAll().stream()
                .anyMatch(p -> key.equals(p.getBookingId()) && !PaymentStatus.fromString(p.getStatus()).isDeleted());
    }

    /**
     * CRUD: READ
     */
    @Override
    public boolean hasPaidPaymentForBooking(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return false;
        }
        String key = bookingId.trim();
        return paymentRepository.findAll().stream()
                .filter(p -> key.equals(p.getBookingId()) && !PaymentStatus.fromString(p.getStatus()).isDeleted())
                .anyMatch(p -> PaymentStatus.fromString(p.getStatus()).isPaid());
    }

    /**
     * CRUD: READ
     */
    @Override
    public List<Payment> findAllByBookingId(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) return new ArrayList<>();
        String key = bookingId.trim();
        return paymentRepository.findAll().stream()
                .filter(p -> key.equals(p.getBookingId()) && !PaymentStatus.fromString(p.getStatus()).isDeleted())
                .collect(Collectors.toList());
    }

    /**
     * CRUD: READ
     */
    @Override
    public boolean isCashPaymentForBooking(String bookingId) {
        Optional<Payment> latest = findLatestByBookingId(bookingId);
        if (latest.isPresent()) {
            Payment p = latest.get();
            String method = p.getPaymentMethod();
            return p instanceof com.sliit.vehiclerentalsystem01.payment.entity.CashPayment ||
                    "CASH".equalsIgnoreCase(method) || 
                    "Cash Transaction".equalsIgnoreCase(method);
        }
        return false;
    }
}
