package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.VehicleDAO;
import com.example.vehiclerentalsystem01.models.Car;
import com.example.vehiclerentalsystem01.models.Motorcycle;
import com.example.vehiclerentalsystem01.models.Vehicle;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet("/vehicles")
public class VehicleController extends HttpServlet {
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    private String normalized(String value) {
        return value == null ? "" : value.trim();
    }

    private Vehicle buildVehicleFromRequest(String id, HttpServletRequest request) {
        String brand = normalized(request.getParameter("brand"));
        String model = normalized(request.getParameter("model"));
        double rate = Double.parseDouble(normalized(request.getParameter("rate")));
        String vehicleType = normalized(request.getParameter("vehicleType"));

        if ("Motorcycle".equalsIgnoreCase(vehicleType)) {
            String helmetValue = request.getParameter("helmetIncluded");
            boolean helmetIncluded = "on".equalsIgnoreCase(helmetValue) || "true".equalsIgnoreCase(helmetValue);
            return new Motorcycle(id, brand, model, rate, helmetIncluded);
        }

        int capacity = Integer.parseInt(normalized(request.getParameter("capacity")));
        return new Car(id, brand, model, rate, capacity);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            String id = request.getParameter("id");
            vehicleDAO.deleteVehicle(id);
            response.sendRedirect("vehicles?action=list");
        } else if ("edit".equals(action)) {
            String id = request.getParameter("id");
            Vehicle vehicle = vehicleDAO.getVehicleById(id);
            if (vehicle != null) {
                request.setAttribute("vehicle", vehicle);
                request.getRequestDispatcher("edit-vehicle.jsp").forward(request, response);
            } else {
                response.sendRedirect("vehicles?action=list");
            }
        } else {
            List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
            request.setAttribute("vehicleList", vehicles);
            request.getRequestDispatcher("vehicle-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        try {
            if ("update".equals(action)) {
                String id = request.getParameter("id");
                Vehicle updatedVehicle = buildVehicleFromRequest(id, request);
                vehicleDAO.updateVehicle(updatedVehicle);
            } else {
                String id = "V-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
                Vehicle newVehicle = buildVehicleFromRequest(id, request);
                vehicleDAO.saveVehicle(newVehicle);
            }

            response.sendRedirect("vehicles?action=list");
        } catch (NumberFormatException ex) {
            response.sendRedirect("add-vehicle.jsp?error=invalid_number");
        }
    }
} //