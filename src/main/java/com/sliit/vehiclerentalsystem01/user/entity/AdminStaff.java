package com.sliit.vehiclerentalsystem01.user.entity;

/**
 * Administrator staff — can access customer/staff CRUD pages (session ADMIN_LOGGED_IN).
 * WHY separate class? Demonstrates multi-level inheritance and polymorphic getAccessLevel().
 */
public class AdminStaff extends Staff {

    public AdminStaff() {
        super();
    }

    public AdminStaff(String staffId, String name, String email, String phone, String username, String password) {
        super(staffId, name, email, phone, username, password, "Admin");
    }

    @Override
    public String getUserRole() {
        return "STAFF";
    }

    @Override
    public String getAccessLevel() {
        return "FULL_ACCESS";
    }
}
