package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.CustomerDAO;
import com.example.vehiclerentalsystem01.dao.StaffDAO;
import com.example.vehiclerentalsystem01.models.Customer;
import com.example.vehiclerentalsystem01.models.Staff;
import com.example.vehiclerentalsystem01.services.SessionManager;
import com.example.vehiclerentalsystem01.services.FileStorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private StaffDAO staffDAO;
    
    @Autowired
    private CustomerDAO customerDAO;
    
    @Autowired
    private SessionManager sessionManager;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    private static final String PENDING_ADMINS_FILE = "pending_admins.txt";

    @GetMapping
    public String showLogin() {
        return "redirect:/admin-login.jsp";
    }

    @PostMapping
    public String handlePost(@RequestParam(required = false) String action,
                             @RequestParam(required = false) String username,
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false, name = "new_username") String newUsername,
                             @RequestParam(required = false, name = "new_password") String newPassword,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            if ("login".equals(action)) {
                Staff loggedInStaff = staffDAO.validateStaffLogin(username, password);
                if (loggedInStaff == null) {
                    redirectAttributes.addFlashAttribute("error", "Invalid username or password");
                    return "redirect:/admin-login.jsp?error=invalid";
                }

                sessionManager.createAdminSession(session, loggedInStaff.getUsername(), 
                    loggedInStaff.getName(), "Admin".equalsIgnoreCase(loggedInStaff.getRole()));
                return "redirect:/admin-home.jsp";
            }

        if ("customer_login".equals(action)) {
            Customer loggedInCustomer = customerDAO.validateCustomer(username, password);
            if (loggedInCustomer == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid username or password");
                return "redirect:/login.jsp?error=invalid";
            }

            sessionManager.createCustomerSession(session, loggedInCustomer.getId(), loggedInCustomer.getUsername());
            return "redirect:/customer-home.jsp";
        }

        if ("request_access".equals(action)) {
            List<String> lines = new ArrayList<>();
            lines.add(newUsername + "," + newPassword + ",PENDING");
            fileStorageService.writeToFile(PENDING_ADMINS_FILE, lines, true);
            return "redirect:/admin-login.jsp?success=requested";
        }

        if ("logout".equals(action)) {
            sessionManager.invalidateSession(session);
            return "redirect:/login.jsp";
        }

        return "redirect:/admin-login.jsp?error=unknown_action";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Authentication error: " + e.getMessage());
            return "redirect:/error";
        }
    }
}
