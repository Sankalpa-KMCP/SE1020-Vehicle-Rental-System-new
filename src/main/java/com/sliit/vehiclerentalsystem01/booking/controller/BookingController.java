package com.sliit.vehiclerentalsystem01.booking.controller;

import com.sliit.vehiclerentalsystem01.booking.model.Booking;
import com.sliit.vehiclerentalsystem01.booking.service.BookingService;
import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.user.model.Customer;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin booking management. Every endpoint requires an {@code ADMIN_LOGGED_IN} session.
 */
@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final VehicleService vehicleService;

    public BookingController(BookingService bookingService,
                             UserService userService,
                             VehicleService vehicleService) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    private static boolean admin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        model.addAttribute("bookings", bookingService.findAll());
        return "booking/booking-list";
    }

    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
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
    public String searchBooking(HttpSession session,
                                @RequestParam(required = false) String id,
                                Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        boolean attempted = id != null && !id.isBlank();
        model.addAttribute("searchAttempted", attempted);
        model.addAttribute("searchedBooking", attempted
                ? bookingService.findById(id.trim()).orElse(null)
                : null);
        return "booking/search-booking";
    }

    @GetMapping("/view-all")
    public String viewAllBookings(HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        model.addAttribute("bookingList", bookingService.findAll());
        return "booking/view-all-bookings";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        return bookingService.findById(id)
                .map(b -> {
                    model.addAttribute("booking", b);
                    return "booking/booking-detail";
                })
                .orElse("redirect:/bookings");
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        return bookingService.findById(id)
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
    public String create(HttpSession session, @ModelAttribute Booking booking) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        bookingService.save(booking);
        return "redirect:/bookings";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable String id, HttpSession session, @ModelAttribute Booking booking) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        booking.setId(id);
        bookingService.save(booking);
        return "redirect:/bookings";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, HttpSession session, RedirectAttributes ra) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        try {
            bookingService.deleteById(id);
        } catch (IllegalStateException ex) {
            ra.addAttribute("error", ex.getMessage());
        }
        return "redirect:/bookings";
    }
}
