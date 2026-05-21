package com.sliit.vehiclerentalsystem01.payment.service;

import com.sliit.vehiclerentalsystem01.payment.entity.Payment;

import java.util.List;
import java.util.Optional;

/**
 * Interface that defines the contract for payment-related operations.
 * By using an interface, we achieve abstraction and loose coupling.
 */
public interface PaymentService {

    /**
     * CRUD: READ
     * Retrieves all payments in the system.
     */
    List<Payment> findAll();

    /**
     * CRUD: READ
     * Finds a specific payment by its unique ID.
     */
    Optional<Payment> findById(String id);

    /**
     * CRUD: CREATE / UPDATE
     * Saves a new payment or updates an existing one.
     */
    void save(Payment payment);

    /**
     * CRUD: DELETE
     * Deletes a payment based on its ID.
     */
    void deleteById(String id);

    /**
     * CRUD: READ
     * Finds the most recent payment made for a specific booking.
     */
    Optional<Payment> findLatestByBookingId(String bookingId);

    /**
     * CRUD: READ
     * Checks if a booking has any associated payment records.
     */
    boolean hasAnyPaymentForBooking(String bookingId);

    /**
     * CRUD: READ
     * Checks if a booking has at least one successful/paid payment.
     */
    boolean hasPaidPaymentForBooking(String bookingId);

    /**
     * CRUD: READ
     * Returns all payments linked to a specific booking ID.
     */
    List<Payment> findAllByBookingId(String bookingId);

    /**
     * CRUD: READ
     * Checks if the latest payment for a booking is a Cash payment.
     */
    boolean isCashPaymentForBooking(String bookingId);
}
