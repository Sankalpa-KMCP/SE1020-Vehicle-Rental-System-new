package com.sliit.vehiclerentalsystem01.reservation.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.reservation.entity.Booking;
import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final VehicleService vehicleService;

    public BookingController(ReservationService reservationService,
                             UserService userService,
                             VehicleService vehicleService) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    private static void addBookingStats(Model model, List<Booking> bookingList) {
        int totalBookings = bookingList.size();
        int cancellationRequests = 0;
        int pendingBookings = 0;
        int updateRequests = 0;
        for (Booking b : bookingList) {
            String s = b.getStatus() == null ? "" : b.getStatus().trim();
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

    @GetMapping
    public String list(Model model) {
        List<Booking> all = reservationService.findAll();
        model.addAttribute("bookings", all);
        addBookingStats(model, all);
        return "booking/booking-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("mode", "create");
        model.addAttribute("entityId", null);
        model.addAttribute("booking", new Booking());
        model.addAttribute("customers", userService.findAll().stream()
                .filter(u -> u instanceof Customer)
                .toList());
        model.addAttribute("vehicles", vehicleService.findAll());
        return "booking/booking-admin-form";
    }

    @GetMapping("/search")
    public String searchBooking(@RequestParam(required = false) String id, Model model) {
        boolean attempted = id != null && !id.isBlank();
        model.addAttribute("searchAttempted", attempted);
        model.addAttribute("searchedBooking", attempted
                ? reservationService.findById(id.trim()).orElse(null)
                : null);
        return "booking/search-booking";
    }

    @GetMapping("/view-all")
    public String viewAllBookings(@RequestParam(required = false) String q, Model model) {
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
        model.addAttribute("useAdminReservationBase", false);
        addBookingStats(model, all);
        return "booking/view-all-bookings";
    }

    private static boolean containsIgnoreCase(String value, String needle) {
        return value != null && !value.isBlank() && value.toLowerCase(Locale.ROOT).contains(needle);
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        return reservationService.findById(id)
                .map(b -> {
                    model.addAttribute("booking", b);
                    vehicleService.findById(b.getVehicleId()).ifPresent(v -> {
                        model.addAttribute("vehicleName", v.getBrand() + " " + v.getModel());
                    });
                    return "booking/booking-detail";
                })
                .orElse("redirect:/bookings");
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        return reservationService.findById(id)
                .map(b -> {
                    model.addAttribute("mode", "edit");
                    model.addAttribute("entityId", id);
                    model.addAttribute("booking", b);
                    model.addAttribute("customers", userService.findAll().stream()
                            .filter(u -> u instanceof Customer)
                            .toList());
                    model.addAttribute("vehicles", vehicleService.findAll());
                    return "booking/booking-admin-form";
                })
                .orElse("redirect:/bookings");
    }

    @PostMapping
    public String create(@ModelAttribute Booking booking) {
        try {
            reservationService.save(booking);
        } catch (ValidationException ex) {
            return "redirect:/bookings/new?error=" + ex.getMessage();
        }
        return "redirect:/bookings";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable String id, @ModelAttribute Booking booking) {
        booking.setId(id);
        try {
            reservationService.save(booking);
        } catch (ValidationException ex) {
            return "redirect:/bookings/" + id + "/edit?error=" + ex.getMessage();
        }
        return "redirect:/bookings";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, RedirectAttributes ra) {
        try {
            reservationService.deleteById(id);
        } catch (IllegalStateException ex) {
            ra.addAttribute("error", ex.getMessage());
        }
        return "redirect:/bookings";
    }
}
