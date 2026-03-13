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

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getNic() { return nic; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getPhoneNumber() { return phoneNumber; }
}