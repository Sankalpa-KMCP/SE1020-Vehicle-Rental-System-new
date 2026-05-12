package com.sliit.vehiclerentalsystem01.payment.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CashPayment extends Payment {

    public CashPayment(String paymentId, String bookingId, double amount, String status) {
        super(paymentId, bookingId, amount, status);
    }

    @Override
    public String getPaymentMethod() {
        return "Cash Transaction";
    }
}
