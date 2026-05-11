package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.BookingDAO;
import com.example.vehiclerentalsystem01.dao.PaymentDAO;
import com.example.vehiclerentalsystem01.dao.VehicleDAO;
import com.example.vehiclerentalsystem01.models.Booking;
import com.example.vehiclerentalsystem01.models.CashPayment;
import com.example.vehiclerentalsystem01.models.Vehicle;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    private final BookingDAO bookingDAO = new BookingDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

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

    @GetMapping
    public String handleGet(@RequestParam(required = false) String action,
                            @RequestParam(required = false) String id,
                            Model model) {
        try {
            if ("cancel_page".equals(action)) {
                if (id != null && !id.trim().isEmpty()) {
                    Booking b = bookingDAO.getBookingById(id.trim());
                    if (b != null) {
                        model.addAttribute("bookingToCancel", b);
                        return "cancel-booking"; // Renders cancel-booking.jsp
                    }
                }
                return "redirect:/customer-home.jsp";
            }
            if ("search".equals(action)) {
                Booking found = null;
                if (id != null && !id.trim().isEmpty()) {
                    found = bookingDAO.getBookingById(id.trim());
                }

                model.addAttribute("searchAttempted", true);
                model.addAttribute("searchedBooking", found);
                return "search-booking";
            }

            if ("list".equals(action) || action == null) {
                List<Booking> allBookings = bookingDAO.getAllBookings();
                if (allBookings == null) {
                    allBookings = new ArrayList<>();
                }

                int confirmedBookings = 0;
                int cancellationRequests = 0;
                for (Booking booking : allBookings) {
                    if ("CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
                        confirmedBookings++;
                    }
                    if ("CANCELLATION_REQUESTED".equalsIgnoreCase(booking.getStatus())) {
                        cancellationRequests++;
                    }
                }

                model.addAttribute("bookings", allBookings);
                model.addAttribute("bookingList", allBookings);
                model.addAttribute("allBookings", allBookings);
                model.addAttribute("totalBookings", allBookings.size());
                model.addAttribute("confirmedBookings", confirmedBookings);
                model.addAttribute("cancellationRequests", cancellationRequests);
                return "booking-dashboard";
            }

            return "redirect:/bookings?action=list";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/bookings?action=list";
        }
    }

    @PostMapping
    public String handlePost(@RequestParam Map<String, String> params, HttpSession session) {
        String action = params.get("action");

        try {
            if ("delete".equals(action)) {
                bookingDAO.deleteBooking(params.get("id"));
                return "redirect:/bookings?action=list";
            }

            // Inside BookingController.java @PostMapping
            if ("update".equals(action)) {
                String id = params.get("id");
                String newDate = params.get("rentalDate");
                String newDays = params.get("days");

                Booking existing = bookingDAO.getBookingById(id);

                if (existing != null) {
                    // We set the status to UPDATE_REQUESTED
                    // and store the new values in staffMessage field: "YYYY-MM-DD|Days"
                    String requestedChanges = newDate + "|" + newDays;

                    Booking updateRequest = new Booking(
                            existing.getTransactionId(),
                            existing.getCustomerName(),
                            existing.getRentalDate(), // Keep old date for now
                            existing.getVehicleId(),
                            existing.getDurationDays(), // Keep old days for now
                            "UPDATE_REQUESTED",
                            requestedChanges // Store pending changes here
                    );

                    bookingDAO.updateBooking(updateRequest);
                }
                return "redirect:/my-bookings.jsp";

            }

            if ("approve".equals(action) || "accept".equals(action) || "reject".equals(action) || "decline".equals(action)) {
                String id = params.get("id");
                String staffMessage = normalized(params.get("staffMessage"));
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
                return "redirect:/bookings?action=list";
            }

            if ("request_cancel".equals(action)) {
                String id = params.get("id");
                Booking existingBooking = bookingDAO.getBookingById(id);
                if (existingBooking != null && isCustomerOwner(session, existingBooking)) {
                    String status = normalized(existingBooking.getStatus()).toUpperCase();
                    if (!"CANCELLED".equals(status) && !"REJECTED".equals(status)) {
                        String reason = normalized(params.get("cancelReason"));
                        String note = reason.isEmpty() ? "Customer requested cancellation." : "Cancellation requested: " + reason;
                        bookingDAO.updateBooking(new Booking(
                                existingBooking.getTransactionId(),
                                existingBooking.getCustomerName(),
                                existingBooking.getRentalDate(),
                                existingBooking.getVehicleId(),
                                existingBooking.getDurationDays(),
                                "CANCELLATION_REQUESTED",
                                note
                        ));
                    }
                }
                return "redirect:/my-bookings.jsp";
            }

            if ("approve_cancel".equals(action) || "reject_cancel".equals(action)) {
                String id = params.get("id");
                Booking existingBooking = bookingDAO.getBookingById(id);
                if (existingBooking != null && "CANCELLATION_REQUESTED".equalsIgnoreCase(existingBooking.getStatus())) {
                    String newStatus = "approve_cancel".equals(action) ? "CANCELLED" : "CONFIRMED";
                    String staffMessage = normalized(params.get("staffMessage"));
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
                return "redirect:/bookings?action=list";
            }

        if ("approve_update".equals(action)) {
            String id = params.get("id");
            Booking b = bookingDAO.getBookingById(id);

            if (b != null && "UPDATE_REQUESTED".equals(b.getStatus())) {
                String[] parts = b.getStaffMessage().split("\\|");
                if (parts.length == 2) {
                    // Create final confirmed booking with the NEW data
                    Booking finalBooking = new Booking(
                            b.getTransactionId(),
                            b.getCustomerName(),
                            parts[0], // The new date from staffMessage
                            b.getVehicleId(),
                            Integer.parseInt(parts[1]), // The new days from staffMessage
                            "CONFIRMED",
                            "Update approved by staff"
                    );
                    bookingDAO.updateBooking(finalBooking);
                }
            }
            return "redirect:/bookings?action=list";
        }

            if ("create".equals(action)) {
                String vehicleId = normalized(params.get("vehicleId"));
                if (!bookingDAO.isVehicleAvailable(vehicleId)) {
                    return "redirect:/add-booking.jsp?vehicleId=" + vehicleId + "&error=vehicle_unavailable";
                }

                String newId = "BKG-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                String customerName = params.get("customerName");
                if (customerName != null) {
                    customerName = customerName.replace(",", "");
                }

                String daysParam = params.get("days");
                int days = daysParam != null && !daysParam.trim().isEmpty() ? Integer.parseInt(daysParam.trim()) : 1;
                bookingDAO.saveBooking(new Booking(newId, customerName, params.get("rentalDate"), vehicleId, days, "PENDING", ""));

                return Boolean.TRUE.equals(session.getAttribute("customerLoggedIn"))
                        ? "redirect:/my-bookings.jsp"
                        : "redirect:/bookings?action=list";
            }
        } catch (Exception e) {
            System.out.println("CRITICAL ERROR IN CONTROLLER: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/my-bookings.jsp";
    }
}