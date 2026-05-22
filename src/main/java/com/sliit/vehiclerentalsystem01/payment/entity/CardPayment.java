package com.sliit.vehiclerentalsystem01.payment.entity;

/**
 * demonstrating Inheritance and Polymorphism.
 */
public class CardPayment extends Payment {

    /**
     * Default constructor calls the superclass (Payment) default constructor.
     */
    public CardPayment() {
        super();
    }

    /**
     * Parameterized constructor passes values to the superclass constructor.
     */
    public CardPayment(String paymentId, String bookingId, double amount, String status) {
        super(paymentId, bookingId, amount, status);
    }

    /**
     * Overrides the abstract method from the Payment class to return
     * a specific type identifier for card payments.
     */
    @Override
    public String getPaymentMethod() {
        return "Credit/Debit Card";
    }
}
