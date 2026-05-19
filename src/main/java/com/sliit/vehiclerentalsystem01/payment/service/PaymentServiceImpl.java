package com.sliit.vehiclerentalsystem01.payment.service;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.payment.entity.Payment;
import com.sliit.vehiclerentalsystem01.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
gi
    @Override
    public List<Payment> findAll() {
        // The repository already knows how to read every payment from storage.
        // This service method simply passes that list to the controller.
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> findById(String id) {
        if (isBlank(id)) {
            return Optional.empty();
        }

        String requestedPaymentId = id.trim();
        List<Payment> allPayments = paymentRepository.findAll();

        for (Payment payment : allPayments) {
            if (paymentIdMatches(payment, requestedPaymentId)) {
                return Optional.of(payment);
            }
        }

        return Optional.empty();
    }

    @Override
    public void save(Payment payment) {
        validatePaymentBeforeSaving(payment);
        fillMissingPaymentDefaults(payment);

        List<Payment> payments = loadPaymentsAsEditableList();

        removeOldVersionOfPayment(payments, payment);
        payments.add(payment);

        paymentRepository.saveAll(payments);
    }

    @Override
    public void deleteById(String id) {
        if (isBlank(id)) {
            return;
        }

        String paymentIdToDelete = id.trim();
        List<Payment> remainingPayments = new ArrayList<>();

        for (Payment payment : paymentRepository.findAll()) {
            boolean thisIsThePaymentToDelete = paymentIdMatches(payment, paymentIdToDelete);

            if (!thisIsThePaymentToDelete) {
                remainingPayments.add(payment);
            }
        }

        paymentRepository.saveAll(remainingPayments);
    }

    @Override
    public Optional<Payment> findLatestByBookingId(String bookingId) {
        if (isBlank(bookingId)) {
            return Optional.empty();
        }

        String requestedBookingId = bookingId.trim();
        Payment latestPayment = null;

        // Keep walking through the list. If more than one payment exists for
        // the same booking, the last matching one becomes the latest payment.
        for (Payment payment : paymentRepository.findAll()) {
            if (bookingIdMatches(payment, requestedBookingId)) {
                latestPayment = payment;
            }
        }

        return Optional.ofNullable(latestPayment);
    }

    @Override
    public boolean hasAnyPaymentForBooking(String bookingId) {
        if (isBlank(bookingId)) {
            return false;
        }

        String requestedBookingId = bookingId.trim();

        for (Payment payment : paymentRepository.findAll()) {
            if (bookingIdMatches(payment, requestedBookingId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasPaidPaymentForBooking(String bookingId) {
        if (isBlank(bookingId)) {
            return false;
        }

        String requestedBookingId = bookingId.trim();

        for (Payment payment : paymentRepository.findAll()) {
            boolean belongsToBooking = bookingIdMatches(payment, requestedBookingId);
            boolean alreadyPaid = isPaid(payment);

            if (belongsToBooking && alreadyPaid) {
                return true;
            }
        }

        return false;
    }

    private void validatePaymentBeforeSaving(Payment payment) {
        if (payment == null) {
            throw new ValidationException("payment_required");
        }

        if (payment.getAmount() < 0) {
            throw new ValidationException("invalid_amount");
        }
    }

    private void fillMissingPaymentDefaults(Payment payment) {
        if (isBlank(payment.getStatus())) {
            payment.setStatus("PENDING");
        }

        if (isBlank(payment.getId())) {
            payment.setId(UUID.randomUUID().toString());
        }
    }

    private List<Payment> loadPaymentsAsEditableList() {
        return new ArrayList<>(paymentRepository.findAll());
    }

    private void removeOldVersionOfPayment(List<Payment> payments, Payment paymentToSave) {
        payments.removeIf(existingPayment ->
                paymentToSave.getId().equals(existingPayment.getId())
        );
    }

    private boolean paymentIdMatches(Payment payment, String requestedPaymentId) {
        return requestedPaymentId.equals(payment.getId());
    }

    private boolean bookingIdMatches(Payment payment, String requestedBookingId) {
        return requestedBookingId.equals(payment.getBookingId());
    }

    private boolean isPaid(Payment payment) {
        return "PAID".equalsIgnoreCase(nullSafe(payment.getStatus()));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String nullSafe(String value) {
        return value == null ? "" : value.trim();
    }
}
