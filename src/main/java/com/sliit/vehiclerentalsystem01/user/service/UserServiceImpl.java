package com.sliit.vehiclerentalsystem01.user.service;

import com.sliit.vehiclerentalsystem01.core.config.CachingConfig;
import com.sliit.vehiclerentalsystem01.core.security.SecurityUtils;
import com.sliit.vehiclerentalsystem01.core.validation.CustomerFieldValidator;
import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.entity.Staff;
import com.sliit.vehiclerentalsystem01.user.entity.User;
import com.sliit.vehiclerentalsystem01.user.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           @Lazy ReservationService reservationService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.reservationService = reservationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Cacheable(CachingConfig.USERS_CACHE)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return findAll().stream().filter(u -> id.equals(u.getId())).findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        String key = username.trim();
        return findAll().stream()
                .filter(user -> {
                    if (user instanceof Customer c) {
                        return key.equalsIgnoreCase(c.getUsername());
                    }
                    if (user instanceof Staff s) {
                        return key.equalsIgnoreCase(s.getUsername());
                    }
                    return false;
                })
                .findFirst();
    }

    @Override
    public List<Customer> findAllCustomers() {
        return findAll().stream()
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Staff> findAllStaff() {
        return findAll().stream()
                .filter(u -> u instanceof Staff)
                .map(u -> (Staff) u)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    @CacheEvict(value = CachingConfig.USERS_CACHE, allEntries = true)
    public void save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }
        validateBeforeSave(user);
        userRepository.saveUser(user);
    }

    @Override
    @CacheEvict(value = CachingConfig.USERS_CACHE, allEntries = true)
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
     * Verify credentials. On first successful login of a legacy SHA-256 user,
     * the password is upgraded to BCrypt and persisted so future logins use
     * the stronger hash.
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
        Optional<User> found = findAll().stream()
                .filter(user -> matchesUsername(user, u))
                .findFirst();

        if (found.isEmpty()) {
            return Optional.empty();
        }

        User user = found.get();
        String storedPass = user.getPassword() == null ? "" : user.getPassword().trim();

        if (SecurityUtils.isBCrypt(storedPass)) {
            if (!passwordEncoder.matches(p, storedPass)) {
                return Optional.empty();
            }
            // Already BCrypt — login succeeds
            return Optional.of(user);
        }

        // Legacy SHA-256 or plain-text — verify and upgrade
        if (!legacyVerify(p, storedPass)) {
            return Optional.empty();
        }

        // Upgrade to BCrypt
        String bcryptHash = passwordEncoder.encode(p);
        user.setPassword(bcryptHash);
        userRepository.saveUser(user);

        return Optional.of(user);
    }

    private boolean matchesUsername(User user, String username) {
        if (user instanceof Customer c) {
            String storedUser = c.getUsername() == null ? "" : c.getUsername().trim();
            String storedEmail = c.getEmail() == null ? "" : c.getEmail().trim();
            return username.equalsIgnoreCase(storedUser) || username.equalsIgnoreCase(storedEmail);
        }
        if (user instanceof Staff s) {
            String storedUser = s.getUsername() == null ? "" : s.getUsername().trim();
            return username.equalsIgnoreCase(storedUser);
        }
        return false;
    }

    private boolean legacyVerify(String plain, String stored) {
        if (SecurityUtils.isSha256Hex(stored)) {
            return SecurityUtils.sha256Hex(plain).equalsIgnoreCase(stored);
        }
        return plain.equals(stored);
    }

    /**
     * Encode a new (pre-registration) password with BCrypt.
     * Call this for all new Customer and Staff registrations.
     */
    public String encodeNewPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        return passwordEncoder.encode(plainPassword);
    }

    private void validateBeforeSave(User user) {
        String email = user.getEmail() == null ? "" : user.getEmail().trim();
        if (email.isEmpty() || !EMAIL_FORMAT.matcher(email).matches()) {
            throw new ValidationException("invalid_email");
        }

        String username = resolveUsername(user);
        if (!username.isBlank()) {
            boolean usernameTaken = findAll().stream()
                    .filter(u -> !user.getId().equals(u.getId()))
                    .anyMatch(u -> username.equalsIgnoreCase(resolveUsername(u)));
            if (usernameTaken) {
                throw new ValidationException("username_taken");
            }
        }

        if (user instanceof Customer customer) {
            boolean emailTaken = findAll().stream()
                    .filter(u -> u instanceof Customer)
                    .filter(u -> !user.getId().equals(u.getId()))
                    .anyMatch(u -> email.equalsIgnoreCase(u.getEmail() == null ? "" : u.getEmail().trim()));
            if (emailTaken) {
                throw new ValidationException("email_already_registered");
            }
            CustomerFieldValidator.validate(customer);
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
