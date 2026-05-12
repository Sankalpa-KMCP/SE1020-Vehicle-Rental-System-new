package com.sliit.vehiclerentalsystem01.review.controller;

import com.sliit.vehiclerentalsystem01.review.model.CustomerReview;
import com.sliit.vehiclerentalsystem01.review.model.Feedback;
import com.sliit.vehiclerentalsystem01.review.service.ReviewService;
import com.sliit.vehiclerentalsystem01.user.model.Customer;
import com.sliit.vehiclerentalsystem01.user.model.Staff;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
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
    public String adminReviewForm(HttpSession session) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/login";
        }
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

    @GetMapping
    public String list(Model model) {
        model.addAttribute("reviews", reviewService.findAll());
        return "review/review-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
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
    public String detail(@PathVariable String id, Model model) {
        return reviewService.findById(id)
                .map(r -> {
                    model.addAttribute("review", r);
                    return "review/review-detail";
                })
                .orElse("redirect:/reviews");
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        return reviewService.findById(id)
                .map(r -> {
                    model.addAttribute("mode", "edit");
                    model.addAttribute("entityId", id);
                    model.addAttribute("reviewType", "CUSTOMER");
                    if (r instanceof CustomerReview c) {
                        model.addAttribute("customerReview", c);
                    } else {
                        model.addAttribute("customerReview", new CustomerReview());
                    }
                    model.addAttribute("customers", userService.findAll().stream()
                            .filter(u -> u instanceof Customer)
                            .toList());
                    model.addAttribute("vehicles", vehicleService.findAll());
                    model.addAttribute("reviews", reviewService.findAll());
                    model.addAttribute("parentReviews", customerReviewsOnly());
                    model.addAttribute("staffUsers", staffUsersOnly());
                    return "review/review-form";
                })
                .orElse("redirect:/reviews");
    }

    @PostMapping
    public String create(
            @RequestParam String reviewType,
            @ModelAttribute CustomerReview customerReview,
            @RequestParam(required = false) String manualId,
            @RequestParam(required = false) String parentReviewId,
            @RequestParam(required = false) String repliedBy,
            @RequestParam(required = false) String staffReply) {
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
            @RequestParam String reviewType,
            @ModelAttribute CustomerReview customerReview,
            @RequestParam(required = false) String parentReviewId,
            @RequestParam(required = false) String repliedBy,
            @RequestParam(required = false) String staffReply) {
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
    public String delete(@PathVariable String id) {
        reviewService.deleteById(id);
        return "redirect:/reviews";
    }
}
