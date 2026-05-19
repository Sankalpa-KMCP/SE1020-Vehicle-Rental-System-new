package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.user.service.MasterAdminPendingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Master admin console (legacy {@code master-console.jsp}). Provides the pending staff/admin
 * request list plus approve / reject actions.
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
        var pendingLines = pendingService.readPendingLines();
        model.addAttribute("pendingLines", pendingLines);
        model.addAttribute("pendingAccessCount", pendingLines.size());
        return "user/master-console";
    }

    @PostMapping("/master-console")
    public String act(HttpSession session,
                      @RequestParam String action,
                      @RequestParam("request") String requestLine,
                      RedirectAttributes ra) {
        if (!master(session)) {
            return "redirect:/auth/staff-login";
        }
        switch (action) {
            case "approve" -> {
                try {
                    String tempPassword = pendingService.approve(requestLine);
                    ra.addFlashAttribute("message",
                            "Approved " + MasterAdminPendingService.parseUsername(requestLine)
                                    + ". Temporary password: " + tempPassword);
                } catch (IllegalArgumentException ex) {
                    ra.addFlashAttribute("error", "Cannot parse the request line.");
                } catch (IllegalStateException ex) {
                    ra.addFlashAttribute("error", "Username already exists; request was discarded.");
                } catch (ValidationException ex) {
                    ra.addFlashAttribute("error", ex.getMessage());
                }
            }
            case "reject" -> {
                if (pendingService.removeLine(requestLine)) {
                    ra.addFlashAttribute("message",
                            "Rejected request for "
                                    + MasterAdminPendingService.parseUsername(requestLine) + ".");
                } else {
                    ra.addFlashAttribute("error", "Could not remove that request line.");
                }
            }
            default -> ra.addFlashAttribute("error", "Unknown action.");
        }
        return "redirect:/user/master-console";
    }
}
