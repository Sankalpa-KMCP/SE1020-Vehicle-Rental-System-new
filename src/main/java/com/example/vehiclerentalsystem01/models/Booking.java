package com.example.vehiclerentalsystem01.models;

/**
 * OOP Concept: Inheritance.
 * Booking inherits core fields from Transaction.
 */
public class Booking extends Transaction {
    private String vehicleId;
    private int durationDays;
    private String status; // e.g., PENDING, CONFIRMED, CANCELLED
    private String staffMessage;

    public Booking(String transactionId, String customerName, String rentalDate, String vehicleId, int durationDays, String status) {
        this(transactionId, customerName, rentalDate, vehicleId, durationDays, status, "");
    }

    public Booking(String transactionId, String customerName, String rentalDate, String vehicleId, int durationDays, String status, String staffMessage) {
        super(transactionId, customerName, rentalDate); // Call parent constructor
        this.vehicleId = vehicleId;
        this.durationDays = durationDays;
        this.status = status;
        this.staffMessage = staffMessage == null ? "" : staffMessage;
    }

    // Getters and Setters
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStaffMessage() { return staffMessage; }
    public void setStaffMessage(String staffMessage) { this.staffMessage = staffMessage == null ? "" : staffMessage; }

    // --- JSP COMPATIBILITY ALIASES ---
    // These methods fix the "method undefined" errors in search-booking.jsp
    public String getId() { return getTransactionId(); }
    public int getDays() { return durationDays; }

    /**
     * OOP Concept: Polymorphism.
     * Overriding the abstract method from the Transaction class.
     */
    @Override
    public String getTransactionType() {
        return "Vehicle Reservation";
    }
}