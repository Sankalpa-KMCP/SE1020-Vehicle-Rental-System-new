package com.sliit.vehiclerentalsystem01.payment.controller;

import com.sliit.vehiclerentalsystem01.booking.service.BookingService;
import com.sliit.vehiclerentalsystem01.payment.model.CardPayment;
import com.sliit.vehiclerentalsystem01.payment.model.CashPayment;
import com.sliit.vehiclerentalsystem01.payment.model.Payment;
import com.sliit.vehiclerentalsystem01.payment.service.PaymentService;
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

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;

    public PaymentController(PaymentService paymentService, BookingService bookingService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public String list(Model model) {
        List<Payment> payments = paymentService.findAll();
        model.addAttribute("payments", payments);
        model.addAttribute("totalTransactions", payments.size());
        long paidCount = payments.stream()
                .filter(p -> statusMatches(p.getStatus(), "PAID", "COMPLETED", "SETTLED"))
                .count();
        long pendingCount = payments.stream()
                .filter(p -> statusMatches(p.getStatus(), "PENDING", "DUE", "AWAITING"))
                .count();
        long refundedCount = payments.stream()
                .filter(p -> statusMatches(p.getStatus(), "REFUND", "REFUNDED"))
                .count();
        double totalAmount = payments.stream().mapToDouble(Payment::getAmount).sum();
        model.addAttribute("paidCount", paidCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("refundedCount", refundedCount);
        model.addAttribute("totalAmount", totalAmount);
        return "payment/payment-dashboard";
    }

    private static boolean statusMatches(String status, String... tokens) {
        if (status == null || status.isBlank()) {
            return false;
        }
        String s = status.trim().toUpperCase();
        for (String t : tokens) {
            if (s.contains(t.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    @GetMapping("/new")
    public String newForm(Model model, @RequestParam(required = false) String error) {
        model.addAttribute("mode", "create");
        model.addAttribute("entityId", null);
        model.addAttribute("paymentType", "CARD");
        model.addAttribute("cardPayment", new CardPayment());
        model.addAttribute("cashPayment", new CashPayment());
        model.addAttribute("bookings", bookingService.findAll());
        model.addAttribute("formError", error);
        return "payment/process-payment";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        return paymentService.findById(id)
                .map(p -> {
                    model.addAttribute("payment", p);
                    return "payment/payment-detail";
                })
                .orElse("redirect:/payments");
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
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
                    model.addAttribute("bookings", bookingService.findAll());
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
            @RequestParam String id,
            @RequestParam String bookingId,
            @RequestParam double amount,
            @RequestParam String method,
            @RequestParam String status) {
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
        paymentService.save(toSave);
        return "redirect:/payments";
    }

    /**
     * Legacy {@code process-payment.jsp} POST: {@code action=create}, {@code bookingId},
     * {@code amount}, {@code method} (Card / Cash), {@code status}, optional {@code manualId}.
     */
    @PostMapping(params = "action=create")
    public String legacyJspCreate(
            @RequestParam String bookingId,
            @RequestParam double amount,
            @RequestParam String method,
            @RequestParam String status,
            @RequestParam(required = false) String manualId,
            RedirectAttributes ra) {
        String bid = bookingId == null ? "" : bookingId.trim();
        if (bid.isEmpty()) {
            ra.addAttribute("error", "booking_required");
            return "redirect:/payments/new";
        }
        if (bookingService.findById(bid).isEmpty()) {
            ra.addAttribute("error", "booking_not_found");
            return "redirect:/payments/new";
        }
        if (!bookingService.isPaymentAllowed(bid)) {
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
        paymentService.save(toSave);
        return "redirect:/payments";
    }

    @PostMapping
    public String create(
            @RequestParam String paymentType,
            @ModelAttribute CardPayment cardPayment,
            @ModelAttribute CashPayment cashPayment) {
        Payment toSave = "CASH".equalsIgnoreCase(paymentType) ? cashPayment : cardPayment;
        paymentService.save(toSave);
        return "redirect:/payments";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable String id,
            @RequestParam String paymentType,
            @ModelAttribute CardPayment cardPayment,
            @ModelAttribute CashPayment cashPayment) {
        Payment toSave = "CASH".equalsIgnoreCase(paymentType) ? cashPayment : cardPayment;
        toSave.setId(id);
        paymentService.save(toSave);
        return "redirect:/payments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        paymentService.deleteById(id);
        return "redirect:/payments";
    }
}
