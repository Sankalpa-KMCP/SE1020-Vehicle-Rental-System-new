package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.user.model.AdminStaff;
import com.sliit.vehiclerentalsystem01.user.model.Customer;
import com.sliit.vehiclerentalsystem01.user.model.Staff;
import com.sliit.vehiclerentalsystem01.user.model.SupportStaff;
import com.sliit.vehiclerentalsystem01.user.model.User;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Generic /users CRUD. All endpoints require an {@code ADMIN_LOGGED_IN} session.
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private static boolean admin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
    }

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

    @PostMapping
    public String create(
            HttpSession session,
            @RequestParam String userType,
            @ModelAttribute Customer customer,
            @ModelAttribute SupportStaff staff) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        User toSave = "STAFF".equalsIgnoreCase(userType) ? staff : customer;
        userService.save(toSave);
        return "redirect:/users";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable String id,
            HttpSession session,
            @RequestParam String userType,
            @ModelAttribute Customer customer,
            @ModelAttribute SupportStaff staff) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        if ("STAFF".equalsIgnoreCase(userType)) {
            User existing = userService.findById(id).orElseThrow();
            Staff replacement = existing instanceof AdminStaff
                    ? new AdminStaff(id, staff.getName(), staff.getEmail(),
                    staff.getPhone(), staff.getUsername(), staff.getPassword())
                    : new SupportStaff(id, staff.getName(), staff.getEmail(), staff.getPhone(), staff.getUsername(),
                    staff.getPassword());
            userService.save(replacement);
            return "redirect:/users";
        }
        customer.setId(id);
        userService.save(customer);
        return "redirect:/users";
    }

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
