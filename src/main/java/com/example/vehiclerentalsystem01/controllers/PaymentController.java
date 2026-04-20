package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.PaymentDAO;
import com.example.vehiclerentalsystem01.models.CardPayment;
import com.example.vehiclerentalsystem01.models.CashPayment;
import com.example.vehiclerentalsystem01.models.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;     // to read what the user typed
import jakarta.servlet.http.HttpServletResponse;    // send an answer back to user

import java.io.IOException;
import java.util.List;
import java.util.UUID;      // generate unique, random IDs


// setup and connection
@WebServlet("/payments")    // URL routing
public class PaymentController extends HttpServlet {
    private PaymentDAO paymentDAO = new PaymentDAO();


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

        String bookingId = request.getParameter("bookingId");
        double amount = Double.parseDouble(request.getParameter("amount"));
        String method = request.getParameter("method");
        String status = request.getParameter("status");


        if ("update".equals(action)) {
            String id = request.getParameter("id");
            Payment updatedPayment = method.equals("Card") ?
                    new CardPayment(id, bookingId, amount, status) :
                    new CashPayment(id, bookingId, amount, status);
            paymentDAO.updatePayment(updatedPayment);
        }
        // Creating a new payment
        else {
            String manualId = request.getParameter("manualId");
            String newId = (manualId != null && !manualId.trim().isEmpty()) ? manualId.trim() : "INV-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

            Payment newPayment = method.equals("Card") ?
                    new CardPayment(newId, bookingId, amount, status) :
                    new CashPayment(newId, bookingId, amount, status);
            paymentDAO.savePayment(newPayment);
        }

        response.sendRedirect("payments?action=list");
    }
}