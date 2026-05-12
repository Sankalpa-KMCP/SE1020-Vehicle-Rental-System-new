package com.sliit.vehiclerentalsystem01.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Abstract staff member; {@code staffId} is stored as {@link User#getId()}.
 */
@Getter
@Setter
@NoArgsConstructor(callSuper = true)
public abstract class Staff extends User {

    private String phone;
    private String username;
    private String password;
    private String role;

    public Staff(String staffId, String name, String email, String phone,
                 String username, String password, String role) {
        super(staffId, name, email);
        this.phone = phone == null ? "" : phone.trim();
        this.username = normalize(username);
        this.password = normalize(password);
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
}
