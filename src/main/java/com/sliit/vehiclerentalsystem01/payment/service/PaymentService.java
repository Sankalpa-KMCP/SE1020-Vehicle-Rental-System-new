package com.sliit.vehiclerentalsystem01.payment.service;

import com.sliit.vehiclerentalsystem01.payment.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    List<Payment> findAll();

    Optional<Payment> findById(String id);

    void save(Payment payment);

    void deleteById(String id);

    Optional<Payment> findLatestByBookingId(String bookingId);

    boolean hasAnyPaymentForBooking(String bookingId);

    boolean hasPaidPaymentForBooking(String bookingId);
}
