package com.sliit.vehiclerentalsystem01.user.service;

import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.entity.Staff;
import com.sliit.vehiclerentalsystem01.user.entity.User;

import java.util.List;
import java.util.Optional;

// User operations: find, save, delete, login
public interface UserService {

    List<User> findAll();

    Optional<User> findById(String id);

    Optional<User> findByUsername(String username);

    List<Customer> findAllCustomers();

    List<Staff> findAllStaff();

    void save(User user);

    void deleteById(String id);

    // Check username/password and return the user if valid
    Optional<User> authenticate(String username, String password);

    // Encode a new password with BCrypt
    String encodeNewPassword(String plainPassword);
}
