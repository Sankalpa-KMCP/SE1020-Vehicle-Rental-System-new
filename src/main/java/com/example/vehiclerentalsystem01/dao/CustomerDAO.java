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

    public Customer validateCustomer(String username, String password) {
        File file = new File(FILE_PATH);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length == 8) {
                    String savedUser = data[3].trim();
                    String savedPass = data[4].trim();

                    if (savedUser.equals(username) && savedPass.equals(password)) {
                        return new Customer(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void saveCustomer(Customer c) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(c.getId() + "," + c.getName() + "," + c.getEmail() + "," + c.getUsername() + "," +
                    c.getPassword() + "," + c.getNic() + "," + c.getLicenseNumber() + "," + c.getPhoneNumber());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return customers;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 8) {
                    customers.add(new Customer(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return customers;
    }

    public Customer getCustomerById(String id) {
        for (Customer c : getAllCustomers()) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    public Customer getCustomerByUsername(String username) {
        for (Customer c : getAllCustomers()) {
            if (c.getUsername().equals(username)) {
                return c;
            }
        }
        return null;
    }

    public void updateCustomer(Customer updatedCustomer) {
        List<Customer> customers = getAllCustomers();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Customer c : customers) {
                if (c.getId().equals(updatedCustomer.getId())) {
                    writer.write(updatedCustomer.getId() + "," + updatedCustomer.getName() + "," + updatedCustomer.getEmail() + "," +
                            updatedCustomer.getUsername() + "," + updatedCustomer.getPassword() + "," + updatedCustomer.getNic() + "," +
                            updatedCustomer.getLicenseNumber() + "," + updatedCustomer.getPhoneNumber());
                } else {
                    writer.write(c.getId() + "," + c.getName() + "," + c.getEmail() + "," + c.getUsername() + "," +
                            c.getPassword() + "," + c.getNic() + "," + c.getLicenseNumber() + "," + c.getPhoneNumber());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomer(String id) {
        List<Customer> customers = getAllCustomers();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Customer c : customers) {
                if (!c.getId().equals(id)) {
                    writer.write(c.getId() + "," + c.getName() + "," + c.getEmail() + "," + c.getUsername() + "," +
                            c.getPassword() + "," + c.getNic() + "," + c.getLicenseNumber() + "," + c.getPhoneNumber());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}