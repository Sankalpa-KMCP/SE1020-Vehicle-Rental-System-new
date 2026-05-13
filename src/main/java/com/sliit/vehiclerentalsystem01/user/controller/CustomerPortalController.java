package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.booking.model.Booking;
import com.sliit.vehiclerentalsystem01.booking.service.BookingService;
import com.sliit.vehiclerentalsystem01.payment.model.Payment;
import com.sliit.vehiclerentalsystem01.payment.service.PaymentService;
import com.sliit.vehiclerentalsystem01.review.model.CustomerReview;
import com.sliit.vehiclerentalsystem01.review.service.ReviewService;
import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.user.model.Customer;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Logged-in customer UI (legacy JSP portal under {@code customer-*.jsp} / {@code browse-cars.jsp}).
 */
@Controller
@RequestMapping("/customer")
public class CustomerPortalController {

    private final UserService userService;
    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;

    public CustomerPortalController(UserService userService,
                                    BookingService bookingService,
                                    VehicleService vehicleService,
                                    PaymentService paymentService,
                                    ReviewService reviewService) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.vehicleService = vehicleService;
        this.paymentService = paymentService;
        this.reviewService = reviewService;
    }

    private static boolean customer(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.CUSTOMER_LOGGED_IN));
    }

    private static String username(HttpSession session) {
        if (session == null) {
            return "";
        }
        Object u = session.getAttribute(UserSessionKeys.USERNAME);
        return u == null ? "" : u.toString().trim();
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String un = username(session);
        Optional<Customer> oc = userService.findByUsername(un).filter(u -> u instanceof Customer).map(u -> (Customer) u);
        String displayName = oc.map(Customer::getName).filter(n -> !n.isBlank()).orElse("@" + un);
        if (displayName.isBlank()) {
            displayName = "Customer";
        }
        model.addAttribute("displayName", displayName);
        List<String> notices = new ArrayList<>();
        for (Booking booking : bookingService.findAll()) {
            String owner = booking.getCustomerName();
            boolean isOwner = owner != null && un.equalsIgnoreCase(owner.trim());
            boolean hasNotice = booking.getStaffMessage() != null && !booking.getStaffMessage().trim().isEmpty();
            boolean decision = "CONFIRMED".equalsIgnoreCase(booking.getStatus())
                    || "REJECTED".equalsIgnoreCase(booking.getStatus());
            if (isOwner && hasNotice && decision) {
                notices.add("Booking " + booking.getId() + " - " + booking.getStatus() + ": " + booking.getStaffMessage());
            }
        }
        int from = Math.max(0, notices.size() - 3);
        List<String> noticeSlice = new ArrayList<>(notices.subList(from, notices.size()));
        Collections.reverse(noticeSlice);
        model.addAttribute("bookingNotices", noticeSlice);
        return "user/customer-dashboard";
    }

    @GetMapping("/fleet")
    public String fleet(HttpSession session, Model model) {
        if (!customer(session)) {
            return "redirect:/";
        }
        List<Vehicle> all = vehicleService.findAll();
        Set<String> unavailable = bookingService.findUnavailableVehicleIds();
        List<Vehicle> available = all.stream()
                .filter(v -> !unavailable.contains(v.getId()))
                .collect(Collectors.toList());
        model.addAttribute("vehicles", available);
        return "vehicle/vehicle-browse";
    }

    @GetMapping("/bookings")
    public String myBookings(HttpSession session, Model model) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String un = username(session);
        List<Booking> list = bookingService.findByCustomerId(un);
        java.util.Map<String, Boolean> payAllowed = new java.util.HashMap<>();
        for (Booking b : list) {
            payAllowed.put(b.getId(), bookingService.isPaymentAllowed(b.getId()));
        }
        model.addAttribute("bookingList", list);
        model.addAttribute("payAllowed", payAllowed);
        return "booking/my-bookings";
    }

    @GetMapping("/bookings/new")
    public String newBookingForm(HttpSession session, Model model,
                                 @RequestParam(required = false) String vehicleId,
                                 @RequestParam(required = false) String error) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        model.addAttribute("sessionUsername", username(session));
        model.addAttribute("preselectedVehicleId", vehicleId == null ? "" : vehicleId);
        model.addAttribute("vehicleUnavailable", "vehicle_unavailable".equals(error));
        return "booking/booking-form";
    }

    @PostMapping("/bookings")
    public String createBooking(HttpSession session, RedirectAttributes ra,
                                @RequestParam String vehicleId,
                                @RequestParam String rentalDate,
                                @RequestParam int days) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String vid = vehicleId == null ? "" : vehicleId.trim();
        if (vid.isEmpty() || !bookingService.isVehicleAvailable(vid)) {
            return "redirect:/customer/bookings/new?vehicleId=" + vid + "&error=vehicle_unavailable";
        }
        String un = username(session);
        Booking b = new Booking(
                UUID.randomUUID().toString(),
                un,
                rentalDate,
                vid,
                Math.max(1, days),
                "PENDING",
                "");
        bookingService.save(b);
        return "redirect:/customer/bookings";
    }

    @GetMapping("/bookings/{id}/cancel")
    public String cancelForm(HttpSession session, @PathVariable String id, Model model) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String un = username(session);
        Optional<Booking> ob = bookingService.findById(id);
        if (ob.isEmpty()) {
            return "redirect:/customer/home";
        }
        Booking b = ob.get();
        if (b.getCustomerName() == null || !un.equalsIgnoreCase(b.getCustomerName().trim())) {
            return "redirect:/customer/home";
        }
        model.addAttribute("bookingToCancel", b);
        return "booking/cancel-booking";
    }

    @PostMapping("/bookings/cancel-request")
    public String cancelRequest(HttpSession session,
                                @RequestParam String id,
                                @RequestParam String cancelReason) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String un = username(session);
        bookingService.findById(id).ifPresent(b -> {
            if (b.getCustomerName() != null && un.equalsIgnoreCase(b.getCustomerName().trim())) {
                b.setStatus("CANCELLATION_REQUESTED");
                b.setStaffMessage(cancelReason == null ? "" : cancelReason.trim());
                bookingService.save(b);
            }
        });
        return "redirect:/customer/bookings";
    }

    @GetMapping("/billing")
    public String billing(HttpSession session, Model model) {
        if (!customer(session)) {
            return "redirect:/";
        }
        String un = username(session);
        Optional<Customer> oc = userService.findByUsername(un).filter(u -> u instanceof Customer).map(u -> (Customer) u);
        String displayName = oc.map(Customer::getName).filter(n -> !n.isBlank()).orElse("@" + un);
        if (displayName.isBlank()) {
            displayName = "Customer";
        }
        List<Booking> myBookings = bookingService.findByCustomerId(un);
        Set<String> myBookingIds = new HashSet<>();
        for (Booking b : myBookings) {
            myBookingIds.add(b.getId());
        }
        List<Payment> myPayments = new ArrayList<>();
        double paidTotal = 0;
        double pendingTotal = 0;
        double refundedTotal = 0;
        for (Payment p : paymentService.findAll()) {
            if (p.getBookingId() != null && myBookingIds.contains(p.getBookingId())) {
                myPayments.add(p);
                String st = p.getStatus() == null ? "" : p.getStatus();
                if ("PAID".equalsIgnoreCase(st)) {
                    paidTotal += p.getAmount();
                } else if ("PENDING".equalsIgnoreCase(st)) {
                    pendingTotal += p.getAmount();
                } else if ("REFUNDED".equalsIgnoreCase(st)) {
                    refundedTotal += p.getAmount();
                }
            }
        }
        double totalBilled = paidTotal + pendingTotal + refundedTotal;
        model.addAttribute("displayName", displayName);
        model.addAttribute("myBookings", myBookings);
        model.addAttribute("myPayments", myPayments);
        model.addAttribute("paidTotal", paidTotal);
        model.addAttribute("pendingTotal", pendingTotal);
        model.addAttribute("refundedTotal", refundedTotal);
        model.addAttribute("totalBilled", totalBilled);
        return "payment/customer-billing";
    }

    @GetMapping("/pay")
    public String payForm(HttpSession session, @RequestParam String bookingId, Model model) {
        if (!customer(session)) {
            return "redirect:/";
        }
        String un = username(session);
        Optional<Booking> ob = bookingService.findById(bookingId);
        if (ob.isEmpty()) {
            return "redirect:/customer/bookings?payment=missing_invoice";
        }
        Booking booking = ob.get();
        if (booking.getCustomerName() == null || !un.equalsIgnoreCase(booking.getCustomerName().trim())) {
            return "redirect:/customer/bookings?payment=missing_invoice";
        }
        Optional<Payment> pay = paymentService.findLatestByBookingId(bookingId);
        if (pay.isEmpty()) {
            return "redirect:/customer/bookings?payment=missing_invoice";
        }
        model.addAttribute("booking", booking);
        model.addAttribute("payment", pay.get());
        return "payment/customer-payment";
    }

    @PostMapping("/pay")
    public String paySubmit(HttpSession session,
                            @RequestParam String bookingId,
                            @RequestParam(required = false) String method) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String un = username(session);
        Optional<Booking> ob = bookingService.findById(bookingId);
        if (ob.isEmpty()) {
            return "redirect:/customer/bookings";
        }
        if (ob.get().getCustomerName() == null || !un.equalsIgnoreCase(ob.get().getCustomerName().trim())) {
            return "redirect:/customer/bookings";
        }
        paymentService.findLatestByBookingId(bookingId).ifPresent(p -> {
            p.setStatus("PAID");
            paymentService.save(p);
        });
        return "redirect:/customer/billing";
    }

    @GetMapping("/support")
    public String support(HttpSession session, Model model) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        model.addAttribute("supportUsername", username(session));
        return "user/customer-support";
    }

    @PostMapping("/support/ticket")
    public String supportTicket(HttpSession session, RedirectAttributes ra,
                                @RequestParam String subject,
                                @RequestParam String message) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        ra.addFlashAttribute("supportMessage", "Ticket recorded: " + subject);
        return "redirect:/customer/support?status=sent";
    }

    @GetMapping("/reviews/new")
    public String reviewNew(HttpSession session, Model model) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        Object customerUsername = session.getAttribute("customerUsername");
        Object un = session.getAttribute(UserSessionKeys.USERNAME);
        Object name = session.getAttribute("name");
        String reviewerName = "Customer";
        if (customerUsername != null && !customerUsername.toString().isBlank()) {
            reviewerName = customerUsername.toString();
        } else if (un != null && !un.toString().isBlank()) {
            reviewerName = un.toString();
        } else if (name != null && !name.toString().isBlank()) {
            reviewerName = name.toString();
        }
        model.addAttribute("reviewerName", reviewerName);
        return "review/customer-review-form";
    }

    @PostMapping("/reviews")
    public String reviewCreate(HttpSession session, RedirectAttributes ra,
                               @RequestParam int rating,
                               @RequestParam String comments,
                               @RequestParam(required = false) String bookingId) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String un = username(session);
        if (comments == null || comments.isBlank()) {
            return "redirect:/customer/reviews/new?error=missing_fields";
        }
        String bid = bookingId == null ? "" : bookingId.trim();
        if (bid.isEmpty()) {
            if (reviewService.countAccountReviewsByCustomer(un) >= 1) {
                return "redirect:/customer/reviews/new?error=account_limit";
            }
        } else {
            Optional<Booking> b = bookingService.findById(bid);
            if (b.isEmpty() || b.get().getCustomerName() == null
                    || !un.equalsIgnoreCase(b.get().getCustomerName().trim())) {
                return "redirect:/customer/reviews/new?error=invalid_booking";
            }
            long forBooking = reviewService.findAll().stream()
                    .filter(r -> r instanceof CustomerReview cr
                            && un.equalsIgnoreCase(cr.getReviewerName())
                            && bid.equalsIgnoreCase(cr.getBookingId()))
                    .count();
            if (forBooking >= 2) {
                return "redirect:/customer/reviews/new?error=booking_limit";
            }
        }
        CustomerReview cr = new CustomerReview();
        cr.setReviewerName(un);
        cr.setRating(rating);
        cr.setComments(comments.trim());
        cr.setStatus("PUBLISHED");
        cr.setBookingId(bid);
        if (bid.isEmpty()) {
            cr.setVehicleId("");
        } else {
            cr.setVehicleId(bookingService.findById(bid).map(Booking::getVehicleId).orElse(""));
        }
        reviewService.save(cr);
        return "redirect:/customer/home";
    }

    @GetMapping("/bookings/{id}/edit")
    public String editBookingForm(HttpSession session, @PathVariable String id, Model model) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String un = username(session);
        Optional<Booking> ob = bookingService.findById(id);
        if (ob.isEmpty()) {
            return "redirect:/customer/home";
        }
        Booking b = ob.get();
        if (b.getCustomerName() == null || !un.equalsIgnoreCase(b.getCustomerName().trim())) {
            return "redirect:/customer/home";
        }
        model.addAttribute("booking", b);
        return "booking/edit-booking";
    }

    /**
     * Customer-side booking edit. Only PENDING / UPDATE_REQUESTED bookings can be edited;
     * confirmed or cancelled bookings must go through admin reservation actions instead.
     */
    @PostMapping("/bookings/{id}/edit")
    public String editBookingSubmit(HttpSession session, @PathVariable String id,
                                    @RequestParam String rentalDate,
                                    @RequestParam int days,
                                    RedirectAttributes ra) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String un = username(session);
        Optional<Booking> ob = bookingService.findById(id);
        if (ob.isEmpty()) {
            return "redirect:/customer/bookings";
        }
        Booking b = ob.get();
        if (b.getCustomerName() == null || !un.equalsIgnoreCase(b.getCustomerName().trim())) {
            return "redirect:/customer/bookings";
        }
        String status = b.getStatus() == null ? "" : b.getStatus().trim().toUpperCase();
        boolean editable = status.isEmpty()
                || "PENDING".equals(status)
                || "UPDATE_REQUESTED".equals(status);
        if (!editable) {
            ra.addFlashAttribute("editError", "Only pending bookings can be edited directly.");
            return "redirect:/customer/bookings";
        }
        b.setRentalDate(rentalDate == null ? b.getRentalDate() : rentalDate.trim());
        b.setDurationDays(Math.max(1, days));
        bookingService.save(b);
        return "redirect:/customer/bookings";
    }
}
