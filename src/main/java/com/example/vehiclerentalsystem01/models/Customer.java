package com.example.vehiclerentalsystem01.models;

public class Customer {
    private String id;
    private String name;
    private String email;
    private String username;
    private String password;
    private String nic;
    private String licenseNumber;
    private String phoneNumber;

    // Default Constructor (Crucial for preventing silent Tomcat crashes)
    public Customer() {}

    // Master 8-Argument Constructor
    public Customer(String id, String name, String email, String username, String password, String nic, String licenseNumber, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.nic = nic;
        this.licenseNumber = licenseNumber;
        this.phoneNumber = phoneNumber;
    }

    // ==========================================
    // GETTERS
    // ==========================================
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getNic() { return nic; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getPhoneNumber() { return phoneNumber; }

    // ==========================================
    // SETTERS (Added!)
    // ==========================================
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setNic(String nic) { this.nic = nic; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}