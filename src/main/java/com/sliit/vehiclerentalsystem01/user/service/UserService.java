package com.sliit.vehiclerentalsystem01.user.service;

import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.entity.Staff;
import com.sliit.vehiclerentalsystem01.user.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * CONTRACT for all user operations (CRUD + login).
 * <p>
 * WHY an interface?
 * - Controllers inject {@code UserService}, not {@code UserServiceImpl} — dependency inversion (OOP).
 * - Tests or future code could swap implementation; Spring wires UserServiceImpl at runtime.
 * <p>
 * Implementations must read from {@code users.txt} on each call so behaviour matches the file on disk.
 */
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
