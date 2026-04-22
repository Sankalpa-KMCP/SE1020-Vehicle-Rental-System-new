package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.BookingDAO;
import com.example.vehiclerentalsystem01.dao.CustomerDAO;
import com.example.vehiclerentalsystem01.models.Booking;
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
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    private String normalized(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeOptionalDoc(String value) {
        String trimmed = normalized(value);
        return trimmed.isEmpty() ? "N/A" : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void loadDashboard(HttpServletRequest request) {
        List<Customer> allCustomers = customerDAO.getAllCustomers();
        if (allCustomers == null) allCustomers = new ArrayList<>();

        int totalGuests = allCustomers.size();
        int verifiedGuests = 0;

        for (Customer c : allCustomers) {
            if (c.isVerified()) verifiedGuests++;
        }

        request.setAttribute("customerList", allCustomers);
        request.setAttribute("totalGuests", totalGuests);
        request.setAttribute("verifiedGuests", verifiedGuests);
        request.setAttribute("unverifiedGuests", totalGuests - verifiedGuests);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "view_directory";
        }

        try {
            if ("delete".equals(action)) {
                String id = normalized(request.getParameter("id"));
                if (!id.isEmpty()) {
                    customerDAO.deleteCustomer(id);
                }
                response.sendRedirect("customers?action=view_directory&msg=deleted");
                return;
            }

            if ("edit".equals(action)) {
                String id = normalized(request.getParameter("id"));
                Customer existingCustomer = customerDAO.getCustomerById(id);
                if (existingCustomer == null) {
                    response.sendRedirect("customers?action=view_directory&error=not_found");
                    return;
                }

                request.setAttribute("customer", existingCustomer);
                request.setAttribute("isCustomerMode", false);
                request.getRequestDispatcher("/edit-customer.jsp").forward(request, response);
                return;
            }

            if ("editSelf".equals(action)) {
                HttpSession session = request.getSession(false);
                boolean isCustomerLoggedIn = session != null && Boolean.TRUE.equals(session.getAttribute("customerLoggedIn"));
                if (!isCustomerLoggedIn) {
                    response.sendRedirect("login.jsp");
                    return;
                }

                String username = normalized((String) session.getAttribute("username"));
                Customer loggedInCustomer = customerDAO.getCustomerByUsername(username);
                if (loggedInCustomer == null) {
                    response.sendRedirect("login.jsp?error=profile_not_found");
                    return;
                }

                request.setAttribute("customer", loggedInCustomer);
                request.setAttribute("isCustomerMode", true);
                request.getRequestDispatcher("/edit-customer.jsp").forward(request, response);
                return;
            }

            loadDashboard(request);

            if ("search".equals(action)) {
                String searchUsername = normalized(request.getParameter("searchUsername"));
                if (!searchUsername.isEmpty()) {
                    Customer foundCustomer = customerDAO.getCustomerByUsername(searchUsername);
                    if (foundCustomer != null) {
                        request.setAttribute("foundCustomer", foundCustomer);

                        List<Booking> customerBookings = new ArrayList<>();
                        for (Booking booking : bookingDAO.getAllBookings()) {
                            String owner = normalized(booking.getCustomerName());
                            if (!owner.isEmpty() && (
                                    owner.equalsIgnoreCase(foundCustomer.getUsername()) ||
                                            owner.equalsIgnoreCase(foundCustomer.getName())
                            )) {
                                customerBookings.add(booking);
                            }
                        }
                        request.setAttribute("customerBookings", customerBookings);
                    }
                }
            }

            request.getRequestDispatcher("/customer-dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("admin-home.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "register";
        }

        if ("delete".equals(action)) {
            String id = normalized(request.getParameter("id"));
            if (!id.isEmpty()) {
                customerDAO.deleteCustomer(id);
            }
            response.sendRedirect("customers?action=view_directory&msg=deleted");
            return;
        }

        String id = normalized(request.getParameter("id"));
        String name = normalized(request.getParameter("name"));
        String email = normalized(request.getParameter("email"));
        String username = normalized(request.getParameter("username"));
        String password = normalized(request.getParameter("password"));
        String phone = normalized(request.getParameter("phone"));
        String nic = normalizeOptionalDoc(request.getParameter("nic"));
        String license = normalizeOptionalDoc(request.getParameter("license"));

        if (isBlank(name) || isBlank(email) || isBlank(username) || isBlank(password) || isBlank(phone)) {
            if ("update".equals(action)) {
                HttpSession session = request.getSession(false);
                boolean isAdmin = session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"));
                if (isAdmin) {
                    response.sendRedirect("customers?action=edit&id=" + id + "&error=missing_fields");
                } else {
                    response.sendRedirect("customers?action=editSelf&error=missing_fields");
                }
            } else {
                response.sendRedirect("register.jsp?error=missing_fields");
            }
            return;
        }

        if (!"update".equals(action)) {
            Customer existingCustomer = customerDAO.getCustomerByUsername(username);
            if (existingCustomer != null) {
                response.sendRedirect("register.jsp?error=username_taken");
                return;
            }
        }

        if ("update".equals(action)) {
            Customer updatedCustomer = new Customer(id, name, email, username, password, nic, license, phone);
            customerDAO.updateCustomer(updatedCustomer);

            HttpSession session = request.getSession(false);
            boolean isAdmin = session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"));
            if (isAdmin) {
                response.sendRedirect("customers?action=view_directory&msg=updated");
            } else {
                response.sendRedirect("customers?action=editSelf&msg=updated");
            }
        } else {
            String newId = "C-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            Customer newCustomer = new Customer(newId, name, email, username, password, nic, license, phone);
            customerDAO.saveCustomer(newCustomer);

            HttpSession session = request.getSession(false);
            Boolean isAdmin = session != null ? (Boolean) session.getAttribute("adminLoggedIn") : null;
            if (isAdmin != null && isAdmin) {
                response.sendRedirect("customers?action=view_directory&msg=created");
            } else {
                response.sendRedirect("login.jsp?success=registered");
            }
        }
    }
}