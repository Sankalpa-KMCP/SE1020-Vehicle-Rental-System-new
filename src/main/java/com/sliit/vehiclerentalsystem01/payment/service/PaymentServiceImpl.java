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
        return paymentRepository.findAll().stream().filter(p -> id.equals(p.getId())).findFirst();
    }

    @Override
    public void save(Payment payment) {
        if (payment.getAmount() < 0) {
            throw new ValidationException("invalid_amount");
        }
        if (payment.getStatus() == null || payment.getStatus().isBlank()) {
            payment.setStatus("PENDING");
        }
        if (payment.getId() == null || payment.getId().isBlank()) {
            payment.setId(UUID.randomUUID().toString());
        }
        List<Payment> list = new ArrayList<>(paymentRepository.findAll());
        list.removeIf(p -> payment.getId().equals(p.getId()));
        list.add(payment);
        paymentRepository.saveAll(list);
    }

    @Override
    public void deleteById(String id) {
        List<Payment> list = paymentRepository.findAll().stream()
                .filter(p -> !id.equals(p.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        paymentRepository.saveAll(list);
    }

    @Override
    public Optional<Payment> findLatestByBookingId(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return Optional.empty();
        }
        String key = bookingId.trim();
        Payment latest = null;
        for (Payment p : paymentRepository.findAll()) {
            if (key.equals(p.getBookingId())) {
                latest = p;
            }
        }
        return Optional.ofNullable(latest);
    }

    @Override
    public boolean hasAnyPaymentForBooking(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return false;
        }
        String key = bookingId.trim();
        return paymentRepository.findAll().stream().anyMatch(p -> key.equals(p.getBookingId()));
    }

    @Override
    public boolean hasPaidPaymentForBooking(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return false;
        }
        String key = bookingId.trim();
        return paymentRepository.findAll().stream()
                .filter(p -> key.equals(p.getBookingId()))
                .anyMatch(p -> "PAID".equalsIgnoreCase(nullSafe(p.getStatus())));
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s.trim();
    }
}
