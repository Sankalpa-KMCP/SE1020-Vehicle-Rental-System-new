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
        model.addAttribute("payments", paymentService.findAll());
        return "payment/payment-dashboard";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("mode", "create");
        model.addAttribute("entityId", null);
        model.addAttribute("paymentType", "CARD");
        model.addAttribute("cardPayment", new CardPayment());
        model.addAttribute("cashPayment", new CashPayment());
        model.addAttribute("bookings", bookingService.findAll());
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
