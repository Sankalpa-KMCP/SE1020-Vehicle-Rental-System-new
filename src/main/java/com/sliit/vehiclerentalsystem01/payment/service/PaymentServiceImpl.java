package com.sliit.vehiclerentalsystem01.payment.service;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.payment.entity.Payment;
import com.sliit.vehiclerentalsystem01.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        String key = id.trim();

        return paymentRepository.findAll()
                .stream()
                .filter(payment -> key.equals(payment.getId()))
                .findFirst();
    }

    @Override
    public void save(Payment payment) {
        if (payment == null) {
            throw new ValidationException("payment_required");
        }

        if (payment.getAmount() < 0) {
            throw new ValidationException("invalid_amount");
        }

        if (payment.getStatus() == null || payment.getStatus().isBlank()) {
            payment.setStatus("PENDING");
        }

        if (payment.getId() == null || payment.getId().isBlank()) {
            payment.setId(UUID.randomUUID().toString());
        }

        List<Payment> payments = new ArrayList<>(paymentRepository.findAll());

        payments.removeIf(existingPayment ->
                payment.getId().equals(existingPayment.getId())
        );

        payments.add(payment);

        paymentRepository.saveAll(payments);
    }

    @Override
    public void deleteById(String id) {
        if (id == null || id.isBlank()) {
            return;
        }

        String key = id.trim();

        List<Payment> payments = paymentRepository.findAll()
                .stream()
                .filter(payment -> !key.equals(payment.getId()))
                .collect(Collectors.toCollection(ArrayList::new));

        paymentRepository.saveAll(payments);
    }

    @Override
    public Optional<Payment> findLatestByBookingId(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return Optional.empty();
        }

        String key = bookingId.trim();
        Payment latestPayment = null;

        for (Payment payment : paymentRepository.findAll()) {
            if (key.equals(payment.getBookingId())) {
                latestPayment = payment;
            }
        }

        return Optional.ofNullable(latestPayment);
    }

    @Override
    public boolean hasAnyPaymentForBooking(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return false;
        }

        String key = bookingId.trim();

        return paymentRepository.findAll()
                .stream()
                .anyMatch(payment -> key.equals(payment.getBookingId()));
    }

    @Override
    public boolean hasPaidPaymentForBooking(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return false;
        }

        String key = bookingId.trim();

        return paymentRepository.findAll()
                .stream()
                .filter(payment -> key.equals(payment.getBookingId()))
                .anyMatch(payment -> "PAID".equalsIgnoreCase(nullSafe(payment.getStatus())));
    }

    private static String nullSafe(String value) {
        return value == null ? "" : value.trim();
    }
}