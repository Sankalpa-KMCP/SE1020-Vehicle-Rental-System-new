package com.sliit.vehiclerentalsystem01.review.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.review.entity.CustomerReview;
import com.sliit.vehiclerentalsystem01.review.entity.Feedback;
import com.sliit.vehiclerentalsystem01.review.service.ReviewService;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    private static final String REVIEW_CUSTOMER_ONLY_MSG =
            "Reviews can only be submitted by customers from their account.";

    @GetMapping("/form/admin")
    public String adminReviewForm(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        redirectAttributes.addFlashAttribute("reviewNotice", REVIEW_CUSTOMER_ONLY_MSG);
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
    public String newForm(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        redirectAttributes.addFlashAttribute("reviewNotice", REVIEW_CUSTOMER_ONLY_MSG);
        return "redirect:/reviews";
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

    @PostMapping
    public String create(
            HttpSession session,
            @RequestParam(required = false) String reviewType,
            @RequestParam(required = false) String parentReviewId,
            @RequestParam(required = false) String repliedBy,
            @RequestParam(required = false) String staffReply,
            RedirectAttributes redirectAttributes) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        if (!"STAFF_REPLY".equalsIgnoreCase(reviewType)) {
            redirectAttributes.addFlashAttribute("reviewNotice", REVIEW_CUSTOMER_ONLY_MSG);
            return "redirect:/reviews";
        }
        return reviewService.findById(parentReviewId)
                .filter(f -> f instanceof CustomerReview)
                .map(f -> {
                    CustomerReview cr = (CustomerReview) f;
                    cr.setStaffReply(staffReply != null ? staffReply : "");
                    cr.setRepliedBy(repliedBy != null ? repliedBy : "");
                    cr.setRepliedAt(LocalDateTime.now().toString());
                    try {
                        reviewService.save(cr);
                    } catch (ValidationException ex) {
                        return "redirect:/reviews?error=" + ex.getMessage();
                    }
                    return "redirect:/reviews";
                })
                .orElse("redirect:/reviews");
    }

}
