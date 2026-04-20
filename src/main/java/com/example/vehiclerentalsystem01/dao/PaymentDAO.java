package com.example.vehiclerentalsystem01.dao;

import com.example.vehiclerentalsystem01.models.CardPayment;
import com.example.vehiclerentalsystem01.models.CashPayment;
import com.example.vehiclerentalsystem01.models.Payment;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class PaymentDAO {
    private static final String FOLDER_PATH = System.getProperty("user.home") + File.separator + "RentalData";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "payments.txt";

    public PaymentDAO() {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) folder.mkdirs();
    }


    public void savePayment(Payment payment) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true)))   // open the file in append mode
        {
            writer.write(payment.getPaymentId() + "," + payment.getBookingId() + "," +
                    payment.getAmount() + "," + payment.getPaymentMethod() + "," + payment.getStatus());
            writer.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Admin dashboard
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return payments;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    double amount = Double.parseDouble(data[2]);
                    // Re-instantiate the correct object based on the saved text!
                    if (data[3].contains("Card")) {
                        payments.add(new CardPayment(data[0], data[1], amount, data[4]));
                    } else {
                        payments.add(new CashPayment(data[0], data[1], amount, data[4]));
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return payments;
    }

    // Edit (finding a record)
    public Payment getPaymentById(String id) {
        for (Payment payment : getAllPayments()) {
            if (payment.getPaymentId().equals(id)) return payment;
        }
        return null;
    }


    public void updatePayment(Payment updatedPayment) {
        List<Payment> payments = getAllPayments();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Payment p : payments) {
                if (p.getPaymentId().equals(updatedPayment.getPaymentId())) {
                    writer.write(updatedPayment.getPaymentId() + "," + updatedPayment.getBookingId() + "," +
                            updatedPayment.getAmount() + "," + updatedPayment.getPaymentMethod() + "," + updatedPayment.getStatus());
                } else {
                    writer.write(p.getPaymentId() + "," + p.getBookingId() + "," +
                            p.getAmount() + "," + p.getPaymentMethod() + "," + p.getStatus());
                }
                writer.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }


    public void deletePayment(String id) {
        List<Payment> payments = getAllPayments();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Payment p : payments) {
                if (!p.getPaymentId().equals(id)) {
                    writer.write(p.getPaymentId() + "," + p.getBookingId() + "," +
                            p.getAmount() + "," + p.getPaymentMethod() + "," + p.getStatus());
                    writer.newLine();
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

}