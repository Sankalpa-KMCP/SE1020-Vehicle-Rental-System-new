package com.sliit.vehiclerentalsystem01.user.entity;

/**
 * Abstract staff member; {@code staffId} is stored as {@link User#getId()}.
 */
public abstract class Staff extends User {

    private String phone;
    private String username;
    private String password;
    private String role;

    public Staff() {
        super();
    }

    public Staff(String staffId, String name, String email, String phone,
                 String username, String password, String role) {
        super(staffId, name, email);
        this.phone = phone == null ? "" : phone.trim();
        this.username = normalize(username);
        this.password = normalize(password);
        this.role = role == null ? "" : role.trim();
    }

    // Getters and Setters
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? "" : phone.trim();
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role == null ? "" : role.trim();
    }

    public abstract String getAccessLevel();

    /** Legacy alias: same as user id. */
    public String getStaffId() {
        return getId();
    }

    public boolean isAdminRole() {
        return "Admin".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    /** Template compatibility with older dashboards. */
    public String getFullName() {
        return getName();
    }

    public void setFullName(String fullName) {
        setName(fullName == null ? "" : fullName);
    }

    /** Legacy UI used {@code ADMIN}/{@code SUPPORT}; file stores {@code Admin}/{@code Support}. */
    public String getStaffRole() {
        return isAdminRole() ? "ADMIN" : "SUPPORT";
    }

    public void setStaffRole(String staffRole) {
        if (staffRole == null) {
            return;
        }
        if ("ADMIN".equalsIgnoreCase(staffRole) || "Admin".equalsIgnoreCase(staffRole)) {
            setRole("Admin");
        } else {
            setRole("Support");
        }
    }

    private static String safeCsvValue(String value) {
        return value == null ? "" : value.replace(",", " ").trim();
    }

    /**
     * CSV for file storage: ID,Name,Email,Phone,Username,Password,Role,ProfilePicture
     */
    @Override
    public String toCSV() {
        String roleValue = getRole();
        if (roleValue == null || roleValue.isEmpty()) {
            roleValue = "Support";
        }
        return safeCsvValue(getId()) + "," + safeCsvValue(getName()) + "," + safeCsvValue(getEmail()) + ","
                + safeCsvValue(phone) + "," + safeCsvValue(username) + "," + safeCsvValue(password) + ","
                + safeCsvValue(roleValue) + "," + safeCsvValue(getProfilePicture());
    }

    public static Staff fromCSV(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] data = line.split(",", -1);
        if (data.length == 7) {
            Staff staff = buildStaffFromFields(data, 6);
            if (staff != null) {
                staff.setProfilePicture("N/A");
            }
            return staff;
        }
        if (data.length == 8) {
            Staff staff = buildStaffFromFields(data, 6);
            if (staff != null) {
                staff.setProfilePicture(data[7].trim());
            }
            return staff;
        }
        return null;
    }

    private static Staff buildStaffFromFields(String[] data, int roleIndex) {
        String id = data[0].trim();
        String name = data[1].trim();
        String email = data[2].trim();
        String phone = data[3].trim();
        String username = data[4].trim();
        String password = data[5].trim();
        String role = data[roleIndex].trim();
        if ("Admin".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            return new AdminStaff(id, name, email, phone, username, password);
        }
        return new SupportStaff(id, name, email, phone, username, password);
    }
}
