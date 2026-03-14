package com.example.vehiclerentalsystem01.controllers;

//MYYYYYY NAMMMMEEEE OISSSSSSKNDAJKBDKJ

import com.example.vehiclerentalsystem01.dao.CustomerDAO;
import com.example.vehiclerentalsystem01.dao.StaffDAO;
import com.example.vehiclerentalsystem01.models.Customer;
import com.example.vehiclerentalsystem01.models.Staff;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.*;

@WebServlet("/auth")
public class AuthController extends HttpServlet {

    private static final String FILE_PATH = System.getProperty("user.home") + File.separator + "RentalData" + File.separator + "pending_admins.txt";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        //submit the code

        // ==========================================
        // 1. ADMIN/STAFF LOGIN (From admin-login.jsp)
        // ==========================================
        if ("login".equals(action)) {
            String user = request.getParameter("username");
            String pass = request.getParameter("password");

            // Look up the staff member in staff.txt using StaffDAO
            StaffDAO staffDAO = new StaffDAO();
            Staff loggedInStaff = staffDAO.validateStaffLogin(user, pass);

            if (loggedInStaff != null) {
                // Successfully found in the text file!
                session.setAttribute("adminLoggedIn", true);
                session.setAttribute("adminUser", loggedInStaff.getUsername());

                // If their role is "Admin", they get master privileges. Otherwise, false.
                boolean isMaster = "Admin".equals(loggedInStaff.getRole());
                session.setAttribute("isMasterAdmin", isMaster);

                response.sendRedirect("admin-home.jsp"); // To Admin Portal
            } else {
                // Not found or wrong password
                response.sendRedirect("admin-login.jsp?error=invalid");
            }
        }
        // ==========================================
        // 2. CUSTOMER LOGIN (From index.jsp)
        // ==========================================
        else if ("customer_login".equals(action)) {
            String user = request.getParameter("username");
            String pass = request.getParameter("password");

            CustomerDAO customerDAO = new CustomerDAO();

            // Assign the returned Customer object to a variable
            Customer loggedInCustomer = customerDAO.validateCustomer(user, pass);

            // Check if the customer exists (is not null)
            if (loggedInCustomer != null) {
                session.setAttribute("customerLoggedIn", true);
                session.setAttribute("customerUser", user);
                response.sendRedirect("customer-home.jsp"); // To Customer Portal
            } else {
                response.sendRedirect("index.jsp?error=invalid");
            }
        }
        // ==========================================
        // 3. STAFF ACCESS REQUEST
        // ==========================================
        //HIruka liyanage
        else if ("request_access".equals(action)) {
            String newUser = request.getParameter("new_username");
            String newPass = request.getParameter("new_password");

            File folder = new File(System.getProperty("user.home") + File.separator + "RentalData");
            if (!folder.exists()) folder.mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
                writer.write(newUser + "," + newPass + ",PENDING");
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            response.sendRedirect("admin-login.jsp?success=requested");
        }
    }
}