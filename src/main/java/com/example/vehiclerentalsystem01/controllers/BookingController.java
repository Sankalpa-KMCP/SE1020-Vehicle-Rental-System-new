package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.BookingDAO;
import com.example.vehiclerentalsystem01.models.Booking;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/bookings")
public class BookingController extends HttpServlet {
    private BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("list".equals(action) || action == null) {
                List<Booking> allBookings = bookingDAO.getAllBookings();
                if (allBookings == null) allBookings = new ArrayList<>();

                int totalBookings = allBookings.size();
                int confirmedBookings = 0;
                for (Booking b : allBookings) {
                    if ("CONFIRMED".equalsIgnoreCase(b.getStatus())) confirmedBookings++;
                }

                // Provide multiple names just in case different JSPs expect different variables
                request.setAttribute("bookings", allBookings);
                request.setAttribute("bookingList", allBookings);
                request.setAttribute("allBookings", allBookings);

                request.setAttribute("totalBookings", totalBookings);
                request.setAttribute("confirmedBookings", confirmedBookings);
                request.getRequestDispatcher("booking-dashboard.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("delete".equals(action)) {
                bookingDAO.deleteBooking(request.getParameter("id"));
                response.sendRedirect("bookings?action=list");

            } else if ("update".equals(action)) {
                String id = request.getParameter("id");
                Booking updatedBooking = new Booking(id, request.getParameter("customerName"),
                        request.getParameter("rentalDate"), request.getParameter("vehicleId"),
                        Integer.parseInt(request.getParameter("days").trim()), "UPDATED");
                bookingDAO.updateBooking(updatedBooking);
                response.sendRedirect("bookings?action=list");

                // --- THE NEW APPROVE / REJECT LOGIC ---
            } else if ("approve".equals(action) || "reject".equals(action)) {
                String id = request.getParameter("id");

                // If they clicked approve, status becomes CONFIRMED. Otherwise, REJECTED.
                String newStatus = "approve".equals(action) ? "CONFIRMED" : "REJECTED";

                Booking updatedBooking = new Booking(id, request.getParameter("customerName"),
                        request.getParameter("rentalDate"), request.getParameter("vehicleId"),
                        Integer.parseInt(request.getParameter("days").trim()), newStatus);

                bookingDAO.updateBooking(updatedBooking);
                response.sendRedirect("bookings?action=list");

                // --- THE CREATION LOGIC (NOW SAVES AS PENDING) ---
            } else if ("create".equals(action)) {
                String newId = "BKG-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

                String custName = request.getParameter("customerName");
                String rentDate = request.getParameter("rentalDate");
                String vehId = request.getParameter("vehicleId");
                String daysParam = request.getParameter("days");

                int days = (daysParam != null && !daysParam.trim().isEmpty()) ? Integer.parseInt(daysParam.trim()) : 1;
                if (custName != null) custName = custName.replace(",", "");

                // SAVES AS PENDING INSTEAD OF CONFIRMED
                Booking newBooking = new Booking(newId, custName, rentDate, vehId, days, "PENDING");
                bookingDAO.saveBooking(newBooking);

                // Smart Redirect
                HttpSession session = request.getSession();
                Boolean customerLoggedIn = (Boolean) session.getAttribute("customerLoggedIn");

                if (customerLoggedIn != null && customerLoggedIn) {
                    response.sendRedirect("my-bookings.jsp");
                } else {
                    response.sendRedirect("bookings?action=list");
                }
            }
        } catch (Exception e) {
            System.out.println("CRITICAL ERROR IN CONTROLLER: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("my-bookings.jsp"); // Safe fallback
        }
    }
}