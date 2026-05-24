package com.sliit.vehiclerentalsystem01.core.security;

/** Canonical role constants — must match the GrantedAuthority names used in AppUserDetailsService. */
public final class Roles {

    public static final String CUSTOMER = "ROLE_CUSTOMER";
    public static final String ADMIN    = "ROLE_ADMIN";
    public static final String SUPPORT  = "ROLE_SUPPORT";
    public static final String STAFF    = "ROLE_STAFF";

    private Roles() {}
}
