package com.example.vehiclerentalsystem01.models;

// OOP Concept: Abstraction (Abstract class cannot be instantiated directly)
public abstract class User {
    // OOP Concept: Encapsulation (Private fields hidden from outside classes)
    private String id;
    private String name;
    private String email;

    protected static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    public User(String id, String name, String email) {
        this.id = normalize(id);
        this.name = normalize(name);
        this.email = normalize(email);
    }

    // OOP Concept: Abstraction (Abstract method to be implemented by child classes)
    public abstract String getUserRole();

    // Getters and Setters (Encapsulation)
    public String getId() { return id; }
    public void setId(String id) { this.id = normalize(id); }
    public String getName() { return name; }
    public void setName(String name) { this.name = normalize(name); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = normalize(email); }
}