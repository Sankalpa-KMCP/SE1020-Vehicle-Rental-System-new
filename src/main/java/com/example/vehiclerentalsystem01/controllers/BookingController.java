package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.BookingDAO;
import com.example.vehiclerentalsystem01.models.Booking;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/bookings")
public class BookingController extends HttpServlet {
    private BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("search".equals(action)) {
            // Handles Search Button
            String id = request.getParameter("id");
            request.setAttribute("searchedBooking", bookingDAO.getBookingById(id));
            request.setAttribute("searchAttempted", true);
            request.getRequestDispatcher("search-booking.jsp").forward(request, response);

        } else if ("edit".equals(action)) {
            // Handles Edit Button
            String id = request.getParameter("id");
            Booking b = bookingDAO.getBookingById(id);
            if (b != null) {
                request.setAttribute("bookingToEdit", b);
            } else {
                request.setAttribute("error", "Not Found");
            }
            request.getRequestDispatcher("edit-booking.jsp").forward(request, response);

        } else if ("view-all".equals(action)) {
            // Handles User View All Button
            request.setAttribute("bookingList", bookingDAO.getAllBookings());
            request.getRequestDispatcher("view-all-bookings.jsp").forward(request, response);
        } else {
            // Handles Admin View All Button
            request.setAttribute("bookingList", bookingDAO.getAllBookings());
            request.getRequestDispatcher("booking-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            // Handles Cancel Button
            bookingDAO.deleteBooking(request.getParameter("id"));
            response.sendRedirect("bookings?action=list");

        } else if ("update".equals(action)) {
            // Saves changes from Edit Page
            String id = request.getParameter("id");
            Booking updatedBooking = new Booking(id, request.getParameter("customerName"),
                    request.getParameter("vehicleId"), request.getParameter("rentalDate"),
                    Integer.parseInt(request.getParameter("days")), "UPDATED");
            bookingDAO.updateBooking(updatedBooking);
            response.sendRedirect("bookings?action=list");

        } else if ("create".equals(action)) {
            // Saves new booking from Main Page
            String newId = "BKG-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            Booking newBooking = new Booking(newId, request.getParameter("customerName"),
                    request.getParameter("vehicleId"), request.getParameter("rentalDate"),
                    Integer.parseInt(request.getParameter("days")), "CONFIRMED");
            bookingDAO.saveBooking(newBooking);
            response.sendRedirect("bookings?action=list");
        }
    }
}