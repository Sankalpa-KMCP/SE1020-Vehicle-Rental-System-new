<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Vehicle" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Vehicle Fleet - DriveAway</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');
    body { font-family: 'Inter', sans-serif; }
  </style>
  <script>
    // Real-Time Search Filter
    function searchFleet() {
      let input = document.getElementById("searchBox").value.toUpperCase();
      let rows = document.querySelectorAll("#fleetTable tbody tr");
      rows.forEach(row => {
        let text = row.innerText.toUpperCase();
        row.style.display = text.includes(input) ? "" : "none";
      });
    }
  </script>
</head>
<body class="bg-[#F8F9FA] flex h-screen overflow-hidden text-gray-800 antialiased">

<aside class="w-64 bg-[#0B132B] text-white flex flex-col hidden md:flex shadow-xl z-10">
  <div class="h-20 flex items-center px-8 border-b border-white/10">
    <i class="ph-fill ph-car-profile text-3xl text-[#0D6EFD] mr-3"></i>
    <h1 class="text-2xl font-extrabold tracking-tight">DriveAway</h1>
  </div>
  <nav class="flex-1 px-4 py-8 space-y-3 overflow-y-auto">
    <a href="customers?action=list" class="flex items-center px-4 py-3.5 text-gray-400 hover:bg-white/5 hover:text-white rounded-xl transition-all font-medium">
      <i class="ph-bold ph-users text-xl mr-3"></i> Customers
    </a>
    <a href="vehicles?action=list" class="flex items-center px-4 py-3.5 bg-[#0D6EFD] text-white rounded-xl shadow-lg shadow-blue-900/20 font-bold transition-all">
      <i class="ph-bold ph-car text-xl mr-3"></i> Vehicle Fleet
    </a>
  </nav>
  <div class="p-6 border-t border-white/10">
    <a href="index.jsp" class="flex items-center text-gray-400 hover:text-red-400 transition-colors font-bold">
      <i class="ph-bold ph-sign-out text-xl mr-2"></i> Secure Logout
    </a>
  </div>
</aside>

<main class="flex-1 flex flex-col h-screen overflow-hidden">
  <header class="h-20 bg-white shadow-sm flex items-center justify-between px-10 border-b border-gray-100 z-0">
    <h2 class="text-xl font-bold text-gray-800">System Dashboard</h2>

    <div class="relative w-80">
      <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
        <i class="ph-bold ph-magnifying-glass text-gray-400 text-lg"></i>
      </div>
      <input type="text" id="searchBox" onkeyup="searchFleet()" placeholder="Search vehicles, ID, or type..." class="w-full pl-12 pr-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] focus:bg-white outline-none transition-all font-medium text-sm">
    </div>
  </header>

  <div class="flex-1 overflow-y-auto p-10">
    <div class="flex justify-between items-end mb-8">
      <div>
        <h3 class="text-3xl font-extrabold text-gray-900 tracking-tight">Fleet Inventory</h3>
        <p class="text-gray-500 font-medium mt-1">Manage rates, view availability, and add new inventory.</p>
      </div>
      <a href="add-vehicle.jsp" class="flex items-center bg-[#0D6EFD] hover:bg-blue-700 text-white px-6 py-3 rounded-xl font-bold shadow-md transition-all">
        <i class="ph-bold ph-plus mr-2 text-lg"></i> Add Vehicle
      </a>
    </div>

    <div class="bg-white shadow-sm rounded-[20px] border border-gray-100 overflow-hidden">
      <table class="min-w-full divide-y divide-gray-100" id="fleetTable">
        <thead class="bg-gray-50/80">
        <tr>
          <th class="px-8 py-5 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">ID</th>
          <th class="px-8 py-5 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Vehicle Details</th>
          <th class="px-8 py-5 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Class</th>
          <th class="px-8 py-5 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Specifics</th>
          <th class="px-8 py-5 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Daily Rate</th>
          <th class="px-8 py-5 text-right text-xs font-bold text-gray-500 uppercase tracking-wider">Actions</th>
        </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-50">
        <%
          List<Vehicle> vehicleList = (List<Vehicle>) request.getAttribute("vehicleList");
          if (vehicleList != null && !vehicleList.isEmpty()) {
            for (Vehicle v : vehicleList) {
        %>
        <tr class="hover:bg-blue-50/50 transition-colors duration-200 group">
          <td class="px-8 py-5 whitespace-nowrap text-sm font-mono font-bold text-gray-400"><%= v.getId() %></td>
          <td class="px-8 py-5 whitespace-nowrap">
            <div class="text-sm font-bold text-gray-900"><%= v.getBrand() %></div>
            <div class="text-sm text-gray-500 font-medium"><%= v.getModel() %></div>
          </td>
          <td class="px-8 py-5 whitespace-nowrap">
            <% if("Motorcycle".equals(v.getVehicleType())) { %>
            <span class="bg-orange-100 text-orange-700 px-3 py-1 rounded-lg text-xs font-bold tracking-wide">MOTORCYCLE</span>
            <% } else { %>
            <span class="bg-blue-100 text-blue-700 px-3 py-1 rounded-lg text-xs font-bold tracking-wide">CAR / SUV</span>
            <% } %>
          </td>
          <td class="px-8 py-5 whitespace-nowrap text-sm font-semibold text-gray-600">
            <%= v.getSpecificDetail() %>
          </td>
          <td class="px-8 py-5 whitespace-nowrap">
              <span class="text-sm font-extrabold text-emerald-600 bg-emerald-50 px-3 py-1.5 rounded-lg border border-emerald-100">
                  Rs. <%= String.format("%.2f", v.calculateRentalRate()) %>
              </span>
          </td>
          <td class="px-8 py-5 whitespace-nowrap text-right text-sm">
            <a href="vehicles?action=delete&id=<%= v.getId() %>" class="text-red-500 hover:text-red-700 bg-red-50 hover:bg-red-100 px-4 py-2 rounded-lg font-bold transition-all opacity-0 group-hover:opacity-100 inline-flex items-center">
              <i class="ph-bold ph-trash mr-1.5 text-lg"></i> Delete
            </a>
          </td>
        </tr>
        <%
          }
        } else {
        %>
        <tr>
          <td colspan="6" class="px-8 py-20 text-center">
            <div class="flex flex-col items-center justify-center">
              <div class="bg-gray-50 p-4 rounded-full mb-4">
                <i class="ph-fill ph-car text-4xl text-gray-400"></i>
              </div>
              <p class="text-xl font-bold text-gray-900 mb-1">No Vehicles Found</p>
              <p class="text-gray-500 font-medium">Your fleet is currently empty. Add a vehicle to get started.</p>
            </div>
          </td>
        </tr>
        <% } %>
        </tbody>
      </table>
    </div>
  </div>
</main>
</body>
</html>