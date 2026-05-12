package com.sliit.vehiclerentalsystem01.user.auth;

/**
 * Session attribute names aligned with legacy JSP flows.
 */
public final class UserSessionKeys {

    public static final String USERNAME = "username";
    public static final String CUSTOMER_LOGGED_IN = "customerLoggedIn";
    public static final String ADMIN_LOGGED_IN = "adminLoggedIn";
    public static final String STAFF_LOGGED_IN = "staffLoggedIn";
    public static final String AUTH_ROLE = "authRole";

    private UserSessionKeys() {
    }
}
