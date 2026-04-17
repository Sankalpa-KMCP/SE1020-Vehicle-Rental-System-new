<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>

<%
  // SECURITY BOUNCER
  Boolean isLoggedIn = (Boolean) session.getAttribute("adminLoggedIn");
  if (isLoggedIn == null || !isLoggedIn) {
    response.sendRedirect("admin-login.jsp");
    return;
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Reservation Management</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <style>
    body { font-family: 'Inter', sans-serif; background-color: #F8F9FA; }
    .fade-in { animation: fadeIn 0.3s ease-in-out; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
  </style>
</head>
<body class="min-h-screen p-8 relative text-gray-800">

<div class="flex gap-4 mb-8 max-w-7xl mx-auto">
  <button onclick="history.back()" class="flex items-center justify-center gap-2 bg-white px-6 py-3 rounded-xl font-bold text-gray-700 shadow-sm border border-gray-200 hover:bg-gray-50 transition text-lg">
    <i class="ph-bold ph-arrow-left text-xl text-gray-500"></i>
    <span>Back</span>
  </button>
  <a href="admin-home.jsp" class="flex items-center justify-center gap-2 bg-gray-900 px-6 py-3 rounded-xl font-bold text-white shadow-sm hover:bg-black transition text-lg">
    <i class="ph-fill ph-house text-gray-300 text-2xl"></i>
    <span>Dashboard Home</span>
  </a>
</div>

<div class="w-full max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-8 mb-10">
  <div class="bg-white rounded-2xl p-8 shadow-sm border border-gray-200 flex justify-between items-center hover:shadow-md transition-shadow">
    <div>
      <p class="text-gray-500 font-bold tracking-wide text-base uppercase">Total Bookings</p>
      <h3 class="text-5xl font-extrabold text-gray-900 mt-2"><%= request.getAttribute("totalBookings") != null ? request.getAttribute("totalBookings") : 0 %></h3>
    </div>
    <div class="bg-blue-50 p-4 rounded-2xl text-blue-600">
      <i class="ph-fill ph-calendar-check text-5xl"></i>
    </div>
  </div>
</div>

<div class="flex justify-center items-start pb-16">
  <div class="w-full max-w-7xl bg-white border border-gray-200 rounded-3xl p-10 shadow-sm">

    <div class="flex justify-between items-end mb-8">
      <div>
        <h2 class="text-3xl font-extrabold text-gray-900 tracking-tight">Reservation Directory</h2>
        <p class="text-gray-500 text-lg mt-1 font-medium">Monitor and manage all vehicle rentals across the system.</p>
      </div>
    </div>

    <div class="overflow-x-auto rounded-2xl border border-gray-200 bg-white shadow-sm">
      <table class="w-full text-left border-collapse">
        <thead>
        <tr class="bg-gray-50 text-gray-500 text-sm uppercase tracking-wider border-b border-gray-200">
          <th class="py-5 px-8 font-bold">Booking ID</th>
          <th class="py-5 px-8 font-bold">Customer</th>
          <th class="py-5 px-8 font-bold">Vehicle ID</th>
          <th class="py-5 px-8 font-bold">Rental Date</th>
          <th class="py-5 px-8 font-bold">Duration</th>
          <th class="py-5 px-8 font-bold">Status</th>
          <th class="py-5 px-8 font-bold text-right">Actions</th>
        </tr>
        </thead>
        <tbody class="text-gray-700 text-base">
        <%
          List<Booking> bookingList = (List<Booking>) request.getAttribute("bookingList");
          if (bookingList != null && !bookingList.isEmpty()) {
            for(Booking b : bookingList) {
        %>
        <tr class="border-b border-gray-100 hover:bg-blue-50/40 transition-colors group">
          <td class="py-5 px-8 font-bold text-gray-900 text-lg">#<%= b.getId() %></td>
          <td class="py-5 px-8 font-semibold"><%= b.getCustomerName() %></td>
          <td class="py-5 px-8 font-semibold text-gray-600"><%= b.getVehicleId() %></td>
          <td class="py-5 px-8 text-gray-500"><%= b.getRentalDate() %></td>
          <td class="py-5 px-8 font-medium"><%= b.getDays() %> Days</td>
          <td class="py-5 px-8">
            <% if ("CONFIRMED".equalsIgnoreCase(b.getStatus()) || "UPDATED".equalsIgnoreCase(b.getStatus())) { %>
            <span class="bg-green-50 text-green-700 border border-green-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max">Active</span>
            <% } else if ("PENDING".equalsIgnoreCase(b.getStatus())) { %>
            <span class="bg-yellow-50 text-yellow-700 border border-yellow-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max">Pending</span>
            <% } else if ("REJECTED".equalsIgnoreCase(b.getStatus())) { %>
            <span class="bg-red-50 text-red-700 border border-red-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max">Rejected</span>
            <% } else { %>
            <span class="bg-gray-100 text-gray-700 border border-gray-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max"><%= b.getStatus() %></span>
            <% } %>
          </td>
          <td class="py-5 px-8 text-right">
            <form action="bookings" method="POST" class="m-0 p-0 flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
              <input type="hidden" name="id" value="<%= b.getId() %>">
              <input type="hidden" name="customerName" value="<%= b.getCustomerName() %>">
              <input type="hidden" name="rentalDate" value="<%= b.getRentalDate() %>">
              <input type="hidden" name="vehicleId" value="<%= b.getVehicleId() %>">
              <input type="hidden" name="days" value="<%= b.getDays() %>">

              <% if ("PENDING".equalsIgnoreCase(b.getStatus())) { %>
              <button type="submit" name="action" value="approve" class="text-green-600 bg-green-50 hover:bg-green-600 hover:text-white p-2 rounded-lg transition-colors shadow-sm" title="Approve Booking">
                <i class="ph-bold ph-check text-xl"></i>
              </button>
              <button type="submit" name="action" value="reject" class="text-yellow-600 bg-yellow-50 hover:bg-yellow-600 hover:text-white p-2 rounded-lg transition-colors shadow-sm" title="Reject Booking">
                <i class="ph-bold ph-x text-xl"></i>
              </button>
              <% } %>

              <button type="submit" name="action" value="delete" onclick="return confirm('WARNING: Are you sure you want to delete <%= b.getId() %>?');" class="text-red-500 bg-red-50 hover:bg-red-600 hover:text-white p-2 rounded-lg transition-colors shadow-sm" title="Delete Booking">
                <i class="ph-bold ph-trash text-xl"></i>
              </button>
            </form>
          </td>
        </tr>
        <%
          }
        } else {
        %>
        <tr><td colspan="7" class="py-10 text-center text-gray-500 font-medium text-lg">No active reservations found in the system.</td></tr>
        <% } %>
        </tbody>
      </table>
    </div>

  </div>
</div>

</body>
</html>