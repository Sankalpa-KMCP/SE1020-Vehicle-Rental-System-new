package com.sliit.vehiclerentalsystem01.payment.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.payment.entity.CardPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.CashPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.Payment;
import com.sliit.vehiclerentalsystem01.payment.entity.PaymentStatus;
import com.sliit.vehiclerentalsystem01.payment.service.PaymentService;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

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

    @GetMapping
    public String list(Model model) {
        List<Payment> payments = paymentService.findAll();
        model.addAttribute("payments", payments);
        model.addAttribute("totalTransactions", payments.size());
        model.addAttribute("paidCount",    payments.stream().filter(p -> PaymentStatus.fromString(p.getStatus()).isPaid()).count());
        model.addAttribute("pendingCount", payments.stream().filter(p -> PaymentStatus.fromString(p.getStatus()).isPending()).count());
        model.addAttribute("refundedCount", payments.stream().filter(p -> PaymentStatus.fromString(p.getStatus()).isRefunded()).count());
        model.addAttribute("totalAmount", payments.stream().mapToDouble(Payment::getAmount).sum());
        return "payment/payment-dashboard";
    }

    @GetMapping("/new")
    public String newForm(Model model, @RequestParam(required = false) String error) {
        model.addAttribute("mode", "create");
        model.addAttribute("cardPayment", new CardPayment());
        model.addAttribute("cashPayment", new CashPayment());
        model.addAttribute("bookings", reservationService.findAll());
        model.addAttribute("formError", error);
        return "payment/process-payment";
    }

    @PostMapping
    public String create(@RequestParam(required = false) String paymentType,
                         @RequestParam(required = false) String method,
                         @RequestParam(required = false) String manualId,
                         @ModelAttribute CardPayment cardPayment,
                         @ModelAttribute CashPayment cashPayment,
                         RedirectAttributes ra) {
        String selectedType = (paymentType == null || paymentType.isBlank()) ? method : paymentType;
        Payment toSave = "CASH".equalsIgnoreCase(selectedType) ? cashPayment : cardPayment;

        if (toSave.getBookingId() == null || toSave.getBookingId().isBlank()) {
            ra.addAttribute("error", "booking_required");
            return "redirect:/payments/new";
        }

        if (manualId != null && !manualId.isBlank()) {
            toSave.setId(manualId.trim());
        }
        if (toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        try { paymentService.save(toSave); } catch (ValidationException ex) { ra.addAttribute("error", ex.getMessage()); return "redirect:/payments/new"; }
        return "redirect:/payments";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        return paymentService.findById(id).map(p -> {
            model.addAttribute("mode", "edit");
            model.addAttribute("payment", p);
            model.addAttribute("paymentType", p instanceof CashPayment ? "CASH" : "CARD");
            model.addAttribute("bookings", reservationService.findAll());
            return "payment/edit-payment";
        }).orElse("redirect:/payments");
    }

    @PostMapping("/{id}")
    public String update(@PathVariable String id,
                         @RequestParam(required = false) String paymentType,
                         @RequestParam(required = false) String method,
                         @ModelAttribute CardPayment cardPayment,
                         @ModelAttribute CashPayment cashPayment) {
        String selectedType = (paymentType == null || paymentType.isBlank()) ? method : paymentType;
        Payment toSave = "CASH".equalsIgnoreCase(selectedType) ? cashPayment : cardPayment;
        toSave.setId(id);
        try { paymentService.save(toSave); } catch (ValidationException ex) { return "redirect:/payments/" + id + "/edit?error=" + ex.getMessage(); }
        return "redirect:/payments";
    }

    @GetMapping("/{id}/confirm")
    public String confirmReview(@PathVariable String id, Model model) {
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
                return "redirect:/payments?error=booking_not_found";
            }
            return "payment/confirm-payment-review";
        }).orElse("redirect:/payments");
    }

    @PostMapping("/{id}/confirm")
    public String confirmPayment(@PathVariable String id) {
        paymentService.findById(id).ifPresent(p -> {
            p.setStatus("PAID");
            try { paymentService.save(p); } catch (ValidationException ignored) {}
        });
        return "redirect:/payments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        paymentService.deleteById(id);
        return "redirect:/payments";
    }

    @PostMapping({"/customer/pay", "/customer/gateway/complete"})
    public String customerSubmit(@RequestParam String bookingId, RedirectAttributes ra) {
        paymentService.findLatestByBookingId(bookingId).ifPresent(p -> {
            if (p instanceof CashPayment) {
                Payment newCardPayment = new CardPayment(p.getId(), p.getBookingId(), p.getAmount(), "VERIFYING");
                try { paymentService.save(newCardPayment); } catch (ValidationException ignored) {}
            } else {
                p.setStatus("VERIFYING");
                try { paymentService.save(p); } catch (ValidationException ignored) {}
            }
        });
        ra.addFlashAttribute("paymentMessage", "Payment submitted for verification.");
        return "redirect:/customer/billing";
    }

    @GetMapping("/customer/validate-gateway")
    public String validateGateway(@RequestParam("bookingId") String bookingId, RedirectAttributes ra) {
        if (paymentService.hasPaidPaymentForBooking(bookingId)) {
            ra.addFlashAttribute("paymentMessage", "This booking is already paid.");
            return "redirect:/customer/billing";
        }
        return "redirect:/customer/billing/gateway?bookingId=" + bookingId;
    }
}
