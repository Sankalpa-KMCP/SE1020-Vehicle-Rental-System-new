package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Simple Thymeleaf admin landing (replaces {@code admin-home.jsp}).
 */
@Controller
@RequestMapping("/user")
public class UserAdminHomeController {

    @GetMapping("/admin-home")
    public String adminHomeLegacy(HttpSession session) {
        return adminDashboard(session);
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }
        return "user/admin-dashboard";
    }
}
