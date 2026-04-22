package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.BookingDAO;
import com.example.vehiclerentalsystem01.dao.ReviewDAO;
import com.example.vehiclerentalsystem01.models.Booking;
import com.example.vehiclerentalsystem01.models.CustomerReview;
import com.example.vehiclerentalsystem01.models.Feedback;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@WebServlet("/reviews")
public class ReviewController extends HttpServlet {
    private ReviewDAO reviewDAO = new ReviewDAO();
    private BookingDAO bookingDAO = new BookingDAO();

    private String normalized(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            String id = request.getParameter("id");
            reviewDAO.deleteReview(id);
            response.sendRedirect("reviews?action=list");
        } else if ("edit".equals(action)) {
            String id = request.getParameter("id");
            Feedback f = reviewDAO.getReviewById(id);
            if(f != null) {
                request.setAttribute("review", f);
                request.getRequestDispatcher("edit-review.jsp").forward(request, response);
            } else {
                response.sendRedirect("reviews?action=list");
            }
        } else {
            List<Feedback> reviewList = reviewDAO.getAllReviews();
            request.setAttribute("reviewList", reviewList);
            request.getRequestDispatcher("review-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("reply".equals(action)) {
            String id = request.getParameter("id");
            String replyText = normalized(request.getParameter("staffReply"));
            Feedback existing = reviewDAO.getReviewById(id);
            if (existing instanceof CustomerReview) {
                CustomerReview cr = (CustomerReview) existing;
                String repliedBy = "staff";
                if (request.getSession(false) != null && request.getSession(false).getAttribute("adminUser") != null) {
                    repliedBy = String.valueOf(request.getSession(false).getAttribute("adminUser"));
                }

                CustomerReview replied = new CustomerReview(
                        cr.getReviewId(),
                        cr.getReviewerName(),
                        cr.getRating(),
                        cr.getComments(),
                        cr.getStatus(),
                        replyText,
                        repliedBy,
                        LocalDateTime.now().toString()
                );
                reviewDAO.updateReview(replied);
            }
            response.sendRedirect("reviews?action=list");
            return;
        }

        String reviewerName = request.getParameter("reviewerName");
        int rating = Integer.parseInt(request.getParameter("rating"));
        String comments = request.getParameter("comments");
        String status = request.getParameter("status"); // Usually PUBLISHED or FLAGGED
        String source = request.getParameter("source");

        if ("customer".equals(source)) {
            HttpSession session = request.getSession(false);
            String username = "";
            if (session != null) {
                username = normalized((String) session.getAttribute("username"));
                if (username.isEmpty()) {
                    username = normalized((String) session.getAttribute("name"));
                }
            }

            String bookingId = normalized(request.getParameter("bookingId"));

            // Check review limits
            if (bookingId.isEmpty()) {
                // Account review - limit to 1 per customer
                int accountReviewCount = reviewDAO.countAccountReviewsByCustomer(username);
                if (accountReviewCount >= 1) {
                    response.sendRedirect("customer-add-review.jsp?error=account_limit");
                    return;
                }
            } else {
                // Booking review - limit to 2 per booking per customer
                int bookingReviewCount = reviewDAO.countReviewsByCustomerAndBooking(username, bookingId);
                if (bookingReviewCount >= 2) {
                    response.sendRedirect("customer-add-review.jsp?error=booking_limit");
                    return;
                }
                // Verify the booking exists and belongs to the customer
                Booking booking = bookingDAO.getBookingById(bookingId);
                if (booking == null || !normalized(booking.getCustomerName()).equalsIgnoreCase(username)) {
                    response.sendRedirect("customer-add-review.jsp?error=invalid_booking");
                    return;
                }
            }
        }

        if ("update".equals(action)) {
            String id = request.getParameter("id");
            Feedback existing = reviewDAO.getReviewById(id);
            CustomerReview existingReview = existing instanceof CustomerReview ? (CustomerReview) existing : null;
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
            String manualId = request.getParameter("manualId");
            String newId = (manualId != null && !manualId.trim().isEmpty()) ? manualId.trim() : "REV-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

            String bookingId = "";
            if ("customer".equals(source)) {
                bookingId = normalized(request.getParameter("bookingId"));
            }

            // New reviews are automatically PUBLISHED unless noted otherwise
            Feedback newReview = new CustomerReview(newId, reviewerName, rating, comments, "PUBLISHED", bookingId, "", "", "");
            reviewDAO.saveReview(newReview);
        }

        if ("customer".equals(source)) {
            response.sendRedirect("customer-home.jsp");
        } else {
            response.sendRedirect("reviews?action=list");
        }
    }
}