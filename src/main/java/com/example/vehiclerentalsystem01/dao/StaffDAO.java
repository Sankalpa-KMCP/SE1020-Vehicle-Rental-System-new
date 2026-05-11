package com.example.vehiclerentalsystem01.dao;

import com.example.vehiclerentalsystem01.models.AdminStaff;
import com.example.vehiclerentalsystem01.models.Staff;
import com.example.vehiclerentalsystem01.models.SupportStaff;
import com.example.vehiclerentalsystem01.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StaffDAO {
    private static final String FILENAME = "staff.txt";
    
    @Autowired
    private FileStorageService fileStorageService;

    // ==========================================
    // AUTHENTICATION: Check Staff Login
    // ==========================================
    public Staff validateStaffLogin(String username, String password) {
        // We iterate through all staff to see if we find a match
        for (Staff s : getAllStaff()) {
            // ADDED .trim() to make it bulletproof against invisible spaces!
            if (s.getUsername().trim().equals(username.trim()) && s.getPassword().trim().equals(password.trim())) {
                return s; // Match found! Return the Staff object so we know their role.
            }
        }
        return null; // No match found
    }

    // CRUD: CREATE
    public void saveStaff(Staff staff) {
        List<String> lines = new ArrayList<>();
        // Write 7 fields: ID, Name, Email, Phone, Username, Password, Role
        lines.add(staff.getStaffId() + "," + staff.getName() + "," + staff.getEmail() + "," +
                staff.getPhone() + "," + staff.getUsername() + "," + staff.getPassword() + "," + staff.getRole());
        fileStorageService.writeToFile(FILENAME, lines, true);
    }

    // CRUD: READ
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        
        if (!fileStorageService.fileExists(FILENAME)) {
            return staffList;
        }

        List<String> lines = fileStorageService.readAllLines(FILENAME);
        for (String line : lines) {
            String[] data = line.split(",");
            // Expecting 7 fields now!
            if (data.length >= 7) {
                // ADDED .trim() to clean up any accidental spaces from Notepad
                String id = data[0].trim();
                String name = data[1].trim();
                String email = data[2].trim();
                String phone = data[3].trim();
                String uname = data[4].trim();
                String pass = data[5].trim();
                String role = data[6].trim();

                if (role.equalsIgnoreCase("Admin")) {
                    staffList.add(new AdminStaff(id, name, email, phone, uname, pass));
                } else {
                    staffList.add(new SupportStaff(id, name, email, phone, uname, pass));
                }
            }
        }
        return staffList;
    }

    public Staff getStaffById(String id) {
        for (Staff s : getAllStaff()) {
            if (s.getStaffId().equals(id)) return s;
        }
        return null;
    }

    // CRUD: UPDATE
    public void updateStaff(Staff updatedStaff) {
        List<Staff> staffList = getAllStaff();
        List<String> lines = new ArrayList<>();
        for (Staff s : staffList) {
            if (s.getStaffId().equals(updatedStaff.getStaffId())) {
                lines.add(updatedStaff.getStaffId() + "," + updatedStaff.getName() + "," + updatedStaff.getEmail() + "," +
                        updatedStaff.getPhone() + "," + updatedStaff.getUsername() + "," + updatedStaff.getPassword() + "," + updatedStaff.getRole());
            } else {
                lines.add(s.getStaffId() + "," + s.getName() + "," + s.getEmail() + "," +
                        s.getPhone() + "," + s.getUsername() + "," + s.getPassword() + "," + s.getRole());
            }
        }
        fileStorageService.writeToFile(FILENAME, lines, false);
    }

    // CRUD: DELETE
    public void deleteStaff(String id) {
        List<Staff> staffList = getAllStaff();
        List<String> lines = new ArrayList<>();
        for (Staff s : staffList) {
            if (!s.getStaffId().equals(id)) {
                lines.add(s.getStaffId() + "," + s.getName() + "," + s.getEmail() + "," +
                        s.getPhone() + "," + s.getUsername() + "," + s.getPassword() + "," + s.getRole());
            }
        }
        fileStorageService.writeToFile(FILENAME, lines, false);
    }
}
