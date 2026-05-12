package com.sliit.vehiclerentalsystem01.user.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract base for all system users. Demonstrates abstraction and encapsulation.
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class User {

    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String email;

    protected static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    protected User() {
        this("", "", "");
    }

    public User(String id, String name, String email) {
        this.id = normalize(id);
        this.name = normalize(name);
        this.email = normalize(email);
    }

    /** Role label for polymorphism (e.g. CUSTOMER, STAFF). */
    public abstract String getUserRole();

    /** Login id; overridden by {@link Customer} and {@link Staff}. */
    public String getUsername() {
        return "";
    }

    public String getPassword() {
        return "";
    }
}
