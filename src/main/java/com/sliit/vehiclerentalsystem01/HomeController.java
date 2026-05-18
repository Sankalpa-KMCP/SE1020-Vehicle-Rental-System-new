package com.sliit.vehiclerentalsystem01;

import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.vehicle.model.Vehicle;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private static final int FLEET_PREVIEW_MAX = 6;

    private final VehicleService vehicleService;

    public HomeController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        String displayName = "";
        String userRole = "";

        if (session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.CUSTOMER_LOGGED_IN))) {
            Object name = session.getAttribute("name");
            displayName = name != null ? name.toString() : "";
            if (displayName.isBlank()) {
                Object u = session.getAttribute(UserSessionKeys.USERNAME);
                displayName = u != null ? u.toString() : "Customer";
            }
            userRole = "customer";
        } else if (session != null && (Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.STAFF_LOGGED_IN))
                || Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN)))) {
            Object name = session.getAttribute("name");
            displayName = name != null ? name.toString() : "";
            if (displayName.isBlank()) {
                displayName = "Staff";
            }
            userRole = "staff";
        }

        model.addAttribute("displayName", displayName);
        model.addAttribute("userRole", userRole);

        List<Vehicle> all = vehicleService.findAll();
        List<FleetPreviewItem> fleetPreview = new ArrayList<>();
        int n = 0;
        for (Vehicle v : all) {
            if (n >= FLEET_PREVIEW_MAX) {
                break;
            }
            n++;
            String vehicleType = v.getVehicleType() != null ? v.getVehicleType().toLowerCase() : "";
            String category = "sedan";
            if (vehicleType.contains("suv") || vehicleType.contains("4x4")) {
                category = "suv";
            } else if (vehicleType.contains("luxury")) {
                category = "luxury";
            }
            String imageUrl = "https://images.unsplash.com/photo-1494976388531-d1058494cdd8?q=80&w=600&auto=format&fit=crop";
            if (vehicleType.contains("suv")) {
                imageUrl = "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?q=80&w=600&auto=format&fit=crop";
            } else if (vehicleType.contains("luxury")) {
                imageUrl = "https://images.unsplash.com/photo-1503376760367-1b6121f1cd9a?q=80&w=600&auto=format&fit=crop";
            } else if (vehicleType.contains("hatchback")) {
                imageUrl = "https://images.unsplash.com/photo-1511919884226-fd3cad34687c?q=80&w=600&auto=format&fit=crop";
            }
            long rate = Math.round(v.calculateRentalRate());
            fleetPreview.add(new FleetPreviewItem(
                    v.getId(),
                    v.getBrand(),
                    v.getModel(),
                    v.getVehicleType(),
                    v.getSpecificDetail(),
                    category,
                    imageUrl,
                    rate
            ));
        }
        model.addAttribute("fleetPreview", fleetPreview);
        return "landing/index";
    }
}
