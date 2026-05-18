package com.sliit.vehiclerentalsystem01.payment.entity;

public class CardPayment extends Payment {

    public CardPayment() {
        super();
    }

    public CardPayment(String paymentId, String bookingId, double amount, String status) {
        super(paymentId, bookingId, amount, status);
    }

    @Override
    public String getPaymentMethod() {
        return "Credit/Debit Card";
    }
}
