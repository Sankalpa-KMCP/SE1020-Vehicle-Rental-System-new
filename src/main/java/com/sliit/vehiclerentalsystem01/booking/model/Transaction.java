package com.sliit.vehiclerentalsystem01.booking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class Transaction {

    private String transactionId;
    private String customerName;
    private String rentalDate;

    public Transaction(String transactionId, String customerName, String rentalDate) {
        this.transactionId = transactionId;
        this.customerName = customerName;
        this.rentalDate = rentalDate;
    }

    public abstract String getTransactionType();
}
