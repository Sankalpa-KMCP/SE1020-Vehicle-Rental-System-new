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

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isSameValue(String left, String right) {
        return normalize(left).equalsIgnoreCase(normalize(right));
    }

    private boolean writeAllCustomers(List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Customer c : customers) {
                writer.write(c.toCSV());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // AUTHENTICATION: Check if Customer exists
    // ==========================================
    // FIXED: Now returns a Customer object instead of a boolean
    public Customer validateCustomer(String username, String password) {
        File file = new File(FILE_PATH);
        if (!file.exists()) return null; // No customers registered yet! Return null.

        String normalizedUsername = normalize(username);
        String normalizedPassword = normalize(password);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Customer customer = Customer.fromCSV(line);
                if (customer != null &&
                        isSameValue(customer.getUsername(), normalizedUsername) &&
                        customer.getPassword().equals(normalizedPassword)) {
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
    public boolean saveCustomer(Customer c) {
        if (c == null || getCustomerByUsername(c.getUsername()) != null) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(c.toCSV());
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
            if (isSameValue(c.getId(), id)) return c;
        }
        return null;
    }

    /**
     * READ: Retrieve a single customer by username
     * OOP: Extracted from controller logic into DAO layer (better encapsulation)
     */
    public Customer getCustomerByUsername(String username) {
        for (Customer c : getAllCustomers()) {
            if (isSameValue(c.getUsername(), username)) return c;
        }
        return null;
    }

    /**
     * UPDATE: Modify an existing customer
     * OOP: Uses Customer.toCSV() for consistent serialization
     */
    public boolean updateCustomer(Customer updatedCustomer) {
        if (updatedCustomer == null || normalize(updatedCustomer.getId()).isEmpty()) {
            return false;
        }

        List<Customer> customers = getAllCustomers();
        boolean updated = false;

        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            if (isSameValue(c.getId(), updatedCustomer.getId())) {
                customers.set(i, updatedCustomer);
                updated = true;
                break;
            }
        }

        return updated && writeAllCustomers(customers);
    }

    /**
     * DELETE: Remove a customer by ID
     * OOP: Uses Customer.toCSV() for consistent serialization
     */
    public boolean deleteCustomer(String id) {
        if (normalize(id).isEmpty()) {
            return false;
        }

        List<Customer> customers = getAllCustomers();
        boolean removed = false;

        List<Customer> filtered = new ArrayList<>();
        for (Customer c : customers) {
            if (isSameValue(c.getId(), id)) {
                removed = true;
                continue;
            }
            filtered.add(c);
        }

        return removed && writeAllCustomers(filtered);
    }
}