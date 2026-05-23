package com.sliit.vehiclerentalsystem01.user.entity;

// Support staff with limited access
public class SupportStaff extends Staff {

    public SupportStaff() {
        super();
    }

    public SupportStaff(String staffId, String name, String email, String phone, String username, String password) {
        super(staffId, name, email, phone, username, password, "Support");
    }

    @Override
    public String getUserRole() {
        return "STAFF";
    }

    @Override
    public String getAccessLevel() {
        return "LIMITED_ACCESS";
    }
}
