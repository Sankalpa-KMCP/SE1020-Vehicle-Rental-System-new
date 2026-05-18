package com.sliit.vehiclerentalsystem01;

import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Role landing page (replaces legacy Spring Security {@code DashboardController}).
 */
@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session == null) {
            return "redirect:/auth/login";
        }
        Object role = session.getAttribute(UserSessionKeys.AUTH_ROLE);
        if (role == null) {
            redirectAttributes.addFlashAttribute("error", "Please sign in");
            return "redirect:/auth/login";
        }
        String r = role.toString();
        if ("ROLE_ADMIN".equals(r)) {
            return "redirect:/user/admin-dashboard";
        }
        if ("ROLE_STAFF".equals(r)) {
            return "redirect:/staff?action=list";
        }
        if ("ROLE_CUSTOMER".equals(r)) {
            return "redirect:/customer/home";
        }
        return "redirect:/";
    }
}
