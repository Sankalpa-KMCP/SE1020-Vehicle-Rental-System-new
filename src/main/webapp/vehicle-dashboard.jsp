<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Vehicle" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Car" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Vehicle Fleet - DriveAway</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
</head>
<body class="bg-gray-50 flex h-screen overflow-hidden text-gray-800">

<!-- PROFESSIONAL SIDEBAR -->
<aside class="w-64 bg-slate-900 text-white flex flex-col hidden md:flex">
  <div class="h-16 flex items-center px-6 border-b border-slate-800">
    <i class="ph ph-car-profile text-2xl text-blue-400 mr-2"></i>
    <h1 class="text-xl font-bold tracking-wider">DriveAway Pro</h1>
  </div>
  <nav class="flex-1 px-4 py-6 space-y-2 overflow-y-auto">
    <!-- Link back to Student 1's Work -->
    <a href="customers?action=list" class="flex items-center px-4 py-3 text-slate-300 hover:bg-slate-800 hover:text-white rounded-lg transition-all">
      <i class="ph ph-users text-xl mr-3"></i>
      <span class="font-medium">Customers</span>
    </a>

    <!-- Link to Student 2's Work (ACTIVE) -->
    <a href="vehicles?action=list" class="flex items-center px-4 py-3 bg-blue-600 text-white rounded-lg shadow-md transition-all">
      <i class="ph ph-car text-xl mr-3"></i>
      <span class="font-medium">Vehicle Fleet</span>
    </a>
  </nav>
  <div class="p-4 border-t border-slate-800">
    <a href="index.jsp" class="flex items-center px-4 py-2 text-sm text-slate-400 hover:text-white transition-all">
      <i class="ph ph-sign-out text-lg mr-2"></i> Logout
    </a>
  </div>
</aside>

<!-- MAIN CONTENT AREA -->
<main class="flex-1 flex flex-col h-screen overflow-hidden">
  <header class="h-16 bg-white shadow-sm flex items-center justify-between px-8 border-b border-gray-200">
    <h2 class="text-xl font-semibold text-gray-800">Fleet Inventory</h2>
  </header>

  <div class="flex-1 overflow-y-auto p-8">
    <div class="flex justify-between items-end mb-6">
      <div>
        <h3 class="text-2xl font-bold text-gray-900">Vehicle Inventory</h3>
        <p class="text-sm text-gray-500 mt-1">Manage all rental cars, rates, and availability.</p>
      </div>
      <a href="add-vehicle.jsp" class="flex items-center bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium shadow-sm transition-all">
        <i class="ph ph-plus font-bold mr-2"></i> Add New Vehicle
      </a>
    </div>

    <div class="bg-white shadow-sm rounded-xl border border-gray-200 overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase">Vehicle ID</th>
          <th class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase">Brand & Model</th>
          <th class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase">Type</th>
          <th class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase">Seats</th>
          <th class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase">Daily Rate</th>
          <th class="px-6 py-4 text-right text-xs font-bold text-gray-500 uppercase">Actions</th>
        </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
        <%
          List<Vehicle> vehicleList = (List<Vehicle>) request.getAttribute("vehicleList");
          if (vehicleList != null && !vehicleList.isEmpty()) {
            for (Vehicle v : vehicleList) {
              Car c = (Car) v; // Cast to Car to get seating capacity
        %>
        <tr class="hover:bg-blue-50 transition-colors duration-150 group">
          <td class="px-6 py-4 whitespace-nowrap text-sm font-mono text-gray-500"><%= c.getId() %></td>
          <td class="px-6 py-4 whitespace-nowrap text-sm font-bold text-gray-900"><%= c.getBrand() %> <%= c.getModel() %></td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
            <span class="bg-gray-100 text-gray-700 px-2 py-1 rounded text-xs font-semibold"><%= c.getVehicleType() %></span>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600"><%= c.getSeatingCapacity() %></td>
          <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-green-600">$<%= String.format("%.2f", c.getDailyRate()) %></td>
          <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
            <a href="vehicles?action=delete&id=<%= c.getId() %>" class="text-red-500 hover:text-red-700 bg-red-50 hover:bg-red-100 px-3 py-1.5 rounded-md transition-all opacity-0 group-hover:opacity-100">
              <i class="ph ph-trash align-middle mr-1"></i> Delete
            </a>
          </td>
        </tr>
        <%
          }
        } else {
        %>
        <tr>
          <td colspan="6" class="px-6 py-12 text-center">
            <div class="flex flex-col items-center">
              <i class="ph ph-car text-4xl text-gray-300 mb-3"></i>
              <p class="text-lg font-medium text-gray-900">No Vehicles in Fleet</p>
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