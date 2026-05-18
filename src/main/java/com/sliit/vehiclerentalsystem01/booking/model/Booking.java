package com.sliit.vehiclerentalsystem01.booking.model;

/**
 * Vehicle reservation transaction. {@link #getId()} aliases {@link #getTransactionId()}.
 * Bookings store {@code customerName} as the customer's login username when created via the app.
 */
public class Booking extends Transaction {

    private String vehicleId;
    private int durationDays;
    private String status;
    private String staffMessage;

    public Booking() {
        super();
        this.vehicleId = "";
        this.durationDays = 0;
        this.status = "";
        this.staffMessage = "";
    }

    public Booking(String transactionId, String customerName, String rentalDate, String vehicleId,
                   int durationDays, String status) {
        this(transactionId, customerName, rentalDate, vehicleId, durationDays, status, "");
    }

    public Booking(String transactionId, String customerName, String rentalDate, String vehicleId,
                   int durationDays, String status, String staffMessage) {
        super(transactionId, customerName, rentalDate);
        this.vehicleId = vehicleId;
        this.durationDays = durationDays;
        this.status = status;
        this.staffMessage = staffMessage;
    }

    // Getters and Setters
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStaffMessage() {
        return staffMessage;
    }

    public void setStaffMessage(String staffMessage) {
        this.staffMessage = staffMessage;
    }

    /** Same as transaction id (legacy JSP compatibility). */
    public String getId() {
        return getTransactionId();
    }

    public void setId(String id) {
        setTransactionId(id);
    }

    /** Alias for {@link #getDurationDays()}. */
    public int getDays() {
        return durationDays;
    }

    /** Stored booking owner key: same as {@link #getCustomerName()} (username). */
    public String getCustomerId() {
        return getCustomerName();
    }

    public void setCustomerId(String customerId) {
        setCustomerName(customerId);
    }

    @Override
    public String getTransactionType() {
        return "Vehicle Reservation";
    }

    /** Thymeleaf compatibility for lists (same as {@link #getTransactionType()}). */
    public String getBookingType() {
        return getTransactionType();
    }
}
