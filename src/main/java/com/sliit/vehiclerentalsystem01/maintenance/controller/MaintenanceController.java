package com.sliit.vehiclerentalsystem01.maintenance.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceEntry;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceRecord;
import com.sliit.vehiclerentalsystem01.maintenance.service.MaintenanceService;
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

    @GetMapping
    public String list(Model model) {
        var vehicles = maintenanceService.findAllVehicles();
        var maintenanceEntries = maintenanceService.findAllEntries();
        model.addAttribute("fleetList", vehicles);
        model.addAttribute("maintenanceEntries", maintenanceEntries);
        model.addAttribute("fleetCount", vehicles.size());
        model.addAttribute("maintenanceCount", maintenanceService.countInMaintenance());
        return "user/maintenance-dashboard";
    }

    @PostMapping("/vehicles/add")
    public String addVehicle(@RequestParam("vehicleId") String vehicleId,
                             @RequestParam("vehicleType") String vehicleType,
                             @RequestParam("vehicleModel") String vehicleModel,
                             @RequestParam("mileage") String mileage,
                             @RequestParam(value = "lastServiceDate", required = false) String lastServiceDate,
                             @RequestParam(value = "nextServiceDate", required = false) String nextServiceDate) {
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
    public String addMaintenanceEntry(@RequestParam("vehicleId") String vehicleId,
                                      @RequestParam("reason") String reason,
                                      @RequestParam(value = "maintenanceDate", required = false) String maintenanceDate,
                                      @RequestParam(value = "notes", required = false) String notes) {
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
    public String deleteVehicle(@RequestParam("vehicleId") String vehicleId) {
        maintenanceService.deleteVehicle(vehicleId);
        return "redirect:/maintenance";
    }

    @PostMapping("/entries/delete")
    public String deleteMaintenanceEntry(@RequestParam("entryId") String entryId) {
        maintenanceService.deleteMaintenanceEntry(entryId);
        return "redirect:/maintenance";
    }
}
