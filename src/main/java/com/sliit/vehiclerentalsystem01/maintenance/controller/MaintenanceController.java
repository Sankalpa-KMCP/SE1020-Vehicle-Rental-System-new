package com.sliit.vehiclerentalsystem01.maintenance.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceEntry;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceRecord;
import com.sliit.vehiclerentalsystem01.maintenance.service.MaintenanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * Controller for maintenance. Only for admins.
 */
@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    /** Maintenance service. */
    private final MaintenanceService maintenanceService;

    /** Constructor. */
    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    /** Checks if session is admin. */
    private static boolean admin(HttpSession session) {
        // Return false if no session
        if (session == null) {
            return false;
        }

        // Get admin flag from session
        Object adminLoginAttribute = session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN);

        // Check if admin is logged in
        boolean isAdminLoggedIn = Boolean.TRUE.equals(adminLoginAttribute);

        return isAdminLoggedIn;
    }

    /** Show maintenance dashboard page. */
    @GetMapping
    public String list(HttpSession session, Model model) {
        // Security check
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect if not admin
            return "redirect:/auth/staff-login";
        }

        // Get vehicles and entries
        var vehicles = maintenanceService.findAllVehicles();
        var maintenanceEntries = maintenanceService.findAllEntries();
        
        // Calculate counts
        int totalFleetCount = vehicles.size();
        int inMaintenanceCount = maintenanceService.countInMaintenance();

        // Add attributes to view model
        model.addAttribute("fleetList", vehicles);
        model.addAttribute("maintenanceEntries", maintenanceEntries);
        model.addAttribute("fleetCount", totalFleetCount);
        model.addAttribute("maintenanceCount", inMaintenanceCount);

        // Return dashboard view template
        return "user/maintenance-dashboard";
    }

    /** Add new vehicle to fleet. */
    @PostMapping("/vehicles/add")
    public String addVehicle(HttpSession session,
                             @RequestParam("vehicleId") String vehicleId,
                             @RequestParam("vehicleType") String vehicleType,
                             @RequestParam("vehicleModel") String vehicleModel,
                             @RequestParam("mileage") String mileage,
                             @RequestParam(value = "lastServiceDate", required = false) String lastServiceDate,
                             @RequestParam(value = "nextServiceDate", required = false) String nextServiceDate) {
        // Security check
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect if not admin
            return "redirect:/auth/staff-login";
        }

        // Create record
        MaintenanceRecord record = new MaintenanceRecord();

        // Set vehicle info
        record.setVehicleId(vehicleId);
        record.setVehicleType(vehicleType);
        record.setVehicleModel(vehicleModel);
        record.setMileage(mileage);

        // Set last service date if provided
        if (lastServiceDate != null && !lastServiceDate.isBlank()) {
            LocalDate parsedLastServiceDate = LocalDate.parse(lastServiceDate);
            record.setLastServiceDate(parsedLastServiceDate);
        }

        // Set next service date if provided
        if (nextServiceDate != null && !nextServiceDate.isBlank()) {
            LocalDate parsedNextServiceDate = LocalDate.parse(nextServiceDate);
            record.setNextServiceDate(parsedNextServiceDate);
        }

        // Save vehicle to database
        try {
            // Set empty photo
            record.setPhotoFileName("");
            maintenanceService.addVehicle(record);
        } catch (ValidationException ex) {
            // Redirect with validation error
            String errorMessage = ex.getMessage();
            return "redirect:/maintenance?error=" + errorMessage;
        }

        // Go to dashboard
        return "redirect:/maintenance";
    }

    /** Add new maintenance entry. */
    @PostMapping("/entries/add")
    public String addMaintenanceEntry(HttpSession session,
                                      @RequestParam("vehicleId") String vehicleId,
                                      @RequestParam("reason") String reason,
                                      @RequestParam(value = "maintenanceDate", required = false) String maintenanceDate,
                                      @RequestParam(value = "notes", required = false) String notes) {
        // Security check
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect if not admin
            return "redirect:/auth/staff-login";
        }

        // Create entry
        MaintenanceEntry entry = new MaintenanceEntry();

        // Set entry details
        entry.setVehicleId(vehicleId);
        entry.setReason(reason);
        entry.setNotes(notes);

        // Set maintenance date if provided
        if (maintenanceDate != null && !maintenanceDate.isBlank()) {
            LocalDate parsedMaintenanceDate = LocalDate.parse(maintenanceDate);
            entry.setMaintenanceDate(parsedMaintenanceDate);
        }

        // Save entry to database
        try {
            maintenanceService.addMaintenanceEntry(entry);
        } catch (ValidationException ex) {
            // Redirect with validation error
            String errorMessage = ex.getMessage();
            return "redirect:/maintenance?error=" + errorMessage;
        }

        // Go to dashboard
        return "redirect:/maintenance";
    }

    /** Delete a vehicle from maintenance. */
    @PostMapping("/vehicles/delete")
    public String deleteVehicle(HttpSession session,
                                @RequestParam("vehicleId") String vehicleId) {
        // Security check
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect if not admin
            return "redirect:/auth/staff-login";
        }

        // Delete vehicle
        maintenanceService.deleteVehicle(vehicleId);

        // Go to dashboard
        return "redirect:/maintenance";
    }

    /** Delete a maintenance entry. */
    @PostMapping("/entries/delete")
    public String deleteMaintenanceEntry(HttpSession session,
                                         @RequestParam("entryId") String entryId) {
        // Security check
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect if not admin
            return "redirect:/auth/staff-login";
        }

        // Delete entry
        maintenanceService.deleteMaintenanceEntry(entryId);

        // Go to dashboard
        return "redirect:/maintenance";
    }
}

