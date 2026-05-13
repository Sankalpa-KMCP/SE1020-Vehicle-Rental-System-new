package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.user.service.MasterAdminPendingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Master admin console (legacy {@code master-console.jsp}): read-only pending list.
 */
@Controller
@RequestMapping("/user")
public class MasterConsoleController {

    private final MasterAdminPendingService pendingService;

    public MasterConsoleController(MasterAdminPendingService pendingService) {
        this.pendingService = pendingService;
    }

    private static boolean master(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.IS_MASTER_ADMIN));
    }

    @GetMapping("/master-console")
    public String masterConsole(HttpSession session, Model model) {
        if (!master(session)) {
            return "redirect:/auth/staff-login";
        }
        model.addAttribute("pendingLines", pendingService.readPendingLines());
        return "user/master-console";
    }
}
