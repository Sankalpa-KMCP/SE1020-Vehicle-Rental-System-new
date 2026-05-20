package com.sliit.vehiclerentalsystem01.core.presentation;

import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Car;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Motorcycle;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Vehicle;
import com.sliit.vehiclerentalsystem01.vehicle.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class WebUIController {

    private static final int FLEET_PREVIEW_MAX = 8;
    private static final int FLEET_PAGE_MAX = 50;

    private final VehicleService vehicleService;

    public WebUIController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping({"/", "/index"})
    public String index(HttpSession session, Model model) {
        applyLandingNav(session, model);
        model.addAttribute("activeNav", "home");
        model.addAttribute("pageTitle", "DriveAway | Car & Motorcycle Rentals");
        model.addAttribute("fleetPreview", buildFleetPreview(FLEET_PREVIEW_MAX));
        return "landing/index";
    }

    @GetMapping("/features")
    public String features(HttpSession session, Model model) {
        applyLandingNav(session, model);
        model.addAttribute("activeNav", "features");
        model.addAttribute("pageTitle", "Why DriveAway | Car & Motorcycle Rentals");
        return "landing/features";
    }

    @GetMapping("/how-it-works")
    public String howItWorks(HttpSession session, Model model) {
        applyLandingNav(session, model);
        model.addAttribute("activeNav", "how-it-works");
        model.addAttribute("pageTitle", "How to Rent | DriveAway");
        return "landing/how-it-works";
    }

    @GetMapping("/fleet")
    public String fleetPage(HttpSession session, Model model) {
        applyLandingNav(session, model);
        model.addAttribute("activeNav", "fleet");
        model.addAttribute("pageTitle", "Our Vehicles | DriveAway");
        model.addAttribute("fleetPreview", buildFleetPreview(FLEET_PAGE_MAX));
        return "landing/fleet-page";
    }

    @GetMapping("/testimonials")
    public String testimonials(HttpSession session, Model model) {
        applyLandingNav(session, model);
        model.addAttribute("activeNav", "testimonials");
        model.addAttribute("pageTitle", "Customer Reviews | DriveAway");
        return "landing/testimonials";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session == null) {
            return "redirect:/auth/login";
        }
        Object role = session.getAttribute(UserSessionKeys.AUTH_ROLE);
        if (role == null) {
            redirectAttributes.addFlashAttribute("error", "Please sign in");
            return "redirect:/auth/login";
        }
        String r = role.toString();
        if ("ROLE_ADMIN".equals(r)) {
            return "redirect:/user/admin-dashboard";
        }
        if ("ROLE_STAFF".equals(r)) {
            return "redirect:/staff?action=list";
        }
        if ("ROLE_CUSTOMER".equals(r)) {
            return "redirect:/customer/home";
        }
        return "redirect:/";
    }

    @GetMapping("/public/about")
    public String about(HttpSession session, Model model) {
        applyPublicNavDisplayName(session, model);
        return "public/about";
    }

    @GetMapping("/public/contact")
    public String contact(HttpSession session, Model model) {
        applyPublicNavDisplayName(session, model);
        return "public/contact";
    }

    @GetMapping("/public/terms")
    public String terms(HttpSession session, Model model) {
        applyPublicNavDisplayName(session, model);
        return "public/terms";
    }

    @GetMapping("/public/privacy")
    public String privacy(HttpSession session, Model model) {
        applyPublicNavDisplayName(session, model);
        return "public/privacy";
    }

    private void applyLandingNav(HttpSession session, Model model) {
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
    }

    private List<FleetPreviewItem> buildFleetPreview(int max) {
        List<Vehicle> all = vehicleService.findAll();
        List<FleetPreviewItem> fleetPreview = new ArrayList<>();
        
        int sedanCount = 0;
        int suvCount = 0;
        int luxuryCount = 0;
        int motorcycleCount = 0;

        for (Vehicle v : all) {
            String model = v.getModel() != null ? v.getModel().toLowerCase().trim() : "";
            String brand = v.getBrand() != null ? v.getBrand().toLowerCase().trim() : "";

            String category = "sedan";
            String displayType = "Premium Sedan";

            if (v instanceof Motorcycle) {
                category = "motorcycle";
                displayType = "Motorcycle";
                if (model.contains("cbr")) {
                    displayType = "Sport Motorcycle";
                } else if (model.contains("r15")) {
                    displayType = "Sport Motorcycle";
                } else if (model.contains("gixxer")) {
                    displayType = "Naked Motorcycle";
                } else if (model.contains("ninja")) {
                    displayType = "Superbike";
                } else if (model.contains("primavera")) {
                    displayType = "Classic Scooter";
                } else if (model.contains("iron 883")) {
                    displayType = "Cruiser Motorcycle";
                } else if (model.contains("panigale")) {
                    category = "luxury";
                    displayType = "Exotic Superbike";
                }
            } else {
                if (model.contains("outlander")) {
                    category = "suv";
                    displayType = "Crossover SUV";
                } else if (model.contains("mustang")) {
                    category = "luxury";
                    displayType = "Muscle Car";
                } else if (model.contains("model 3")) {
                    category = "luxury";
                    displayType = "Premium Electric Sedan";
                } else if (model.contains("3 series") || brand.contains("bmw")) {
                    category = "luxury";
                    displayType = "Luxury Sedan";
                } else if (model.contains("aqua")) {
                    category = "sedan";
                    displayType = "Hybrid Hatchback";
                } else if (model.contains("leaf")) {
                    category = "sedan";
                    displayType = "Electric Hatchback";
                } else if (model.contains("elantra")) {
                    category = "sedan";
                    displayType = "Premium Sedan";
                } else if (model.contains("wagon r")) {
                    category = "sedan";
                    displayType = "Compact Hatchback";
                }
            }

            // Enforce diversity for landing page preview (max < 15)
            if (max < 15) {
                if (category.equals("sedan") && sedanCount >= 2) continue;
                if (category.equals("suv") && suvCount >= 2) continue;
                if (category.equals("luxury") && luxuryCount >= 2) continue;
                if (category.equals("motorcycle") && motorcycleCount >= 2) continue;
            }

            // Fallback image urls if the loaded image is default or blank
            String imageUrl = v.getImagePath();
            if (imageUrl == null || imageUrl.isBlank() || imageUrl.contains("default-vehicle.png")) {
                if (category.equals("suv")) {
                    imageUrl = "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?q=80&w=600&auto=format&fit=crop";
                } else if (category.equals("luxury")) {
                    imageUrl = "https://images.unsplash.com/photo-1614162692292-7ac56d7f7f1e?q=80&w=600&auto=format&fit=crop";
                } else if (category.equals("motorcycle")) {
                    imageUrl = "https://images.unsplash.com/photo-1558981806-ec527fa84c39?q=80&w=600&auto=format&fit=crop";
                } else {
                    imageUrl = "https://images.unsplash.com/photo-1494976388531-d1058494cdd8?q=80&w=600&auto=format&fit=crop";
                }
            }

            // Increment the counter for selected category
            if (category.equals("sedan")) sedanCount++;
            else if (category.equals("suv")) suvCount++;
            else if (category.equals("luxury")) luxuryCount++;
            else if (category.equals("motorcycle")) motorcycleCount++;

            long rate = Math.round(v.calculateRentalRate());
            fleetPreview.add(new FleetPreviewItem(
                    v.getId(),
                    v.getBrand(),
                    v.getModel(),
                    displayType,
                    v.getSpecificDetail(),
                    category,
                    imageUrl,
                    rate
            ));

            if (fleetPreview.size() >= max) {
                break;
            }
        }
        return fleetPreview;
    }

    private void applyPublicNavDisplayName(HttpSession session, Model model) {
        if (session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.CUSTOMER_LOGGED_IN))) {
            Object u = session.getAttribute(UserSessionKeys.USERNAME);
            model.addAttribute("displayName", u != null ? u.toString() : "Customer");
        }
    }
}
