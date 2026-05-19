package com.sliit.vehiclerentalsystem01.payment.entity;

/**
 * Canonical payment lifecycle states. File and form layers still use {@link String};
 * use {@link #fromString(String)} to parse legacy or synonym values safely.
 */
public enum PaymentStatus {
    PAID,
    PENDING,
    REFUNDED,
    FAILED;

    public static PaymentStatus fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            return PENDING;
        }
        String s = raw.trim().toUpperCase();
        try {
            return valueOf(s);
        } catch (IllegalArgumentException ignored) {
            if (containsAny(s, "PAID", "COMPLETED", "SETTLED")) {
                return PAID;
            }
            if (s.contains("REFUND")) {
                return REFUNDED;
            }
            if (s.contains("FAIL")) {
                return FAILED;
            }
            if (containsAny(s, "PENDING", "DUE", "AWAITING")) {
                return PENDING;
            }
            return PENDING;
        }
    }

    private static boolean containsAny(String value, String... tokens) {
        for (String token : tokens) {
            if (value.contains(token)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPaid() {
        return this == PAID;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isRefunded() {
        return this == REFUNDED;
    }
}
