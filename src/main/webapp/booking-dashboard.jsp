<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
  // Security check to ensure only admins can view this page
  Boolean isLoggedIn = (Boolean) session.getAttribute("adminLoggedIn");
  if (isLoggedIn == null || !isLoggedIn) {
    response.sendRedirect("index.jsp");
    return;
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Reservation Management - DriveAway Pro</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');
    body { font-family: 'Inter', sans-serif; background-color: #F8F9FA; }
    .sidebar { background-color: #0F172A; } /* Deep Navy */
    .accent-cyan { background-color: #06B6D4; } /* The Cyan accent for Student 3 */
    .text-accent-cyan { color: #06B6D4; }
  </style>
</head>
<body class="flex h-screen overflow-hidden text-gray-800">

<!-- PROFESSIONAL SIDEBAR -->
<aside class="sidebar w-64 text-white flex flex-col shrink-0 z-20 shadow-xl">
  <div class="h-24 flex items-center px-8">
    <i class="ph-bold ph-car-profile text-3xl text-blue-500 mr-3"></i>
    <h1 class="text-xl font-bold tracking-wide">DriveAway Pro</h1>
  </div>

  <nav class="flex-1 px-5 py-2 space-y-2 overflow-y-auto">
    <a href="customers?action=list" class="flex items-center px-4 py-3.5 text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl font-medium transition-all">
      <i class="ph-bold ph-users text-xl mr-3"></i> Customers
    </a>

    <a href="vehicles?action=list" class="flex items-center px-4 py-3.5 text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl font-medium transition-all">
      <i class="ph-bold ph-car text-xl mr-3"></i> Vehicle Fleet
    </a>

    <!-- ACTIVE TAB (Cyan for Bookings) -->
    <a href="bookings?action=list" class="flex items-center px-4 py-3.5 accent-cyan text-white rounded-xl font-semibold shadow-md transition-all">
      <i class="ph-bold ph-calendar-check text-xl mr-3"></i> Bookings
    </a>

    <a href="payments?action=list" class="flex items-center px-4 py-3.5 text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl font-medium transition-all">
      <i class="ph-bold ph-credit-card text-xl mr-3"></i> Payments
    </a>
    <a href="staff?action=list" class="flex items-center px-4 py-3.5 text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl font-medium transition-all">
      <i class="ph-bold ph-identification-badge text-xl mr-3"></i> Staff & Admins
    </a>
    <a href="reviews?action=list" class="flex items-center px-4 py-3.5 text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl font-medium transition-all">
      <i class="ph-bold ph-star text-xl mr-3"></i> Reviews
    </a>
  </nav>

  <div class="p-5 mb-2 space-y-2">
    <a href="admin-home.jsp" class="flex items-center px-4 py-3 text-sm text-slate-400 hover:text-white font-medium transition-all rounded-xl hover:bg-slate-800">
      <i class="ph-bold ph-arrow-square-out text-lg mr-3"></i> Admin Hub
    </a>
    <a href="index.jsp" class="flex items-center px-4 py-3 text-sm text-slate-400 hover:text-white font-medium transition-all rounded-xl hover:bg-slate-800">
      <i class="ph-bold ph-sign-out text-lg mr-3"></i> Logout
    </a>
  </div>
</aside>

<!-- MAIN CONTENT AREA -->
<main class="flex-1 flex flex-col h-screen overflow-hidden">

  <!-- Top Header (Pure white, matching pixel-perfect screenshots) -->
  <header class="h-24 bg-white flex items-center justify-between px-10 z-10 border-b border-gray-100">
    <h2 class="text-2xl font-medium text-gray-800">Reservation Management Portal</h2>
    <div class="flex items-center gap-4">
      <span class="text-sm font-medium text-gray-500">Welcome, <%= session.getAttribute("adminUser") != null ? session.getAttribute("adminUser") : "Admin" %></span>
      <div class="w-11 h-11 rounded-full accent-cyan text-white flex items-center justify-center font-bold text-lg shadow-sm">
        <%= session.getAttribute("adminUser") != null ? session.getAttribute("adminUser").toString().substring(0,1).toUpperCase() : "A" %>
      </div>
    </div>
  </header>

  <!-- Dashboard Content -->
  <div class="flex-1 overflow-y-auto p-10 bg-[#F8F9FA]">

    <div class="flex justify-between items-end mb-8">
      <div>
        <h3 class="text-3xl font-extrabold text-gray-900 mb-2 tracking-tight">Active Bookings</h3>
        <p class="text-gray-500 text-sm font-medium">Manage, approve, or cancel upcoming rentals.</p>
      </div>
      <a href="add-booking.jsp" class="flex items-center accent-cyan hover:bg-cyan-600 text-white px-6 py-3.5 rounded-xl font-bold shadow-md transition-all">
        <i class="ph-bold ph-plus mr-2 text-xl"></i> New Booking
      </a>
    </div>

    <!-- Seamless Data Table Card -->
    <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
      <table class="w-full text-left border-collapse">
        <thead>
        <tr class="border-b border-gray-100">
          <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Booking ID</th>
          <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Customer</th>
          <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Vehicle ID</th>
          <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Date & Duration</th>
          <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Status</th>
          <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest text-right">Actions</th>
        </tr>
        </thead>
        <tbody>
        <%
          List<Booking> bookingList = (List<Booking>) request.getAttribute("bookingList");
          if (bookingList != null && !bookingList.isEmpty()) {
            for (Booking b : bookingList) {
        %>
        <tr class="hover:bg-cyan-50/40 transition-colors group border-b border-gray-50 last:border-none">
          <td class="py-5 px-8 whitespace-nowrap text-sm font-semibold text-gray-500">#<%= b.getTransactionId() %></td>
          <td class="py-5 px-8 whitespace-nowrap text-sm font-bold text-gray-900"><%= b.getCustomerName() %></td>
          <td class="py-5 px-8 whitespace-nowrap text-sm text-gray-600 font-medium"><%= b.getVehicleId() %></td>
          <td class="py-5 px-8 whitespace-nowrap text-sm text-gray-600">
            <%= b.getRentalDate() %> <span class="text-xs text-gray-400 ml-1">(<%= b.getDurationDays() %> Days)</span>
          </td>
          <td class="py-5 px-8 whitespace-nowrap text-sm font-bold">
                                <span class="bg-cyan-50 text-[#06B6D4] px-3 py-1 rounded-lg text-xs font-extrabold uppercase tracking-wide">
                                    <%= b.getStatus() %>
                                </span>
          </td>
          <td class="py-5 px-8 whitespace-nowrap text-right text-sm font-medium">
            <div class="flex items-center justify-end gap-3 opacity-0 group-hover:opacity-100 transition-opacity">
              <a href="bookings?action=edit&id=<%= b.getTransactionId() %>" class="text-blue-600 hover:text-blue-800 bg-blue-50 hover:bg-blue-100 p-2.5 rounded-lg transition-colors" title="Edit Booking">
                <i class="ph-bold ph-pencil-simple text-lg"></i>
              </a>
              <a href="bookings?action=delete&id=<%= b.getTransactionId() %>" class="text-red-500 hover:text-red-700 bg-red-50 hover:bg-red-100 p-2.5 rounded-lg transition-colors" onclick="return confirm('WARNING: Are you sure you want to cancel this booking?');" title="Cancel Booking">
                <i class="ph-bold ph-trash text-lg"></i>
              </a>
            </div>
          </td>
        </tr>
        <%
          }
        } else {
        %>
        <!-- Empty State - Exactly matching image_4dd781.png -->
        <tr>
          <td colspan="6" class="py-24 text-center">
            <div class="flex flex-col items-center justify-center">
              <div class="bg-gray-50 p-6 rounded-full mb-4">
                <i class="ph ph-calendar-blank text-5xl text-gray-300"></i>
              </div>
              <p class="text-xl font-bold text-gray-900 mb-2 tracking-tight">No bookings found.</p>
              <p class="text-sm text-gray-500 font-medium">Get started by creating a new reservation in the system.</p>
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