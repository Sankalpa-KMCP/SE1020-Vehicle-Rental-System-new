<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Search Booking</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <style>@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap'); body { font-family: 'Inter', sans-serif; }</style>
</head>
<body class="bg-[#FAFAFA] min-h-screen flex items-center justify-center p-6">
<div class="w-full max-w-2xl bg-white p-10 rounded-[24px] shadow-lg border-[3px] border-[#06B6D4]">
  <div class="flex justify-between items-center mb-8">
    <h2 class="text-3xl font-bold text-[#06B6D4] flex items-center gap-3"><i class="ph-bold ph-magnifying-glass"></i> Search Reservation</h2>
    <a href="add-booking.jsp" class="text-gray-500 hover:text-gray-800 font-bold">Close ✕</a>
  </div>

  <form action="bookings" method="GET" class="flex gap-4 mb-8">
    <input type="hidden" name="action" value="search">
    <input type="text" name="id" placeholder="Enter Booking ID (e.g. BKG-101)" required class="flex-1 border border-gray-300 rounded-xl px-4 py-3 text-lg outline-none focus:border-[#06B6D4]">
    <button type="submit" class="bg-[#06B6D4] text-white px-8 rounded-xl font-bold text-lg hover:bg-cyan-600 transition">Find</button>
  </form>

  <%
    Booking b = (Booking) request.getAttribute("searchedBooking");
    if (b != null) {
  %>
  <div class="bg-cyan-50 border border-cyan-100 p-6 rounded-xl">
    <h3 class="text-xl font-bold text-gray-900 mb-4 border-b border-cyan-200 pb-2">Booking Details: <%= b.getId() %></h3>
    <p class="text-gray-700 mb-2"><strong>Customer:</strong> <%= b.getCustomerName() %></p>
    <p class="text-gray-700 mb-2"><strong>Vehicle ID:</strong> <%= b.getVehicleId() %></p>
    <p class="text-gray-700 mb-2"><strong>Date:</strong> <%= b.getRentalDate() %> (<%= b.getDays() %> Days)</p>
    <p class="text-gray-700"><strong>Status:</strong> <span class="bg-[#06B6D4] text-white px-3 py-1 rounded-lg text-sm font-bold"><%= b.getStatus() %></span></p>
  </div>
  <% } else if (request.getAttribute("searchAttempted") != null) { %>
  <div class="bg-red-50 text-red-600 p-4 rounded-xl font-bold border border-red-100"><i class="ph-bold ph-warning-circle"></i> Booking not found.</div>
  <% } %>
</div>
</body>
</html>