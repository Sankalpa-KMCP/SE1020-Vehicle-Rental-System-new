package com.example.vehiclerentalsystem01.models;

/**
 * OOP Concept: Abstraction.
 * Abstract base class for all employees. Cannot be instantiated directly.
 */
public abstract class Staff {
    // OOP Concept: Encapsulation (Private fields)
    private String staffId;
    private String name;
    private String email;
    private String phone;
    private String username; // NEW FIELD
    private String password; // NEW FIELD
    private String role;

    public Staff(String staffId, String name, String email, String phone, String username, String password, String role) {
        this.staffId = staffId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // OOP Concept: Abstraction (Child classes MUST define this)
    public abstract String getAccessLevel();

    // Getters and Setters
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}