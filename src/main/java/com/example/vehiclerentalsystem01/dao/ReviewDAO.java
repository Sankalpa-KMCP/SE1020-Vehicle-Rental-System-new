package com.example.vehiclerentalsystem01.dao;

import com.example.vehiclerentalsystem01.models.CustomerReview;
import com.example.vehiclerentalsystem01.models.Feedback;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    private static final String FOLDER_PATH = System.getProperty("user.home") + File.separator + "RentalData";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "reviews.txt";

    public ReviewDAO() {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) folder.mkdirs();
    }

    private String csvSafe(String value) {
        if (value == null) return "";
        return value.replace(",", ".").replace("\r", " ").replace("\n", " ").trim();
    }

    // CRUD: CREATE
    public void saveReview(Feedback feedback) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            CustomerReview cr = (CustomerReview) feedback;
            writer.write(cr.getReviewId() + "," + csvSafe(cr.getReviewerName()) + "," + csvSafe(cr.getBookingId()) + "," + cr.getRating() + ","
                    + csvSafe(cr.getComments()) + "," + csvSafe(cr.getStatus()) + ","
                    + csvSafe(cr.getStaffReply()) + "," + csvSafe(cr.getRepliedBy()) + "," + csvSafe(cr.getRepliedAt()));
            writer.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // CRUD: READ
    public List<Feedback> getAllReviews() {
        List<Feedback> reviews = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return reviews;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 5) {
                    String bookingId = "";
                    int ratingIndex = 2;
                    int commentsIndex = 3;
                    int statusIndex = 4;
                    int replyIndex = 5;
                    int repliedByIndex = 6;
                    int repliedAtIndex = 7;

                    // Detect format: if data[2] is not a number, it's the new format with bookingId
                    try {
                        Integer.parseInt(data[2]);
                        // Old format: reviewId, reviewerName, rating, comments, status, staffReply, repliedBy, repliedAt
                    } catch (NumberFormatException e) {
                        // New format: reviewId, reviewerName, bookingId, rating, comments, status, staffReply, repliedBy, repliedAt
                        bookingId = data[2];
                        ratingIndex = 3;
                        commentsIndex = 4;
                        statusIndex = 5;
                        replyIndex = 6;
                        repliedByIndex = 7;
                        repliedAtIndex = 8;
                    }

                    String reply = data.length > replyIndex ? data[replyIndex] : "";
                    String repliedBy = data.length > repliedByIndex ? data[repliedByIndex] : "";
                    String repliedAt = data.length > repliedAtIndex ? data[repliedAtIndex] : "";

                    reviews.add(new CustomerReview(data[0], data[1], Integer.parseInt(data[ratingIndex]),
                            data[commentsIndex], data[statusIndex], bookingId, reply, repliedBy, repliedAt));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return reviews;
    }

    public Feedback getReviewById(String id) {
        for (Feedback f : getAllReviews()) {
            if (f.getReviewId().equals(id)) return f;
        }
        return null;
    }

    // CRUD: UPDATE
    public void updateReview(Feedback updatedReview) {
        List<Feedback> reviews = getAllReviews();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Feedback f : reviews) {
                CustomerReview cr = (CustomerReview) (f.getReviewId().equals(updatedReview.getReviewId()) ? updatedReview : f);
                writer.write(cr.getReviewId() + "," + csvSafe(cr.getReviewerName()) + "," + csvSafe(cr.getBookingId()) + "," + cr.getRating() + ","
                        + csvSafe(cr.getComments()) + "," + csvSafe(cr.getStatus()) + ","
                        + csvSafe(cr.getStaffReply()) + "," + csvSafe(cr.getRepliedBy()) + "," + csvSafe(cr.getRepliedAt()));
                writer.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // CRUD: DELETE
    public void deleteReview(String id) {
        List<Feedback> reviews = getAllReviews();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Feedback f : reviews) {
                if (!f.getReviewId().equals(id)) {
                    CustomerReview cr = (CustomerReview) f;
                    writer.write(cr.getReviewId() + "," + csvSafe(cr.getReviewerName()) + "," + csvSafe(cr.getBookingId()) + "," + cr.getRating() + ","
                            + csvSafe(cr.getComments()) + "," + csvSafe(cr.getStatus()) + ","
                            + csvSafe(cr.getStaffReply()) + "," + csvSafe(cr.getRepliedBy()) + "," + csvSafe(cr.getRepliedAt()));
                    writer.newLine();
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Count reviews by customer (includes both booking reviews and account reviews)
    public int countReviewsByCustomer(String customerName) {
        String normalizedCustomer = customerName == null ? "" : customerName.trim().toLowerCase();
        int count = 0;
        for (Feedback f : getAllReviews()) {
            if (f instanceof CustomerReview) {
                CustomerReview cr = (CustomerReview) f;
                if (cr.getReviewerName() != null && cr.getReviewerName().trim().toLowerCase().equals(normalizedCustomer)) {
                    count++;
                }
            }
        }
        return count;
    }

    // Count reviews by customer for a specific booking
    public int countReviewsByCustomerAndBooking(String customerName, String bookingId) {
        String normalizedCustomer = customerName == null ? "" : customerName.trim().toLowerCase();
        String normalizedBookingId = bookingId == null ? "" : bookingId.trim();
        int count = 0;
        for (Feedback f : getAllReviews()) {
            if (f instanceof CustomerReview) {
                CustomerReview cr = (CustomerReview) f;
                if (cr.getReviewerName() != null && cr.getReviewerName().trim().toLowerCase().equals(normalizedCustomer)
                        && cr.getBookingId() != null && cr.getBookingId().trim().equals(normalizedBookingId)) {
                    count++;
                }
            }
        }
        return count;
    }

    // Count account-only reviews (reviews with empty bookingId)
    public int countAccountReviewsByCustomer(String customerName) {
        String normalizedCustomer = customerName == null ? "" : customerName.trim().toLowerCase();
        int count = 0;
        for (Feedback f : getAllReviews()) {
            if (f instanceof CustomerReview) {
                CustomerReview cr = (CustomerReview) f;
                if (cr.getReviewerName() != null && cr.getReviewerName().trim().toLowerCase().equals(normalizedCustomer)
                        && (cr.getBookingId() == null || cr.getBookingId().trim().isEmpty())) {
                    count++;
                }
            }
        }
        return count;
    }
}