package com.sliit.vehiclerentalsystem01.booking.model;

public abstract class Transaction {

    private String transactionId;
    private String customerName;
    private String rentalDate;

    public Transaction() {
        this.transactionId = "";
        this.customerName = "";
        this.rentalDate = "";
    }

    public Transaction(String transactionId, String customerName, String rentalDate) {
        this.transactionId = transactionId;
        this.customerName = customerName;
        this.rentalDate = rentalDate;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(String rentalDate) {
        this.rentalDate = rentalDate;
    }

    public abstract String getTransactionType();
}
