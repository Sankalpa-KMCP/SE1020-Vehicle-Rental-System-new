package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.user.model.Customer;
import com.sliit.vehiclerentalsystem01.user.model.Staff;
import com.sliit.vehiclerentalsystem01.user.model.User;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class UserAuthController {

    private final UserService userService;

    public UserAuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "user/login";
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
                    clearAuthFlags(session);
                    session.setAttribute(UserSessionKeys.USERNAME, sessionUsername(u));
                    if (u instanceof Customer) {
                        session.setAttribute(UserSessionKeys.CUSTOMER_LOGGED_IN, Boolean.TRUE);
                        session.setAttribute(UserSessionKeys.AUTH_ROLE, "ROLE_CUSTOMER");
                    } else if (u instanceof Staff s) {
                        if (s.isAdminRole()) {
                            session.setAttribute(UserSessionKeys.ADMIN_LOGGED_IN, Boolean.TRUE);
                            session.setAttribute(UserSessionKeys.AUTH_ROLE, "ROLE_ADMIN");
                        } else {
                            session.setAttribute(UserSessionKeys.STAFF_LOGGED_IN, Boolean.TRUE);
                            session.setAttribute(UserSessionKeys.AUTH_ROLE, "ROLE_STAFF");
                        }
                    }
                    redirectAttributes.addFlashAttribute("message", "Welcome, " + sessionUsername(u));
                    return "redirect:/dashboard";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Invalid username or password");
                    return "redirect:/auth/login?error=1";
                });
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
        if (session != null) {
            session.invalidate();
        }
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully");
        return "redirect:/auth/login?logout=true";
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
    }

    private static String blankToNa(String v) {
        if (v == null || v.trim().isEmpty()) {
            return "N/A";
        }
        return v.trim();
    }
}
