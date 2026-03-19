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
import java.util.List;
import java.util.UUID;
//we have to create the customer review part from the scratch

@WebServlet("/customers")
public class CustomerController extends HttpServlet {
    private CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            customerDAO.deleteCustomer(request.getParameter("id"));
            response.sendRedirect("customers?action=list");
        } else if ("edit".equals(action)) {
            Customer existingCustomer = customerDAO.getCustomerById(request.getParameter("id"));
            if(existingCustomer != null) {
                request.setAttribute("customer", existingCustomer);
                request.getRequestDispatcher("edit-customer.jsp").forward(request, response);
            } else {
                response.sendRedirect("customers?action=list");
            }
        } else {
            // 1. Get the list of all customers
            List<Customer> allCustomers = customerDAO.getAllCustomers();

            // 2. Set up counters
            int totalGuests = allCustomers.size();
            int verifiedGuests = 0;

            // 3. Loop through to check who has both NIC and License
            for (Customer c : allCustomers) {
                boolean hasNic = c.getNic() != null && !c.getNic().trim().isEmpty() && !c.getNic().equalsIgnoreCase("N/A");
                boolean hasLicense = c.getLicenseNumber() != null && !c.getLicenseNumber().trim().isEmpty() && !c.getLicenseNumber().equalsIgnoreCase("N/A");

                if (hasNic && hasLicense) {
                    verifiedGuests++;
                }
            }

            // 4. Calculate missing docs
            int unverifiedGuests = totalGuests - verifiedGuests;

            // 5. Send all this data to the JSP
            request.setAttribute("customerList", allCustomers);
            request.setAttribute("totalGuests", totalGuests);
            request.setAttribute("verifiedGuests", verifiedGuests);
            request.setAttribute("unverifiedGuests", unverifiedGuests);

            request.getRequestDispatcher("customer-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // Fetch all 8 fields from the form
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");

        // Handle optionals
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

        // Redirect logic
        HttpSession session = request.getSession();
        Boolean isAdmin = (Boolean) session.getAttribute("adminLoggedIn");
        if (isAdmin != null && isAdmin) {
            response.sendRedirect("customers?action=list");
        } else {
            response.sendRedirect("index.jsp?success=registered");
        }
    }
}