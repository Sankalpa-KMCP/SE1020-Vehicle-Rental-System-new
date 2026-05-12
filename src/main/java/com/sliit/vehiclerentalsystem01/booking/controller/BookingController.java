package com.sliit.vehiclerentalsystem01.booking.controller;

import com.sliit.vehiclerentalsystem01.booking.model.Booking;
import com.sliit.vehiclerentalsystem01.booking.service.BookingService;
import com.sliit.vehiclerentalsystem01.user.model.Customer;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping
    public String list(Model model) {
        model.addAttribute("bookings", bookingService.findAll());
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

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        return bookingService.findById(id)
                .map(b -> {
                    model.addAttribute("booking", b);
                    return "booking/booking-detail";
                })
                .orElse("redirect:/bookings");
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
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
    public String create(@ModelAttribute Booking booking) {
        bookingService.save(booking);
        return "redirect:/bookings";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable String id, @ModelAttribute Booking booking) {
        booking.setId(id);
        bookingService.save(booking);
        return "redirect:/bookings";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        bookingService.deleteById(id);
        return "redirect:/bookings";
    }
}
