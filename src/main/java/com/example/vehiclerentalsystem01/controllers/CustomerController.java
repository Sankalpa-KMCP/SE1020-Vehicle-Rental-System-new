package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.CustomerDAO;
import com.example.vehiclerentalsystem01.models.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/customers")
public class CustomerController extends HttpServlet {
    private CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // DEBUGGER: This will print to your IntelliJ/Eclipse terminal!
        System.out.println("==== CUSTOMER CONTROLLER TRIGGERED ====");
        System.out.println("Action Requested: " + action);

        try {
            if ("delete".equals(action)) {
                customerDAO.deleteCustomer(request.getParameter("id"));
                response.sendRedirect("customers?action=view_directory");

            } else if ("edit".equals(action)) {
                Customer existingCustomer = customerDAO.getCustomerById(request.getParameter("id"));
                if(existingCustomer != null) {
                    request.setAttribute("customer", existingCustomer);
                    request.getRequestDispatcher("/edit-customer.jsp").forward(request, response);
                } else {
                    response.sendRedirect("customers?action=view_directory");
                }

            } else {
                System.out.println("Fetching all customers from database...");
                List<Customer> allCustomers = customerDAO.getAllCustomers();

                // CRITICAL CRASH FIX: If the file is empty and DAO returns null, make an empty list so it doesn't crash!
                if (allCustomers == null) {
                    System.out.println("WARNING: DAO returned null! Creating an empty safety list.");
                    allCustomers = new ArrayList<>();
                }

                int totalGuests = allCustomers.size();
                int verifiedGuests = 0;

                for (Customer c : allCustomers) {
                    boolean hasNic = c.getNic() != null && !c.getNic().trim().isEmpty() && !c.getNic().equalsIgnoreCase("N/A");
                    boolean hasLicense = c.getLicenseNumber() != null && !c.getLicenseNumber().trim().isEmpty() && !c.getLicenseNumber().equalsIgnoreCase("N/A");

                    if (hasNic && hasLicense) {
                        verifiedGuests++;
                    }
                }

                System.out.println("Total Guests Found: " + totalGuests);

                request.setAttribute("customerList", allCustomers);
                request.setAttribute("totalGuests", totalGuests);
                request.setAttribute("verifiedGuests", verifiedGuests);
                request.setAttribute("unverifiedGuests", totalGuests - verifiedGuests);

                System.out.println("Forwarding safely to /customer-dashboard.jsp...");
                request.getRequestDispatcher("/customer-dashboard.jsp").forward(request, response);
            }
        } catch (Exception e) {
            // If it still crashes, it will print the exact reason in red text in your IDE!
            System.out.println("!!! MASSIVE CRASH IN CUSTOMER CONTROLLER !!!");
            e.printStackTrace();
            // Send back to admin home safely instead of index.jsp
            response.sendRedirect("admin-home.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");

        String nic = request.getParameter("nic");
        if (nic == null || nic.trim().isEmpty()) nic = "N/A";

        String license = request.getParameter("license");
        if (license == null || license.trim().isEmpty()) license = "N/A";

        if ("update".equals(action)) {
            String id = request.getParameter("id");
            Customer updatedCustomer = new Customer(id, name, email, username, password, nic, license, phone);
            customerDAO.updateCustomer(updatedCustomer);
        } else {
            String newId = "C-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            Customer newCustomer = new Customer(newId, name, email, username, password, nic, license, phone);
            customerDAO.saveCustomer(newCustomer);
        }

        HttpSession session = request.getSession();
        Boolean isAdmin = (Boolean) session.getAttribute("adminLoggedIn");
        if (isAdmin != null && isAdmin) {
            response.sendRedirect("customers?action=view_directory");
        } else {
            response.sendRedirect("index.jsp?success=registered");
        }
    }
}