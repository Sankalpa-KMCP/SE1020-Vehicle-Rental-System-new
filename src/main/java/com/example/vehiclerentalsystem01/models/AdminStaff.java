package com.example.vehiclerentalsystem01.models;

/**
 * OOP Concept: Inheritance.
 * Inherits all attributes from the base Staff class.
 */
public class AdminStaff extends Staff {
    public AdminStaff(String staffId, String name, String email, String phone, String username, String password) {
        // Passing all 7 fields to the parent constructor
        super(staffId, name, email, phone, username, password, "Admin");
    }

    /**
     * OOP Concept: Polymorphism.
     */
    @Override
    public String getAccessLevel() {
        return "FULL_ACCESS";
    }
}