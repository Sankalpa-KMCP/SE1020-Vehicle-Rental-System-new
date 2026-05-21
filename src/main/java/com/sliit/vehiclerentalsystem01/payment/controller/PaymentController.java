package com.sliit.vehiclerentalsystem01.payment.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.payment.entity.CardPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.CashPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.Payment;
import com.sliit.vehiclerentalsystem01.payment.entity.PaymentStatus;
import com.sliit.vehiclerentalsystem01.payment.service.PaymentService;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

/**
 * PaymentController: Manages administrative and customer payment workflows.
 */
@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final ReservationService reservationService;
    private final VehicleService vehicleService;

    public PaymentController(PaymentService paymentService, ReservationService reservationService, VehicleService vehicleService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
        this.vehicleService = vehicleService;
    }

    // --- Helper Methods ---
    private boolean isAdmin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
    }

    private boolean isCustomer(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.CUSTOMER_LOGGED_IN));
    }

    // --- Admin Dashboard & List ---
    /**
     * Display all payment records and summary metrics on the admin dashboard.
     */
    @GetMapping
    public String list(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/auth/staff-login";
        List<Payment> payments = paymentService.findAll();
        model.addAttribute("payments", payments);
        model.addAttribute("totalTransactions", payments.size());
        model.addAttribute("paidCount", payments.stream().filter(p -> PaymentStatus.fromString(p.getStatus()).isPaid()).count());
        model.addAttribute("pendingCount", payments.stream().filter(p -> PaymentStatus.fromString(p.getStatus()).isPending()).count());
        model.addAttribute("refundedCount", payments.stream().filter(p -> PaymentStatus.fromString(p.getStatus()).isRefunded()).count());
        model.addAttribute("totalAmount", payments.stream().mapToDouble(Payment::getAmount).sum());
        return "payment/payment-dashboard";
    }

    // --- Create & New Entry ---
    /**
     * Show form to initiate a new payment record from the admin side.
     */
    @GetMapping("/new")
    public String newForm(HttpSession session, Model model, @RequestParam(required = false) String error) {
        if (!isAdmin(session)) return "redirect:/auth/staff-login";
        model.addAttribute("mode", "create");
        model.addAttribute("cardPayment", new CardPayment());
        model.addAttribute("cashPayment", new CashPayment());
        model.addAttribute("bookings", reservationService.findAll());
        model.addAttribute("formError", error);
        return "payment/process-payment";
    }

    /**
     * Process the submission of a new payment record.
     */
    @PostMapping
    public String create(HttpSession session,
                         @RequestParam(required = false) String paymentType,
                         @RequestParam(required = false) String method,
                         @RequestParam(required = false) String manualId,
                         @ModelAttribute CardPayment cardPayment,
                         @ModelAttribute CashPayment cashPayment,
                         RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/auth/staff-login";
        String selectedType = (paymentType == null || paymentType.isBlank()) ? method : paymentType;
        Payment toSave = "CASH".equalsIgnoreCase(selectedType) ? cashPayment : cardPayment;
        
        // Ensure a booking ID is provided before saving
        if (toSave.getBookingId() == null || toSave.getBookingId().isBlank()) {
            ra.addAttribute("error", "booking_required");
            return "redirect:/payments/new";
        }
        
        if (manualId != null && !manualId.isBlank()) {
            toSave.setId(manualId.trim());
        }

        // Auto-generate a unique Payment ID if not manually entered
        if (toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        try { paymentService.save(toSave); } catch (ValidationException ex) { ra.addAttribute("error", ex.getMessage()); return "redirect:/payments/new"; }
        return "redirect:/payments";
    }

    // --- Edit & Update ---
    /**
     * Show the edit form for an existing payment record.
     */
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/auth/staff-login";
        return paymentService.findById(id).map(p -> {
            model.addAttribute("mode", "edit");
            model.addAttribute("payment", p);
            model.addAttribute("paymentType", p instanceof CashPayment ? "CASH" : "CARD");
            model.addAttribute("bookings", reservationService.findAll());
            return "payment/edit-payment";
        }).orElse("redirect:/payments");
    }

    /**
     * Process the update of an existing payment record.
     */
    @PostMapping("/{id}")
    public String update(@PathVariable String id,
                         HttpSession session,
                         @RequestParam(required = false) String paymentType,
                         @RequestParam(required = false) String method,
                         @ModelAttribute CardPayment cardPayment,
                         @ModelAttribute CashPayment cashPayment) {
        if (!isAdmin(session)) return "redirect:/auth/staff-login";
        String selectedType = (paymentType == null || paymentType.isBlank()) ? method : paymentType;
        Payment toSave = "CASH".equalsIgnoreCase(selectedType) ? cashPayment : cardPayment;
        toSave.setId(id);
        try { paymentService.save(toSave); } catch (ValidationException ex) { return "redirect:/payments/" + id + "/edit?error=" + ex.getMessage(); }
        return "redirect:/payments";
    }

    // --- Verification & Confirmation Portal ---
    /**
     * Display the comparison between system-estimated cost and user-submitted payment.
     */
    @GetMapping("/{id}/confirm")
    public String confirmReview(@PathVariable String id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/auth/staff-login";
        return paymentService.findById(id).map(payment -> {
            model.addAttribute("payment", payment);
            var bookingOpt = reservationService.findById(payment.getBookingId());
            if (bookingOpt.isPresent()) {
                var booking = bookingOpt.get();
                model.addAttribute("booking", booking);
                var vehicleOpt = vehicleService.findById(booking.getVehicleId());
                if (vehicleOpt.isPresent()) {
                    var v = vehicleOpt.get();
                    double expected = v.calculateRentalRate() * Math.max(1, booking.getDurationDays());
                    model.addAttribute("expectedAmount", expected);
                    model.addAttribute("matches", Math.abs(expected - payment.getAmount()) < 0.01);
                    model.addAttribute("difference", Math.abs(expected - payment.getAmount()));
                    model.addAttribute("vehicle", v);
                } else {
                    return "redirect:/payments?error=vehicle_not_found";
                }
            } else {
                model.addAttribute("errorMessage", "Booking ID not found for this payment.");
                return "redirect:/payments?error=booking_not_found";
            }
            return "payment/confirm-payment-review";
        }).orElse("redirect:/payments");
    }

    /**
     * Finalize the payment settlement process, marking the record as 'PAID'.
     */
    @PostMapping("/{id}/confirm")
    public String confirmPayment(@PathVariable String id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/staff-login";
        paymentService.findById(id).ifPresent(p -> {
            p.setStatus("PAID");
            try { paymentService.save(p); } catch (Exception ignored) {}
        });
        return "redirect:/payments";
    }

    // --- Deletion ---
    /**
     * Delete a payment record from the ledger.
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/staff-login";
        paymentService.deleteById(id);
        return "redirect:/payments";
    }

    // --- Customer Portal Actions ---
    /**
     * Handle payment submissions from the customer portal, marking them for staff verification.
     */
    @PostMapping({"/customer/pay", "/customer/gateway/complete"})
    public String customerSubmit(@RequestParam String bookingId, HttpSession session, RedirectAttributes ra) {
        if (!isCustomer(session)) return "redirect:/auth/login";
        paymentService.findLatestByBookingId(bookingId).ifPresent(p -> {
            if (p instanceof CashPayment) {
                Payment newCardPayment = new CardPayment(p.getId(), p.getBookingId(), p.getAmount(), "VERIFYING");
                try { paymentService.save(newCardPayment); } catch (Exception ignored) {}
            } else {
                p.setStatus("VERIFYING");
                try { paymentService.save(p); } catch (Exception ignored) {}
            }
        });
        ra.addFlashAttribute("paymentMessage", "Payment submitted for verification.");
        return "redirect:/customer/billing";
    }

    /**
     * Intercept the user's attempt to go to the card gateway.
     * Prevents access if the payment is already marked as CASH by staff.
     */
    @GetMapping("/customer/validate-gateway")
    public String validateGateway(@RequestParam("bookingId") String bookingId, HttpSession session, RedirectAttributes ra) {
        if (!isCustomer(session)) return "redirect:/auth/login";
        
        if (paymentService.hasPaidPaymentForBooking(bookingId)) {
            ra.addFlashAttribute("paymentMessage", "This booking is already paid.");
            return "redirect:/customer/billing";
        }
        
        // Allowed: Forward the customer to the actual gateway handled by the external Customer Portal Controller
        return "redirect:/customer/billing/gateway?bookingId=" + bookingId;
    }
}
