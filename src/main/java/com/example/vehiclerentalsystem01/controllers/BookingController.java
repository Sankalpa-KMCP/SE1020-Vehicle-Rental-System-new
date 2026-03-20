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
        System.out.println("==== BOOKING CONTROLLER TRIGGERED ====");
        System.out.println("Action: " + action);

        try {
            // ========================================================
            // ADMIN SIDE: The Unified Dashboard
            // ========================================================
            if ("list".equals(action) || action == null) {
                List<Booking> allBookings = bookingDAO.getAllBookings();
                if (allBookings == null) allBookings = new ArrayList<>();

                int totalBookings = allBookings.size();
                int activeBookings = 0;
                int pendingBookings = 0;

                // Calculate Stats for the Dashboard Cards
                for (Booking b : allBookings) {
                    if ("CONFIRMED".equalsIgnoreCase(b.getStatus()) || "UPDATED".equalsIgnoreCase(b.getStatus())) {
                        activeBookings++;
                    } else if ("PENDING".equalsIgnoreCase(b.getStatus())) {
                        pendingBookings++;
                    }
                }

                request.setAttribute("bookingList", allBookings);
                request.setAttribute("totalBookings", totalBookings);
                request.setAttribute("activeBookings", activeBookings);
                request.setAttribute("pendingBookings", pendingBookings);

                request.getRequestDispatcher("booking-dashboard.jsp").forward(request, response);
            }
            // ========================================================
            // CUSTOMER SIDE: Keeps your existing customer logic intact
            // ========================================================
            else if ("view-all".equals(action)) {
                request.setAttribute("bookingList", bookingDAO.getAllBookings());
                request.getRequestDispatcher("view-all-bookings.jsp").forward(request, response);

            } else if ("search".equals(action)) {
                String id = request.getParameter("id");
                request.setAttribute("searchedBooking", bookingDAO.getBookingById(id));
                request.setAttribute("searchAttempted", true);
                request.getRequestDispatcher("search-booking.jsp").forward(request, response);

            } else if ("edit".equals(action)) {
                String id = request.getParameter("id");
                Booking b = bookingDAO.getBookingById(id);
                if (b != null) {
                    request.setAttribute("bookingToEdit", b);
                } else {
                    request.setAttribute("error", "Not Found");
                }
                request.getRequestDispatcher("edit-booking.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("!!! CRASH IN BOOKING CONTROLLER !!!");
            e.printStackTrace();
            response.sendRedirect("admin-home.jsp");
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
                        request.getParameter("vehicleId"), request.getParameter("rentalDate"),
                        Integer.parseInt(request.getParameter("days")), "UPDATED");
                bookingDAO.updateBooking(updatedBooking);
                response.sendRedirect("bookings?action=list");

            } else if ("create".equals(action)) {
                String newId = "BKG-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                Booking newBooking = new Booking(newId, request.getParameter("customerName"),
                        request.getParameter("vehicleId"), request.getParameter("rentalDate"),
                        Integer.parseInt(request.getParameter("days")), "CONFIRMED");
                bookingDAO.saveBooking(newBooking);
                response.sendRedirect("bookings?action=list");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("bookings?action=list");
        }
    }
}