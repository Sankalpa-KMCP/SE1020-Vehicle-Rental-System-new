package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.core.service.ImageStorageService;
import com.sliit.vehiclerentalsystem01.core.security.SecurityUtils;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.user.entity.AdminStaff;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.entity.Staff;
import com.sliit.vehiclerentalsystem01.user.entity.SupportStaff;
import com.sliit.vehiclerentalsystem01.user.entity.User;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ADMIN CRUD UI at /users — one form for both Customer and SupportStaff types.
 * <p>
 * HOW it maps to layers:
 * GET pages load data via userService.findAll/findById → Thymeleaf templates under templates/user/.
 * POST create/update hash password (SecurityUtils), attach optional MultipartFile avatar,
 * then userService.save() → UserServiceImpl → UserRepository → users.txt.
 * <p>
 * WHY separate from /customers and /staff?
 * Historical JSP migration: three admin entry points exist; all call the same UserService methods.
 * <p>
 * DELETE may fail with customer_in_use if ReservationService finds bookings for that customer.
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ImageStorageService imageStorageService;

    public UserController(UserService userService, ImageStorageService imageStorageService) {
        this.userService = userService;
        this.imageStorageService = imageStorageService;
    }

    /** Gate: only admin staff session may use /users CRUD */
    private static boolean admin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
    }

    /** READ list — all users */
    @GetMapping
    public String list(HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        model.addAttribute("users", userService.findAll());
        return "user/user-list";
    }

    @GetMapping("/new")
    public String newUserForm(HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        model.addAttribute("mode", "create");
        model.addAttribute("entityId", null);
        model.addAttribute("userType", "CUSTOMER");
        model.addAttribute("customer", new Customer());
        model.addAttribute("staff", new SupportStaff());
        return "user/user-form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        return userService.findById(id)
                .map(u -> {
                    model.addAttribute("user", u);
                    return "user/user-detail";
                })
                .orElse("redirect:/users");
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        return userService.findById(id)
                .map(u -> {
                    model.addAttribute("mode", "edit");
                    model.addAttribute("entityId", id);
                    model.addAttribute("userType", u.getUserRole());
                    if (u instanceof Customer c) {
                        model.addAttribute("customer", c);
                        model.addAttribute("staff", new SupportStaff());
                    } else if (u instanceof Staff s) {
                        model.addAttribute("customer", new Customer());
                        model.addAttribute("staff", s);
                    } else {
                        model.addAttribute("customer", new Customer());
                        model.addAttribute("staff", new SupportStaff());
                    }
                    return "user/user-form";
                })
                .orElse("redirect:/users");
    }

    /** CREATE customer or staff from user-form */
    @PostMapping
    public String create(
            HttpSession session,
            @RequestParam String userType,
            @ModelAttribute Customer customer,
            @ModelAttribute SupportStaff staff,
            @RequestParam(value = "avatarImage", required = false) MultipartFile avatarImage) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        User toSave = "STAFF".equalsIgnoreCase(userType) ? staff : customer;
        if (toSave instanceof Customer c) {
            c.setPassword(SecurityUtils.hashPasswordIfNeeded(c.getPassword()));
        } else if (toSave instanceof Staff s) {
            s.setPassword(SecurityUtils.hashPasswordIfNeeded(s.getPassword()));
        }
        imageStorageService.attachAvatar(toSave, avatarImage);
        try {
            userService.save(toSave);
        } catch (ValidationException ex) {
            return "redirect:/users/new?error=" + ex.getMessage();
        }
        return "redirect:/users";
    }

    /** UPDATE existing user by id */
    @PostMapping("/{id}")
    public String update(
            @PathVariable String id,
            HttpSession session,
            @RequestParam String userType,
            @ModelAttribute Customer customer,
            @ModelAttribute SupportStaff staff,
            @RequestParam(value = "avatarImage", required = false) MultipartFile avatarImage) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        if ("STAFF".equalsIgnoreCase(userType)) {
            User existing = userService.findById(id).orElseThrow();
            String hashedPassword = SecurityUtils.hashPasswordIfNeeded(staff.getPassword());
            Staff replacement = existing instanceof AdminStaff
                    ? new AdminStaff(id, staff.getName(), staff.getEmail(),
                    staff.getPhone(), staff.getUsername(), hashedPassword)
                    : new SupportStaff(id, staff.getName(), staff.getEmail(), staff.getPhone(), staff.getUsername(),
                    hashedPassword);
            replacement.setProfilePicture(existing.getProfilePicture());
            imageStorageService.attachAvatar(replacement, avatarImage);
            try {
                userService.save(replacement);
            } catch (ValidationException ex) {
                return "redirect:/users/" + id + "/edit?error=" + ex.getMessage();
            }
            return "redirect:/users";
        }
        customer.setId(id);
        customer.setPassword(SecurityUtils.hashPasswordIfNeeded(customer.getPassword()));
        userService.findById(id).ifPresent(existing -> customer.setProfilePicture(existing.getProfilePicture()));
        imageStorageService.attachAvatar(customer, avatarImage);
        try {
            userService.save(customer);
        } catch (ValidationException ex) {
            return "redirect:/users/" + id + "/edit?error=" + ex.getMessage();
        }
        return "redirect:/users";
    }

    /** DELETE user (customers with bookings may throw customer_in_use) */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, HttpSession session, RedirectAttributes ra) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        try {
            userService.deleteById(id);
        } catch (IllegalStateException ex) {
            ra.addAttribute("error", ex.getMessage());
        }
        return "redirect:/users";
    }
}
