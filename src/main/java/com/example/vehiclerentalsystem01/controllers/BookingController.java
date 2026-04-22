package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.BookingDAO;
import com.example.vehiclerentalsystem01.dao.PaymentDAO;
import com.example.vehiclerentalsystem01.dao.VehicleDAO;
import com.example.vehiclerentalsystem01.models.Booking;
import com.example.vehiclerentalsystem01.models.CashPayment;
import com.example.vehiclerentalsystem01.models.Vehicle;
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
    private PaymentDAO paymentDAO = new PaymentDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();

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

    private double resolveBookingAmount(Booking booking) {
        if (booking == null) {
            return 0.0;
        }

        Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());
        double dailyRate = vehicle == null ? 100.0 : vehicle.calculateRentalRate();
        return dailyRate * Math.max(1, booking.getDurationDays());
    }

    private void ensurePendingInvoice(Booking booking) {
        if (booking == null || paymentDAO.hasAnyPaymentForBooking(booking.getTransactionId())) {
            return;
        }

        String paymentId = "INV-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        paymentDAO.savePayment(new CashPayment(paymentId, booking.getTransactionId(), resolveBookingAmount(booking), "PENDING"));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("search".equals(action)) {
                String id = request.getParameter("id");
                Booking found = null;
                if (id != null && !id.trim().isEmpty()) {
                    found = bookingDAO.getBookingById(id.trim());
                }

                request.setAttribute("searchAttempted", true);
                request.setAttribute("searchedBooking", found);
                request.getRequestDispatcher("search-booking.jsp").forward(request, response);
                return;
            }

            if ("list".equals(action) || action == null) {
                List<Booking> allBookings = bookingDAO.getAllBookings();
                if (allBookings == null) allBookings = new ArrayList<>();

                int totalBookings = allBookings.size();
                int confirmedBookings = 0;
                int cancellationRequests = 0;
                for (Booking b : allBookings) {
                    if ("CONFIRMED".equalsIgnoreCase(b.getStatus())) confirmedBookings++;
                    if ("CANCELLATION_REQUESTED".equalsIgnoreCase(b.getStatus())) cancellationRequests++;
                }

                // Provide multiple names just in case different JSPs expect different variables
                request.setAttribute("bookings", allBookings);
                request.setAttribute("bookingList", allBookings);
                request.setAttribute("allBookings", allBookings);

                request.setAttribute("totalBookings", totalBookings);
                request.setAttribute("confirmedBookings", confirmedBookings);
                request.setAttribute("cancellationRequests", cancellationRequests);
                request.getRequestDispatcher("booking-dashboard.jsp").forward(request, response);
                return;
            }

            response.sendRedirect("bookings?action=list");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("bookings?action=list");
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
                Booking existingBooking = bookingDAO.getBookingById(id);
                if (existingBooking != null) {
                    Booking updatedBooking = new Booking(
                            id,
                            existingBooking.getCustomerName(),
                            request.getParameter("rentalDate"),
                            existingBooking.getVehicleId(),
                            Integer.parseInt(request.getParameter("days").trim()),
                            existingBooking.getStatus(),
                            existingBooking.getStaffMessage()
                    );
                    bookingDAO.updateBooking(updatedBooking);
                }
                response.sendRedirect("my-bookings.jsp");

                // --- THE NEW APPROVE / REJECT LOGIC ---
            } else if ("approve".equals(action) || "accept".equals(action) || "reject".equals(action) || "decline".equals(action)) {
                String id = request.getParameter("id");
                String staffMessage = normalized(request.getParameter("staffMessage"));

                // Read existing booking from storage to avoid relying on form-hidden values.
                Booking existingBooking = bookingDAO.getBookingById(id);
                if (existingBooking != null) {
                    String newStatus = ("approve".equals(action) || "accept".equals(action)) ? "CONFIRMED" : "REJECTED";
                    Booking updatedBooking = new Booking(
                            existingBooking.getTransactionId(),
                            existingBooking.getCustomerName(),
                            existingBooking.getRentalDate(),
                            existingBooking.getVehicleId(),
                            existingBooking.getDurationDays(),
                            newStatus,
                            staffMessage
                    );
                    bookingDAO.updateBooking(updatedBooking);
                    if ("CONFIRMED".equals(newStatus)) {
                        ensurePendingInvoice(updatedBooking);
                    }
                }
                response.sendRedirect("bookings?action=list");

            } else if ("request_cancel".equals(action)) {
                String id = request.getParameter("id");
                String reason = normalized(request.getParameter("cancelReason"));
                Booking existingBooking = bookingDAO.getBookingById(id);
                HttpSession session = request.getSession(false);

                if (existingBooking != null && isCustomerOwner(session, existingBooking)) {
                    String status = normalized(existingBooking.getStatus()).toUpperCase();
                    if (!"CANCELLED".equals(status) && !"REJECTED".equals(status)) {
                        String note = reason.isEmpty() ? "Customer requested cancellation." : "Cancellation requested: " + reason;
                        Booking updatedBooking = new Booking(
                                existingBooking.getTransactionId(),
                                existingBooking.getCustomerName(),
                                existingBooking.getRentalDate(),
                                existingBooking.getVehicleId(),
                                existingBooking.getDurationDays(),
                                "CANCELLATION_REQUESTED",
                                note
                        );
                        bookingDAO.updateBooking(updatedBooking);
                    }
                }
                response.sendRedirect("my-bookings.jsp");

            } else if ("approve_cancel".equals(action) || "reject_cancel".equals(action)) {
                String id = request.getParameter("id");
                String staffMessage = normalized(request.getParameter("staffMessage"));
                Booking existingBooking = bookingDAO.getBookingById(id);

                if (existingBooking != null && "CANCELLATION_REQUESTED".equalsIgnoreCase(existingBooking.getStatus())) {
                    String newStatus = "approve_cancel".equals(action) ? "CANCELLED" : "CONFIRMED";
                    String finalMessage = staffMessage.isEmpty()
                            ? ("approve_cancel".equals(action) ? "Cancellation approved." : "Cancellation request declined.")
                            : staffMessage;

                    Booking updatedBooking = new Booking(
                            existingBooking.getTransactionId(),
                            existingBooking.getCustomerName(),
                            existingBooking.getRentalDate(),
                            existingBooking.getVehicleId(),
                            existingBooking.getDurationDays(),
                            newStatus,
                            finalMessage
                    );
                    bookingDAO.updateBooking(updatedBooking);
                    if ("CONFIRMED".equals(newStatus)) {
                        ensurePendingInvoice(updatedBooking);
                    }
                }
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

                if (!bookingDAO.isVehicleAvailable(vehId)) {
                    response.sendRedirect("add-booking.jsp?vehicleId=" + normalized(vehId) + "&error=vehicle_unavailable");
                    return;
                }

                // SAVES AS PENDING INSTEAD OF CONFIRMED
                Booking newBooking = new Booking(newId, custName, rentDate, vehId, days, "PENDING", "");
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