package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.user.model.AdminStaff;
import com.sliit.vehiclerentalsystem01.user.model.Customer;
import com.sliit.vehiclerentalsystem01.user.model.Staff;
import com.sliit.vehiclerentalsystem01.user.model.SupportStaff;
import com.sliit.vehiclerentalsystem01.user.model.User;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/user-list";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("mode", "create");
        model.addAttribute("entityId", null);
        model.addAttribute("userType", "CUSTOMER");
        model.addAttribute("customer", new Customer());
        model.addAttribute("staff", new SupportStaff());
        return "user/user-form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        return userService.findById(id)
                .map(u -> {
                    model.addAttribute("user", u);
                    return "user/user-detail";
                })
                .orElse("redirect:/users");
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
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
            @RequestParam String userType,
            @ModelAttribute Customer customer,
            @ModelAttribute SupportStaff staff) {
        User toSave = "STAFF".equalsIgnoreCase(userType) ? staff : customer;
        userService.save(toSave);
        return "redirect:/users";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable String id,
            @RequestParam String userType,
            @ModelAttribute Customer customer,
            @ModelAttribute SupportStaff staff) {
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
    public String delete(@PathVariable String id) {
        userService.deleteById(id);
        return "redirect:/users";
    }
}
