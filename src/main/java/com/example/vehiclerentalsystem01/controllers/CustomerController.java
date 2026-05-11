package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.BookingDAO;
import com.example.vehiclerentalsystem01.dao.CustomerDAO;
import com.example.vehiclerentalsystem01.models.Booking;
import com.example.vehiclerentalsystem01.models.Customer;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerDAO customerDAO;
    private final BookingDAO bookingDAO;

    @Autowired
    public CustomerController(CustomerDAO customerDAO, BookingDAO bookingDAO) {
        this.customerDAO = customerDAO;
        this.bookingDAO = bookingDAO;
    }

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

    private void loadDashboard(Model model) {
        List<Customer> allCustomers = customerDAO.getAllCustomers();
        if (allCustomers == null) {
            allCustomers = new ArrayList<>();
        }

        int verifiedGuests = 0;
        for (Customer customer : allCustomers) {
            if (customer.isVerified()) {
                verifiedGuests++;
            }
        }

        model.addAttribute("customerList", allCustomers);
        model.addAttribute("totalGuests", allCustomers.size());
        model.addAttribute("verifiedGuests", verifiedGuests);
        model.addAttribute("unverifiedGuests", allCustomers.size() - verifiedGuests);
    }

    @GetMapping
    public String handleGet(@RequestParam(required = false, defaultValue = "view_directory") String action,
                            @RequestParam(required = false) String id,
                            @RequestParam(required = false) String searchUsername,
                            HttpSession session,
                            Model model) {
        try {
            if ("delete".equals(action)) {
                String customerId = normalized(id);
                if (!customerId.isEmpty()) {
                    customerDAO.deleteCustomer(customerId);
                }
                return "redirect:/customers?action=view_directory&msg=deleted";
            }

            if ("edit".equals(action)) {
                Customer existingCustomer = customerDAO.getCustomerById(normalized(id));
                if (existingCustomer == null) {
                    return "redirect:/customers?action=view_directory&error=not_found";
                }
                model.addAttribute("customer", existingCustomer);
                model.addAttribute("isCustomerMode", false);
                return "edit-customer";
            }

            if ("editSelf".equals(action)) {
                boolean isCustomerLoggedIn = session != null && Boolean.TRUE.equals(session.getAttribute("customerLoggedIn"));
                if (!isCustomerLoggedIn) {
                    return "redirect:/login.jsp";
                }

                Customer loggedInCustomer = customerDAO.getCustomerByUsername(normalized((String) session.getAttribute("username")));
                if (loggedInCustomer == null) {
                    return "redirect:/login.jsp?error=profile_not_found";
                }

                model.addAttribute("customer", loggedInCustomer);
                model.addAttribute("isCustomerMode", true);
                return "edit-customer";
            }

            loadDashboard(model);

            if ("search".equals(action)) {
                String username = normalized(searchUsername);
                if (!username.isEmpty()) {
                    Customer foundCustomer = customerDAO.getCustomerByUsername(username);
                    if (foundCustomer != null) {
                        model.addAttribute("foundCustomer", foundCustomer);

                        List<Booking> customerBookings = new ArrayList<>();
                        for (Booking booking : bookingDAO.getAllBookings()) {
                            String owner = normalized(booking.getCustomerName());
                            if (!owner.isEmpty() && (owner.equalsIgnoreCase(foundCustomer.getUsername())
                                    || owner.equalsIgnoreCase(foundCustomer.getName()))) {
                                customerBookings.add(booking);
                            }
                        }
                        model.addAttribute("customerBookings", customerBookings);
                    }
                }
            }

            return "customer-dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin-home.jsp";
        }
    }

    @PostMapping
    public String handlePost(@RequestParam Map<String, String> params, HttpSession session) {
        String action = normalized(params.get("action"));
        if (action.isEmpty()) {
            action = "register";
        }

        if ("delete".equals(action)) {
            String id = normalized(params.get("id"));
            if (!id.isEmpty()) {
                customerDAO.deleteCustomer(id);
            }
            return "redirect:/customers?action=view_directory&msg=deleted";
        }

        String id = normalized(params.get("id"));
        String name = normalized(params.get("name"));
        String email = normalized(params.get("email"));
        String username = normalized(params.get("username"));
        String password = normalized(params.get("password"));
        String phone = normalized(params.get("phone"));
        String nic = normalizeOptionalDoc(params.get("nic"));
        String license = normalizeOptionalDoc(params.get("license"));

        if (isBlank(name) || isBlank(email) || isBlank(username) || isBlank(password) || isBlank(phone)) {
            if ("update".equals(action)) {
                boolean isAdmin = session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"));
                return isAdmin
                        ? "redirect:/customers?action=edit&id=" + id + "&error=missing_fields"
                        : "redirect:/customers?action=editSelf&error=missing_fields";
            }
            if ("create".equals(action)) {
                return "redirect:/customers?action=view_directory&error=missing_fields";
            }
            return "redirect:/register.jsp?error=missing_fields";
        }

        if (!"update".equals(action) && customerDAO.getCustomerByUsername(username) != null) {
            return "create".equals(action)
                    ? "redirect:/customers?action=view_directory&error=username_taken"
                    : "redirect:/register.jsp?error=username_taken";
        }

        if ("update".equals(action)) {
            customerDAO.updateCustomer(new Customer(id, name, email, username, password, nic, license, phone));
            boolean isAdmin = session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"));
            return isAdmin
                    ? "redirect:/customers?action=view_directory&msg=updated"
                    : "redirect:/customers?action=editSelf&msg=updated";
        }

        String newId = "C-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        customerDAO.saveCustomer(new Customer(newId, name, email, username, password, nic, license, phone));

        if ("create".equals(action) || (session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn")))) {
            return "redirect:/customers?action=view_directory&msg=created";
        }
        return "redirect:/login.jsp?success=registered";
    }
}
