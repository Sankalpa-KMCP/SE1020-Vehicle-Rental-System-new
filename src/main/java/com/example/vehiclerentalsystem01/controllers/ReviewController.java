package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.BookingDAO;
import com.example.vehiclerentalsystem01.dao.ReviewDAO;
import com.example.vehiclerentalsystem01.services.SessionManager;
import com.example.vehiclerentalsystem01.models.Booking;
import com.example.vehiclerentalsystem01.models.CustomerReview;
import com.example.vehiclerentalsystem01.models.Feedback;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    private String normalized(String value) {
        return value == null ? "" : value.trim();
    }

    @GetMapping
    public String handleGet(@RequestParam(required = false) String action,
                            @RequestParam(required = false) String id,
                            Model model) {
        if ("delete".equals(action)) {
            reviewDAO.deleteReview(id);
            return "redirect:/reviews?action=list";
        }

        if ("edit".equals(action)) {
            Feedback review = reviewDAO.getReviewById(id);
            if (review == null) {
                return "redirect:/reviews?action=list";
            }
            model.addAttribute("review", review);
            return "edit-review";
        }

        model.addAttribute("reviewList", reviewDAO.getAllReviews());
        return "review-dashboard";
    }

    @PostMapping
    public String handlePost(@RequestParam Map<String, String> params, HttpSession session) {
        String action = params.get("action");

        if ("reply".equals(action)) {
            String id = params.get("id");
            Feedback existing = reviewDAO.getReviewById(id);
            if (existing instanceof CustomerReview cr) {
                String repliedBy = "staff";
                if (session != null && session.getAttribute("adminUser") != null) {
                    repliedBy = String.valueOf(session.getAttribute("adminUser"));
                }

                CustomerReview replied = new CustomerReview(
                        cr.getReviewId(),
                        cr.getReviewerName(),
                        cr.getRating(),
                        cr.getComments(),
                        cr.getStatus(),
                        normalized(params.get("staffReply")),
                        repliedBy,
                        LocalDateTime.now().toString()
                );
                reviewDAO.updateReview(replied);
            }
            return "redirect:/reviews?action=list";
        }

        String reviewerName = normalized(params.get("reviewerName"));
        String rawRating = params.get("rating");
        if (rawRating == null || rawRating.isBlank()) {
            return "customer".equals(normalized(params.get("source")))
                    ? "redirect:/customer-add-review.jsp?error=missing_fields"
                    : "redirect:/reviews?action=list&error=missing_fields";
        }
        int rating = Integer.parseInt(rawRating.trim());
        String comments = normalized(params.get("comments"));
        String status = normalized(params.get("status"));
        if (status.isEmpty()) {
            status = "PUBLISHED";
        }
        String source = params.get("source");

        if ("customer".equals(source)) {
            String username = "";
            if (session != null) {
                username = normalized((String) session.getAttribute(SessionManager.CUSTOMER_USERNAME));
                if (username.isEmpty()) {
                    username = normalized((String) session.getAttribute(SessionManager.USERNAME));
                }
                if (username.isEmpty()) {
                    username = normalized((String) session.getAttribute(SessionManager.NAME));
                }
            }

            String bookingId = normalized(params.get("bookingId"));
            if (bookingId.isEmpty()) {
                if (reviewDAO.countAccountReviewsByCustomer(username) >= 1) {
                    return "redirect:/customer-add-review.jsp?error=account_limit";
                }
            } else {
                if (reviewDAO.countReviewsByCustomerAndBooking(username, bookingId) >= 2) {
                    return "redirect:/customer-add-review.jsp?error=booking_limit";
                }

                Booking booking = bookingDAO.getBookingById(bookingId);
                if (booking == null || !normalized(booking.getCustomerName()).equalsIgnoreCase(username)) {
                    return "redirect:/customer-add-review.jsp?error=invalid_booking";
                }
            }
        }

        if (!"update".equals(action) && comments.isEmpty()) {
            return "customer".equals(source)
                    ? "redirect:/customer-add-review.jsp?error=missing_fields"
                    : "redirect:/add-review.jsp?error=missing_fields";
        }

        if ("update".equals(action)) {
            String id = params.get("id");
            Feedback existing = reviewDAO.getReviewById(id);
            CustomerReview existingReview = existing instanceof CustomerReview cr ? cr : null;
            Feedback updatedReview = new CustomerReview(
                    id,
                    reviewerName,
                    rating,
                    comments,
                    status,
                    existingReview == null ? "" : existingReview.getBookingId(),
                    existingReview == null ? "" : existingReview.getStaffReply(),
                    existingReview == null ? "" : existingReview.getRepliedBy(),
                    existingReview == null ? "" : existingReview.getRepliedAt()
            );
            reviewDAO.updateReview(updatedReview);
        } else {
            String manualId = params.get("manualId");
            String newId = manualId != null && !manualId.trim().isEmpty()
                    ? manualId.trim()
                    : "REV-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            String bookingId = "customer".equals(source) ? normalized(params.get("bookingId")) : "";
            reviewDAO.saveReview(new CustomerReview(newId, reviewerName, rating, comments, "PUBLISHED", bookingId, "", "", ""));
        }

        return "customer".equals(source)
                ? "redirect:/customer-home.jsp"
                : "redirect:/reviews?action=list";
    }
}
