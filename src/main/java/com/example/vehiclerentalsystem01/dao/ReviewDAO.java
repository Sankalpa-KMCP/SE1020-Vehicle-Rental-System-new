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

    // CRUD: CREATE
    public void saveReview(Feedback feedback) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            CustomerReview cr = (CustomerReview) feedback;
            // Clean comments to remove commas that would break our CSV logic
            String safeComments = cr.getComments().replace(",", ".");
            writer.write(cr.getReviewId() + "," + cr.getReviewerName() + "," + cr.getRating() + "," + safeComments + "," + cr.getStatus());
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
                String[] data = line.split(",");
                if (data.length == 5) {
                    reviews.add(new CustomerReview(data[0], data[1], Integer.parseInt(data[2]), data[3], data[4]));
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
                String safeComments = cr.getComments().replace(",", ".");
                writer.write(cr.getReviewId() + "," + cr.getReviewerName() + "," + cr.getRating() + "," + safeComments + "," + cr.getStatus());
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
                    writer.write(cr.getReviewId() + "," + cr.getReviewerName() + "," + cr.getRating() + "," + cr.getComments() + "," + cr.getStatus());
                    writer.newLine();
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}