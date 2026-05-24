package com.sliit.vehiclerentalsystem01.core.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Password utilities for both legacy SHA-256 and BCrypt migration.
 *
 * <p>Migration path: existing SHA-256-hex passwords are accepted during login.
 * On first successful login the plain text is re-hashed with BCrypt and stored
 * back to users.txt. New registrations always use BCrypt.
 *
 * @see AppUserDetailsService  Spring Security integration
 * @see com.sliit.vehiclerentalsystem01.user.service.UserServiceImpl  BCrypt migration trigger
 */
public final class SecurityUtils {

    private static final int SHA256_HEX_LENGTH = 64;

    private SecurityUtils() {}

    // --- BCrypt delegation ---------------------------------------------------

    /** Encode a plain-text password with BCrypt (Spring Security's encoder injected via static holder). */
    public static String encodeBCrypt(String plainText, PasswordEncoder encoder) {
        return encoder.encode(plainText);
    }

    /**
     * Verify a plain-text password against a BCrypt hash.
     * BCrypt hashes start with "$2" (a, b, or y/a/$2).
     */
    public static boolean matchesBCrypt(String plainText, String encoded, PasswordEncoder encoder) {
        return encoder.matches(plainText, encoded);
    }

    /** True when the stored value looks like a BCrypt hash. */
    public static boolean isBCrypt(String value) {
        return value != null && value.startsWith("$2");
    }

    // --- Legacy SHA-256 ------------------------------------------------------

    public static String sha256Hex(String plainText) {
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

    /**
     * Legacy hash-or-pass through. Prefer {@link #encodeBCrypt} for new passwords.
     * @deprecated use BCrypt exclusively
     */
    @Deprecated
    public static String hashPasswordIfNeeded(String password) {
        if (password == null || password.isBlank()) {
            return "";
        }
        String trimmed = password.trim();
        if (isSha256Hex(trimmed)) {
            return trimmed;
        }
        return sha256Hex(password);
    }

    /**
     * Legacy SHA-256 verification. Prefer {@link #matchesBCrypt} for BCrypt hashes.
     * @deprecated use BCrypt exclusively
     */
    @Deprecated
    public static boolean verifyPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }
        String plain = plainPassword.trim();
        String stored = storedPassword.trim();
        if (plain.isEmpty() || stored.isEmpty()) {
            return false;
        }
        if (isBCrypt(stored)) {
            return false; // call matchesBCrypt instead
        }
        if (isSha256Hex(stored)) {
            return sha256Hex(plain).equalsIgnoreCase(stored);
        }
        return plain.equals(stored);
    }
}
