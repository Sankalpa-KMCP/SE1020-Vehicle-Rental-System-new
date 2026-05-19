package com.sliit.vehiclerentalsystem01.payment.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.payment.entity.CardPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.CashPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.Payment;
import com.sliit.vehiclerentalsystem01.payment.entity.PaymentStatus;
import com.sliit.vehiclerentalsystem01.payment.service.PaymentService;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Admin payment management. Every endpoint requires an {@code ADMIN_LOGGED_IN} session;
 * customer-facing payment flows live under {@code /customer/*}.
 */
@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final ReservationService reservationService;

    public PaymentController(PaymentService paymentService,
                             ReservationService reservationService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
    }

    private static boolean admin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }

        List<Payment> payments = paymentService.findAll();
        model.addAttribute("payments", payments);
        model.addAttribute("totalTransactions", payments.size());
        long paidCount = payments.stream()
                .filter(p -> PaymentStatus.fromString(p.getStatus()).isPaid())
                .count();
        long pendingCount = payments.stream()
                .filter(p -> PaymentStatus.fromString(p.getStatus()).isPending())
                .count();
        long refundedCount = payments.stream()
                .filter(p -> PaymentStatus.fromString(p.getStatus()).isRefunded())
                .count();
        double totalAmount = payments.stream().mapToDouble(Payment::getAmount).sum();
        model.addAttribute("paidCount", paidCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("refundedCount", refundedCount);
        model.addAttribute("totalAmount", totalAmount);
        return "payment/payment-dashboard";
    }

    @GetMapping("/new")
    public String newForm(HttpSession session, Model model, @RequestParam(required = false) String error) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        model.addAttribute("mode", "create");
        model.addAttribute("entityId", null);
        model.addAttribute("paymentType", "CARD");
        model.addAttribute("cardPayment", new CardPayment());
        model.addAttribute("cashPayment", new CashPayment());
        model.addAttribute("bookings", reservationService.findAll());
        model.addAttribute("formError", error);
        return "payment/process-payment";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        return paymentService.findById(id)
                .map(p -> {
                    model.addAttribute("payment", p);
                    return "payment/payment-detail";
                })
                .orElse("redirect:/payments");
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        return paymentService.findById(id)
                .map(p -> {
                    model.addAttribute("mode", "edit");
                    model.addAttribute("entityId", id);
                    model.addAttribute("payment", p);
                    model.addAttribute("paymentType", p instanceof CashPayment ? "CASH" : "CARD");
                    if (p instanceof CardPayment c) {
                        model.addAttribute("cardPayment", c);
                        model.addAttribute("cashPayment", new CashPayment());
                    } else {
                        model.addAttribute("cardPayment", new CardPayment());
                        model.addAttribute("cashPayment", (CashPayment) p);
                    }
                    model.addAttribute("bookings", reservationService.findAll());
                    return "payment/edit-payment";
                })
                .orElse("redirect:/payments");
    }

    /**
     * Legacy {@code edit-payment.jsp} POST: {@code action=update}, {@code id}, {@code bookingId},
     * {@code amount}, {@code method} (Card/Cash), {@code status}.
     */
    @PostMapping(params = "action=update")
    public String legacyJspUpdate(
            HttpSession session,
            @RequestParam String id,
            @RequestParam String bookingId,
            @RequestParam double amount,
            @RequestParam String method,
            @RequestParam String status) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        Optional<Payment> opt = paymentService.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/payments";
        }
        Payment existing = opt.get();
        boolean wantCash = method != null && method.toLowerCase().contains("cash");
        Payment toSave;
        if (wantCash && existing instanceof CardPayment) {
            toSave = new CashPayment(id, bookingId, amount, status);
        } else if (!wantCash && existing instanceof CashPayment) {
            toSave = new CardPayment(id, bookingId, amount, status);
        } else if (existing instanceof CardPayment) {
            toSave = new CardPayment(id, bookingId, amount, status);
        } else {
            toSave = new CashPayment(id, bookingId, amount, status);
        }
        try {
            paymentService.save(toSave);
        } catch (ValidationException ex) {
            return "redirect:/payments/" + id + "/edit?error=" + ex.getMessage();
        }
        return "redirect:/payments";
    }

    /**
     * Legacy {@code process-payment.jsp} POST: {@code action=create}, {@code bookingId},
     * {@code amount}, {@code method} (Card / Cash), {@code status}, optional {@code manualId}.
     */
    @PostMapping(params = "action=create")
    public String legacyJspCreate(
            HttpSession session,
            @RequestParam String bookingId,
            @RequestParam double amount,
            @RequestParam String method,
            @RequestParam String status,
            @RequestParam(required = false) String manualId,
            RedirectAttributes ra) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        String bid = bookingId == null ? "" : bookingId.trim();
        if (bid.isEmpty()) {
            ra.addAttribute("error", "booking_required");
            return "redirect:/payments/new";
        }
        if (reservationService.findById(bid).isEmpty()) {
            ra.addAttribute("error", "booking_not_found");
            return "redirect:/payments/new";
        }
        if (!reservationService.isPaymentAllowed(bid)) {
            ra.addAttribute("error", "booking_not_confirmed");
            return "redirect:/payments/new";
        }
        String id = (manualId != null && !manualId.isBlank())
                ? manualId.trim()
                : "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        boolean wantCash = method != null && method.toLowerCase().contains("cash");
        Payment toSave = wantCash
                ? new CashPayment(id, bid, amount, status == null ? "" : status.trim())
                : new CardPayment(id, bid, amount, status == null ? "" : status.trim());
        try {
            paymentService.save(toSave);
        } catch (ValidationException ex) {
            ra.addAttribute("error", ex.getMessage());
            return "redirect:/payments/new";
        }
        return "redirect:/payments";
    }

    /**
     * Modern create POST. Mirrors the booking-validity checks from {@link #legacyJspCreate}:
     * the linked booking must exist and must be CONFIRMED before a payment row can be added.
     */
    @PostMapping
    public String create(
            HttpSession session,
            @RequestParam String paymentType,
            @ModelAttribute CardPayment cardPayment,
            @ModelAttribute CashPayment cashPayment,
            RedirectAttributes ra) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        Payment toSave = "CASH".equalsIgnoreCase(paymentType) ? cashPayment : cardPayment;
        String bookingId = toSave.getBookingId() == null ? "" : toSave.getBookingId().trim();
        if (bookingId.isEmpty()) {
            ra.addAttribute("error", "booking_required");
            return "redirect:/payments/new";
        }
        if (reservationService.findById(bookingId).isEmpty()) {
            ra.addAttribute("error", "booking_not_found");
            return "redirect:/payments/new";
        }
        if (!reservationService.isPaymentAllowed(bookingId)) {
            ra.addAttribute("error", "booking_not_confirmed");
            return "redirect:/payments/new";
        }
        try {
            paymentService.save(toSave);
        } catch (ValidationException ex) {
            ra.addAttribute("error", ex.getMessage());
            return "redirect:/payments/new";
        }
        return "redirect:/payments";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable String id,
            HttpSession session,
            @RequestParam String paymentType,
            @ModelAttribute CardPayment cardPayment,
            @ModelAttribute CashPayment cashPayment) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        Payment toSave = "CASH".equalsIgnoreCase(paymentType) ? cashPayment : cardPayment;
        toSave.setId(id);
        try {
            paymentService.save(toSave);
        } catch (ValidationException ex) {
            return "redirect:/payments/" + id + "/edit?error=" + ex.getMessage();
        }
        return "redirect:/payments";
    }

    @PostMapping("/{id}/confirm")
    public String confirmPayment(@PathVariable String id, HttpSession session) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        Optional<Payment> payment = paymentService.findById(id);
        if (payment.isEmpty()) {
            return "redirect:/payments";
        }
        Payment confirmedPayment = payment.get();
        confirmedPayment.setStatus("PAID");
        try {
            paymentService.save(confirmedPayment);
        } catch (ValidationException ex) {
            return "redirect:/payments/" + id + "/edit?error=" + ex.getMessage();
        }
        return "redirect:/payments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, HttpSession session) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        paymentService.deleteById(id);
        return "redirect:/payments";
    }

    private static boolean customer(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.CUSTOMER_LOGGED_IN));
    }

    @PostMapping("/customer/gateway/complete")
    public String customerGatewayComplete(HttpSession session,
                                          @RequestParam String bookingId,
                                          RedirectAttributes ra) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        Optional<Payment> payment = paymentService.findLatestByBookingId(bookingId);
        if (payment.isPresent()) {
            Payment p = payment.get();
            p.setStatus("VERIFYING");
            try {
                paymentService.save(p);
            } catch (ValidationException ex) {
                return "redirect:/customer/billing/gateway?bookingId=" + bookingId + "&error=" + ex.getMessage();
            }
        }
        ra.addFlashAttribute("paymentMessage", "Your payment is pending for staff approval.");
        return "redirect:/customer/billing?payment=pending_approval";
    }

    @PostMapping("/customer/pay")
    public String customerPay(HttpSession session,
                              @RequestParam String bookingId,
                              RedirectAttributes ra) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        Optional<Payment> latest = paymentService.findLatestByBookingId(bookingId);
        if (latest.isPresent()) {
            Payment p = latest.get();
            p.setStatus("VERIFYING");
            try {
                paymentService.save(p);
            } catch (ValidationException ex) {
                return "redirect:/customer/pay?bookingId=" + bookingId + "&error=" + ex.getMessage();
            }
        }
        ra.addFlashAttribute("paymentMessage", "Your payment is pending for staff approval.");
        return "redirect:/customer/billing?payment=pending_approval";
    }
}
