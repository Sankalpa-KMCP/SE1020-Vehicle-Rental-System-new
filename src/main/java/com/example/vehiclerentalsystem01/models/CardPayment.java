package com.example.vehiclerentalsystem01.models;

/**
 * OOP Concept: Inheritance.
 * CardPayment inherits from Payment.
 */
public class CardPayment extends Payment {

    public CardPayment(String paymentId, String bookingId, double amount, String status) {
        super(paymentId, bookingId, amount, status);
    }

    /**
     * OOP Concept: Polymorphism.
     * Overriding the abstract method.
     */
    @Override
    public String getPaymentMethod() {
        return "Credit/Debit Card";
    }
}