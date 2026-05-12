package com.sliit.vehiclerentalsystem01.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(callSuper = true)
public class SupportStaff extends Staff {

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
