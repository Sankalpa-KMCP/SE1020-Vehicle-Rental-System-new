package com.sliit.vehiclerentalsystem01.user.entity;

/**
 * Customer IS-A {@link User} with rental-specific fields.
 */
public class Customer extends User {

    private String username;
    private String password;
    private String nic;
    private String licenseNumber;
    private String phoneNumber;

    public Customer() {
        super();
    }

    public Customer(String id, String name, String email, String username, String password,
                    String nic, String licenseNumber, String phoneNumber) {
        super(id, name, email);
        this.username = normalize(username);
        this.password = normalize(password);
        this.nic = normalizeDocument(nic);
        this.licenseNumber = normalizeDocument(licenseNumber);
        this.phoneNumber = normalize(phoneNumber);
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = normalize(username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = normalize(password);
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = normalizeDocument(nic);
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = normalizeDocument(licenseNumber);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = normalize(phoneNumber);
    }

    private static String normalizeDocument(String value) {
        String cleaned = normalize(value);
        return cleaned.isEmpty() ? "N/A" : cleaned;
    }

    private static String safeCsvValue(String value) {
        return value == null ? "" : value.replace(",", " ").trim();
    }

    @Override
    public String getUserRole() {
        return "CUSTOMER";
    }

    /**
     * CSV for file storage: ID,Name,Email,Username,Password,NIC,License,Phone
     */
    public String toCSV() {
        return safeCsvValue(getId()) + "," + safeCsvValue(getName()) + "," + safeCsvValue(getEmail()) + ","
                + safeCsvValue(username) + "," + safeCsvValue(password) + "," + safeCsvValue(nic) + ","
                + safeCsvValue(licenseNumber) + "," + safeCsvValue(phoneNumber);
    }

    public static Customer fromCSV(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] data = line.split(",", -1);
        if (data.length == 8) {
            return new Customer(data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(),
                    data[4].trim(), data[5].trim(), data[6].trim(), data[7].trim());
        }
        return null;
    }

    /** Alias for {@link #getName()} used by admin forms. */
    public String getFullName() {
        return getName();
    }

    public void setFullName(String fullName) {
        setName(fullName == null ? "" : fullName);
    }

    public String getPhone() {
        return phoneNumber;
    }

    public void setPhone(String phone) {
        setPhoneNumber(phone);
    }

    public String getDriverLicense() {
        return licenseNumber;
    }

    public void setDriverLicense(String license) {
        setLicenseNumber(license);
    }

    public boolean isVerified() {
        return isDocumentAvailable(nic) && isDocumentAvailable(licenseNumber);
    }

    public boolean isDocumentAvailable(String value) {
        String cleaned = normalize(value);
        return !cleaned.isEmpty() && !"N/A".equalsIgnoreCase(cleaned);
    }
}
