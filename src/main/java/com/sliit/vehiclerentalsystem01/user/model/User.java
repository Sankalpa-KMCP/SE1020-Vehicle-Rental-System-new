package com.sliit.vehiclerentalsystem01.user.model;

/**
 * Abstract base for all system users. Demonstrates abstraction and encapsulation.
 */
public abstract class User {

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

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = normalize(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = normalize(name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
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
