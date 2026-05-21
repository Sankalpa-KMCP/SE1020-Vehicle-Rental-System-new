package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.reservation.entity.Booking;
import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.payment.entity.CardPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.Payment;
import com.sliit.vehiclerentalsystem01.payment.service.PaymentService;
import com.sliit.vehiclerentalsystem01.review.entity.CustomerReview;
import com.sliit.vehiclerentalsystem01.review.service.ReviewService;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Vehicle;
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
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Logged-in customer UI (legacy JSP portal under {@code customer-*.jsp} / {@code browse-cars.jsp}).
 */
@Controller
@RequestMapping("/customer")
public class CustomerPortalController {

    private final UserService userService;
    private final ReservationService reservationService;
    private final VehicleService vehicleService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;

    public CustomerPortalController(UserService userService,
                                    ReservationService reservationService,
                                    VehicleService vehicleService,
                                    PaymentService paymentService,
                                    ReviewService reviewService) {
        this.userService = userService;
        this.reservationService = reservationService;
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
        if (oc.isEmpty()) {
            return "redirect:/auth/login?error=profile_not_found";
        }
        Customer customer = oc.get();
        String displayName = customer.getName() != null && !customer.getName().isBlank()
                ? customer.getName().trim()
                : "@" + un;
        if (displayName.isBlank()) {
            displayName = "Customer";
        }
        String firstName = displayName;
        String fullName = customer.getName();
        if (fullName != null && !fullName.isBlank()) {
            String trimmed = fullName.trim();
            int space = trimmed.indexOf(' ');
            firstName = space > 0 ? trimmed.substring(0, space) : trimmed;
        }
        model.addAttribute("customer", customer);
        model.addAttribute("displayName", displayName);
        model.addAttribute("firstName", firstName);
        model.addAttribute("verified", customer.isVerified());
        model.addAttribute("bookingCount", reservationService.findBookingsLinkedToCustomer(customer).size());
        List<String> notices = new ArrayList<>();
        for (Booking booking : reservationService.findAll()) {
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
        model.addAttribute("vehicles", all);
        return "vehicle/vehicle-browse";
    }

    @GetMapping("/bookings")
    public String myBookings(HttpSession session, Model model) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String un = username(session);
        List<Booking> list = reservationService.findByCustomerId(un);
        java.util.Map<String, Boolean> payAllowed = new java.util.HashMap<>();
        for (Booking b : list) {
            payAllowed.put(b.getId(), reservationService.isPaymentAllowed(b.getId()));
        }
        // Fetch vehicle names for display
        java.util.Map<String, String> vehicleNames = vehicleService.findAll().stream()
                .collect(Collectors.toMap(Vehicle::getId, v -> v.getBrand() + " " + v.getModel()));
        model.addAttribute("bookingList", list);
        model.addAttribute("payAllowed", payAllowed);
        model.addAttribute("vehicleNames", vehicleNames);
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
        String un = username(session);
        Booking b = new Booking(
                UUID.randomUUID().toString(),
                un,
                rentalDate,
                vid,
                Math.max(1, days),
                "PENDING",
                "");
        try {
            reservationService.save(b);
        } catch (ValidationException ex) {
            return "redirect:/customer/bookings/new?vehicleId=" + vid + "&error=" + ex.getMessage();
        }
        return "redirect:/customer/bookings";
    }

    @GetMapping("/bookings/{id}/cancel")
    public String cancelForm(HttpSession session, @PathVariable String id, Model model) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String un = username(session);
        Optional<Booking> ob = reservationService.findById(id);
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
        try {
            reservationService.findById(id).ifPresent(b -> {
                if (b.getCustomerName() != null && un.equalsIgnoreCase(b.getCustomerName().trim())) {
                    b.setStatus("CANCELLATION_REQUESTED");
                    b.setStaffMessage(cancelReason == null ? "" : cancelReason.trim());
                    reservationService.save(b);
                }
            });
        } catch (ValidationException ex) {
            return "redirect:/customer/bookings?error=" + ex.getMessage();
        }
        return "redirect:/customer/bookings";
    }

    @GetMapping("/billing")
    public String billing(HttpSession session, Model model, @RequestParam(required = false) String payment) {
        if (!customer(session)) {
            return "redirect:/";
        }
        String un = username(session);
        Optional<Customer> oc = userService.findByUsername(un).filter(u -> u instanceof Customer).map(u -> (Customer) u);
        String displayName = oc.map(Customer::getName).filter(n -> !n.isBlank()).orElse("@" + un);
        if (displayName.isBlank()) {
            displayName = "Customer";
        }
        List<Booking> myBookings = reservationService.findByCustomerId(un);
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
        model.addAttribute("paymentStatus", payment);
        return "payment/customer-billing";
    }

    @GetMapping("/billing/gateway")
    public String billingGateway(HttpSession session,
                                 @RequestParam String bookingId,
                                 @RequestParam(required = false) String error,
                                 Model model) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String bid = bookingId == null ? "" : bookingId.trim();
        String un = username(session);
        Optional<Booking> ob = reservationService.findById(bid);
        if (ob.isEmpty()) {
            return "redirect:/customer/billing?payment=booking_not_found";
        }
        Booking booking = ob.get();
        if (booking.getCustomerName() == null || !un.equalsIgnoreCase(booking.getCustomerName().trim())) {
            return "redirect:/customer/billing?payment=booking_not_found";
        }
        if (!reservationService.isPaymentAllowed(bid)) {
            return "redirect:/customer/billing?payment=booking_not_confirmed";
        }

        Payment payment = paymentService.findLatestByBookingId(bid)
                .orElseGet(() -> createPendingGatewayPayment(booking));

        model.addAttribute("booking", booking);
        model.addAttribute("payment", payment);
        model.addAttribute("vehicle", vehicleService.findById(booking.getVehicleId()).orElse(null));
        model.addAttribute("gatewayError", error);
        return "payment/fake-payment-gateway";
    }

    @PostMapping("/billing/gateway/complete")
    public String completeBillingGateway(HttpSession session,
                                         @RequestParam String bookingId,
                                         @RequestParam(required = false) Double paymentAmount,
                                         @RequestParam(required = false) String cardNumber,
                                         @RequestParam(required = false) String cardName,
                                         @RequestParam(required = false) String expiry,
                                         @RequestParam(required = false) String cvv) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String bid = bookingId == null ? "" : bookingId.trim();
        String un = username(session);
        Optional<Booking> ob = reservationService.findById(bid);
        if (ob.isEmpty() || ob.get().getCustomerName() == null
                || !un.equalsIgnoreCase(ob.get().getCustomerName().trim())) {
            return "redirect:/customer/billing?payment=booking_not_found";
        }
        Payment payment = paymentService.findLatestByBookingId(bid)
                .orElseGet(() -> createPendingGatewayPayment(ob.get()));
        if (isBlank(cardName) || isBlank(cardNumber) || isBlank(expiry) || isBlank(cvv)) {
            return "redirect:/customer/billing/gateway?bookingId=" + bid + "&error=missing_card_details";
        }
        if (paymentAmount == null || Math.abs(paymentAmount - payment.getAmount()) > 0.01) {
            return "redirect:/customer/billing/gateway?bookingId=" + bid + "&error=amount_mismatch";
        }
        payment.setStatus("PAID");
        try {
            paymentService.save(payment);
        } catch (ValidationException ex) {
            return "redirect:/customer/billing/gateway?bookingId=" + bid + "&error=" + ex.getMessage();
        }
        return "redirect:/customer/billing?payment=success";
    }

    private Payment createPendingGatewayPayment(Booking booking) {
        double amount = vehicleService.findById(booking.getVehicleId())
                .map(vehicle -> vehicle.calculateRentalRate() * Math.max(1, booking.getDurationDays()))
                .orElse(0.0);
        Payment payment = new CardPayment("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                booking.getId(),
                amount,
                "PENDING");
        paymentService.save(payment);
        return payment;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @GetMapping("/pay")
    public String payForm(HttpSession session, @RequestParam String bookingId, Model model) {
        if (!customer(session)) {
            return "redirect:/";
        }
        String un = username(session);
        Optional<Booking> ob = reservationService.findById(bookingId);
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
        Optional<Booking> ob = reservationService.findById(bookingId);
        if (ob.isEmpty()) {
            return "redirect:/customer/bookings";
        }
        if (ob.get().getCustomerName() == null || !un.equalsIgnoreCase(ob.get().getCustomerName().trim())) {
            return "redirect:/customer/bookings";
        }
        Optional<Payment> latest = paymentService.findLatestByBookingId(bookingId);
        if (latest.isPresent()) {
            Payment p = latest.get();
            p.setStatus("PAID");
            try {
                paymentService.save(p);
            } catch (ValidationException ex) {
                return "redirect:/customer/pay?bookingId=" + bookingId + "&error=" + ex.getMessage();
            }
        }
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

    private Optional<CustomerReview> ownReview(HttpSession session, String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        String un = username(session);
        return reviewService.findById(id.trim())
                .filter(r -> r instanceof CustomerReview)
                .map(r -> (CustomerReview) r)
                .filter(cr -> reviewOwnedBy(cr, un));
    }

    private static String sessionCustomerKey(HttpSession session) {
        return username(session);
    }

    private static boolean reviewOwnedBy(CustomerReview cr, String customerKey) {
        if (customerKey == null || customerKey.isBlank()) {
            return false;
        }
        return customerKey.equalsIgnoreCase(Objects.toString(cr.getReviewerName(), "").trim());
    }

    @GetMapping("/reviews")
    public String reviewList(HttpSession session, Model model) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String customerKey = sessionCustomerKey(session);
        List<CustomerReview> myReviews;
        try {
            myReviews = reviewService.findAll().stream()
                    .filter(r -> r instanceof CustomerReview)
                    .map(r -> (CustomerReview) r)
                    .filter(cr -> reviewOwnedBy(cr, customerKey))
                    .toList();
        } catch (RuntimeException ex) {
            myReviews = List.of();
            model.addAttribute("reviewError", "Could not load your reviews. Please try again later.");
        }
        double avgRating = 0.0;
        if (!myReviews.isEmpty()) {
            avgRating = myReviews.stream()
                    .mapToInt(CustomerReview::getRating)
                    .average()
                    .orElse(0.0);
        }
        model.addAttribute("myReviews", myReviews);
        model.addAttribute("avgRating", avgRating);
        return "review/customer-reviews-list";
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
        String customerKey = username(session);
        List<Booking> customerBookings = reservationService.findByCustomerId(customerKey);
        model.addAttribute("reviewerName", reviewerName);
        model.addAttribute("customerBookings", customerBookings);
        return "review/customer-review-form";
    }

    @PostMapping("/reviews")
    public String reviewCreate(HttpSession session, RedirectAttributes ra,
                               @RequestParam int rating,
                               @RequestParam String comments,
                               @RequestParam(required = false) String bookingId,
                               @RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {
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
            Optional<Booking> b = reservationService.findById(bid);
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
            cr.setVehicleId(reservationService.findById(bid).map(Booking::getVehicleId).orElse(""));
        }
        cr.setImageUrl(saveReviewImage(image));
        try {
            reviewService.save(cr);
        } catch (ValidationException ex) {
            return "redirect:/customer/reviews/new?error=" + ex.getMessage();
        }
        return "redirect:/customer/reviews?created";
    }

    @GetMapping("/reviews/{id}/edit")
    public String reviewEdit(HttpSession session, @PathVariable String id, Model model) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        return ownReview(session, id)
                .map(cr -> {
                    model.addAttribute("customerReview", cr);
                    return "review/edit-review";
                })
                .orElse("redirect:/customer/reviews");
    }

    @PostMapping("/reviews/{id}")
    public String reviewUpdate(HttpSession session, @PathVariable String id,
                               @RequestParam int rating,
                               @RequestParam String comments,
                               @RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        if (comments == null || comments.isBlank()) {
            return "redirect:/customer/reviews/" + id + "/edit?error=missing_fields";
        }
        return ownReview(session, id)
                .map(cr -> {
                    cr.setRating(rating);
                    cr.setComments(comments.trim());
                    String imgUrl = saveReviewImage(image);
                    if (imgUrl != null && !imgUrl.isEmpty()) {
                        String oldImgUrl = cr.getImageUrl();
                        if (oldImgUrl != null && !oldImgUrl.isBlank()) {
                            try {
                                String pathStr = oldImgUrl.startsWith("/") ? oldImgUrl.substring(1) : oldImgUrl;
                                java.nio.file.Path path = java.nio.file.Paths.get(pathStr);
                                java.nio.file.Files.deleteIfExists(path);
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }
                        }
                        cr.setImageUrl(imgUrl);
                    }
                    try {
                        reviewService.save(cr);
                    } catch (ValidationException ex) {
                        return "redirect:/customer/reviews/" + id + "/edit?error=" + ex.getMessage();
                    }
                    return "redirect:/customer/reviews?updated";
                })
                .orElse("redirect:/customer/reviews");
    }

    @PostMapping("/reviews/{id}/delete")
    public String reviewDelete(HttpSession session, @PathVariable String id) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        if (ownReview(session, id).isPresent()) {
            reviewService.deleteById(id.trim());
        }
        return "redirect:/customer/reviews?deleted";
    }

    @GetMapping("/bookings/{id}/edit")
    public String editBookingForm(HttpSession session, @PathVariable String id, Model model) {
        if (!customer(session)) {
            return "redirect:/auth/login";
        }
        String un = username(session);
        Optional<Booking> ob = reservationService.findById(id);
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
        Optional<Booking> ob = reservationService.findById(id);
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
        try {
            reservationService.save(b);
        } catch (ValidationException ex) {
            return "redirect:/customer/bookings/" + id + "/edit?error=" + ex.getMessage();
        }
        return "redirect:/customer/bookings";
    }

    private String saveReviewImage(org.springframework.web.multipart.MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return "";
        }
        try {
            String uploadDir = "uploads/reviews/";
            java.io.File dir = new java.io.File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filename = UUID.randomUUID().toString() + "_" + org.springframework.util.StringUtils.cleanPath(image.getOriginalFilename());
            java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + filename);
            java.nio.file.Files.copy(image.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/reviews/" + filename;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
