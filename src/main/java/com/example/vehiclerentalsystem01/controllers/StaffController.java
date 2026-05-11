package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.StaffDAO;
import com.example.vehiclerentalsystem01.models.AdminStaff;
import com.example.vehiclerentalsystem01.models.Staff;
import com.example.vehiclerentalsystem01.models.SupportStaff;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/staff")
public class StaffController {
    private final StaffDAO staffDAO = new StaffDAO();

    @GetMapping
    public String handleGet(@RequestParam(required = false) String action,
                            @RequestParam(required = false) String id,
                            Model model) {
        if ("delete".equals(action)) {
            staffDAO.deleteStaff(id);
            return "redirect:/staff?action=list";
        }

        if ("edit".equals(action)) {
            Staff staff = staffDAO.getStaffById(id);
            if (staff == null) {
                return "redirect:/staff?action=list";
            }
            model.addAttribute("staff", staff);
            return "edit-staff";
        }

        model.addAttribute("staffList", staffDAO.getAllStaff());
        return "staff-dashboard";
    }

    @PostMapping
    public String handlePost(@RequestParam Map<String, String> params) {
        String action = params.get("action");
        String role = params.get("role");
        String id = params.get("id");
        String name = params.get("name");
        String email = params.get("email");
        String phone = params.get("phone");
        String username = params.get("username");
        String password = params.get("password");

        if ("update".equals(action)) {
            Staff updatedStaff = "Admin".equals(role)
                    ? new AdminStaff(id, name, email, phone, username, password)
                    : new SupportStaff(id, name, email, phone, username, password);
            staffDAO.updateStaff(updatedStaff);
        } else {
            String manualId = params.get("manualId");
            String newId = manualId != null && !manualId.trim().isEmpty()
                    ? manualId.trim()
                    : "EMP-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            Staff newStaff = "Admin".equals(role)
                    ? new AdminStaff(newId, name, email, phone, username, password)
                    : new SupportStaff(newId, name, email, phone, username, password);
            staffDAO.saveStaff(newStaff);
        }

        return "redirect:/staff?action=list";
    }
}
