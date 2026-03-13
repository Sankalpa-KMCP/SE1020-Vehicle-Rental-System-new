package com.example.vehiclerentalsystem01.dao;

import com.example.vehiclerentalsystem01.models.AdminStaff;
import com.example.vehiclerentalsystem01.models.Staff;
import com.example.vehiclerentalsystem01.models.SupportStaff;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    private static final String FOLDER_PATH = System.getProperty("user.home") + File.separator + "RentalData";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "staff.txt";

    public StaffDAO() {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) folder.mkdirs();
    }

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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            // Write 7 fields: ID, Name, Email, Phone, Username, Password, Role
            writer.write(staff.getStaffId() + "," + staff.getName() + "," + staff.getEmail() + "," +
                    staff.getPhone() + "," + staff.getUsername() + "," + staff.getPassword() + "," + staff.getRole());
            writer.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // CRUD: READ
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        File file = new File(FILE_PATH);

        // DEBUGGING: Prints to your IntelliJ terminal so you can see if it finds the file!
        System.out.println("Checking file: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.out.println("FILE NOT FOUND: staff.txt does not exist here!");
            return staffList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Read line from file: " + line); // DEBUGGING

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
                } else {
                    System.out.println("SKIPPED LINE: Does not have 7 columns. Only has " + data.length);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Staff s : staffList) {
                if (s.getStaffId().equals(updatedStaff.getStaffId())) {
                    writer.write(updatedStaff.getStaffId() + "," + updatedStaff.getName() + "," + updatedStaff.getEmail() + "," +
                            updatedStaff.getPhone() + "," + updatedStaff.getUsername() + "," + updatedStaff.getPassword() + "," + updatedStaff.getRole());
                } else {
                    writer.write(s.getStaffId() + "," + s.getName() + "," + s.getEmail() + "," +
                            s.getPhone() + "," + s.getUsername() + "," + s.getPassword() + "," + s.getRole());
                }
                writer.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // CRUD: DELETE
    public void deleteStaff(String id) {
        List<Staff> staffList = getAllStaff();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Staff s : staffList) {
                if (!s.getStaffId().equals(id)) {
                    writer.write(s.getStaffId() + "," + s.getName() + "," + s.getEmail() + "," +
                            s.getPhone() + "," + s.getUsername() + "," + s.getPassword() + "," + s.getRole());
                    writer.newLine();
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}