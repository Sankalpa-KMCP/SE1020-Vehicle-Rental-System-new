package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.core.service.ImageStorageService;
import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private String sessionUsername(HttpSession session) {
        Object u = session != null ? session.getAttribute("username") : null;
        return u == null ? "" : u.toString().trim();
    }

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
                // Customers can edit their own profile
                String un = sessionUsername(session);
                if (un.isEmpty()) {
                    return "redirect:/auth/login";
                }
                Customer loggedIn = userService.findByUsername(un)
                        .filter(u -> u instanceof Customer)
                        .map(u -> (Customer) u)
                        .orElse(null);
                if (loggedIn == null) {
                    return "redirect:/auth/login?error=profile_not_found";
                }
                model.addAttribute("customer", loggedIn);
                model.addAttribute("isCustomerMode", true);
                String displayName = loggedIn.getName() != null && !loggedIn.getName().isBlank()
                        ? loggedIn.getName().trim()
                        : "@" + loggedIn.getUsername();
                model.addAttribute("displayName", displayName);
                model.addAttribute("activeNav", "profile");
                return "user/edit-customer";
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
            model.addAttribute("status", 500);
            model.addAttribute("errorTitle", "Customer Directory Error");
            model.addAttribute("errorMessage", "Error loading customer data: " + e.getMessage());
            model.addAttribute("pageTitle", "Error | DriveAway");
            return "error";
        }
    }

    @PostMapping
    public String handlePost(@RequestParam Map<String, String> params, HttpSession session,
                             @RequestParam(value = "avatarImage", required = false) MultipartFile avatarImage) {
        String action = normalized(params.get("action"));
        if (action.isEmpty()) {
            action = "register";
        }

        if ("delete".equals(action)) {
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

        String id = normalized(params.get("id"));
        String name = normalized(params.get("name"));
        String email = normalized(params.get("email"));
        String username = normalized(params.get("username"));
        String password = normalized(params.get("password"));
        String currentPassword = normalized(params.get("currentPassword"));
        String newPassword = normalized(params.get("newPassword"));
        String confirmPassword = normalized(params.get("confirmPassword"));
        String phone = normalized(params.get("phone"));
        String nic = normalizeOptionalDoc(params.get("nic"));
        String license = normalizeOptionalDoc(params.get("license"));

        boolean isCustomerSelfEdit = "update".equals(action) && id.equals(sessionUsername(session));

        if ("update".equals(action)) {
            if (isBlank(name) || isBlank(email) || isBlank(username) || isBlank(phone)) {
                return profileUpdateRedirect(isCustomerSelfEdit, id, "missing_fields");
            }
        } else if (isBlank(name) || isBlank(email) || isBlank(username) || isBlank(password) || isBlank(phone)) {
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
                        .orElse(null);

                if (existing == null) {
                    return "redirect:/auth/login?error=unauthorized";
                }

                // Customer self-edit: verify ownership
                if (isCustomerSelfEdit) {
                    // Already confirmed above via id.equals(sessionUsername)
                }

                String passwordError = applyPasswordChange(existing, isCustomerSelfEdit,
                        password, currentPassword, newPassword, confirmPassword);
                if (passwordError != null) {
                    return profileUpdateRedirect(isCustomerSelfEdit, id, passwordError);
                }

                existing.setUsername(username);
                existing.setEmail(email);
                existing.setPhone(phone);
                existing.setNic(nic);
                existing.setDriverLicense(license);
                existing.setFullName(name);
                imageStorageService.attachAvatar(existing, avatarImage);
                userService.save(existing);

                if (!isCustomerSelfEdit) {
                    return "redirect:/customers?action=view_directory&msg=updated";
                }
                return "redirect:/customer/home?msg=updated";
            }

            String newId = "C-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            Customer created = new Customer();
            created.setId(newId);
            created.setUsername(username);
            created.setPassword(userService.encodeNewPassword(password));
            created.setEmail(email);
            created.setPhone(phone);
            created.setNic(nic);
            created.setDriverLicense(license);
            created.setFullName(name);
            imageStorageService.attachAvatar(created, avatarImage);
            userService.save(created);

            if ("create".equals(action)) {
                return "redirect:/customers?action=view_directory&msg=created";
            }
            return "redirect:/auth/login?success=registered";
        } catch (ValidationException ex) {
            if ("update".equals(action)) {
                return profileUpdateRedirect(isCustomerSelfEdit, id, ex.getMessage());
            }
            if ("create".equals(action)) {
                return "redirect:/customers?action=view_directory&error=" + ex.getMessage();
            }
            return "redirect:/auth/register?error=" + ex.getMessage();
        }
    }

    private static String profileUpdateRedirect(boolean isCustomer, String id, String error) {
        if (isCustomer) {
            return "redirect:/customers?action=editSelf&error=" + error;
        }
        return "redirect:/customers?action=edit&id=" + id + "&error=" + error;
    }

    private String applyPasswordChange(Customer existing, boolean isCustomerSelfEdit,
                                         String adminPassword, String currentPassword,
                                         String newPassword, String confirmPassword) {
        if (isCustomerSelfEdit) {
            // Customer changing their own password
            if (isBlank(newPassword)) {
                return null;
            }
            if (isBlank(currentPassword)) {
                return "current_password_required";
            }
            if (!newPassword.equals(confirmPassword)) {
                return "password_mismatch";
            }
            if (newPassword.length() < 6) {
                return "password_too_short";
            }
            // Verify current password
            if (!userService.authenticate(existing.getUsername(), currentPassword).isPresent()) {
                return "wrong_current_password";
            }
            existing.setPassword(userService.encodeNewPassword(newPassword));
            return null;
        }

        // Admin setting/updating password
        if (!isBlank(adminPassword)) {
            existing.setPassword(userService.encodeNewPassword(adminPassword));
        }
        return null;
    }
}
