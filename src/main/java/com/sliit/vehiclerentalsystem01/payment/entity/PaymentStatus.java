package com.sliit.vehiclerentalsystem01.payment.entity;

/**
 * Enumeration defining the canonical payment lifecycle states.
 * Enums are useful for restricting a variable to have one of a few predefined values.
 */
public enum PaymentStatus {
    PAID,
    PENDING,
    VERIFYING,
    REFUNDED,
    FAILED,
    DELETED;

    /**
     * Parses a string into a known PaymentStatus safely.
     * Maps different legacy or synonym values to canonical statuses.
     */
    public static PaymentStatus fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            return PENDING;
        }
        
        String s = raw.trim().toUpperCase();
        
        // Try exact match first
        try {
            return valueOf(s);
        } catch (IllegalArgumentException ignored) {
            // Fallback: determine status by keywords in the string
            if (containsAny(s, "PAID", "COMPLETED", "SETTLED")) {
                return PAID;
            }
            if (s.contains("REFUND")) {
                return REFUNDED;
            }
            if (s.contains("FAIL")) {
                return FAILED;
            }
            if (s.contains("DELETE")) {
                return DELETED;
            }
            if (containsAny(s, "PENDING", "DUE", "AWAITING")) {
                return PENDING;
            }
            // Default edge case
            return PENDING;
        }
    }

    /**
     * Helper method used by fromString to check against multiple keywords.
     */
    private static boolean containsAny(String value, String... tokens) {
        for (String token : tokens) {
            if (value.contains(token)) {
                return true;
            }
        }
        return false;
    }

    // --- Helper methods to easily check the exact status ---

    public boolean isPaid() {
        return this == PAID;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isRefunded() {
        return this == REFUNDED;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }
}
