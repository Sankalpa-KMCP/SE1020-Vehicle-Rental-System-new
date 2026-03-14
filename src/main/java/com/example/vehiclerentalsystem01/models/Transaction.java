package com.example.vehiclerentalsystem01.models;

/**
 * OOP Concept: Abstraction.
 * An abstract base class for any type of transaction in the system.
 */
public abstract class Transaction {
    // OOP Concept: Encapsulation
    private String transactionId;
    private String customerName;
    private String rentalDate;

    public Transaction(String transactionId, String customerName, String rentalDate) {
        this.transactionId = transactionId;
        this.customerName = customerName;
        this.rentalDate = rentalDate;
    }

    // OOP Concept: Abstraction (Forces child classes to define how status is handled)
    public abstract String getTransactionType();

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getRentalDate() { return rentalDate; }
    public void setRentalDate(String rentalDate) { this.rentalDate = rentalDate; }
}