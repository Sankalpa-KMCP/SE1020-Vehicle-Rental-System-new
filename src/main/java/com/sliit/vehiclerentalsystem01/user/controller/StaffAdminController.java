package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.core.service.ImageStorageService;
import com.sliit.vehiclerentalsystem01.core.security.SecurityUtils;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.user.entity.AdminStaff;
import com.sliit.vehiclerentalsystem01.user.entity.Staff;
import com.sliit.vehiclerentalsystem01.user.entity.SupportStaff;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

// Add, edit, and delete staff at /staff
@Controller
@RequestMapping("/staff")
public class StaffAdminController {

    private final UserService userService;
    private final ImageStorageService imageStorageService;

    public StaffAdminController(UserService userService, ImageStorageService imageStorageService) {
        this.userService = userService;
        this.imageStorageService = imageStorageService;
    }

    // Show staff list or edit form
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
        long adminStaffCount = staffList.stream().filter(Staff::isAdminRole).count();
        model.addAttribute("adminStaffCount", adminStaffCount);
        model.addAttribute("supportStaffCount", staffList.size() - adminStaffCount);
        return "user/staff-dashboard";
    }

    // Save or delete staff
    @PostMapping
    public String handlePost(HttpSession session, @RequestParam Map<String, String> params,
                             @RequestParam(value = "avatarImage", required = false) MultipartFile avatarImage) {
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
        String password = SecurityUtils.hashPasswordIfNeeded(nv(params.get("password")));

        boolean admin = "Admin".equalsIgnoreCase(role);

        if ("update".equals(action)) {
            Staff existing = userService.findById(id)
                    .filter(u -> u instanceof Staff)
                    .map(u -> (Staff) u)
                    .orElseThrow();
            Staff updated = admin
                    ? new AdminStaff(id, nv(name), nv(email), nv(phone), nv(username), password)
                    : new SupportStaff(id, nv(name), nv(email), nv(phone), nv(username), password);
            updated.setProfilePicture(existing.getProfilePicture());
            imageStorageService.attachAvatar(updated, avatarImage);
            try {
                userService.save(updated);
            } catch (ValidationException ex) {
                return "redirect:/staff?action=edit&id=" + id + "&error=" + ex.getMessage();
            }
        } else {
            String manualId = params.get("manualId");
            String newId = manualId != null && !manualId.trim().isEmpty()
                    ? manualId.trim()
                    : "EMP-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            Staff created = admin
                    ? new AdminStaff(newId, nv(name), nv(email), nv(phone), nv(username), password)
                    : new SupportStaff(newId, nv(name), nv(email), nv(phone), nv(username), password);
            imageStorageService.attachAvatar(created, avatarImage);
            try {
                userService.save(created);
            } catch (ValidationException ex) {
                return "redirect:/staff?action=add&error=" + ex.getMessage();
            }
        }

        return "redirect:/staff?action=list";
    }

    private static String nv(String s) {
        return s == null ? "" : s.trim();
    }
}
