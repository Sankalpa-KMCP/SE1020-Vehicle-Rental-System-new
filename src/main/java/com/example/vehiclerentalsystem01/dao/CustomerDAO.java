package com.example.vehiclerentalsystem01.dao;

import com.example.vehiclerentalsystem01.models.Customer;
import com.example.vehiclerentalsystem01.services.FileStorageService;
import com.example.vehiclerentalsystem01.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerDAO {
    private static final String FILENAME = "customers.txt";
    
    @Autowired
    private FileStorageService fileStorageService;

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isSameValue(String left, String right) {
        return normalize(left).equalsIgnoreCase(normalize(right));
    }

    private boolean writeAllCustomers(List<Customer> customers) {
        List<String> lines = new ArrayList<>();
        for (Customer c : customers) {
            lines.add(c.toCSV());
        }
        return fileStorageService.writeToFile(FILENAME, lines, false);
    }

    // ==========================================
    // AUTHENTICATION: Check if Customer exists
    // ==========================================
    // FIXED: Now returns a Customer object instead of a boolean
    public Customer validateCustomer(String username, String password) {
        if (!fileStorageService.fileExists(FILENAME)) {
            return null;
        }

        String normalizedUsername = normalize(username);
        String normalizedPassword = normalize(password);

        List<String> lines = fileStorageService.readAllLines(FILENAME);
        for (String line : lines) {
            Customer customer = Customer.fromCSV(line);
            if (customer != null &&
                    isSameValue(customer.getUsername(), normalizedUsername) &&
                    customer.getPassword().equals(normalizedPassword)) {
                return customer;
            }
        }
        return null;
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
            throw new BusinessException("Customer already exists or invalid data");
        }

        List<String> lines = new ArrayList<>();
        lines.add(c.toCSV());
        return fileStorageService.writeToFile(FILENAME, lines, true);
    }

    /**
     * READ: Retrieve all customers from file
     * OOP: Uses Customer.fromCSV() for deserialization
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        if (!fileStorageService.fileExists(FILENAME)) {
            return customers;
        }

        List<String> lines = fileStorageService.readAllLines(FILENAME);
        for (String line : lines) {
            Customer customer = Customer.fromCSV(line);
            if (customer != null) {
                customers.add(customer);
            }
        }
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
