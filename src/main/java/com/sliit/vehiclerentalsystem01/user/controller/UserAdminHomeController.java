package com.sliit.vehiclerentalsystem01.user.controller;

import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.maintenance.service.MaintenanceService;
import com.sliit.vehiclerentalsystem01.payment.service.PaymentService;
import com.sliit.vehiclerentalsystem01.review.service.ReviewService;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.user.service.MasterAdminPendingService;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Admin dashboard at /user/admin-dashboard — aggregates counts from user module AND other modules
 * (vehicles, bookings, payments, etc.) for a single overview screen after staff login.
 */
@Controller
@RequestMapping("/user")
public class UserAdminHomeController {

    private final UserService userService;
    private final VehicleService vehicleService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;
    private final MasterAdminPendingService masterAdminPendingService;
    private final MaintenanceService maintenanceService;

    public UserAdminHomeController(
            UserService userService,
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

    @GetMapping("/admin-home")
    public String adminHomeLegacy(HttpSession session, Model model) {
        return adminDashboard(session, model);
    }

    /** Admin dashboard metrics (users + other modules); requires ADMIN_LOGGED_IN */
    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN))) {
            return "redirect:/auth/staff-login";
        }

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

        int pendingAccessCount = 0;
        if (Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.IS_MASTER_ADMIN))) {
            pendingAccessCount = masterAdminPendingService.readPendingLines().size();
        }
        model.addAttribute("pendingAccessCount", pendingAccessCount);

        return "user/admin-dashboard";
    }
}
