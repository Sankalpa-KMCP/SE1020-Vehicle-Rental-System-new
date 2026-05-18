package com.sliit.vehiclerentalsystem01.booking.controller;

import com.sliit.vehiclerentalsystem01.booking.model.Booking;
import com.sliit.vehiclerentalsystem01.booking.service.BookingService;
import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.vehicle.model.Vehicle;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Admin reservation panel (legacy {@code booking-dashboard.jsp}).
 */
@Controller
@RequestMapping("/admin/reservations")
public class ReservationAdminController {

    private final BookingService bookingService;
    private final VehicleService vehicleService;

    public ReservationAdminController(BookingService bookingService, VehicleService vehicleService) {
        this.bookingService = bookingService;
        this.vehicleService = vehicleService;
    }

    private static boolean admin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
    }

    @GetMapping
    public String panel(HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
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

        // Fetch vehicle names for display
        Map<String, String> vehicleNames = vehicleService.findAll().stream()
                .collect(Collectors.toMap(Vehicle::getId, v -> v.getBrand() + " " + v.getModel()));

        model.addAttribute("bookingList", bookingList);
        model.addAttribute("vehicleNames", vehicleNames);
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

    @GetMapping("/{id}/view")
    public String viewBooking(@PathVariable String id, HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        return bookingService.findById(id)
                .map(b -> {
                    model.addAttribute("booking", b);
                    // Fetch vehicle name for display
                    vehicleService.findById(b.getVehicleId()).ifPresent(v -> {
                        model.addAttribute("vehicleName", v.getBrand() + " " + v.getModel());
                    });
                    return "booking/booking-detail";
                })
                .orElse("redirect:/admin/reservations");
    }

    @PostMapping("/{id}/action")
    public String handleBookingAction(
            @PathVariable String id,
            @RequestParam String action,
            HttpSession session,
            RedirectAttributes ra) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        Booking b = bookingService.findById(id).orElse(null);
        if (b == null) {
            return "redirect:/admin/reservations";
        }
        switch (action) {
            case "confirm" -> bookingService.updateStatus(id, "CONFIRMED");
            case "reject" -> {
                b.setStatus("REJECTED");
                b.setStaffMessage("Rejected by admin");
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
            default -> {
            }
        }
        return "redirect:/admin/reservations/{id}/view".replace("{id}", id);
    }

    @GetMapping("/view-all")
    public String viewAllBookings(HttpSession session, Model model,
                                  @RequestParam(required = false) String q) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        List<Booking> all = bookingService.findAll();
        if (q != null && !q.isBlank()) {
            String needle = q.trim().toLowerCase(Locale.ROOT);
            all = all.stream()
                    .filter(b -> containsIgnoreCase(b.getId(), needle)
                            || containsIgnoreCase(b.getCustomerName(), needle)
                            || containsIgnoreCase(b.getVehicleId(), needle)
                            || containsIgnoreCase(b.getStatus(), needle))
                    .toList();
        }
        // Fetch vehicle names for display
        Map<String, String> vehicleNames = vehicleService.findAll().stream()
                .collect(Collectors.toMap(Vehicle::getId, v -> v.getBrand() + " " + v.getModel()));
        model.addAttribute("bookingList", all);
        model.addAttribute("vehicleNames", vehicleNames);
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
            case "delete" -> {
                try {
                    bookingService.deleteById(id);
                } catch (IllegalStateException ex) {
                    ra.addAttribute("error", ex.getMessage());
                }
            }
            default -> {
            }
        }
        return "redirect:/admin/reservations";
    }

    private static String norm(String s) {
        return s == null ? "" : s.trim();
    }
}
