package com.sliit.vehiclerentalsystem01.core.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Zero-dependency password hashing utilities.
 */
public final class SecurityUtils {

    private static final int SHA256_HEX_LENGTH = 64;

    private SecurityUtils() {
    }

    public static String hashPassword(String plainText) {
        if (plainText == null) {
            plainText = "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static boolean isSha256Hex(String value) {
        if (value == null) {
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.length() != SHA256_HEX_LENGTH) {
            return false;
        }
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            boolean hex = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
            if (!hex) {
                return false;
            }
        }
        return true;
    }

    public static String hashPasswordIfNeeded(String password) {
        if (password == null || password.isBlank()) {
            return "";
        }
        String trimmed = password.trim();
        if (isSha256Hex(trimmed)) {
            return trimmed;
        }
        return hashPassword(password);
    }
}
