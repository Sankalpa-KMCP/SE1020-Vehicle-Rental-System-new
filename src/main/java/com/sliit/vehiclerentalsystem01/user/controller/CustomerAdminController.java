package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.core.service.ImageStorageService;
import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.core.security.SecurityUtils;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * CUSTOMER management at /customers — directory, search, admin edit, and customer self-service.
 * <p>
 * HOW actions work: ?action= on GET (view_directory, edit, delete, search, editSelf) and action= on POST.
 * WHY editSelf? Logged-in customers update their own row without admin session — still uses userService.save().
 * WHY ReservationService injected? Search shows bookings for a customer; delete rule is in UserServiceImpl.
 */
@Controller
@RequestMapping("/customers")
public class CustomerAdminController {

    private final UserService userService;
    private final ReservationService reservationService;
    private final ImageStorageService imageStorageService;

    public CustomerAdminController(UserService userService, ReservationService reservationService,
                                   ImageStorageService imageStorageService) {
        this.userService = userService;
        this.reservationService = reservationService;
        this.imageStorageService = imageStorageService;
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim();
    }

    private static String normalizeOptionalDoc(String value) {
        String trimmed = normalized(value);
        return trimmed.isEmpty() ? "N/A" : trimmed;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static boolean adminSession(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
    }

    /** READ stats for customer-directory (total / verified / unverified) */
    private void loadDashboard(Model model) {
        List<Customer> allCustomers = userService.findAllCustomers();
        if (allCustomers == null) {
            allCustomers = new ArrayList<>();
        }
        int verifiedGuests = 0;
        for (Customer customer : allCustomers) {
            if (customer.isVerified()) {
                verifiedGuests++;
            }
        }
        model.addAttribute("customerList", allCustomers);
        model.addAttribute("totalGuests", allCustomers.size());
        model.addAttribute("verifiedGuests", verifiedGuests);
        model.addAttribute("unverifiedGuests", allCustomers.size() - verifiedGuests);
    }

    /** GET: directory, search, edit, delete, or customer self-edit (editSelf) */
    @GetMapping
    public String handleGet(
            @RequestParam(required = false, defaultValue = "view_directory") String action,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String searchUsername,
            HttpServletRequest request,
            HttpSession session,
            Model model) {
        try {
            if ("editSelf".equals(action)) {
                if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.CUSTOMER_LOGGED_IN))) {
                    return "redirect:/auth/login";
                }
                Customer loggedIn = userService.findByUsername(normalized((String) session.getAttribute(UserSessionKeys.USERNAME)))
                        .filter(u -> u instanceof Customer)
                        .map(u -> (Customer) u)
                        .orElse(null);
                if (loggedIn == null) {
                    return "redirect:/auth/login?error=profile_not_found";
                }
                model.addAttribute("customer", loggedIn);
                model.addAttribute("isCustomerMode", true);
                return "user/edit-customer";
            }

            if (!adminSession(session)) {
                return "redirect:/auth/staff-login";
            }

            if ("delete".equals(action)) {
                String customerId = normalized(id);
                if (!customerId.isEmpty()) {
                    try {
                        userService.deleteById(customerId);
                    } catch (IllegalStateException ex) {
                        return "redirect:/customers?action=view_directory&error=" + ex.getMessage();
                    }
                }
                return "redirect:/customers?action=view_directory&msg=deleted";
            }

            if ("edit".equals(action)) {
                Customer existing = userService.findById(normalized(id))
                        .filter(u -> u instanceof Customer)
                        .map(u -> (Customer) u)
                        .orElse(null);
                if (existing == null) {
                    return "redirect:/customers?action=view_directory&error=not_found";
                }
                model.addAttribute("customer", existing);
                model.addAttribute("isCustomerMode", false);
                return "user/edit-customer";
            }

            loadDashboard(model);
            model.addAttribute("customerBookings", java.util.Collections.emptyList());

            if ("search".equals(action)) {
                String username = normalized(searchUsername);
                if (!username.isEmpty()) {
                    userService.findByUsername(username)
                            .filter(u -> u instanceof Customer)
                            .map(u -> (Customer) u)
                            .ifPresent(found -> {
                                model.addAttribute("foundCustomer", found);
                                model.addAttribute("customerBookings",
                                        reservationService.findBookingsLinkedToCustomer(found));
                            });
                }
            }

            boolean jspOpenCreate = "missing_fields".equals(request.getParameter("error"))
                    || "username_taken".equals(request.getParameter("error"));
            boolean jspInitialSearch = model.containsAttribute("foundCustomer") || "search".equals(action);
            model.addAttribute("jspOpenCreateForm", jspOpenCreate);
            model.addAttribute("jspInitialSearchView", jspInitialSearch);

            return "user/customer-directory";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading customer data: " + e.getMessage());
            return "user/error";
        }
    }

    /** POST: create, update, or delete customer (action param) */
    @PostMapping
    public String handlePost(@RequestParam Map<String, String> params, HttpSession session,
                             @RequestParam(value = "avatarImage", required = false) MultipartFile avatarImage) {
        String action = normalized(params.get("action"));
        if (action.isEmpty()) {
            action = "register";
        }

        if ("delete".equals(action)) {
            if (!adminSession(session)) {
                return "redirect:/auth/staff-login";
            }
            String id = normalized(params.get("id"));
            if (!id.isEmpty()) {
                try {
                    userService.deleteById(id);
                } catch (IllegalStateException ex) {
                    return "redirect:/customers?action=view_directory&error=" + ex.getMessage();
                }
            }
            return "redirect:/customers?action=view_directory&msg=deleted";
        }

        if ("create".equals(action) && !adminSession(session)) {
            return "redirect:/auth/staff-login";
        }

        String id = normalized(params.get("id"));
        String name = normalized(params.get("name"));
        String email = normalized(params.get("email"));
        String username = normalized(params.get("username"));
        String password = normalized(params.get("password"));
        String phone = normalized(params.get("phone"));
        String nic = normalizeOptionalDoc(params.get("nic"));
        String license = normalizeOptionalDoc(params.get("license"));

        if (isBlank(name) || isBlank(email) || isBlank(username) || isBlank(password) || isBlank(phone)) {
            if ("update".equals(action)) {
                boolean isAdmin = session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
                return isAdmin
                        ? "redirect:/customers?action=edit&id=" + id + "&error=missing_fields"
                        : "redirect:/customers?action=editSelf&error=missing_fields";
            }
            if ("create".equals(action)) {
                return "redirect:/customers?action=view_directory&error=missing_fields";
            }
            return "redirect:/auth/register?error=missing_fields";
        }

        try {
            if ("update".equals(action)) {
                Customer existing = userService.findById(id)
                        .filter(u -> u instanceof Customer)
                        .map(u -> (Customer) u)
                        .orElseThrow();
                existing.setUsername(username);
                existing.setPassword(SecurityUtils.hashPasswordIfNeeded(password));
                existing.setEmail(email);
                existing.setPhone(phone);
                existing.setNic(nic);
                existing.setDriverLicense(license);
                existing.setFullName(name);
                imageStorageService.attachAvatar(existing, avatarImage);
                userService.save(existing);
                boolean isAdmin = session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
                return isAdmin
                        ? "redirect:/customers?action=view_directory&msg=updated"
                        : "redirect:/customer/home?msg=updated";
            }

            String newId = "C-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            Customer created = new Customer();
            created.setId(newId);
            created.setUsername(username);
            created.setPassword(SecurityUtils.hashPasswordIfNeeded(password));
            created.setEmail(email);
            created.setPhone(phone);
            created.setNic(nic);
            created.setDriverLicense(license);
            created.setFullName(name);
            imageStorageService.attachAvatar(created, avatarImage);
            userService.save(created);

            if ("create".equals(action) || (session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN)))) {
                return "redirect:/customers?action=view_directory&msg=created";
            }
            return "redirect:/auth/login?success=registered";
        } catch (ValidationException ex) {
            if ("update".equals(action)) {
                boolean isAdmin = session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
                return isAdmin
                        ? "redirect:/customers?action=edit&id=" + id + "&error=" + ex.getMessage()
                        : "redirect:/customers?action=editSelf&error=" + ex.getMessage();
            }
            if ("create".equals(action)) {
                return "redirect:/customers?action=view_directory&error=" + ex.getMessage();
            }
            return "redirect:/auth/register?error=" + ex.getMessage();
        }
    }
}
