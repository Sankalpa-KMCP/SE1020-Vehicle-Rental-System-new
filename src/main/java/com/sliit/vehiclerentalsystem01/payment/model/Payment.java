package com.sliit.vehiclerentalsystem01.payment.model;

public abstract class Payment {

    private String id;
    private String bookingId;
    private double amount;
    private String status;

    public Payment() {
        this.id = "";
        this.bookingId = "";
        this.amount = 0.0;
        this.status = "";
    }

    public Payment(String id, String bookingId, double amount, String status) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public abstract String getPaymentMethod();

    /** Legacy CSV column name; same as {@link #getId()}. */
    public String getPaymentId() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Payment payment = (Payment) obj;
        return id != null ? id.equals(payment.id) : payment.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /** Polymorphic label used by older controllers; delegates to {@link #getPaymentMethod()}. */
    public String getPaymentType() {
        return getPaymentMethod();
    }
}
