package com.example.vehiclerentalsystem01.models;

public class SupportStaff extends Staff {
    public SupportStaff(String staffId, String name, String email, String phone, String username, String password) {
        // Passing all 7 fields to the parent constructor
        super(staffId, name, email, phone, username, password, "Support");
    }

    @Override
    public String getAccessLevel() {
        return "LIMITED_ACCESS";
    }
}