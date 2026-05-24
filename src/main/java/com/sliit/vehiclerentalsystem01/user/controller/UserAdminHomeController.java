package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.maintenance.service.MaintenanceService;
import com.sliit.vehiclerentalsystem01.payment.service.PaymentService;
import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.review.service.ReviewService;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.entity.Staff;
import com.sliit.vehiclerentalsystem01.user.entity.User;
import com.sliit.vehiclerentalsystem01.user.service.MasterAdminPendingService;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserAdminHomeController {

    private final UserService userService;
    private final VehicleService vehicleService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;
    private final MasterAdminPendingService masterAdminPendingService;
    private final MaintenanceService maintenanceService;

    public UserAdminHomeController(UserService userService,
                                   VehicleService vehicleService,
                                   ReservationService reservationService,
                                   PaymentService paymentService,
                                   ReviewService reviewService,
                                   MasterAdminPendingService masterAdminPendingService,
                                   MaintenanceService maintenanceService) {
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.reviewService = reviewService;
        this.masterAdminPendingService = masterAdminPendingService;
        this.maintenanceService = maintenanceService;
    }

    private boolean isMaster(HttpSession session) {
        if (session == null) return false;
        // Master admin: ROLE_ADMIN + has no Staff record OR is a known admin username
        Boolean isAdmin = (Boolean) session.getAttribute("adminLoggedIn");
        if (!Boolean.TRUE.equals(isAdmin)) return false;
        String un = (String) session.getAttribute("username");
        // The system master account (no DB record, added via staff-access-request flow)
        return "admin".equalsIgnoreCase(un) || "master1".equalsIgnoreCase(un);
    }

    @GetMapping("/user/admin-home")
    public String adminHomeLegacy(HttpSession session, Model model) {
        return adminDashboard(session, model);
    }

    @GetMapping("/user/admin-dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        int customers = userService.findAllCustomers().size();
        int staff = userService.findAllStaff().size();
        model.addAttribute("customerCount", customers);
        model.addAttribute("staffCount", staff);
        model.addAttribute("userCount", customers + staff);
        model.addAttribute("vehicleCount", vehicleService.findAll().size());
        model.addAttribute("bookingCount", reservationService.findAll().size());
        model.addAttribute("paymentCount", paymentService.findAll().size());
        model.addAttribute("reviewCount", reviewService.findAll().size());
        model.addAttribute("maintenanceCount", maintenanceService.countInMaintenance());
        model.addAttribute("pendingAccessCount", isMaster(session) ? masterAdminPendingService.readPendingLines().size() : 0);
        return "user/admin-dashboard";
    }

    @GetMapping("/user-management")
    public String userManagement(HttpSession session, Model model) {
        model.addAttribute("customerCount", userService.findAllCustomers().size());
        model.addAttribute("staffCount", userService.findAllStaff().size());
        model.addAttribute("pendingAccessCount", isMaster(session) ? masterAdminPendingService.readPendingLines().size() : 0);
        return "user/user-management";
    }

    @GetMapping("/user/master-console")
    public String masterConsole(HttpSession session, Model model) {
        if (!isMaster(session)) {
            return "redirect:/auth/staff-login";
        }
        var pendingLines = masterAdminPendingService.readPendingLines();
        model.addAttribute("pendingLines", pendingLines);
        model.addAttribute("pendingAccessCount", pendingLines.size());
        return "user/master-console";
    }

    @PostMapping("/user/master-console")
    public String masterConsoleAction(HttpSession session,
                                     @RequestParam String action,
                                     @RequestParam("request") String requestLine,
                                     RedirectAttributes ra) {
        if (!isMaster(session)) {
            return "redirect:/auth/staff-login";
        }
        switch (action) {
            case "approve" -> {
                try {
                    String tempPassword = masterAdminPendingService.approve(requestLine);
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
                if (masterAdminPendingService.removeLine(requestLine)) {
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

    @GetMapping("/users")
    public String legacyUserList(HttpSession session) {
        return "redirect:/user-management";
    }

    @GetMapping("/users/new")
    public String legacyNewUser(HttpSession session) {
        return "redirect:/customers?action=view_directory";
    }

    @GetMapping("/users/{id}")
    public String legacyUserDetail(@PathVariable String id, HttpSession session) {
        return legacyEditRedirect(id);
    }

    @GetMapping("/users/{id}/edit")
    public String legacyUserEdit(@PathVariable String id, HttpSession session) {
        return legacyEditRedirect(id);
    }

    @PostMapping("/users")
    public String legacyCreate(HttpSession session) {
        return "redirect:/user-management";
    }

    @PostMapping("/users/{id}")
    public String legacyUpdate(@PathVariable String id, HttpSession session) {
        return legacyEditRedirect(id);
    }

    @PostMapping("/users/{id}/delete")
    public String legacyDelete(@PathVariable String id, HttpSession session, RedirectAttributes ra) {
        try {
            userService.deleteById(id);
            ra.addFlashAttribute("message", "User deleted.");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/user-management";
    }

    private String legacyEditRedirect(String id) {
        return userService.findById(id)
                .map(UserAdminHomeController::editRedirectFor)
                .orElse("redirect:/user-management");
    }

    private static String editRedirectFor(User user) {
        if (user instanceof Customer) {
            return "redirect:/customers?action=edit&id=" + user.getId();
        }
        if (user instanceof Staff) {
            return "redirect:/staff?action=edit&id=" + user.getId();
        }
        return "redirect:/user-management";
    }
}
