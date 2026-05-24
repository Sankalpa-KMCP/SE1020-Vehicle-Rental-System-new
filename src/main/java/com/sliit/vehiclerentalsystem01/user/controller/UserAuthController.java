package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.core.service.ImageStorageService;
import com.sliit.vehiclerentalsystem01.core.security.SecurityUtils;
import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.entity.Staff;
import com.sliit.vehiclerentalsystem01.user.entity.User;
import com.sliit.vehiclerentalsystem01.user.service.MasterAdminPendingService;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/auth")
public class UserAuthController {

    private static final int RATE_LIMIT_REQUESTS = 3;
    private static final long RATE_LIMIT_WINDOW_MS = 10 * 60 * 1000L; // 10 minutes

    private final UserService userService;
    private final MasterAdminPendingService pendingService;
    private final ImageStorageService imageStorageService;

    // Simple in-memory rate limiter: IP -> { count, window start }
    private final Map<String, RateBucket> rateLimitMap = new ConcurrentHashMap<>();

    public UserAuthController(UserService userService, MasterAdminPendingService pendingService,
                              ImageStorageService imageStorageService) {
        this.userService = userService;
        this.pendingService = pendingService;
        this.imageStorageService = imageStorageService;
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
                        return "redirect:/auth/login?error=staff_account";
                    }
                    establishAuthenticatedSession(session, u);
                    redirectAttributes.addFlashAttribute("message", "Welcome, " + sessionUsername(u));
                    return "redirect:/dashboard";
                })
                .orElseGet(() -> "redirect:/auth/login?error=1");
    }

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

    @PostMapping("/staff-access-request")
    public String staffAccessRequest(
            @RequestParam("new_username") String newUsername,
            @RequestParam("new_password") String newPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Rate limiting
        String ip = resolveClientIp(session);
        if (!tryAcquire(ip)) {
            redirectAttributes.addFlashAttribute("error", "Too many requests. Please wait 10 minutes.");
            return "redirect:/auth/staff-login?error=rate_limited";
        }

        if (newUsername == null || newUsername.isBlank() || newPassword == null || newPassword.isBlank()) {
            return "redirect:/auth/staff-login?error=unknown_action";
        }
        String user = newUsername.trim();
        if (userService.findByUsername(user).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "That username is already taken.");
            return "redirect:/auth/staff-login?error=username_taken";
        }
        try {
            pendingService.appendStaffAccessRequestLine(user);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Could not save your request. Try again later.");
            return "redirect:/auth/staff-login?error=invalid";
        }
        redirectAttributes.addFlashAttribute("message", "Request recorded for Master Admin review.");
        return "redirect:/auth/staff-login?success=requested";
    }

    private void establishAuthenticatedSession(HttpSession session, User u) {
        session.invalidate();
        org.springframework.mock.web.MockHttpSession newSession = new org.springframework.mock.web.MockHttpSession();
        session = newSession;

        session.setAttribute("username", sessionUsername(u));
        session.setAttribute("name", u.getName());
        session.setAttribute("avatarUrl", u.getAvatarUrl());

        if (u instanceof Customer) {
            session.setAttribute("customerLoggedIn", Boolean.TRUE);
            session.setAttribute("authRole", "ROLE_CUSTOMER");
        } else if (u instanceof Staff s) {
            session.setAttribute("adminLoggedIn", s.isAdminRole());
            session.setAttribute("staffLoggedIn", !s.isAdminRole());
            session.setAttribute("authRole", s.isAdminRole() ? "ROLE_ADMIN" : "ROLE_STAFF");
            // Master admin = ROLE_ADMIN with no Staff object (system account)
        }
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
            @RequestParam(value = "avatarImage", required = false) MultipartFile avatarImage,
            RedirectAttributes redirectAttributes) {
        try {
            Customer customer = new Customer();
            customer.setId("C-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
            customer.setName(name != null ? name.trim() : "");
            customer.setUsername(username);
            // Always encode new registrations with BCrypt
            customer.setPassword(userService.encodeNewPassword(password));
            customer.setEmail(email);
            customer.setPhoneNumber(phone);
            customer.setLicenseNumber(blankToNa(license));
            customer.setNic(blankToNa(nic));
            imageStorageService.attachAvatar(customer, avatarImage);
            userService.save(customer);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/auth/login?success=registered";
        } catch (ValidationException ex) {
            return "redirect:/auth/register?error=" + ex.getMessage();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/auth/register?error=true";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/auth/register?error=true";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        String target = "/auth/login?logout=true";
        if (session != null) {
            Boolean adminLoggedIn = (Boolean) session.getAttribute("adminLoggedIn");
            Boolean staffLoggedIn = (Boolean) session.getAttribute("staffLoggedIn");
            if (Boolean.TRUE.equals(adminLoggedIn) || Boolean.TRUE.equals(staffLoggedIn)) {
                target = "/auth/staff-login?logout=true";
            }
            session.invalidate();
        }
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully");
        return "redirect:" + target;
    }

    @PostMapping(params = "action=logout")
    public String logoutPost(HttpSession session, RedirectAttributes redirectAttributes) {
        return logout(session, redirectAttributes);
    }

    private static String blankToNa(String v) {
        if (v == null || v.trim().isEmpty()) {
            return "N/A";
        }
        return v.trim();
    }

    // --- Rate limiting -------------------------------------------------------

    private boolean tryAcquire(String ip) {
        long now = Instant.now().toEpochMilli();
        rateLimitMap.compute(ip, (key, bucket) -> {
            if (bucket == null || now - bucket.windowStart > RATE_LIMIT_WINDOW_MS) {
                return new RateBucket(1, now);
            }
            if (bucket.count >= RATE_LIMIT_REQUESTS) {
                return bucket; // keep blocking
            }
            return new RateBucket(bucket.count + 1, bucket.windowStart);
        });
        RateBucket b = rateLimitMap.get(ip);
        return b != null && (Instant.now().toEpochMilli() - b.windowStart <= RATE_LIMIT_WINDOW_MS)
                && b.count <= RATE_LIMIT_REQUESTS;
    }

    private String resolveClientIp(HttpSession session) {
        // In production, check X-Forwarded-For header behind a proxy
        return "unknown";
    }

    private record RateBucket(int count, long windowStart) {}
}
