package com.sliit.vehiclerentalsystem01.vehicle.controller;

import com.sliit.vehiclerentalsystem01.vehicle.model.Car;
import com.sliit.vehiclerentalsystem01.vehicle.model.Motorcycle;
import com.sliit.vehiclerentalsystem01.vehicle.model.Vehicle;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Legacy query-param flow ({@code action=delete|edit|list}) and dashboard, plus default fleet view.
     */
    @GetMapping
    public String vehiclesEntry(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String id,
            Model model) {
        if ("delete".equals(action) && id != null && !id.isBlank()) {
            vehicleService.deleteById(id.trim());
            return "redirect:/vehicles?action=list";
        }
        if ("edit".equals(action) && id != null && !id.isBlank()) {
            return vehicleService.findById(id.trim())
                    .map(v -> {
                        model.addAttribute("vehicle", v);
                        return "vehicle/edit-vehicle";
                    })
                    .orElse("redirect:/vehicles?action=list");
        }
        List<Vehicle> all = vehicleService.findAll();
        model.addAttribute("vehicleList", all);
        model.addAttribute("vehicles", all);
        long carCount = all.stream().filter(v -> v instanceof Car).count();
        long motorcycleCount = all.stream().filter(v -> v instanceof Motorcycle).count();
        double avgRate = all.stream().mapToDouble(Vehicle::calculateRentalRate).average().orElse(0.0);
        model.addAttribute("totalVehicles", all.size());
        model.addAttribute("carCount", carCount);
        model.addAttribute("motorcycleCount", motorcycleCount);
        model.addAttribute("avgRate", avgRate);
        return "vehicle/vehicle-dashboard";
    }

    @GetMapping("/add")
    public String addLegacyForm() {
        return "vehicle/vehicle-form";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("mode", "create");
        model.addAttribute("entityId", null);
        model.addAttribute("vehicleType", "CAR");
        model.addAttribute("car", new Car());
        model.addAttribute("motorcycle", new Motorcycle());
        return "vehicle/vehicle-entity-form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        return vehicleService.findById(id)
                .map(v -> {
                    model.addAttribute("vehicle", v);
                    return "vehicle/vehicle-detail";
                })
                .orElse("redirect:/vehicles");
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        return vehicleService.findById(id)
                .map(v -> {
                    model.addAttribute("mode", "edit");
                    model.addAttribute("entityId", id);
                    model.addAttribute("vehicleType", v instanceof Motorcycle ? "MOTORCYCLE" : "CAR");
                    if (v instanceof Car c) {
                        model.addAttribute("car", c);
                        model.addAttribute("motorcycle", new Motorcycle());
                    } else {
                        model.addAttribute("car", new Car());
                        model.addAttribute("motorcycle", (Motorcycle) v);
                    }
                    return "vehicle/vehicle-entity-form";
                })
                .orElse("redirect:/vehicles");
    }

    @PostMapping
    public String postRoot(
            @RequestParam Map<String, String> params,
            @RequestParam(required = false) String vehicleType,
            @ModelAttribute Car car,
            @ModelAttribute Motorcycle motorcycle) {
        if (isModernVehicleType(vehicleType)) {
            createModern(vehicleType, car, motorcycle);
            return "redirect:/vehicles";
        }
        return handleLegacyPost(params);
    }

    private static boolean isModernVehicleType(String vehicleType) {
        return vehicleType != null
                && ("CAR".equalsIgnoreCase(vehicleType) || "MOTORCYCLE".equalsIgnoreCase(vehicleType));
    }

    private String handleLegacyPost(Map<String, String> params) {
        String action = params.get("action");
        try {
            if ("update".equals(action)) {
                String id = normalized(params.get("id"));
                vehicleService.save(buildVehicleLegacy(id, params));
            } else {
                String id = "V-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
                vehicleService.save(buildVehicleLegacy(id, params));
            }
            return "redirect:/vehicles?action=list";
        } catch (NumberFormatException ex) {
            return "redirect:/vehicles/new?error=invalid_number";
        }
    }

    private Vehicle buildVehicleLegacy(String id, Map<String, String> params) {
        String brand = normalized(params.get("brand"));
        String model = normalized(params.get("model"));
        double rate = Double.parseDouble(normalized(params.get("rate")));
        String vType = normalized(params.get("vehicleType"));

        if ("Motorcycle".equalsIgnoreCase(vType)) {
            String helmetValue = params.get("helmetIncluded");
            boolean helmetIncluded = "on".equalsIgnoreCase(helmetValue) || "true".equalsIgnoreCase(helmetValue);
            return new Motorcycle(id, brand, model, rate, helmetIncluded);
        }

        int capacity = Integer.parseInt(normalized(params.get("capacity")));
        return new Car(id, brand, model, rate, capacity);
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim();
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable String id,
            @RequestParam String vehicleType,
            @ModelAttribute Car car,
            @ModelAttribute Motorcycle motorcycle) {
        Vehicle toSave = "MOTORCYCLE".equalsIgnoreCase(vehicleType) ? motorcycle : car;
        toSave.setId(id);
        vehicleService.save(toSave);
        return "redirect:/vehicles";
    }

    private void createModern(String vehicleType, Car car, Motorcycle motorcycle) {
        Vehicle toSave = "MOTORCYCLE".equalsIgnoreCase(vehicleType) ? motorcycle : car;
        vehicleService.save(toSave);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        vehicleService.deleteById(id);
        return "redirect:/vehicles";
    }
}
