package com.sliit.vehiclerentalsystem01.review.controller;

import com.sliit.vehiclerentalsystem01.review.model.CustomerReview;
import com.sliit.vehiclerentalsystem01.review.model.Feedback;
import com.sliit.vehiclerentalsystem01.review.service.ReviewService;
import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.user.model.Customer;
import com.sliit.vehiclerentalsystem01.user.model.Staff;
import com.sliit.vehiclerentalsystem01.user.model.User;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final VehicleService vehicleService;

    public ReviewController(ReviewService reviewService,
                            UserService userService,
                            VehicleService vehicleService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    @GetMapping("/form/admin")
    public String adminReviewForm(HttpSession session, Model model) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        model.addAttribute("customers", userService.findAll().stream()
                .filter(u -> u instanceof Customer)
                .toList());
        model.addAttribute("vehicles", vehicleService.findAll());
        model.addAttribute("parentReviews", customerReviewsOnly());
        model.addAttribute("staffUsers", staffUsersOnly());
        return "review/admin-review-form";
    }

    private List<Feedback> customerReviewsOnly() {
        return reviewService.findAll().stream()
                .filter(r -> r instanceof CustomerReview)
                .toList();
    }

    private List<User> staffUsersOnly() {
        return userService.findAll().stream()
                .filter(u -> u instanceof Staff)
                .toList();
    }

    @GetMapping(params = {"action=delete", "id"})
    public String deleteLegacyQuery(HttpSession session, @RequestParam String id) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        if (id != null && !id.isBlank()) {
            reviewService.deleteById(id.trim());
        }
        return "redirect:/reviews";
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        List<Feedback> all = reviewService.findAll();
        model.addAttribute("reviews", all);
        List<CustomerReview> customerReviews = all.stream()
                .filter(r -> r instanceof CustomerReview)
                .map(r -> (CustomerReview) r)
                .toList();
        model.addAttribute("customerReviews", customerReviews);
        model.addAttribute("totalReviews", customerReviews.size());
        long publishedCount = customerReviews.stream()
                .filter(r -> "PUBLISHED".equalsIgnoreCase(Objects.toString(r.getStatus(), "")))
                .count();
        long flaggedCount = customerReviews.stream()
                .filter(r -> "FLAGGED".equalsIgnoreCase(Objects.toString(r.getStatus(), "")))
                .count();
        model.addAttribute("publishedCount", publishedCount);
        model.addAttribute("flaggedCount", flaggedCount);
        double avgRating = customerReviews.stream().mapToInt(CustomerReview::getRating).average().orElse(0.0);
        model.addAttribute("avgRating", avgRating);

        String staffUser = "";
        if (session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            Object u = session.getAttribute(UserSessionKeys.USERNAME);
            staffUser = u == null ? "" : u.toString().trim();
        }
        model.addAttribute("currentStaffUsername", staffUser);
        return "review/review-dashboard";
    }

    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        model.addAttribute("mode", "create");
        model.addAttribute("entityId", null);
        model.addAttribute("reviewType", "CUSTOMER");
        model.addAttribute("customerReview", new CustomerReview());
        model.addAttribute("customers", userService.findAll().stream()
                .filter(u -> u instanceof Customer)
                .toList());
        model.addAttribute("vehicles", vehicleService.findAll());
        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("parentReviews", customerReviewsOnly());
        model.addAttribute("staffUsers", staffUsersOnly());
        return "review/review-form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, HttpSession session, Model model) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        return reviewService.findById(id)
                .map(r -> {
                    model.addAttribute("review", r);
                    return "review/review-detail";
                })
                .orElse("redirect:/reviews");
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, HttpSession session, Model model) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        return reviewService.findById(id)
                .filter(r -> r instanceof CustomerReview)
                .map(r -> {
                    model.addAttribute("customerReview", (CustomerReview) r);
                    return "review/edit-review";
                })
                .orElse("redirect:/reviews");
    }

    @PostMapping
    public String create(
            HttpSession session,
            @RequestParam String reviewType,
            @ModelAttribute CustomerReview customerReview,
            @RequestParam(required = false) String manualId,
            @RequestParam(required = false) String parentReviewId,
            @RequestParam(required = false) String repliedBy,
            @RequestParam(required = false) String staffReply) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        if ("STAFF_REPLY".equalsIgnoreCase(reviewType)) {
            return reviewService.findById(parentReviewId)
                    .filter(f -> f instanceof CustomerReview)
                    .map(f -> {
                        CustomerReview cr = (CustomerReview) f;
                        cr.setStaffReply(staffReply != null ? staffReply : "");
                        cr.setRepliedBy(repliedBy != null ? repliedBy : "");
                        cr.setRepliedAt(LocalDateTime.now().toString());
                        reviewService.save(cr);
                        return "redirect:/reviews";
                    })
                    .orElse("redirect:/reviews");
        }
        if (customerReview.getStatus() == null || customerReview.getStatus().isBlank()) {
            customerReview.setStatus("PUBLISHED");
        }
        if (manualId != null && !manualId.isBlank()) {
            customerReview.setReviewId(manualId.trim());
        }
        reviewService.save(customerReview);
        return "redirect:/reviews";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable String id,
            HttpSession session,
            @RequestParam String reviewType,
            @ModelAttribute CustomerReview customerReview,
            @RequestParam(required = false) String parentReviewId,
            @RequestParam(required = false) String repliedBy,
            @RequestParam(required = false) String staffReply) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        if ("STAFF_REPLY".equalsIgnoreCase(reviewType)) {
            return reviewService.findById(parentReviewId != null && !parentReviewId.isBlank() ? parentReviewId : id)
                    .filter(f -> f instanceof CustomerReview)
                    .map(f -> {
                        CustomerReview cr = (CustomerReview) f;
                        cr.setStaffReply(staffReply != null ? staffReply : "");
                        cr.setRepliedBy(repliedBy != null ? repliedBy : "");
                        cr.setRepliedAt(LocalDateTime.now().toString());
                        reviewService.save(cr);
                        return "redirect:/reviews";
                    })
                    .orElse("redirect:/reviews");
        }
        customerReview.setId(id);
        reviewService.save(customerReview);
        return "redirect:/reviews";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, HttpSession session) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        reviewService.deleteById(id);
        return "redirect:/reviews";
    }
}
