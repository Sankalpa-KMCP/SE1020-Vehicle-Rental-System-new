<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>View All Bookings</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <style>body { font-family: 'Inter', sans-serif; }</style>
</head>
<body class="bg-[#FAFAFA] min-h-screen flex items-center justify-center p-6">
<div class="w-full max-w-4xl bg-white p-10 rounded-[24px] shadow-lg border-[3px] border-[#10B981]">
  <div class="flex justify-between items-center mb-8">
    <h2 class="text-3xl font-bold text-[#10B981] flex items-center gap-3"><i class="ph-bold ph-list-dashes"></i> All Bookings</h2>
    <a href="add-booking.jsp" class="text-gray-500 hover:text-gray-800 font-bold">Close ✕</a>
  </div>

  <div class="overflow-x-auto">
    <table class="w-full text-left border-collapse">
      <thead>
        <tr class="border-b border-gray-200">
          <th class="py-4 px-4 font-bold text-gray-600">ID</th>
          <th class="py-4 px-4 font-bold text-gray-600">Customer</th>
          <th class="py-4 px-4 font-bold text-gray-600">Vehicle ID</th>
          <th class="py-4 px-4 font-bold text-gray-600">Date</th>
          <th class="py-4 px-4 font-bold text-gray-600">Days</th>
          <th class="py-4 px-4 font-bold text-gray-600">Status</th>
        </tr>
      </thead>
      <tbody>
      <%
        List<Booking> bookingList = (List<Booking>) request.getAttribute("bookingList");
        if (bookingList != null && !bookingList.isEmpty()) {
          for (Booking b : bookingList) {
      %>
        <tr class="border-b border-gray-100 hover:bg-green-50 transition-colors">
          <td class="py-4 px-4 hidden"><%= b.getId() %></td> 
          <td class="py-4 px-4 font-semibold text-gray-800"><%= b.getTransactionId() %></td>
          <td class="py-4 px-4 text-gray-700"><%= b.getCustomerName() %></td>
          <td class="py-4 px-4 text-gray-700"><%= b.getVehicleId() %></td>
          <td class="py-4 px-4 text-gray-700"><%= b.getRentalDate() %></td>
          <td class="py-4 px-4 text-gray-700"><%= b.getDays() %></td>
          <td class="py-4 px-4">
            <span class="bg-[#10B981] text-white px-3 py-1 rounded-lg text-sm font-bold"><%= b.getStatus() %></span>
          </td>
        </tr>
      <%
          }
        } else {
      %>
        <tr>
          <td colspan="6" class="py-12 text-center text-gray-500 font-medium">No bookings found.</td>
        </tr>
      <% } %>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>
