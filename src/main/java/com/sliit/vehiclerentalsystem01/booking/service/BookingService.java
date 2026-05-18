package com.sliit.vehiclerentalsystem01.booking.service;

import com.sliit.vehiclerentalsystem01.booking.model.Booking;
import com.sliit.vehiclerentalsystem01.user.model.Customer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingService {

    List<Booking> findAll();

    List<Booking> findByCustomerId(String customerId);

    List<Booking> findBookingsLinkedToCustomer(Customer customer);

    Optional<Booking> findById(String id);

    void save(Booking booking);

    void deleteById(String id);

    void updateStatus(String bookingId, String newStatus);

    boolean isPaymentAllowed(String bookingId);

    boolean hasEligibleReviewBooking(String customerId);

    Set<String> findUnavailableVehicleIds();

    boolean isVehicleAvailable(String vehicleId);

    boolean hasDateOverlap(String bookingId, LocalDate start, LocalDate end);
}
