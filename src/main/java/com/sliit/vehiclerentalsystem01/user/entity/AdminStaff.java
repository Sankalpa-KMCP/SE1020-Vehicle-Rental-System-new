package com.sliit.vehiclerentalsystem01.user.entity;

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
