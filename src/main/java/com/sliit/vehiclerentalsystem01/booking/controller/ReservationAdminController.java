package com.sliit.vehiclerentalsystem01.booking.controller;

import com.sliit.vehiclerentalsystem01.booking.model.Booking;
import com.sliit.vehiclerentalsystem01.booking.service.BookingService;
import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Admin reservation panel (legacy {@code booking-dashboard.jsp}).
 */
@Controller
@RequestMapping("/admin/reservations")
public class ReservationAdminController {

    private final BookingService bookingService;

    public ReservationAdminController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private static boolean admin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
    }

    @GetMapping
    public String panel(HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/login";
        }
        List<Booking> bookingList = bookingService.findAll();
        int totalBookings = bookingList.size();
        int cancellationRequests = 0;
        int pendingBookings = 0;
        int updateRequests = 0;
        for (Booking b : bookingList) {
            String s = norm(b.getStatus());
            if ("CANCELLATION_REQUESTED".equalsIgnoreCase(s)) {
                cancellationRequests++;
            } else if ("PENDING".equalsIgnoreCase(s)) {
                pendingBookings++;
            } else if ("UPDATE_REQUESTED".equalsIgnoreCase(s)) {
                updateRequests++;
            }
        }
        model.addAttribute("bookingList", bookingList);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("cancellationRequests", cancellationRequests);
        model.addAttribute("pendingBookings", pendingBookings);
        model.addAttribute("updateRequests", updateRequests);
        return "booking/booking-dashboard";
    }

    @PostMapping
    public String post(
            HttpSession session,
            @RequestParam String action,
            @RequestParam String id) {
        if (!admin(session)) {
            return "redirect:/auth/login";
        }
        Booking b = bookingService.findById(id).orElse(null);
        if (b == null) {
            return "redirect:/admin/reservations";
        }
        switch (action) {
            case "accept" -> bookingService.updateStatus(id, "CONFIRMED");
            case "decline" -> {
                b.setStatus("REJECTED");
                b.setStaffMessage("Declined by staff");
                bookingService.save(b);
            }
            case "approve_update" -> {
                b.setStatus("CONFIRMED");
                b.setStaffMessage("");
                bookingService.save(b);
            }
            case "reject_update" -> {
                b.setStatus("PENDING");
                b.setStaffMessage("");
                bookingService.save(b);
            }
            case "approve_cancel" -> bookingService.updateStatus(id, "CANCELLED");
            case "reject_cancel" -> {
                b.setStatus("CONFIRMED");
                b.setStaffMessage("");
                bookingService.save(b);
            }
            case "delete" -> bookingService.deleteById(id);
            default -> {
            }
        }
        return "redirect:/admin/reservations";
    }

    private static String norm(String s) {
        return s == null ? "" : s.trim();
    }
}
