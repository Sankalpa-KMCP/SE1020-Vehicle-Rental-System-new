package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.BookingDAO;
import com.example.vehiclerentalsystem01.dao.PaymentDAO;
import com.example.vehiclerentalsystem01.models.Booking;
import com.example.vehiclerentalsystem01.models.CardPayment;
import com.example.vehiclerentalsystem01.models.CashPayment;
import com.example.vehiclerentalsystem01.models.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;     // to read what the user typed
import jakarta.servlet.http.HttpServletResponse;    // send an answer back to user
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.UUID;      // generate unique, random IDs


// setup and connection
@WebServlet("/payments")    // URL routing
public class PaymentController extends HttpServlet {
    private PaymentDAO paymentDAO = new PaymentDAO();
    private BookingDAO bookingDAO = new BookingDAO();

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


    // Handling links and views
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            String id = request.getParameter("id");
            paymentDAO.deletePayment(id);
            response.sendRedirect("payments?action=list");
        }
        else if ("edit".equals(action)) {
            String id = request.getParameter("id");
            Payment payment = paymentDAO.getPaymentById(id);
            if(payment != null) {
                request.setAttribute("payment", payment);
                request.getRequestDispatcher("edit-payment.jsp").forward(request, response);
            } else {
                response.sendRedirect("payments?action=list");
            }
        }

        else if ("customer_checkout".equals(action)) {
            String bookingId = normalized(request.getParameter("bookingId"));
            Booking booking = bookingDAO.getBookingById(bookingId);
            HttpSession session = request.getSession(false);

            if (!bookingDAO.isPaymentAllowed(bookingId) || !isCustomerOwner(session, booking)) {
                response.sendRedirect("my-bookings.jsp?payment=blocked");
                return;
            }

            Payment payment = paymentDAO.getLatestPaymentByBookingId(bookingId);
            if (payment == null) {
                response.sendRedirect("my-bookings.jsp?payment=missing_invoice");
                return;
            }

            request.setAttribute("booking", booking);
            request.setAttribute("payment", payment);
            request.getRequestDispatcher("customer-payment.jsp").forward(request, response);
        }

        else {
            List<Payment> payments = paymentDAO.getAllPayments();
            request.setAttribute("paymentList", payments);
            request.getRequestDispatcher("payment-dashboard.jsp").forward(request, response);
        }
    }


    // Handling form submissions
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("customer_pay".equals(action)) {
            String bookingId = normalized(request.getParameter("bookingId"));
            String method = normalized(request.getParameter("method"));

            Booking booking = bookingDAO.getBookingById(bookingId);
            HttpSession session = request.getSession(false);
            if (!bookingDAO.isPaymentAllowed(bookingId) || !isCustomerOwner(session, booking)) {
                response.sendRedirect("my-bookings.jsp?payment=blocked");
                return;
            }

            Payment existing = paymentDAO.getLatestPaymentByBookingId(bookingId);
            if (existing == null) {
                response.sendRedirect("my-bookings.jsp?payment=missing_invoice");
                return;
            }

            Payment paidPayment = "Cash".equalsIgnoreCase(method)
                    ? new CashPayment(existing.getPaymentId(), bookingId, existing.getAmount(), "PAID")
                    : new CardPayment(existing.getPaymentId(), bookingId, existing.getAmount(), "PAID");
            paymentDAO.updatePayment(paidPayment);

            response.sendRedirect("my-bookings.jsp?payment=success");
            return;
        }

        String bookingId = normalized(request.getParameter("bookingId"));
        String amountValue = normalized(request.getParameter("amount"));
        String method = normalized(request.getParameter("method"));
        String status = normalized(request.getParameter("status"));

        if (bookingId.isEmpty()) {
            response.sendRedirect("process-payment.jsp?error=missing_booking");
            return;
        }

        if (!bookingDAO.isPaymentAllowed(bookingId)) {
            response.sendRedirect("process-payment.jsp?error=booking_not_confirmed&bookingId=" + bookingId);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountValue);
        } catch (NumberFormatException ex) {
            response.sendRedirect("process-payment.jsp?error=invalid_amount&bookingId=" + bookingId);
            return;
        }


        if ("update".equals(action)) {
            String id = request.getParameter("id");
            Payment updatedPayment = "Card".equalsIgnoreCase(method) ?
                    new CardPayment(id, bookingId, amount, status) :
                    new CashPayment(id, bookingId, amount, status);
            paymentDAO.updatePayment(updatedPayment);
        }
        // Creating a new payment
        else {
            String manualId = request.getParameter("manualId");
            String newId = (manualId != null && !manualId.trim().isEmpty()) ? manualId.trim() : "INV-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

            Payment newPayment = "Card".equalsIgnoreCase(method) ?
                    new CardPayment(newId, bookingId, amount, status) :
                    new CashPayment(newId, bookingId, amount, status);
            paymentDAO.savePayment(newPayment);
        }

        response.sendRedirect("payments?action=list");
    }
}