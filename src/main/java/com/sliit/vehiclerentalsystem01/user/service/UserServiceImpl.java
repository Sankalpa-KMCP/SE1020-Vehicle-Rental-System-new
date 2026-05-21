package com.sliit.vehiclerentalsystem01.user.service;

import com.sliit.vehiclerentalsystem01.core.security.SecurityUtils;
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

/**
 * BUSINESS LAYER for user management.
 * <p>
 * WHY this class exists (between controller and repository):
 * - Controllers should not parse CSV or check "username already taken" — that belongs here.
 * - Repository should not know about bookings or password rules — only load/save lines.
 * <p>
 * HOW a typical request flows:
 * Browser → UserController → UserServiceImpl.save() → validate → UserRepository.saveUser() → users.txt
 * <p>
 * Important behaviours implemented here (not in the repository):
 * - Email/username uniqueness before save
 * - Login checks SHA-256 hash (or legacy plain password in old data files)
 * - Customers cannot be deleted if they still have reservations (needs ReservationService)
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Pattern EMAIL_FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserRepository userRepository;
    private final ReservationService reservationService; // used to block customer delete when bookings exist

    /**
     * ReservationService is {@link Lazy} to break the cycle: ReservationServiceImpl itself depends
     * on UserService.
     */
    public UserServiceImpl(UserRepository userRepository, @Lazy ReservationService reservationService) {
        this.userRepository = userRepository;
        this.reservationService = reservationService;
    }

    /*
     * READ operations
     * WHY we call findAll() even for findById: there is no index or SQL WHERE clause — we load the
     * whole file each time. That also means login always sees the latest disk contents (good if you
     * edit users.txt manually while the app runs).
     */

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

    /**
     * HOW: Scan all users; match Customer/Staff username field (case-insensitive).
     * WHY: Used for login lookup, duplicate checks, and loading the logged-in customer profile.
     * Note: authenticate() also allows customers to log in with email — this method is username-only.
     */
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

    /**
     * CREATE or UPDATE (same code path).
     * HOW: Assign random UUID id only when missing; run validateBeforeSave(); delegate to repository upsert.
     * WHY UUID: Controllers often generate ids like C-xxxxx or EMP-xxxx, but this is a safety net for
     * any code path that forgot to set id before save.
     */
    @Override
    public void save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }
        validateBeforeSave(user);
        userRepository.saveUser(user);
    }

    /**
     * DELETE with a business rule for customers.
     * WHY block delete when bookings exist: bookings.txt stores customerName (username). Deleting the
     * user would leave orphan bookings and break reports. Staff have no such rule.
     * HOW: ReservationService.findBookingsLinkedToCustomer() — if non-empty, throw customer_in_use;
     * controllers redirect with error message. Otherwise repository removes the line from users.txt.
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

        userRepository.deleteUserById(id);
    }

    /**
     * LOGIN validation (read-only — does not change users.txt).
     * HOW:
     * 1. Reload all users from disk (fresh credentials).
     * 2. Hash the typed password with SHA-256 and compare to stored value.
     * 3. Customer: match username OR email; Staff: username only.
     * WHY hash on every attempt: stored password is a 64-char hex string; we hash input once and compare.
     * Legacy plain-text passwords in old sample files still work via passwordsMatch().
     * On success, UserAuthController sets HttpSession flags — not done in this class.
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
        String hashedInput = SecurityUtils.hashPassword(p);
        return userRepository.findAll().stream()
                .filter(user -> {
                    String storedUser = "";
                    String storedPass = "";
                    if (user instanceof Customer c) {
                        storedUser = c.getUsername() == null ? "" : c.getUsername().trim();
                        String storedEmail = c.getEmail() == null ? "" : c.getEmail().trim();
                        storedPass = c.getPassword() == null ? "" : c.getPassword().trim();
                        boolean idMatches = u.equalsIgnoreCase(storedUser) || u.equalsIgnoreCase(storedEmail);
                        return idMatches && passwordsMatch(p, hashedInput, storedPass);
                    } else if (user instanceof Staff s) {
                        storedUser = s.getUsername() == null ? "" : s.getUsername().trim();
                        storedPass = s.getPassword() == null ? "" : s.getPassword().trim();
                    }
                    return u.equalsIgnoreCase(storedUser) && passwordsMatch(p, hashedInput, storedPass);
                })
                .findFirst();
    }

    /**
     * WHY validate before touching the file: avoid half-written invalid rows and give the UI clear
     * error codes (invalid_email, username_taken, email_already_registered).
     * HOW: Scan current findAll() list and compare to the user being saved (same id allowed — that is an update).
     */
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

    /** Supports stored SHA-256 hash or legacy plain-text password in users.txt */
    private static boolean passwordsMatch(String plainPassword, String hashedInput, String storedPass) {
        if (SecurityUtils.isSha256Hex(storedPass)) {
            return hashedInput.equalsIgnoreCase(storedPass);
        }
        return plainPassword.equals(storedPass);
    }
}
