package com.sliit.vehiclerentalsystem01.payment.model;

public class CashPayment extends Payment {

    public CashPayment() {
        super();
    }

    public CashPayment(String paymentId, String bookingId, double amount, String status) {
        super(paymentId, bookingId, amount, status);
    }

    @Override
    public String getPaymentMethod() {
        return "Cash Transaction";
    }
}
