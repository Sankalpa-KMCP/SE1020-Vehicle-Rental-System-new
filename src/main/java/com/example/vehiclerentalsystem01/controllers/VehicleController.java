package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.VehicleDAO;
import com.example.vehiclerentalsystem01.models.Car;
import com.example.vehiclerentalsystem01.models.Motorcycle;
import com.example.vehiclerentalsystem01.models.Vehicle;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    private String normalized(String value) {
        return value == null ? "" : value.trim();
    }

    private Vehicle buildVehicle(String id, Map<String, String> params) {
        String brand = normalized(params.get("brand"));
        String model = normalized(params.get("model"));
        double rate = Double.parseDouble(normalized(params.get("rate")));
        String vehicleType = normalized(params.get("vehicleType"));

        if ("Motorcycle".equalsIgnoreCase(vehicleType)) {
            String helmetValue = params.get("helmetIncluded");
            boolean helmetIncluded = "on".equalsIgnoreCase(helmetValue) || "true".equalsIgnoreCase(helmetValue);
            return new Motorcycle(id, brand, model, rate, helmetIncluded);
        }

        int capacity = Integer.parseInt(normalized(params.get("capacity")));
        return new Car(id, brand, model, rate, capacity);
    }

    @GetMapping
    public String handleGet(@RequestParam(required = false) String action,
                            @RequestParam(required = false) String id,
                            Model model) {
        if ("delete".equals(action)) {
            vehicleDAO.deleteVehicle(id);
            return "redirect:/vehicles?action=list";
        }

        if ("edit".equals(action)) {
            Vehicle vehicle = vehicleDAO.getVehicleById(id);
            if (vehicle == null) {
                return "redirect:/vehicles?action=list";
            }
            model.addAttribute("vehicle", vehicle);
            return "edit-vehicle";
        }

        model.addAttribute("vehicleList", vehicleDAO.getAllVehicles());
        return "vehicle-dashboard";
    }

    @PostMapping
    public String handlePost(@RequestParam Map<String, String> params) {
        String action = params.get("action");

        try {
            if ("update".equals(action)) {
                String id = params.get("id");
                vehicleDAO.updateVehicle(buildVehicle(id, params));
            } else {
                String id = "V-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
                vehicleDAO.saveVehicle(buildVehicle(id, params));
            }
            return "redirect:/vehicles?action=list";
        } catch (NumberFormatException ex) {
            return "redirect:/add-vehicle.jsp?error=invalid_number";
        }
    }
}
