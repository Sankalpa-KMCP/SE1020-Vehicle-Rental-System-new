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
 * Handles HTTP requests for the maintenance dashboard, fleet vehicles, and entries.
 * Enforces admin security on all endpoints.
 */
@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    /** Service for managing maintenance records and entries. */
    private final MaintenanceService maintenanceService;

    /** Constructs the controller with the required MaintenanceService dependency. */
    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    /** Checks if the session has admin privileges. */
    private static boolean admin(HttpSession session) {
        // Return false if session is null
        if (session == null) {
            return false;
        }

        // Get admin status from session
        Object adminLoginAttribute = session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN);

        // Safe check for admin privilege
        boolean isAdminLoggedIn = Boolean.TRUE.equals(adminLoginAttribute);

        return isAdminLoggedIn;
    }

    /** Renders the maintenance dashboard. */
    @GetMapping
    public String list(HttpSession session, Model model) {
        // Admin security check
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect if not authorized
            return "redirect:/auth/staff-login";
        }

        // Get vehicles and entries
        var vehicles = maintenanceService.findAllVehicles();
        var maintenanceEntries = maintenanceService.findAllEntries();
        
        // Calculate statistics
        int totalFleetCount = vehicles.size();
        int inMaintenanceCount = maintenanceService.countInMaintenance();

        // Populate model for view
        model.addAttribute("fleetList", vehicles);
        model.addAttribute("maintenanceEntries", maintenanceEntries);
        model.addAttribute("fleetCount", totalFleetCount);
        model.addAttribute("maintenanceCount", inMaintenanceCount);

        // Return view path
        return "user/maintenance-dashboard";
    }

    /** Adds a new vehicle to the maintenance fleet. */
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
            // Redirect if unauthorized
            return "redirect:/auth/staff-login";
        }

        // Create new record
        MaintenanceRecord record = new MaintenanceRecord();

        // Populate vehicle details
        record.setVehicleId(vehicleId);
        record.setVehicleType(vehicleType);
        record.setVehicleModel(vehicleModel);
        record.setMileage(mileage);

        // Parse last service date if provided
        if (lastServiceDate != null && !lastServiceDate.isBlank()) {
            LocalDate parsedLastServiceDate = LocalDate.parse(lastServiceDate);
            record.setLastServiceDate(parsedLastServiceDate);
        }

        // Parse next service date if provided
        if (nextServiceDate != null && !nextServiceDate.isBlank()) {
            LocalDate parsedNextServiceDate = LocalDate.parse(nextServiceDate);
            record.setNextServiceDate(parsedNextServiceDate);
        }

        // Save record via service
        try {
            // Default photo filename
            record.setPhotoFileName("");
            maintenanceService.addVehicle(record);
        } catch (ValidationException ex) {
            // Redirect with error message on validation failure
            String errorMessage = ex.getMessage();
            return "redirect:/maintenance?error=" + errorMessage;
        }

        // Redirect to dashboard
        return "redirect:/maintenance";
    }

    /** Adds a new maintenance entry for a vehicle. */
    @PostMapping("/entries/add")
    public String addMaintenanceEntry(HttpSession session,
                                      @RequestParam("vehicleId") String vehicleId,
                                      @RequestParam("reason") String reason,
                                      @RequestParam(value = "maintenanceDate", required = false) String maintenanceDate,
                                      @RequestParam(value = "notes", required = false) String notes) {
        // Security check
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect if unauthorized
            return "redirect:/auth/staff-login";
        }

        // Create new entry
        MaintenanceEntry entry = new MaintenanceEntry();

        // Populate entry properties
        entry.setVehicleId(vehicleId);
        entry.setReason(reason);
        entry.setNotes(notes);

        // Parse date if provided
        if (maintenanceDate != null && !maintenanceDate.isBlank()) {
            LocalDate parsedMaintenanceDate = LocalDate.parse(maintenanceDate);
            entry.setMaintenanceDate(parsedMaintenanceDate);
        }

        // Save entry
        try {
            maintenanceService.addMaintenanceEntry(entry);
        } catch (ValidationException ex) {
            // Redirect with error message on validation failure
            String errorMessage = ex.getMessage();
            return "redirect:/maintenance?error=" + errorMessage;
        }

        // Redirect to dashboard
        return "redirect:/maintenance";
    }

    /** Deletes a vehicle from the maintenance records. */
    @PostMapping("/vehicles/delete")
    public String deleteVehicle(HttpSession session,
                                @RequestParam("vehicleId") String vehicleId) {
        // Security check
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect if unauthorized
            return "redirect:/auth/staff-login";
        }

        // Delete vehicle
        maintenanceService.deleteVehicle(vehicleId);

        // Redirect to dashboard
        return "redirect:/maintenance";
    }

    /** Deletes a maintenance entry. */
    @PostMapping("/entries/delete")
    public String deleteMaintenanceEntry(HttpSession session,
                                         @RequestParam("entryId") String entryId) {
        // Security check
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect if unauthorized
            return "redirect:/auth/staff-login";
        }

        // Delete entry
        maintenanceService.deleteMaintenanceEntry(entryId);

        // Redirect to dashboard
        return "redirect:/maintenance";
    }
}

