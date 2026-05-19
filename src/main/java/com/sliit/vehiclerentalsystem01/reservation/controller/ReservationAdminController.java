package com.sliit.vehiclerentalsystem01.reservation.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.reservation.entity.Booking;
import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

/**
 * Admin reservation panel (legacy {@code booking-dashboard.jsp}).
 */
@Controller
@RequestMapping("/admin/reservations")
public class ReservationAdminController {

    private final ReservationService reservationService;

    public ReservationAdminController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    private static boolean admin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
    }

    @GetMapping
    public String panel(HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        List<Booking> bookingList = reservationService.findAll();
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

    @GetMapping("/search")
    public String searchBooking(HttpSession session) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        return "booking/search-booking";
    }

    @GetMapping("/view-all")
    public String viewAllBookings(HttpSession session, Model model,
                                  @RequestParam(required = false) String q) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        List<Booking> all = reservationService.findAll();
        if (q != null && !q.isBlank()) {
            String needle = q.trim().toLowerCase(Locale.ROOT);
            all = all.stream()
                    .filter(b -> containsIgnoreCase(b.getId(), needle)
                            || containsIgnoreCase(b.getCustomerName(), needle)
                            || containsIgnoreCase(b.getVehicleId(), needle)
                            || containsIgnoreCase(b.getStatus(), needle))
                    .toList();
        }
        model.addAttribute("bookingList", all);
        model.addAttribute("searchQuery", q == null ? "" : q.trim());
        model.addAttribute("useAdminReservationBase", true);
        addViewAllStats(model, all);
        return "booking/view-all-bookings";
    }

    private static void addViewAllStats(Model model, List<Booking> bookingList) {
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
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("cancellationRequests", cancellationRequests);
        model.addAttribute("pendingBookings", pendingBookings);
        model.addAttribute("updateRequests", updateRequests);
    }

    private static boolean containsIgnoreCase(String value, String needle) {
        return value != null && !value.isBlank() && value.toLowerCase(Locale.ROOT).contains(needle);
    }

    @PostMapping
    public String post(
            HttpSession session,
            @RequestParam String action,
            @RequestParam String id,
            RedirectAttributes ra) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        Booking b = reservationService.findById(id).orElse(null);
        if (b == null) {
            return "redirect:/admin/reservations";
        }
        try {
            switch (action) {
                case "accept" -> reservationService.updateStatus(id, "CONFIRMED");
                case "decline" -> {
                    b.setStatus("REJECTED");
                    b.setStaffMessage("Declined by staff");
                    reservationService.save(b);
                }
                case "approve_update" -> {
                    b.setStatus("CONFIRMED");
                    b.setStaffMessage("");
                    reservationService.save(b);
                }
                case "reject_update" -> {
                    b.setStatus("PENDING");
                    b.setStaffMessage("");
                    reservationService.save(b);
                }
                case "approve_cancel" -> reservationService.updateStatus(id, "CANCELLED");
                case "reject_cancel" -> {
                    b.setStatus("CONFIRMED");
                    b.setStaffMessage("");
                    reservationService.save(b);
                }
                case "delete" -> {
                    try {
                        reservationService.deleteById(id);
                    } catch (IllegalStateException ex) {
                        ra.addAttribute("error", ex.getMessage());
                    }
                }
                default -> {
                }
            }
        } catch (ValidationException ex) {
            return "redirect:/admin/reservations?error=" + ex.getMessage();
        }
        return "redirect:/admin/reservations";
    }

    private static String norm(String s) {
        return s == null ? "" : s.trim();
    }
}
