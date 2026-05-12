package com.sliit.vehiclerentalsystem01.payment.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardPayment extends Payment {

    public CardPayment(String paymentId, String bookingId, double amount, String status) {
        super(paymentId, bookingId, amount, status);
    }

    @Override
    public String getPaymentMethod() {
        return "Credit/Debit Card";
    }
}
