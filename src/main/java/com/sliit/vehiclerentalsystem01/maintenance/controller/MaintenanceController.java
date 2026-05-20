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

@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    private static boolean admin(HttpSession session) {
        if (session == null) {
            return false;
        }

        Object adminLoginAttribute = session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN);

        return Boolean.TRUE.equals(adminLoginAttribute);
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }

        model.addAttribute("fleetList", maintenanceService.findAllVehicles());
        model.addAttribute("maintenanceEntries", maintenanceService.findAllEntries());
        model.addAttribute("fleetCount", maintenanceService.findAllVehicles().size());
        model.addAttribute("maintenanceCount", maintenanceService.countInMaintenance());

        return "user/maintenance-dashboard";
    }

    @PostMapping("/vehicles/add")
    public String addVehicle(HttpSession session,
                             @RequestParam("vehicleId") String vehicleId,
                             @RequestParam("vehicleType") String vehicleType,
                             @RequestParam("vehicleModel") String vehicleModel,
                             @RequestParam("mileage") String mileage,
                             @RequestParam(value = "lastServiceDate", required = false) String lastServiceDate,
                             @RequestParam(value = "nextServiceDate", required = false) String nextServiceDate) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }

        MaintenanceRecord record = new MaintenanceRecord();

        record.setVehicleId(vehicleId);
        record.setVehicleType(vehicleType);
        record.setVehicleModel(vehicleModel);
        record.setMileage(mileage);

        if (lastServiceDate != null && !lastServiceDate.isBlank()) {
            record.setLastServiceDate(LocalDate.parse(lastServiceDate));
        }

        if (nextServiceDate != null && !nextServiceDate.isBlank()) {
            record.setNextServiceDate(LocalDate.parse(nextServiceDate));
        }

        try {
            record.setPhotoFileName("");
            maintenanceService.addVehicle(record);
        } catch (ValidationException ex) {
            return "redirect:/maintenance?error=" + ex.getMessage();
        }

        return "redirect:/maintenance";
    }

    @PostMapping("/entries/add")
    public String addMaintenanceEntry(HttpSession session,
                                      @RequestParam("vehicleId") String vehicleId,
                                      @RequestParam("reason") String reason,
                                      @RequestParam(value = "maintenanceDate", required = false) String maintenanceDate,
                                      @RequestParam(value = "notes", required = false) String notes) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }

        MaintenanceEntry entry = new MaintenanceEntry();

        entry.setVehicleId(vehicleId);
        entry.setReason(reason);
        entry.setNotes(notes);

        if (maintenanceDate != null && !maintenanceDate.isBlank()) {
            entry.setMaintenanceDate(LocalDate.parse(maintenanceDate));
        }

        try {
            maintenanceService.addMaintenanceEntry(entry);
        } catch (ValidationException ex) {
            return "redirect:/maintenance?error=" + ex.getMessage();
        }

        return "redirect:/maintenance";
    }

    @PostMapping("/vehicles/delete")
    public String deleteVehicle(HttpSession session,
                                @RequestParam("vehicleId") String vehicleId) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }

        maintenanceService.deleteVehicle(vehicleId);

        return "redirect:/maintenance";
    }

    @PostMapping("/entries/delete")
    public String deleteMaintenanceEntry(HttpSession session,
                                         @RequestParam("entryId") String entryId) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }

        maintenanceService.deleteMaintenanceEntry(entryId);

        return "redirect:/maintenance";
    }


}
