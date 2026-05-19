package com.sliit.vehiclerentalsystem01.maintenance.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceRecord;
import com.sliit.vehiclerentalsystem01.maintenance.service.MaintenanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;

@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    private static boolean admin(HttpSession session) {
        boolean sessionExists = session != null;

        if (!sessionExists) {
            return false;
        }

        Object adminLoginAttribute = session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN);

        return Boolean.TRUE.equals(adminLoginAttribute);
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        boolean userIsAdmin = admin(session);

        if (!userIsAdmin) {
            return "redirect:/auth/staff-login";
        }

        model.addAttribute("maintenanceList", maintenanceService.findAll());

        model.addAttribute("availableCount", maintenanceService.countAvailable());

        model.addAttribute("inUseCount", maintenanceService.countInUse());

        model.addAttribute("maintenanceCount", maintenanceService.countInMaintenance());

        return "user/maintenance-dashboard";
    }

    @PostMapping("/add")
    public String addRecord(HttpSession session,
                            @RequestParam String vehicleId,
                            @RequestParam String status,
                            @RequestParam(required = false) String lastServiceDate,
                            @RequestParam(required = false) String nextServiceDate,
                            @RequestParam(required = false) String notes) {
        boolean userIsAdmin = admin(session);

        if (!userIsAdmin) {
            return "redirect:/auth/staff-login";
        }

        MaintenanceRecord record = new MaintenanceRecord();

        record.setVehicleId(vehicleId);

        record.setStatus(status);

        boolean lastServiceDateWasProvided = lastServiceDate != null && !lastServiceDate.isBlank();

        if (lastServiceDateWasProvided) {
            LocalDate parsedLastServiceDate = LocalDate.parse(lastServiceDate);

            record.setLastServiceDate(parsedLastServiceDate);
        }

        boolean nextServiceDateWasProvided = nextServiceDate != null && !nextServiceDate.isBlank();

        if (nextServiceDateWasProvided) {
            LocalDate parsedNextServiceDate = LocalDate.parse(nextServiceDate);

            record.setNextServiceDate(parsedNextServiceDate);
        }

        record.setNotes(notes);

        try {
            maintenanceService.addRecord(record);
        } catch (ValidationException ex) {
            return "redirect:/maintenance?error=" + ex.getMessage();
        }

        return "redirect:/maintenance";
    }

    @PostMapping("/update-status")
    @ResponseBody
    public String updateStatus(HttpSession session,
                               @RequestParam String vehicleId,
                               @RequestParam String status) {
        boolean userIsAdmin = admin(session);

        if (!userIsAdmin) {
            return "UNAUTHORIZED";
        }

        try {
            maintenanceService.updateStatus(vehicleId, status);
        } catch (ValidationException ex) {
            return ex.getMessage();
        }

        return "OK";
    }

    @PostMapping("/delete")
    public String deleteRecord(HttpSession session,
                               @RequestParam String vehicleId) {
        boolean userIsAdmin = admin(session);

        if (!userIsAdmin) {
            return "redirect:/auth/staff-login";
        }

        maintenanceService.deleteRecord(vehicleId);

        return "redirect:/maintenance";
    }
}