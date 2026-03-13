package com.example.vehiclerentalsystem01.models;

/**
 * OOP Concept: Abstraction.
 * Abstract base class for all financial transactions.
 */
public abstract class Payment {
    // OOP Concept: Encapsulation
    private String paymentId;
    private String bookingId;
    private double amount;
    private String status; // e.g., PAID, PENDING, REFUNDED

    public Payment(String paymentId, String bookingId, double amount, String status) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.status = status;
    }

    // OOP Concept: Abstraction (Forces child classes to define the specific method)
    public abstract String getPaymentMethod();

    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}