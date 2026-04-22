<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Vehicle" %>
<%@ page import="com.example.vehiclerentalsystem01.dao.VehicleDAO" %>
<%@ page import="com.example.vehiclerentalsystem01.dao.BookingDAO" %>
<%@ page import="java.util.Set" %>

<%
    // Security Check: Only logged-in customers can view the cars!
    Boolean customerLoggedIn = (Boolean) session.getAttribute("customerLoggedIn");
    if (customerLoggedIn == null || !customerLoggedIn) {
        response.sendRedirect("index.jsp");
        return;
    }

    // Fetch all vehicles from the text file using Student 2's DAO!
    VehicleDAO vehicleDAO = new VehicleDAO();
    List<Vehicle> availableVehicles = vehicleDAO.getAllVehicles();
    BookingDAO bookingDAO = new BookingDAO();
    Set<String> unavailableVehicleIds = bookingDAO.getUnavailableVehicleIds();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DriveAway | Browse Vehicles</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');
        body { font-family: 'Inter', sans-serif; background-color: #F3F4F6; }
        .fade-in { animation: fadeIn 0.5s ease-in-out; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
    </style>
</head>
<body class="min-h-screen p-6 md:p-10 relative">

<div class="max-w-7xl mx-auto flex justify-between items-center mb-10">
    <a href="customer-home.jsp" class="flex items-center gap-2 bg-white px-5 py-2.5 rounded-xl font-bold text-gray-800 shadow-sm border border-gray-200 hover:bg-gray-50 transition">
        <i class="ph-bold ph-arrow-left text-xl"></i>
        <span>Back to Portal</span>
    </a>
    <h1 class="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
        <i class="ph-fill ph-car-profile text-[#0D6EFD]"></i> Available Fleet
    </h1>
</div>

<div class="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 fade-in">

    <%
        int renderedVehicles = 0;
        if (availableVehicles != null && !availableVehicles.isEmpty()) {
            for (Vehicle v : availableVehicles) {
                if (unavailableVehicleIds.contains(v.getId())) {
                    continue;
                }
                renderedVehicles++;
    %>
    <div class="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden hover:-translate-y-2 hover:shadow-xl transition-all duration-300 flex flex-col">

        <div class="bg-slate-900 p-6 text-white relative overflow-hidden">
            <div class="absolute -right-6 -top-6 opacity-10">
                <i class="ph-fill ph-car text-9xl"></i>
            </div>
            <div class="relative z-10">
                <p class="text-blue-400 font-bold tracking-wider text-sm uppercase mb-1"><%= v.getBrand() %></p>
                <h2 class="text-2xl font-extrabold truncate"><%= v.getModel() %></h2>
            </div>
        </div>

        <div class="p-6 flex-grow">
            <div class="flex justify-between items-center mb-6">
                <div class="bg-blue-50 text-blue-700 font-bold px-3 py-1 rounded-lg text-sm flex items-center gap-1.5">
                    <i class="ph-bold ph-steering-wheel"></i> <%= v.getVehicleType() %>
                </div>
                <div class="bg-gray-100 text-gray-700 font-bold px-3 py-1 rounded-lg text-sm flex items-center gap-1.5">
                    <i class="ph-bold ph-list-checks"></i> <%= v.getSpecificDetail() %>
                </div>
            </div>

            <div class="border-t border-b border-gray-100 py-4 mb-6 text-center">
                <p class="text-gray-500 font-medium text-sm">Daily Rental Rate</p>
                <p class="text-3xl font-extrabold text-gray-900 mt-1">$<%= String.format("%.2f", v.calculateRentalRate()) %> <span class="text-lg text-gray-400 font-medium">/day</span></p>
            </div>

            <ul class="text-sm text-gray-600 space-y-2 font-medium mb-6">
                <li class="flex items-center gap-2"><i class="ph-bold ph-check-circle text-green-500 text-lg"></i> Vehicle ID: <%= v.getId() %></li>
                <li class="flex items-center gap-2"><i class="ph-bold ph-check-circle text-green-500 text-lg"></i> Fully Insured & Inspected</li>
            </ul>
        </div>

        <div class="p-4 bg-gray-50 border-t border-gray-100">
            <a href="add-booking.jsp?vehicleId=<%= v.getId() %>&rate=<%= v.calculateRentalRate() %>" class="w-full block text-center bg-[#0D6EFD] text-white font-bold py-3.5 rounded-xl hover:bg-blue-700 transition-colors shadow-md text-lg">
                Book This Vehicle
            </a>
        </div>
    </div>
    <%
            }
        }

        if (renderedVehicles == 0) {
    %>
    <div class="col-span-1 md:col-span-2 lg:col-span-3 text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-200 border-dashed">
        <i class="ph-fill ph-car-profile text-6xl text-gray-300 mb-4"></i>
        <h3 class="text-2xl font-bold text-gray-700">No Vehicles Available</h3>
        <p class="text-gray-500 mt-2">No vehicles are currently open for booking decisions.</p>
    </div>
    <% } %>

</div>

</body>
</html>
//