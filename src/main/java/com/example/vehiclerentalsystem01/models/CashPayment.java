package com.example.vehiclerentalsystem01.models;

public class CashPayment extends Payment {

    public CashPayment(String paymentId, String bookingId, double amount, String status) {
        super(paymentId, bookingId, amount, status);
    }

    @Override
    public String getPaymentMethod() {
        return "Cash Transaction";
    }
}