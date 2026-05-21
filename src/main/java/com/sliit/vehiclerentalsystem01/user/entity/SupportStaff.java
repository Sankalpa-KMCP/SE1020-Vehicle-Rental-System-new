package com.sliit.vehiclerentalsystem01.user.entity;

/**
 * Support desk staff — limited session (STAFF_LOGGED_IN). Often created when master admin
 * approves a row in staff_access_requests.txt (see MasterAdminPendingService).
 */
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
