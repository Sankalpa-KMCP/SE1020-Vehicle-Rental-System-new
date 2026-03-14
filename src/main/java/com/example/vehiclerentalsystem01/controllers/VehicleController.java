package com.example.vehiclerentalsystem01.controllers;

import com.example.vehiclerentalsystem01.dao.VehicleDAO;
import com.example.vehiclerentalsystem01.models.Car;
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
    private VehicleDAO vehicleDAO = new VehicleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            String id = request.getParameter("id");
            vehicleDAO.deleteVehicle(id);
            response.sendRedirect("vehicles?action=list");
        } else {
            // Default list view
            List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
            request.setAttribute("vehicleList", vehicles);
            request.getRequestDispatcher("vehicle-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String brand = request.getParameter("brand");
        String model = request.getParameter("model");
        double rate = Double.parseDouble(request.getParameter("rate"));
        int capacity = Integer.parseInt(request.getParameter("capacity"));

        String id = "V-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        Vehicle newCar = new Car(id, brand, model, rate, capacity);
        vehicleDAO.saveVehicle(newCar);

        response.sendRedirect("vehicles?action=list");
    }
}