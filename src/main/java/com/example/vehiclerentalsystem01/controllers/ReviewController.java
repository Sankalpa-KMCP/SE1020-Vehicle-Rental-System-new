package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.ReviewDAO;
import com.example.vehiclerentalsystem01.models.CustomerReview;
import com.example.vehiclerentalsystem01.models.Feedback;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet("/reviews")
public class ReviewController extends HttpServlet {
    private ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            String id = request.getParameter("id");
            reviewDAO.deleteReview(id);
            response.sendRedirect("reviews?action=list");
        } else if ("edit".equals(action)) {
            String id = request.getParameter("id");
            Feedback f = reviewDAO.getReviewById(id);
            if (f != null) {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        String reviewerName = request.getParameter("reviewerName");
        int rating = Integer.parseInt(request.getParameter("rating"));
        String comments = request.getParameter("comments");
        String status = request.getParameter("status"); // Usually PUBLISHED or FLAGGED

        if ("update".equals(action)) {
            String id = request.getParameter("id");
            Feedback updatedReview = new CustomerReview(id, reviewerName, rating, comments, status);
            reviewDAO.updateReview(updatedReview);
        } else {
            String manualId = request.getParameter("manualId");
            String newId = (manualId != null && !manualId.trim().isEmpty()) ? manualId.trim()
                    : "REV-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

            // New reviews are automatically PUBLISHED unless noted otherwise
            Feedback newReview = new CustomerReview(newId, reviewerName, rating, comments, "PUBLISHED");
            reviewDAO.saveReview(newReview);
        }

        response.sendRedirect("reviews?action=list");
    }
}