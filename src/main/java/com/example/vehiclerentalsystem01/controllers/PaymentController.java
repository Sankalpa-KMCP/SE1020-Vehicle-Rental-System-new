package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.BookingDAO;
import com.example.vehiclerentalsystem01.dao.PaymentDAO;
import com.example.vehiclerentalsystem01.models.Booking;
import com.example.vehiclerentalsystem01.models.CardPayment;
import com.example.vehiclerentalsystem01.models.CashPayment;
import com.example.vehiclerentalsystem01.models.Payment;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/payme2222222nts")
public class PaymentController {
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    private String normalized(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isCustomerOwner(HttpSession session, Booking booking) {
        if (booking == null || session == null) {
            return false;
        }

        String sessionUser = normalized((String) session.getAttribute("username"));
        if (sessionUser.isEmpty()) {
            sessionUser = normalized((String) session.getAttribute("name"));
        }
        return !sessionUser.isEmpty() && sessionUser.equalsIgnoreCase(normalized(booking.getCustomerName()));
    }

    @GetMapping
    public String handleGet(@RequestParam(required = false) String action,
                            @RequestParam(required = false) String id,
                            @RequestParam(required = false) String bookingId,
                            HttpSession session,
                            Model model) {
        if ("delete".equals(action)) {
            paymentDAO.deletePayment(id);
            return "redirect:/payments?action=list";
        }

        if ("edit".equals(action)) {
            Payment payment = paymentDAO.getPaymentById(id);
            if (payment == null) {
                return "redirect:/payments?action=list";
            }
            model.addAttribute("payment", payment);
            return "edit-payment";
        }

        if ("customer_checkout".equals(action)) {
            String normalizedBookingId = normalized(bookingId);
            Booking booking = bookingDAO.getBookingById(normalizedBookingId);

            if (!bookingDAO.isPaymentAllowed(normalizedBookingId) || !isCustomerOwner(session, booking)) {
                return "redirect:/my-bookings.jsp?payment=blocked";
            }

            Payment payment = paymentDAO.getLatestPaymentByBookingId(normalizedBookingId);
            if (payment == null) {
                return "redirect:/my-bookings.jsp?payment=missing_invoice";
            }

            model.addAttribute("booking", booking);
            model.addAttribute("payment", payment);
            return "customer-payment";
        }

        model.addAttribute("paymentList", paymentDAO.getAllPayments());
        return "payment-dashboard";
    }

    @PostMapping
    public String handlePost(@RequestParam Map<String, String> params, HttpSession session) {
        String action = params.get("action");

        if ("customer_pay".equals(action)) {
            String bookingId = normalized(params.get("bookingId"));
            String method = normalized(params.get("method"));
            Booking booking = bookingDAO.getBookingById(bookingId);

            if (!bookingDAO.isPaymentAllowed(bookingId) || !isCustomerOwner(session, booking)) {
                return "redirect:/my-bookings.jsp?payment=blocked";
            }

            Payment existing = paymentDAO.getLatestPaymentByBookingId(bookingId);
            if (existing == null) {
                return "redirect:/my-bookings.jsp?payment=missing_invoice";
            }

            Payment paidPayment = "Cash".equalsIgnoreCase(method)
                    ? new CashPayment(existing.getPaymentId(), bookingId, existing.getAmount(), "PAID")
                    : new CardPayment(existing.getPaymentId(), bookingId, existing.getAmount(), "PAID");
            paymentDAO.updatePayment(paidPayment);
            return "redirect:/my-bookings.jsp?payment=success";
        }

        String bookingId = normalized(params.get("bookingId"));
        String method = normalized(params.get("method"));
        String status = normalized(params.get("status"));

        if (bookingId.isEmpty()) {
            return "redirect:/process-payment.jsp?error=missing_booking";
        }

        if (!bookingDAO.isPaymentAllowed(bookingId)) {
            return "redirect:/process-payment.jsp?error=booking_not_confirmed&bookingId=" + bookingId;
        }

        double amount;
        try {
            amount = Double.parseDouble(normalized(params.get("amount")));
        } catch (NumberFormatException ex) {
            return "redirect:/process-payment.jsp?error=invalid_amount&bookingId=" + bookingId;
        }

        if ("update".equals(action)) {
            String id = params.get("id");
            Payment updatedPayment = "Card".equalsIgnoreCase(method)
                    ? new CardPayment(id, bookingId, amount, status)
                    : new CashPayment(id, bookingId, amount, status);
            paymentDAO.updatePayment(updatedPayment);
        } else {
            String manualId = params.get("manualId");
            String newId = manualId != null && !manualId.trim().isEmpty()
                    ? manualId.trim()
                    : "INV-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            Payment newPayment = "Card".equalsIgnoreCase(method)
                    ? new CardPayment(newId, bookingId, amount, status)
                    : new CashPayment(newId, bookingId, amount, status);
            paymentDAO.savePayment(newPayment);
        }

        return "redirect:/payments?action=list";
    }
}
