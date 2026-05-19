package com.sliit.vehiclerentalsystem01.core.security;

/**
 * Session attribute names aligned with legacy JSP flows.
 */
public final class UserSessionKeys {

    public static final String USERNAME = "username";
    public static final String CUSTOMER_LOGGED_IN = "customerLoggedIn";
    public static final String ADMIN_LOGGED_IN = "adminLoggedIn";
    public static final String STAFF_LOGGED_IN = "staffLoggedIn";
    public static final String AUTH_ROLE = "authRole";
    /** Optional elevated session for master admin console. */
    public static final String IS_MASTER_ADMIN = "isMasterAdmin";

    private UserSessionKeys() {
    }
}
