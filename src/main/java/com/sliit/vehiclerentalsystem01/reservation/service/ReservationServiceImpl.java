package com.sliit.vehiclerentalsystem01.reservation.service;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.payment.service.PaymentService;
import com.sliit.vehiclerentalsystem01.reservation.entity.Booking;
import com.sliit.vehiclerentalsystem01.reservation.repository.ReservationRepository;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final PaymentService paymentService;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  UserService userService,
                                  @Lazy PaymentService paymentService) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @Override
    public List<Booking> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Booking> findByCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return new ArrayList<>();
        }
        Set<String> keys = customerKeysForBookings(customerId.trim());
        return reservationRepository.findAll().stream()
                .filter(b -> keys.contains(b.getCustomerName()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Booking> findBookingsLinkedToCustomer(Customer customer) {
        if (customer == null) {
            return new ArrayList<>();
        }
        Set<String> keys = new LinkedHashSet<>();
        if (customer.getId() != null && !customer.getId().isBlank()) {
            keys.add(customer.getId().trim());
        }
        if (customer.getUsername() != null && !customer.getUsername().isBlank()) {
            keys.add(customer.getUsername().trim());
        }
        return reservationRepository.findAll().stream()
                .filter(b -> keys.contains(b.getCustomerName()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Optional<Booking> findById(String id) {
        return reservationRepository.findAll().stream().filter(b -> id.equals(b.getId())).findFirst();
    }

    @Override
    public void save(Booking booking) {
        if (booking.getTransactionId() == null || booking.getTransactionId().isBlank()) {
            booking.setTransactionId(UUID.randomUUID().toString());
        }
        validateBeforeSave(booking);
        List<Booking> list = new ArrayList<>(reservationRepository.findAll());
        list.removeIf(b -> booking.getId().equals(b.getId()));
        list.add(booking);
        reservationRepository.saveAll(list);
    }

    @Override
    public void deleteById(String id) {
        if (id != null && !id.isBlank() && paymentService.hasAnyPaymentForBooking(id)) {
            throw new IllegalStateException("booking_in_use");
        }
        List<Booking> list = reservationRepository.findAll().stream()
                .filter(b -> !id.equals(b.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        reservationRepository.saveAll(list);
    }

    @Override
    public void updateStatus(String bookingId, String newStatus) {
        if (bookingId == null || bookingId.isBlank() || newStatus == null) {
            return;
        }
        List<Booking> list = new ArrayList<>(reservationRepository.findAll());
        boolean changed = false;
        for (Booking b : list) {
            if (bookingId.equals(b.getId())) {
                b.setStatus(newStatus);
                changed = true;
                break;
            }
        }
        if (changed) {
            reservationRepository.saveAll(list);
        }
    }

    @Override
    public boolean isPaymentAllowed(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return false;
        }
        return reservationRepository.findAll().stream()
                .filter(b -> bookingId.equals(b.getId()))
                .anyMatch(b -> "CONFIRMED".equalsIgnoreCase(normalized(b.getStatus())));
    }

    @Override
    public boolean hasEligibleReviewBooking(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return false;
        }
        Set<String> keys = customerKeysForBookings(customerId.trim());
        if (keys.isEmpty()) {
            return false;
        }
        return reservationRepository.findAll().stream()
                .filter(b -> keys.contains(b.getCustomerName()))
                .anyMatch(b -> {
                    String s = normalized(b.getStatus());
                    return "CONFIRMED".equalsIgnoreCase(s) || "COMPLETED".equalsIgnoreCase(s);
                });
    }

    private Set<String> customerKeysForBookings(String customerRef) {
        Set<String> keys = new LinkedHashSet<>();
        String t = customerRef == null ? "" : customerRef.trim();
        if (t.isEmpty()) {
            return keys;
        }
        keys.add(t);
        userService.findByUsername(t)
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .ifPresent(c -> {
                    keys.add(c.getId());
                    keys.add(c.getUsername());
                });
        userService.findById(t)
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .ifPresent(c -> {
                    keys.add(c.getId());
                    keys.add(c.getUsername());
                });
        return keys;
    }

    @Override
    public Set<String> findUnavailableVehicleIds() {
        Set<String> ids = new HashSet<>();
        for (Booking b : reservationRepository.findAll()) {
            String s = normalized(b.getStatus());
            if ("CONFIRMED".equalsIgnoreCase(s)
                    || "CANCELLATION_REQUESTED".equalsIgnoreCase(s)
                    || "UPDATED".equalsIgnoreCase(s)) {
                String vid = normalized(b.getVehicleId());
                if (!vid.isEmpty()) {
                    ids.add(vid);
                }
            }
        }
        return ids;
    }

    @Override
    public boolean isVehicleAvailable(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return false;
        }
        return !findUnavailableVehicleIds().contains(vehicleId.trim());
    }

    @Override
    public boolean hasDateOverlap(String bookingId, LocalDate start, LocalDate end) {
        if (bookingId == null || bookingId.isBlank() || start == null || end == null) {
            return false;
        }
        Optional<Booking> ob = reservationRepository.findAll().stream()
                .filter(b -> bookingId.equals(b.getId()))
                .findFirst();
        if (ob.isEmpty()) {
            return false;
        }
        Booking b = ob.get();
        try {
            LocalDate bStart = LocalDate.parse(normalized(b.getRentalDate()));
            int days = Math.max(1, b.getDurationDays());
            LocalDate bEnd = bStart.plusDays(days - 1L);
            return intervalOverlap(bStart, bEnd, start, end);
        } catch (Exception e) {
            return false;
        }
    }

    private void validateBeforeSave(Booking booking) {
        int days = booking.getDurationDays();
        if (days < 1) {
            throw new ValidationException("invalid_dates");
        }
        LocalDate start;
        try {
            start = LocalDate.parse(normalized(booking.getRentalDate()));
        } catch (Exception e) {
            throw new ValidationException("invalid_dates");
        }
        LocalDate end = start.plusDays(days - 1L);
        if (end.isBefore(start)) {
            throw new ValidationException("invalid_dates");
        }

        String vehicleId = normalized(booking.getVehicleId());
        if (vehicleId.isEmpty()) {
            throw new ValidationException("vehicle_unavailable");
        }

        String selfId = booking.getId() == null ? "" : booking.getId();
        for (Booking existing : reservationRepository.findAll()) {
            if (selfId.equals(existing.getId())) {
                continue;
            }
            if (!vehicleId.equalsIgnoreCase(normalized(existing.getVehicleId()))) {
                continue;
            }
            if (!isBlockingStatus(existing.getStatus())) {
                continue;
            }
            try {
                LocalDate existingStart = LocalDate.parse(normalized(existing.getRentalDate()));
                int existingDays = Math.max(1, existing.getDurationDays());
                LocalDate existingEnd = existingStart.plusDays(existingDays - 1L);
                if (intervalOverlap(start, end, existingStart, existingEnd)) {
                    throw new ValidationException("vehicle_unavailable");
                }
            } catch (ValidationException ex) {
                throw ex;
            } catch (Exception ignored) {
                // skip corrupt row
            }
        }
    }

    private static boolean isBlockingStatus(String status) {
        String s = normalized(status);
        return "CONFIRMED".equalsIgnoreCase(s)
                || "PENDING".equalsIgnoreCase(s)
                || "UPDATED".equalsIgnoreCase(s)
                || "CANCELLATION_REQUESTED".equalsIgnoreCase(s);
    }

    private static boolean intervalOverlap(LocalDate aStart, LocalDate aEnd, LocalDate bStart, LocalDate bEnd) {
        return !aEnd.isBefore(bStart) && !aStart.isAfter(bEnd);
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim();
    }
}
