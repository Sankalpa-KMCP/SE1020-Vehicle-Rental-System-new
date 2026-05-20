package com.sliit.vehiclerentalsystem01.payment.entity;

/**
 * Abstract base class for all payment types in the system.
 * This demonstrates the Object-Oriented concept of Abstraction and Encapsulation.
 * By defining common properties and methods here, subclasses can inherit them.
 */
public abstract class Payment {

    // Encapsulated fields (accessible only via getters/setters)
    private String id;
    private String bookingId;
    private double amount;
    private String status;

    /**
     * Default constructor for basic instantiation.
     */
    public Payment() {
        this.id = "";
        this.bookingId = "";
        this.amount = 0.0;
        this.status = "";
    }

    /**
     * Parameterized constructor used to initialize common fields.
     */
    public Payment(String id, String bookingId, double amount, String status) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.status = status;
    }

    // --- Getters and Setters for Encapsulated Fields ---

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

    /**
     * Abstract method demonstrating Polymorphism.
     * Subclasses MUST implement this to provide their specific payment type name.
     */
    public abstract String getPaymentMethod();

    /** 
     * Legacy CSV column name; same as {@link #getId()}. 
     */
    public String getPaymentId() {
        return getId();
    }

    /**
     * Overridden equals method to compare payments by their unique ID.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Payment payment = (Payment) obj;
        return id != null ? id.equals(payment.id) : payment.id == null;
    }

    /**
     * Overridden hashCode method aligned with the equals method.
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /** 
     * Polymorphic label used by older controllers; delegates to {@link #getPaymentMethod()}. 
     */
    public String getPaymentType() {
        return getPaymentMethod();
    }
}
