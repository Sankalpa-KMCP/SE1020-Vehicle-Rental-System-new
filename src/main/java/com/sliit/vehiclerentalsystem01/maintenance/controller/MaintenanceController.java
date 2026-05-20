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
 * MaintenanceController is a Spring MVC Controller that handles HTTP requests
 * related to the maintenance dashboard, fleet vehicle management, and maintenance entries.
 * <p>
 * This controller provides endpoints to list vehicles in the maintenance fleet,
 * add new vehicles to the fleet, add maintenance records/entries for vehicles,
 * and delete vehicles or maintenance entries.
 * <p>
 * Security is enforced across all endpoints by checking if the active session
 * belongs to an administrative user.
 */
@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    /**
     * Service layer dependency used to manage maintenance records and entries.
     */
    private final MaintenanceService maintenanceService;

    /**
     * Constructs a new MaintenanceController with the required MaintenanceService dependency.
     *
     * @param maintenanceService the maintenance service to be injected
     */
    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    /**
     * Helper method to verify if the current user session has administrative privileges.
     *
     * @param session the current HTTP session
     * @return true if the session contains the admin logged-in attribute set to true; false otherwise
     */
    private static boolean admin(HttpSession session) {
        // If the session is null, the user is not authenticated or session has expired.
        if (session == null) {
            return false;
        }

        // Retrieve the admin login attribute from the HTTP session.
        Object adminLoginAttribute = session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN);

        // Perform a safe check: if the attribute is null or false, return false.
        // If the attribute is Boolean.TRUE, then return true.
        boolean isAdminLoggedIn = Boolean.TRUE.equals(adminLoginAttribute);

        return isAdminLoggedIn;
    }

    /**
     * Renders the main maintenance dashboard.
     * Displays all fleet vehicles, maintenance entries, and statistics (fleet count, maintenance count).
     * Access is restricted to admin staff.
     *
     * @param session the HTTP session of the user
     * @param model   the Spring UI Model to pass data to the view
     * @return the view name to render, or a redirect to the login page if not authorized
     */
    @GetMapping
    public String list(HttpSession session, Model model) {
        // Step 1: Security check. Verify if the logged-in user is an administrator.
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect the user to the staff login page if not authorized.
            return "redirect:/auth/staff-login";
        }

        // Step 2: Retrieve all vehicles and maintenance entries from the service layer.
        var vehicles = maintenanceService.findAllVehicles();
        var maintenanceEntries = maintenanceService.findAllEntries();
        
        // Step 3: Calculate relevant statistics.
        int totalFleetCount = vehicles.size();
        int inMaintenanceCount = maintenanceService.countInMaintenance();

        // Step 4: Populate the UI model with the retrieved data.
        model.addAttribute("fleetList", vehicles);
        model.addAttribute("maintenanceEntries", maintenanceEntries);
        model.addAttribute("fleetCount", totalFleetCount);
        model.addAttribute("maintenanceCount", inMaintenanceCount);

        // Step 5: Return the path to the Thymeleaf view template for the dashboard.
        return "user/maintenance-dashboard";
    }

    /**
     * Endpoint to add a new vehicle to the maintenance fleet.
     * Maps to HTTP POST /maintenance/vehicles/add.
     * Validates input fields and service dates before creating a new maintenance record.
     *
     * @param session          the HTTP session of the user
     * @param vehicleId        the unique ID of the vehicle
     * @param vehicleType      the category/type of the vehicle (e.g., Car, SUV)
     * @param vehicleModel     the model of the vehicle
     * @param mileage          the current mileage of the vehicle as a string
     * @param lastServiceDate  the date when the vehicle was last serviced (optional)
     * @param nextServiceDate  the date when the vehicle is scheduled for its next service (optional)
     * @return a redirect path to the maintenance dashboard, with an error query parameter if validation fails
     */
    @PostMapping("/vehicles/add")
    public String addVehicle(HttpSession session,
                             @RequestParam("vehicleId") String vehicleId,
                             @RequestParam("vehicleType") String vehicleType,
                             @RequestParam("vehicleModel") String vehicleModel,
                             @RequestParam("mileage") String mileage,
                             @RequestParam(value = "lastServiceDate", required = false) String lastServiceDate,
                             @RequestParam(value = "nextServiceDate", required = false) String nextServiceDate) {
        // Step 1: Security check. Verify if the logged-in user is an administrator.
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect to staff login if unauthorized.
            return "redirect:/auth/staff-login";
        }

        // Step 2: Instantiate a new MaintenanceRecord model object.
        MaintenanceRecord record = new MaintenanceRecord();

        // Step 3: Populate the basic details of the vehicle.
        record.setVehicleId(vehicleId);
        record.setVehicleType(vehicleType);
        record.setVehicleModel(vehicleModel);
        record.setMileage(mileage);

        // Step 4: Check and parse the last service date.
        // It is parsed from an ISO-8601 date string (e.g., "YYYY-MM-DD") to a LocalDate.
        if (lastServiceDate != null && !lastServiceDate.isBlank()) {
            LocalDate parsedLastServiceDate = LocalDate.parse(lastServiceDate);
            record.setLastServiceDate(parsedLastServiceDate);
        }

        // Step 5: Check and parse the next service date.
        if (nextServiceDate != null && !nextServiceDate.isBlank()) {
            LocalDate parsedNextServiceDate = LocalDate.parse(nextServiceDate);
            record.setNextServiceDate(parsedNextServiceDate);
        }

        // Step 6: Attempt to save the record via the service layer.
        try {
            // Photo filename is initialized to an empty string.
            record.setPhotoFileName("");
            maintenanceService.addVehicle(record);
        } catch (ValidationException ex) {
            // If validation fails (e.g. invalid format or duplicate vehicle ID),
            // redirect back to the dashboard with the error message as a query parameter.
            String errorMessage = ex.getMessage();
            return "redirect:/maintenance?error=" + errorMessage;
        }

        // Step 7: Redirect back to the maintenance dashboard upon successful creation.
        return "redirect:/maintenance";
    }

    /**
     * Endpoint to add a new maintenance entry for a vehicle.
     * Maps to HTTP POST /maintenance/entries/add.
     *
     * @param session         the HTTP session of the user
     * @param vehicleId       the unique ID of the vehicle being sent to maintenance
     * @param reason          the reason for maintenance (e.g., Repair, Routine Check)
     * @param maintenanceDate the date of the maintenance (optional)
     * @param notes           any additional notes/comments (optional)
     * @return a redirect path to the maintenance dashboard
     */
    @PostMapping("/entries/add")
    public String addMaintenanceEntry(HttpSession session,
                                      @RequestParam("vehicleId") String vehicleId,
                                      @RequestParam("reason") String reason,
                                      @RequestParam(value = "maintenanceDate", required = false) String maintenanceDate,
                                      @RequestParam(value = "notes", required = false) String notes) {
        // Step 1: Security check. Verify if the logged-in user is an administrator.
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect to staff login if unauthorized.
            return "redirect:/auth/staff-login";
        }

        // Step 2: Instantiate a new MaintenanceEntry model object.
        MaintenanceEntry entry = new MaintenanceEntry();

        // Step 3: Populate the properties of the maintenance entry.
        entry.setVehicleId(vehicleId);
        entry.setReason(reason);
        entry.setNotes(notes);

        // Step 4: Parse the maintenance date if provided.
        if (maintenanceDate != null && !maintenanceDate.isBlank()) {
            LocalDate parsedMaintenanceDate = LocalDate.parse(maintenanceDate);
            entry.setMaintenanceDate(parsedMaintenanceDate);
        }

        // Step 5: Save the maintenance entry using the service.
        try {
            maintenanceService.addMaintenanceEntry(entry);
        } catch (ValidationException ex) {
            // Redirect back with the error message if a validation constraint is violated.
            String errorMessage = ex.getMessage();
            return "redirect:/maintenance?error=" + errorMessage;
        }

        // Step 6: Redirect to the dashboard.
        return "redirect:/maintenance";
    }

    /**
     * Endpoint to delete/remove a vehicle from the fleet maintenance records.
     * Maps to HTTP POST /maintenance/vehicles/delete.
     *
     * @param session   the HTTP session of the user
     * @param vehicleId the unique ID of the vehicle to remove
     * @return a redirect path to the maintenance dashboard
     */
    @PostMapping("/vehicles/delete")
    public String deleteVehicle(HttpSession session,
                                @RequestParam("vehicleId") String vehicleId) {
        // Step 1: Security check. Verify if the logged-in user is an administrator.
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect to staff login if unauthorized.
            return "redirect:/auth/staff-login";
        }

        // Step 2: Delegate the deletion task to the maintenance service.
        maintenanceService.deleteVehicle(vehicleId);

        // Step 3: Redirect to the dashboard.
        return "redirect:/maintenance";
    }

    /**
     * Endpoint to delete/remove a maintenance entry.
     * Maps to HTTP POST /maintenance/entries/delete.
     *
     * @param session the HTTP session of the user
     * @param entryId the unique ID of the maintenance entry to remove
     * @return a redirect path to the maintenance dashboard
     */
    @PostMapping("/entries/delete")
    public String deleteMaintenanceEntry(HttpSession session,
                                         @RequestParam("entryId") String entryId) {
        // Step 1: Security check. Verify if the logged-in user is an administrator.
        boolean isAuthorized = admin(session);
        if (!isAuthorized) {
            // Redirect to staff login if unauthorized.
            return "redirect:/auth/staff-login";
        }

        // Step 2: Delegate the maintenance entry deletion to the service.
        maintenanceService.deleteMaintenanceEntry(entryId);

        // Step 3: Redirect to the dashboard.
        return "redirect:/maintenance";
    }
}

