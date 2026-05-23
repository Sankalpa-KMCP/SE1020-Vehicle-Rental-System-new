package com.sliit.vehiclerentalsystem01.vehicle.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Car;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Motorcycle;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Vehicle;
import com.sliit.vehiclerentalsystem01.core.service.ImageStorageService;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final ImageStorageService imageStorageService;

    public VehicleController(VehicleService vehicleService, ImageStorageService imageStorageService) {
        this.vehicleService = vehicleService;
        this.imageStorageService = imageStorageService;
    }

    private static boolean admin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN));
    }

    @GetMapping
    public String vehiclesEntry(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String id,
            HttpSession session,
            RedirectAttributes ra,
            Model model) {
        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }
        if ("delete".equals(action) && id != null && !id.isBlank()) {
            try {
                vehicleService.deleteById(id.trim());
            } catch (IllegalStateException ex) {
                ra.addAttribute("error", ex.getMessage());
            }
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
        double avgCarRate = all.stream().filter(v -> v instanceof Car).mapToDouble(Vehicle::calculateRentalRate).average().orElse(0.0);
        double avgMotoRate = all.stream().filter(v -> v instanceof Motorcycle).mapToDouble(Vehicle::calculateRentalRate).average().orElse(0.0);

        model.addAttribute("totalVehicles", all.size());
        model.addAttribute("carCount", carCount);
        model.addAttribute("motorcycleCount", motorcycleCount);
        model.addAttribute("avgCarRate", avgCarRate);
        model.addAttribute("avgMotoRate", avgMotoRate);

        return "vehicle/vehicle-dashboard";
    }

    @GetMapping("/add")
    public String addLegacyForm(HttpSession session) {
        if (!admin(session)) { return "redirect:/auth/staff-login"; }
        return "vehicle/add-vehicle";
    }

    // FIXED ROUTING: Now points to the correct Tailwind Add Form!
    @GetMapping("/new")
    public String newForm(HttpSession session) {
        if (!admin(session)) { return "redirect:/auth/staff-login"; }
        return "vehicle/add-vehicle";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        return vehicleService.findById(id).map(v -> {
            model.addAttribute("vehicle", v);
            return "vehicle/vehicle-detail";
        }).orElse("redirect:/vehicles");
    }

    // FIXED ROUTING: Now points to the correct Tailwind Edit Form!
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, HttpSession session, Model model) {
        if (!admin(session)) { return "redirect:/auth/staff-login"; }
        return vehicleService.findById(id).map(v -> {
            model.addAttribute("vehicle", v);
            return "vehicle/edit-vehicle";
        }).orElse("redirect:/vehicles");
    }

    @PostMapping
    public String postRoot(
            HttpSession session,
            @RequestParam Map<String, String> params,
            @RequestParam(required = false) String vehicleType,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes ra) {

        if (!admin(session)) {
            return "redirect:/auth/staff-login";
        }

        String action = params.get("action");
        String idParam = params.get("id");

        if ("delete".equals(action)) {
            if (idParam != null && !idParam.isBlank()) {
                try {
                    vehicleService.deleteById(idParam.trim());
                } catch (IllegalStateException ex) {
                    ra.addAttribute("error", ex.getMessage());
                }
            }
            return "redirect:/vehicles?action=list";
        }

        if (isModernVehicleType(vehicleType) || isModernVehicleType(params.get("vehicleType"))) {
            try {
                String targetId = ("update".equals(action) && idParam != null && !idParam.isBlank())
                        ? idParam.trim()
                        : "V-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

                String imagePath = "/images/default-vehicle.png";

                if (imageFile != null && !imageFile.isEmpty()) {
                    imagePath = imageStorageService.saveVehicleImage(imageFile, targetId);
                } else if ("update".equals(action) && idParam != null && !idParam.isBlank()) {
                    Optional<Vehicle> existingVehicle = vehicleService.findById(targetId);
                    if (existingVehicle.isPresent()) imagePath = existingVehicle.get().getImagePath();
                }

                Vehicle toSave = buildVehicleLegacy(targetId, params, imagePath);
                try {
                    vehicleService.save(toSave);
                } catch (ValidationException ex) {
                    if ("update".equals(action) && idParam != null && !idParam.isBlank()) {
                        return "redirect:/vehicles/" + idParam.trim() + "/edit?error=" + ex.getMessage();
                    }
                    return "redirect:/vehicles/new?error=" + ex.getMessage();
                }
                return "redirect:/vehicles";
            } catch (NumberFormatException ex) {
                return "redirect:/vehicles/new?error=invalid_number";
            }
        }
        return handleLegacyPost(params);
    }

    private static boolean isModernVehicleType(String vehicleType) {
        return vehicleType != null && ("CAR".equalsIgnoreCase(vehicleType) || "MOTORCYCLE".equalsIgnoreCase(vehicleType));
    }

    private String handleLegacyPost(Map<String, String> params) {
        String action = params.get("action");
        try {
            if ("update".equals(action)) {
                String id = normalized(params.get("id"));
                String existingImage = vehicleService.findById(id).map(Vehicle::getImagePath).orElse("/images/default-vehicle.png");
                try {
                    vehicleService.save(buildVehicleLegacy(id, params, existingImage));
                } catch (ValidationException ex) { return "redirect:/vehicles/" + id + "/edit?error=" + ex.getMessage(); }
            } else {
                String id = "V-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
                try {
                    vehicleService.save(buildVehicleLegacy(id, params, "/images/default-vehicle.png"));
                } catch (ValidationException ex) { return "redirect:/vehicles/new?error=" + ex.getMessage(); }
            }
            return "redirect:/vehicles?action=list";
        } catch (NumberFormatException ex) { return "redirect:/vehicles/new?error=invalid_number"; }
    }

    private Vehicle buildVehicleLegacy(String id, Map<String, String> params, String imagePath) {
        String brand = normalized(params.get("brand"));
        String model = normalized(params.get("model"));
        double rate = Double.parseDouble(normalized(params.get("rate")));
        String vType = normalized(params.get("vehicleType"));
        String status = normalized(params.get("status"));
        if (status.isEmpty()) status = "AVAILABLE";

        if ("Motorcycle".equalsIgnoreCase(vType)) {
            String helmetValue = params.get("helmetIncluded");
            boolean helmetIncluded = "on".equalsIgnoreCase(helmetValue) || "true".equalsIgnoreCase(helmetValue);
            return new Motorcycle(id, brand, model, rate, helmetIncluded, imagePath, status);
        }
        int capacity = params.get("capacity") != null && !params.get("capacity").isEmpty() ? Integer.parseInt(normalized(params.get("capacity"))) : 4;
        return new Car(id, brand, model, rate, capacity, imagePath, status);
    }

    private static String normalized(String value) { return value == null ? "" : value.trim(); }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, HttpSession session, RedirectAttributes ra) {
        if (!admin(session)) return "redirect:/auth/staff-login";
        try { vehicleService.deleteById(id); }
        catch (IllegalStateException ex) { ra.addAttribute("error", ex.getMessage()); }
        return "redirect:/vehicles";
    }
}