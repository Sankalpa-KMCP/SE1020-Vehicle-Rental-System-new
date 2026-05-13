package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.user.model.Customer;
import com.sliit.vehiclerentalsystem01.user.model.Staff;
import com.sliit.vehiclerentalsystem01.user.model.User;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class UserAuthController {

    private static final String STAFF_REQUESTS_FILE = "staff_access_requests.txt";

    private final UserService userService;

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    public UserAuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "user/login";
    }

    @GetMapping("/staff-login")
    public String showStaffLoginPage() {
        return "user/staff-login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "auth/register";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        return userService.authenticate(username, password)
                .map(u -> {
                    if (u instanceof Staff) {
                        return "redirect:/auth/login?error=staff_portal";
                    }
                    establishAuthenticatedSession(session, u);
                    redirectAttributes.addFlashAttribute("message", "Welcome, " + sessionUsername(u));
                    return "redirect:/dashboard";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Invalid username or password");
                    return "redirect:/auth/login?error=1";
                });
    }

    /**
     * Staff portal sign-in: only {@link Staff} accounts may complete here; customers use {@code POST /auth/login}.
     */
    @PostMapping("/staff-login")
    public String staffLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        return userService.authenticate(username, password)
                .map(u -> {
                    if (u instanceof Customer) {
                        return "redirect:/auth/staff-login?error=customer_portal";
                    }
                    establishAuthenticatedSession(session, u);
                    redirectAttributes.addFlashAttribute("message", "Welcome, " + sessionUsername(u));
                    return "redirect:/dashboard";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Invalid username or password");
                    return "redirect:/auth/staff-login?error=invalid";
                });
    }

    /**
     * Records a pending staff access request (username only; password is never persisted).
     */
    @PostMapping("/staff-access-request")
    public String staffAccessRequest(
            @RequestParam("new_username") String newUsername,
            @RequestParam("new_password") String newPassword,
            RedirectAttributes redirectAttributes) {
        if (newUsername == null || newUsername.isBlank() || newPassword == null || newPassword.isBlank()) {
            return "redirect:/auth/staff-login?error=unknown_action";
        }
        String user = newUsername.trim();
        if (userService.findByUsername(user).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "That username is already taken.");
            return "redirect:/auth/staff-login?error=username_taken";
        }
        try {
            appendStaffAccessRequestLine(user);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Could not save your request. Try again later.");
            return "redirect:/auth/staff-login?error=invalid";
        }
        redirectAttributes.addFlashAttribute("message", "Request recorded for Master Admin review.");
        return "redirect:/auth/staff-login?success=requested";
    }

    private void appendStaffAccessRequestLine(String username) throws IOException {
        Path dir = Paths.get(dataDirectory).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        Path file = dir.resolve(STAFF_REQUESTS_FILE);
        String line = Instant.now().toString() + "," + username + System.lineSeparator();
        Files.writeString(file, line, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private void establishAuthenticatedSession(HttpSession session, User u) {
        clearAuthFlags(session);
        session.setAttribute(UserSessionKeys.USERNAME, sessionUsername(u));
        if (u instanceof Customer) {
            session.setAttribute(UserSessionKeys.CUSTOMER_LOGGED_IN, Boolean.TRUE);
            session.setAttribute(UserSessionKeys.AUTH_ROLE, "ROLE_CUSTOMER");
        } else if (u instanceof Staff s) {
            if (s.isAdminRole()) {
                session.setAttribute(UserSessionKeys.ADMIN_LOGGED_IN, Boolean.TRUE);
                session.setAttribute(UserSessionKeys.AUTH_ROLE, "ROLE_ADMIN");
                session.setAttribute(UserSessionKeys.IS_MASTER_ADMIN, isMasterAdminAccount(s));
            } else {
                session.setAttribute(UserSessionKeys.STAFF_LOGGED_IN, Boolean.TRUE);
                session.setAttribute(UserSessionKeys.AUTH_ROLE, "ROLE_STAFF");
            }
        }
    }

    /** Master console and highest-privilege UI (legacy usernames plus dedicated master account). */
    private static boolean isMasterAdminAccount(Staff s) {
        String u = s.getUsername();
        if (u == null || u.isBlank()) {
            return false;
        }
        u = u.trim();
        return "admin".equalsIgnoreCase(u) || "master1".equalsIgnoreCase(u);
    }

    private static String sessionUsername(User u) {
        if (u instanceof Customer c) {
            return c.getUsername();
        }
        if (u instanceof Staff s) {
            return s.getUsername();
        }
        return "";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String phone,
            @RequestParam(required = false) String nic,
            @RequestParam(required = false) String license,
            RedirectAttributes redirectAttributes) {
        try {
            if (userService.findByUsername(username).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/auth/register?error=username_taken";
            }
            Customer customer = new Customer();
            customer.setId("C-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
            customer.setName(name != null ? name.trim() : "");
            customer.setUsername(username);
            customer.setPassword(password);
            customer.setEmail(email);
            customer.setPhoneNumber(phone);
            customer.setLicenseNumber(blankToNa(license));
            customer.setNic(blankToNa(nic));
            userService.save(customer);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/auth/login?success=registered";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/auth/register?error=true";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        String target = "/auth/login?logout=true";
        if (session != null) {
            boolean staffSession = Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))
                    || Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.STAFF_LOGGED_IN));
            if (staffSession) {
                target = "/auth/staff-login?logout=true";
            }
            session.invalidate();
        }
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully");
        return "redirect:" + target;
    }

    /** Legacy admin dashboard POST sign-out ({@code action=logout}). */
    @PostMapping(params = "action=logout")
    public String logoutPost(HttpSession session, RedirectAttributes redirectAttributes) {
        return logout(session, redirectAttributes);
    }

    private static void clearAuthFlags(HttpSession session) {
        if (session == null) {
            return;
        }
        session.removeAttribute(UserSessionKeys.USERNAME);
        session.removeAttribute(UserSessionKeys.CUSTOMER_LOGGED_IN);
        session.removeAttribute(UserSessionKeys.ADMIN_LOGGED_IN);
        session.removeAttribute(UserSessionKeys.STAFF_LOGGED_IN);
        session.removeAttribute(UserSessionKeys.AUTH_ROLE);
        session.removeAttribute(UserSessionKeys.IS_MASTER_ADMIN);
    }

    private static String blankToNa(String v) {
        if (v == null || v.trim().isEmpty()) {
            return "N/A";
        }
        return v.trim();
    }
}
