package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.StaffDAO;
import com.example.vehiclerentalsystem01.models.Staff;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@WebServlet("/auth")
public class AuthController extends HttpServlet {

    private static final String FILE_PATH =
            System.getProperty("user.home")
                    + File.separator + "RentalData"
                    + File.separator + "pending_admins.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin-login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        if ("login".equals(action)) {
            String user = request.getParameter("username");
            String pass = request.getParameter("password");

            StaffDAO staffDAO = new StaffDAO();
            Staff loggedInStaff = staffDAO.validateStaffLogin(user, pass);

            if (loggedInStaff != null) {
                session.setAttribute("adminLoggedIn", true);
                session.setAttribute("adminUser", loggedInStaff.getUsername());
                session.setAttribute("isMasterAdmin", "Admin".equalsIgnoreCase(loggedInStaff.getRole()));

                response.sendRedirect(request.getContextPath() + "/admin-home.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin-login.jsp?error=invalid");
            }

        } else if ("request_access".equals(action)) {
            String newUser = request.getParameter("new_username");
            String newPass = request.getParameter("new_password");

            File folder = new File(System.getProperty("user.home") + File.separator + "RentalData");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
                writer.write(newUser + "," + newPass + ",PENDING");
                writer.newLine();
            }

            response.sendRedirect(request.getContextPath() + "/admin-login.jsp?success=requested");

        } else {
            response.sendRedirect(request.getContextPath() + "/admin-login.jsp?error=unknown_action");
        }
    }
}