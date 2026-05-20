package com.sliit.vehiclerentalsystem01.payment.entity;

/**
 * Represents a payment made via Cash.
 * This class inherits from the abstract Payment class,
 * providing a concrete implementation for cash transactions.
 */
public class CashPayment extends Payment {

    /**
     * Default constructor calls the superclass (Payment) default constructor.
     */
    public CashPayment() {
        super();
    }

    /**
     * Parameterized constructor passes values to the superclass constructor.
     */
    public CashPayment(String paymentId, String bookingId, double amount, String status) {
        super(paymentId, bookingId, amount, status);
    }

    /**
     * Overrides the abstract method from the Payment class to return
     * a specific type identifier for cash payments.
     */
    @Override
    public String getPaymentMethod() {
        return "Cash Transaction";
    }
}
