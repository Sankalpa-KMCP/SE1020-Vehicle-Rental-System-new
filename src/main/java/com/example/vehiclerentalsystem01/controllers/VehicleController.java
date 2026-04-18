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

@WebServlet("/vehicles")  //Servlet Mapping
public class VehicleController extends HttpServlet { //we are inheriting from the jakartha lib for web properties

    private VehicleDAO vehicleDAO = new VehicleDAO();  //to call methods on we need a object

    @Override
    //read (when GET request) . To shao all vehicles
    //protected because the parent class will be accessing the method.
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action"); //to get use input (we cant use scanner because we are dealing with mouse clicks as well)
                                                           //user input data will be labelled (by identifier s string) as action
        if ("delete".equals(action)) {
            String id = request.getParameter("id"); //saving what exactly to delete.
            vehicleDAO.deleteVehicle(id);
            response.sendRedirect("vehicles?action=list"); //desitantion
        } else {
            // Default list view
            List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
            request.setAttribute("vehicleList", vehicles);
            request.getRequestDispatcher("vehicle-dashboard.jsp").forward(request, response);
        }
    }
    //write (when POST (enter) requested). To add new vehicle data.
    @Override
    //input output  are the parameters
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 1. Grab all inputs from the add-vehicle.jsp form
            String type = request.getParameter("type");
            String brand = request.getParameter("brand");
            String model = request.getParameter("model");
            double rate = Double.parseDouble(request.getParameter("rate"));

            // THE FIX: Grab "extraDetail" instead of "capacity"
            String extraDetail = request.getParameter("extraDetail");

            // 2. Generate a standard ID (e.g., V-4829)
            int randomId = (int) (Math.random() * 9000) + 1000;
            String id = "V-" + randomId;

            // 3. Create the correct object based on the dropdown selection
            Vehicle newVehicle;
            if ("Car".equals(type)) {
                int capacity = Integer.parseInt(extraDetail);
                newVehicle = new Car(id, brand, model, rate, capacity);
            } else {
                boolean helmet = Boolean.parseBoolean(extraDetail);
                newVehicle = new Motorcycle(id, brand, model, rate, helmet);
            }

            // 4. Save and redirect
            vehicleDAO.saveVehicle(newVehicle);
            response.sendRedirect("vehicles?action=list");

        } catch (Exception e) {
            // Prevents a 500 crash if someone types letters in the number box
            System.out.println("Invalid input detected! Error: " + e.getMessage());
            response.sendRedirect("add-vehicle.jsp?error=invalid_input");
        }
    }
}