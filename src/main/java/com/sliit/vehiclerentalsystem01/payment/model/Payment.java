package com.sliit.vehiclerentalsystem01.payment.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Payment {

    @EqualsAndHashCode.Include
    private String id;
    private String bookingId;
    private double amount;
    private String status;

    public Payment(String id, String bookingId, double amount, String status) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.status = status;
    }

    public abstract String getPaymentMethod();

    /** Legacy CSV column name; same as {@link #getId()}. */
    public String getPaymentId() {
        return id;
    }

    public void setPaymentId(String paymentId) {
        this.id = paymentId;
    }

    /** Polymorphic label used by older controllers; delegates to {@link #getPaymentMethod()}. */
    public String getPaymentType() {
        return getPaymentMethod();
    }
}
