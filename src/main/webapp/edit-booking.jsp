<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Edit Booking</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <style>body { font-family: 'Inter', sans-serif; }</style>
</head>
<body class="bg-[#FAFAFA] min-h-screen flex items-center justify-center p-6">
<div class="w-full max-w-2xl bg-white p-10 rounded-[24px] shadow-lg border-[3px] border-[#F59E0B]">
  <div class="flex justify-between items-center mb-8">
    <h2 class="text-3xl font-bold text-[#F59E0B] flex items-center gap-3"><i class="ph-bold ph-pencil-simple"></i> Edit Booking</h2>
    <a href="add-booking.jsp" class="text-gray-500 hover:text-gray-800 font-bold">Close ✕</a>
  </div>

  <% Booking b = (Booking) request.getAttribute("bookingToEdit"); %>

  <% if (b == null) { %>
  <!-- STEP 1: Ask for ID -->
  <form action="bookings" method="GET" class="flex gap-4">
    <input type="hidden" name="action" value="edit">
    <input type="text" name="id" placeholder="Enter Booking ID to Edit" required class="flex-1 border border-gray-300 rounded-xl px-4 py-3 text-lg outline-none focus:border-[#F59E0B]">
    <button type="submit" class="bg-[#F59E0B] text-white px-8 rounded-xl font-bold text-lg hover:bg-yellow-600 transition">Load</button>
  </form>
  <% if (request.getAttribute("error") != null) { %>
  <p class="text-red-500 font-bold mt-4">Booking not found!</p>
  <% } %>
  <% } else { %>
  <!-- STEP 2: Show Edit Form -->
  <form action="bookings" method="POST" class="space-y-4">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="id" value="<%= b.getId() %>">

    <div>
      <label class="block font-bold text-gray-700 mb-1">Customer Name:</label>
      <input type="text" name="customerName" value="<%= b.getCustomerName() %>" required class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#F59E0B]">
    </div>
    <div>
      <label class="block font-bold text-gray-700 mb-1">Vehicle ID:</label>
      <input type="text" name="vehicleId" value="<%= b.getVehicleId() %>" required class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#F59E0B]">
    </div>
    <div class="flex gap-4">
      <div class="flex-1">
        <label class="block font-bold text-gray-700 mb-1">Date:</label>
        <input type="date" name="rentalDate" value="<%= b.getRentalDate() %>" required class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#F59E0B]">
      </div>
      <div class="w-1/3">
        <label class="block font-bold text-gray-700 mb-1">Days:</label>
        <input type="number" name="days" value="<%= b.getDays() %>" required class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#F59E0B]">
      </div>
    </div>

    <button type="submit" class="w-full bg-[#F59E0B] text-white font-bold py-4 rounded-xl text-xl hover:bg-yellow-600 transition mt-4">Save Changes</button>
  </form>
  <% } %>
</div>
</body>
</html>