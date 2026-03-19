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

        try {
            // 1. Handle Deletions (from the table view)
            if ("delete".equals(action)) {
                customerDAO.deleteCustomer(request.getParameter("id"));
                response.sendRedirect("customers?action=view_directory");

                // 2. Handle Edit Page Requests
            } else if ("edit".equals(action)) {
                Customer existingCustomer = customerDAO.getCustomerById(request.getParameter("id"));
                if(existingCustomer != null) {
                    request.setAttribute("customer", existingCustomer);
                    request.getRequestDispatcher("/edit-customer.jsp").forward(request, response);
                } else {
                    response.sendRedirect("customers?action=view_directory");
                }

                // 3. Handle Directory & Search Views
            } else {
                List<Customer> allCustomers = customerDAO.getAllCustomers();
                if (allCustomers == null) allCustomers = new ArrayList<>();

                int totalGuests = allCustomers.size();
                int verifiedGuests = 0;

                for (Customer c : allCustomers) {
                    boolean hasNic = c.getNic() != null && !c.getNic().trim().isEmpty() && !c.getNic().equalsIgnoreCase("N/A");
                    boolean hasLicense = c.getLicenseNumber() != null && !c.getLicenseNumber().trim().isEmpty() && !c.getLicenseNumber().equalsIgnoreCase("N/A");

                    if (hasNic && hasLicense) verifiedGuests++;
                }

                // ==========================================
                // THE NEW SEARCH LOGIC
                // ==========================================
                if ("search".equals(action)) {
                    String searchUsername = request.getParameter("searchUsername");
                    if (searchUsername != null && !searchUsername.trim().isEmpty()) {
                        for (Customer c : allCustomers) {
                            // Check if the typed username matches a database username (ignoring case)
                            if (c.getUsername().equalsIgnoreCase(searchUsername.trim())) {
                                request.setAttribute("foundCustomer", c);
                                break; // Stop searching once we find them!
                            }
                        }
                    }
                }

                request.setAttribute("customerList", allCustomers);
                request.setAttribute("totalGuests", totalGuests);
                request.setAttribute("verifiedGuests", verifiedGuests);
                request.setAttribute("unverifiedGuests", totalGuests - verifiedGuests);

                request.getRequestDispatcher("/customer-dashboard.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("admin-home.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // Handle POST deletions from the Search Profile view
        if ("delete".equals(action)) {
            customerDAO.deleteCustomer(request.getParameter("id"));
            response.sendRedirect("customers?action=view_directory");
            return;
        }

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