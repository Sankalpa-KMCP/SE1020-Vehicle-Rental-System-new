package com.sliit.vehiclerentalsystem01.user.service;

import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.entity.Staff;
import com.sliit.vehiclerentalsystem01.user.entity.User;
import com.sliit.vehiclerentalsystem01.user.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Pattern EMAIL_FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserRepository userRepository;
    private final ReservationService reservationService;

    /**
     * ReservationService is {@link Lazy} to break the cycle: ReservationServiceImpl itself depends
     * on UserService.
     */
    public UserServiceImpl(UserRepository userRepository, @Lazy ReservationService reservationService) {
        this.userRepository = userRepository;
        this.reservationService = reservationService;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findAll().stream().filter(u -> id.equals(u.getId())).findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        String key = username.trim();
        return userRepository.findAll().stream()
                .filter(u -> {
                    if (u instanceof Customer c) {
                        return key.equalsIgnoreCase(c.getUsername());
                    }
                    if (u instanceof Staff s) {
                        return key.equalsIgnoreCase(s.getUsername());
                    }
                    return false;
                })
                .findFirst();
    }

    @Override
    public List<Customer> findAllCustomers() {
        return userRepository.findAll().stream()
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Staff> findAllStaff() {
        return userRepository.findAll().stream()
                .filter(u -> u instanceof Staff)
                .map(u -> (Staff) u)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }
        validateBeforeSave(user);
        List<User> users = new ArrayList<>(userRepository.findAll());
        users.removeIf(u -> user.getId().equals(u.getId()));
        users.add(user);
        userRepository.saveAll(users);
    }

    /**
     * Refuses to delete a customer whose username still has linked bookings. Throws
     * {@link IllegalStateException} with code {@code customer_in_use} so controllers can surface
     * a friendly error. Staff deletions are unaffected.
     */
    @Override
    public void deleteById(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        findById(id)
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .ifPresent(c -> {
                    if (!reservationService.findBookingsLinkedToCustomer(c).isEmpty()) {
                        throw new IllegalStateException("customer_in_use");
                    }
                });

        List<User> users = userRepository.findAll().stream()
                .filter(u -> !id.equals(u.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        userRepository.saveAll(users);
    }

    /**
     * Validates credentials against the latest contents of the user store on disk.
     * Customers may sign in with username or email (case-insensitive); staff match username only.
     */
    @Override
    public Optional<User> authenticate(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        String u = username.trim();
        String p = password.trim();
        if (u.isEmpty() || p.isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findAll().stream()
                .filter(user -> {
                    String storedUser = "";
                    String storedPass = "";
                    if (user instanceof Customer c) {
                        storedUser = c.getUsername() == null ? "" : c.getUsername().trim();
                        String storedEmail = c.getEmail() == null ? "" : c.getEmail().trim();
                        storedPass = c.getPassword() == null ? "" : c.getPassword().trim();
                        boolean idMatches = u.equalsIgnoreCase(storedUser) || u.equalsIgnoreCase(storedEmail);
                        return idMatches && p.equals(storedPass);
                    } else if (user instanceof Staff s) {
                        storedUser = s.getUsername() == null ? "" : s.getUsername().trim();
                        storedPass = s.getPassword() == null ? "" : s.getPassword().trim();
                    }
                    return u.equalsIgnoreCase(storedUser) && p.equals(storedPass);
                })
                .findFirst();
    }

    private void validateBeforeSave(User user) {
        String email = user.getEmail() == null ? "" : user.getEmail().trim();
        if (email.isEmpty() || !EMAIL_FORMAT.matcher(email).matches()) {
            throw new ValidationException("invalid_email");
        }

        String username = resolveUsername(user);
        if (!username.isBlank()) {
            boolean usernameTaken = userRepository.findAll().stream()
                    .filter(u -> !user.getId().equals(u.getId()))
                    .anyMatch(u -> username.equalsIgnoreCase(resolveUsername(u)));
            if (usernameTaken) {
                throw new ValidationException("username_taken");
            }
        }

        if (user instanceof Customer) {
            boolean emailTaken = userRepository.findAll().stream()
                    .filter(u -> u instanceof Customer)
                    .filter(u -> !user.getId().equals(u.getId()))
                    .anyMatch(u -> email.equalsIgnoreCase(u.getEmail() == null ? "" : u.getEmail().trim()));
            if (emailTaken) {
                throw new ValidationException("email_already_registered");
            }
        }
    }

    private static String resolveUsername(User user) {
        if (user instanceof Customer c) {
            return c.getUsername() == null ? "" : c.getUsername().trim();
        }
        if (user instanceof Staff s) {
            return s.getUsername() == null ? "" : s.getUsername().trim();
        }
        return "";
    }
}
