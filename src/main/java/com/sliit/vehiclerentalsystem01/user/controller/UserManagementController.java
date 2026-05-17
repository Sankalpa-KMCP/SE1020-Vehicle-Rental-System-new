package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.user.service.MasterAdminPendingService;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserManagementController {

    private final UserService userService;
    private final MasterAdminPendingService masterAdminPendingService;

    public UserManagementController(UserService userService,
                                    MasterAdminPendingService masterAdminPendingService) {
        this.userService = userService;
        this.masterAdminPendingService = masterAdminPendingService;
    }

    private static boolean admin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
    }

    @GetMapping("/user-management")
    public String userManagement(HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        model.addAttribute("customerCount", userService.findAllCustomers().size());
        model.addAttribute("staffCount", userService.findAllStaff().size());
        int pending = 0;
        if (Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.IS_MASTER_ADMIN))) {
            pending = masterAdminPendingService.readPendingLines().size();
        }
        model.addAttribute("pendingAccessCount", pending);
        return "user/user-management";
    }
}
