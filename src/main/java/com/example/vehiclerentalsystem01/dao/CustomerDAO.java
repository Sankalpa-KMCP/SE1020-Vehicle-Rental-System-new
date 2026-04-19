package com.example.vehiclerentalsystem01.dao;

import com.example.vehiclerentalsystem01.models.Customer;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private static final String FOLDER_PATH = System.getProperty("user.home") + File.separator + "RentalData";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "customers.txt";

    public CustomerDAO() {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) folder.mkdirs();
    }

    // ==========================================
    // AUTHENTICATION: Check if Customer exists
    // ==========================================
    // FIXED: Now returns a Customer object instead of a boolean
    public Customer validateCustomer(String username, String password) {
        File file = new File(FILE_PATH);
        if (!file.exists()) return null; // No customers registered yet! Return null.

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Customer customer = Customer.fromCSV(line);
                if (customer != null && customer.getUsername().equals(username) &&
                    customer.getPassword().equals(password)) {
                    return customer;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // No match found, return null
    }

    // ==========================================
    // CRUD OPERATIONS
    // ==========================================

    /**
     * CREATE: Save a new customer to the file
     * OOP: Uses polymorphism - delegates serialization to Customer.toCSV()
     */
    public void saveCustomer(Customer c) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(c.toCSV());
            writer.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * READ: Retrieve all customers from file
     * OOP: Uses Customer.fromCSV() for deserialization
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return customers;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Customer customer = Customer.fromCSV(line);
                if (customer != null) {
                    customers.add(customer);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return customers;
    }

    /**
     * READ: Retrieve a single customer by ID
     */
    public Customer getCustomerById(String id) {
        for (Customer c : getAllCustomers()) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    /**
     * READ: Retrieve a single customer by username
     * OOP: Extracted from controller logic into DAO layer (better encapsulation)
     */
    public Customer getCustomerByUsername(String username) {
        for (Customer c : getAllCustomers()) {
            if (c.getUsername().equalsIgnoreCase(username)) return c;
        }
        return null;
    }

    /**
     * UPDATE: Modify an existing customer
     * OOP: Uses Customer.toCSV() for consistent serialization
     */
    public void updateCustomer(Customer updatedCustomer) {
        List<Customer> customers = getAllCustomers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Customer c : customers) {
                if (c.getId().equals(updatedCustomer.getId())) {
                    writer.write(updatedCustomer.toCSV());
                } else {
                    writer.write(c.toCSV());
                }
                writer.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * DELETE: Remove a customer by ID
     * OOP: Uses Customer.toCSV() for consistent serialization
     */
    public void deleteCustomer(String id) {
        List<Customer> customers = getAllCustomers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Customer c : customers) {
                if (!c.getId().equals(id)) {
                    writer.write(c.toCSV());
                    writer.newLine();
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}