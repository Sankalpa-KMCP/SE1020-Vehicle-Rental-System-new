package com.sliit.vehiclerentalsystem01.user.service;

import com.sliit.vehiclerentalsystem01.user.model.Customer;
import com.sliit.vehiclerentalsystem01.user.model.Staff;
import com.sliit.vehiclerentalsystem01.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(String id);

    Optional<User> findByUsername(String username);

    List<Customer> findAllCustomers();

    List<Staff> findAllStaff();

    void save(User user);

    void deleteById(String id);

    /**
     * Password match after trim on both inputs and stored credentials.
     * For customers, the first argument may be username or email (case-insensitive); staff match username only.
     * File-backed implementations should read the persistent user store (e.g. {@code users.txt}) on each call.
     */
    Optional<User> authenticate(String username, String password);
}
