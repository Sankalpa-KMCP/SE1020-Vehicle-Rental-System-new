package com.sliit.vehiclerentalsystem01.core.presentation;

import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
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

    private static final int FLEET_PREVIEW_MAX = 6;
    private static final int FLEET_PAGE_MAX = 50;

    private final VehicleService vehicleService;

    public WebUIController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping({"/", "/index"})
    public String index(HttpSession session, Model model) {
        applyLandingNav(session, model);
        model.addAttribute("activeNav", "home");
        model.addAttribute("pageTitle", "DriveAway | Premium Vehicle Rentals");
        model.addAttribute("fleetPreview", buildFleetPreview(3));
        return "landing/index";
    }

    @GetMapping("/features")
    public String features(HttpSession session, Model model) {
        applyLandingNav(session, model);
        model.addAttribute("activeNav", "features");
        model.addAttribute("pageTitle", "Features | DriveAway");
        return "landing/features";
    }

    @GetMapping("/how-it-works")
    public String howItWorks(HttpSession session, Model model) {
        applyLandingNav(session, model);
        model.addAttribute("activeNav", "how-it-works");
        model.addAttribute("pageTitle", "How It Works | DriveAway");
        return "landing/how-it-works";
    }

    @GetMapping("/fleet")
    public String fleetPage(HttpSession session, Model model) {
        applyLandingNav(session, model);
        model.addAttribute("activeNav", "fleet");
        model.addAttribute("pageTitle", "Our Fleet | DriveAway");
        model.addAttribute("fleetPreview", buildFleetPreview(FLEET_PAGE_MAX));
        return "landing/fleet-page";
    }

    @GetMapping("/testimonials")
    public String testimonials(HttpSession session, Model model) {
        applyLandingNav(session, model);
        model.addAttribute("activeNav", "testimonials");
        model.addAttribute("pageTitle", "Reviews | DriveAway");
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
        int n = 0;
        for (Vehicle v : all) {
            if (n >= max) {
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
        return fleetPreview;
    }

    private void applyPublicNavDisplayName(HttpSession session, Model model) {
        if (session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.CUSTOMER_LOGGED_IN))) {
            Object u = session.getAttribute(UserSessionKeys.USERNAME);
            model.addAttribute("displayName", u != null ? u.toString() : "Customer");
        }
    }
}
