package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.user.model.AdminStaff;
import com.sliit.vehiclerentalsystem01.user.model.Staff;
import com.sliit.vehiclerentalsystem01.user.model.SupportStaff;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

/**
 * Staff admin flows under {@code /staff}, backed by {@link UserService}.
 */
@Controller
@RequestMapping("/staff")
public class StaffAdminController {

    private final UserService userService;

    public StaffAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String handleGet(
            HttpSession session,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String id,
            Model model) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        if ("delete".equals(action) && id != null && !id.isBlank()) {
            userService.deleteById(id.trim());
            return "redirect:/staff?action=list";
        }

        if ("edit".equals(action)) {
            Staff staff = userService.findById(id)
                    .filter(u -> u instanceof Staff)
                    .map(u -> (Staff) u)
                    .orElse(null);
            if (staff == null) {
                return "redirect:/staff?action=list";
            }
            model.addAttribute("staff", staff);
            return "user/edit-staff";
        }

        java.util.List<Staff> staffList = userService.findAllStaff();
        model.addAttribute("staffList", staffList);
        model.addAttribute("staffCount", staffList.size());
        long adminStaffCount = staffList.stream().filter(Staff::isAdmin).count();
        model.addAttribute("adminStaffCount", adminStaffCount);
        model.addAttribute("supportStaffCount", staffList.size() - adminStaffCount);
        return "user/staff-dashboard";
    }

    @PostMapping
    public String handlePost(HttpSession session, @RequestParam Map<String, String> params) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        String action = params.get("action");
        if ("delete".equals(action)) {
            String deleteId = params.get("id");
            if (deleteId != null && !deleteId.isBlank()) {
                userService.deleteById(deleteId.trim());
            }
            return "redirect:/staff?action=list";
        }
        String role = params.get("role");
        String id = params.get("id");
        String name = params.get("name");
        String email = params.get("email");
        String phone = params.get("phone");
        String username = params.get("username");
        String password = params.get("password");

        boolean admin = "Admin".equalsIgnoreCase(role);

        if ("update".equals(action)) {
            Staff existing = userService.findById(id)
                    .filter(u -> u instanceof Staff)
                    .map(u -> (Staff) u)
                    .orElseThrow();
            Staff updated = admin
                    ? new AdminStaff(id, nv(name), nv(email), nv(phone), nv(username), nv(password))
                    : new SupportStaff(id, nv(name), nv(email), nv(phone), nv(username), nv(password));
            userService.save(updated);
        } else {
            String manualId = params.get("manualId");
            String newId = manualId != null && !manualId.trim().isEmpty()
                    ? manualId.trim()
                    : "EMP-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            Staff created = admin
                    ? new AdminStaff(newId, nv(name), nv(email), nv(phone), nv(username), nv(password))
                    : new SupportStaff(newId, nv(name), nv(email), nv(phone), nv(username), nv(password));
            userService.save(created);
        }

        return "redirect:/staff?action=list";
    }

    private static String nv(String s) {
        return s == null ? "" : s.trim();
    }
}
