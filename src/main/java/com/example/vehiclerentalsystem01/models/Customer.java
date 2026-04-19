package com.example.vehiclerentalsystem01.models;

/**
 * OOP Concept: Inheritance
 * The Customer class extends the User abstract class, demonstrating the IS-A relationship.
 * A Customer IS-A User with additional rental-specific fields.
 */
public class Customer extends User {
    // OOP Concept: Encapsulation (Customer-specific fields)
    private String username;
    private String password;
    private String nic;
    private String licenseNumber;
    private String phoneNumber;

    // Default Constructor (Crucial for Tomcat/JSP instantiation)
    public Customer() {
        super("", "", ""); // Initialize parent with default values
    }

    // Master Constructor with 8 arguments
    public Customer(String id, String name, String email, String username, String password, String nic, String licenseNumber, String phoneNumber) {
        super(id, name, email); // Call parent constructor with common fields
        this.username = username;
        this.password = password;
        this.nic = nic;
        this.licenseNumber = licenseNumber;
        this.phoneNumber = phoneNumber;
    }

    // ==========================================
    // OOP: Implement Abstract Method
    // ==========================================
    @Override
    public String getUserRole() {
        return "CUSTOMER";
    }

    // ==========================================
    // GETTERS (Customer-specific fields)
    // ==========================================
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getNic() { return nic; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getPhoneNumber() { return phoneNumber; }

    // ==========================================
    // SETTERS (Customer-specific fields)
    // ==========================================
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setNic(String nic) { this.nic = nic; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // ==========================================
    // OOP: Data Serialization (Encapsulation)
    // ==========================================
    /**
     * Converts Customer object to CSV string for file storage
     * Format: ID,Name,Email,Username,Password,NIC,License,Phone
     */
    public String toCSV() {
        return getId() + "," + getName() + "," + getEmail() + "," + username + "," +
                password + "," + nic + "," + licenseNumber + "," + phoneNumber;
    }

    /**
     * Parses a CSV line and creates a Customer object
     * OOP: Static factory method for object creation
     */
    public static Customer fromCSV(String line) {
        String[] data = line.split(",");
        if (data.length == 8) {
            return new Customer(data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(),
                    data[4].trim(), data[5].trim(), data[6].trim(), data[7].trim());
        }
        return null;
    }

    /**
     * OOP: Helper method to check if customer has complete verification documents
     */
    public boolean isVerified() {
        return nic != null && !nic.isEmpty() && !nic.equalsIgnoreCase("N/A") &&
               licenseNumber != null && !licenseNumber.isEmpty() && !licenseNumber.equalsIgnoreCase("N/A");
    }
}