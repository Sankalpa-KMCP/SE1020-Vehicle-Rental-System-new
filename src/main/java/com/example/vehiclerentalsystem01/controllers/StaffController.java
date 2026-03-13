package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.StaffDAO;
import com.example.vehiclerentalsystem01.models.AdminStaff;
import com.example.vehiclerentalsystem01.models.Staff;
import com.example.vehiclerentalsystem01.models.SupportStaff;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet("/staff")
public class StaffController extends HttpServlet {
    private StaffDAO staffDAO = new StaffDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            String id = request.getParameter("id");
            staffDAO.deleteStaff(id);
            response.sendRedirect("staff?action=list");
        } else if ("edit".equals(action)) {
            String id = request.getParameter("id");
            Staff s = staffDAO.getStaffById(id);
            if(s != null) {
                request.setAttribute("staff", s);
                request.getRequestDispatcher("edit-staff.jsp").forward(request, response);
            } else {
                response.sendRedirect("staff?action=list");
            }
        } else {
            List<Staff> staffList = staffDAO.getAllStaff();
            request.setAttribute("staffList", staffList);
            request.getRequestDispatcher("staff-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ("update".equals(action)) {
            String id = request.getParameter("id");
            Staff updatedStaff = role.equals("Admin") ? new AdminStaff(id, name, email, phone, username, password) : new SupportStaff(id, name, email, phone, username, password);
            staffDAO.updateStaff(updatedStaff);
        } else {
            String manualId = request.getParameter("manualId");
            String newId = (manualId != null && !manualId.trim().isEmpty()) ? manualId.trim() : "EMP-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

            Staff newStaff = role.equals("Admin") ? new AdminStaff(newId, name, email, phone, username, password) : new SupportStaff(newId, name, email, phone, username, password);
            staffDAO.saveStaff(newStaff);
        }

        response.sendRedirect("staff?action=list");
    }
}