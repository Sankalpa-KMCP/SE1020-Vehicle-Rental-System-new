package com.sliit.vehiclerentalsystem01.reservation.service;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.payment.service.PaymentService;
import com.sliit.vehiclerentalsystem01.reservation.entity.Booking;
import com.sliit.vehiclerentalsystem01.reservation.repository.ReservationRepository;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Vehicle;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
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
    private final VehicleService vehicleService;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  UserService userService,
                                  @Lazy PaymentService paymentService,
                                  @Lazy VehicleService vehicleService) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.paymentService = paymentService;
        this.vehicleService = vehicleService;
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

        if ("RENTED".equalsIgnoreCase(booking.getStatus())) {
            updateVehicleStatus(booking.getVehicleId(), "RENTED");
        } else if ("MAINTENANCE".equalsIgnoreCase(booking.getStatus())) {
            updateVehicleStatus(booking.getVehicleId(), "MAINTENANCE");
        } else if ("COMPLETED".equalsIgnoreCase(booking.getStatus()) || "CANCELLED".equalsIgnoreCase(booking.getStatus()) || "REJECTED".equalsIgnoreCase(booking.getStatus())) {
            vehicleService.findById(booking.getVehicleId()).ifPresent(v -> {
                if ("RENTED".equalsIgnoreCase(v.getStatus())) {
                    updateVehicleStatus(booking.getVehicleId(), "AVAILABLE");
                }
            });
        }
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
        String vehicleIdToUpdate = null;
        String statusToSet = null;
        for (Booking b : list) {
            if (bookingId.equals(b.getId())) {
                b.setStatus(newStatus);
                changed = true;
                if ("RENTED".equalsIgnoreCase(newStatus)) {
                    vehicleIdToUpdate = b.getVehicleId();
                    statusToSet = "RENTED";
                } else if ("MAINTENANCE".equalsIgnoreCase(newStatus)) {
                    vehicleIdToUpdate = b.getVehicleId();
                    statusToSet = "MAINTENANCE";
                } else if ("COMPLETED".equalsIgnoreCase(newStatus) || "CANCELLED".equalsIgnoreCase(newStatus) || "REJECTED".equalsIgnoreCase(newStatus)) {
                    vehicleIdToUpdate = b.getVehicleId();
                    statusToSet = "AVAILABLE";
                }
                break;
            }
        }
        if (changed) {
            reservationRepository.saveAll(list);
            if (vehicleIdToUpdate != null && statusToSet != null) {
                if ("AVAILABLE".equals(statusToSet)) {
                    final String targetStatus = statusToSet;
                    vehicleService.findById(vehicleIdToUpdate.trim()).ifPresent(v -> {
                        if ("RENTED".equalsIgnoreCase(v.getStatus())) {
                            updateVehicleStatus(v.getId(), targetStatus);
                        }
                    });
                } else {
                    updateVehicleStatus(vehicleIdToUpdate, statusToSet);
                }
            }
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

    private void updateVehicleStatus(String vehicleId, String status) {
        if (vehicleId != null && !vehicleId.isBlank()) {
            vehicleService.findById(vehicleId.trim()).ifPresent(v -> {
                v.setStatus(status);
                vehicleService.save(v);
            });
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

        boolean isNewBooking = true;
        if (booking.getId() != null && !booking.getId().isBlank()) {
            isNewBooking = reservationRepository.findAll().stream().noneMatch(b -> booking.getId().equals(b.getId()));
        }
        if (isNewBooking) {
            validateCustomerVerifiedForNewBooking(booking);
            Optional<Vehicle> vOpt = vehicleService.findById(vehicleId);
            if (vOpt.isEmpty() || !"AVAILABLE".equalsIgnoreCase(vOpt.get().getStatus())) {
                throw new ValidationException("vehicle_unavailable");
            }
        } else {
            Optional<Vehicle> vOpt = vehicleService.findById(vehicleId);
            if (vOpt.isPresent() && "MAINTENANCE".equalsIgnoreCase(vOpt.get().getStatus())) {
                throw new ValidationException("vehicle_unavailable");
            }
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
                || "UPDATE_REQUESTED".equalsIgnoreCase(s)
                || "RENTED".equalsIgnoreCase(s)
                || "MAINTENANCE".equalsIgnoreCase(s)
                || "CANCELLATION_REQUESTED".equalsIgnoreCase(s);
    }

    private static boolean intervalOverlap(LocalDate aStart, LocalDate aEnd, LocalDate bStart, LocalDate bEnd) {
        return !aEnd.isBefore(bStart) && !aStart.isAfter(bEnd);
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * New bookings require a verified customer profile (NIC + license on file).
     * Skips when the booking customer name does not match a stored customer.
     */
    private void validateCustomerVerifiedForNewBooking(Booking booking) {
        String customerRef = normalized(booking.getCustomerName());
        if (customerRef.isEmpty()) {
            return;
        }
        Optional<Customer> customer = userService.findByUsername(customerRef)
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u);
        if (customer.isEmpty()) {
            customer = userService.findById(customerRef)
                    .filter(u -> u instanceof Customer)
                    .map(u -> (Customer) u);
        }
        customer.ifPresent(c -> {
            if (!c.isVerified()) {
                throw new ValidationException("verification_required");
            }
        });
    }
}
