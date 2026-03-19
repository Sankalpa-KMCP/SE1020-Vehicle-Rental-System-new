<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
  // SECURITY UPDATE: Check for BOTH Admin and Customer sessions
  Boolean isAdmin = (Boolean) session.getAttribute("adminLoggedIn");
  Boolean isCustomer = (Boolean) session.getAttribute("customerLoggedIn");

  // If NEITHER is logged in, kick them back to the login page
  if ((isAdmin == null || !isAdmin) && (isCustomer == null || !isCustomer)) {
    response.sendRedirect("index.jsp");
    return;
  }

  // Determine which mode we are rendering
  boolean adminMode = (isAdmin != null && isAdmin);

  // Grab the username for the header
  String displayName = (String) session.getAttribute("name");
  if (displayName == null) {
    displayName = (String) session.getAttribute("username");
    if (displayName == null) displayName = adminMode ? "Admin" : "Customer";
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><%= adminMode ? "Reservation Management" : "My Bookings" %> - DriveAway</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');
    body { font-family: 'Inter', sans-serif; }
    .sidebar { background-color: #0F172A; } /* Deep Navy */
    .accent-cyan { background-color: #06B6D4; } /* Admin Accent */
    .text-accent-cyan { color: #06B6D4; }
    .fade-in { animation: fadeIn 0.4s ease-out; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
  </style>
</head>

<% if (adminMode) { %>
<body class="flex h-screen overflow-hidden text-gray-800 bg-[#F8F9FA]">
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
    <a href="bookings?action=list" class="flex items-center px-4 py-3.5 accent-cyan text-white rounded-xl font-semibold shadow-md transition-all">
      <i class="ph-bold ph-calendar-check text-xl mr-3"></i> Bookings
    </a>
    <a href="payments?action=list" class="flex items-center px-4 py-3.5 text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl font-medium transition-all">
      <i class="ph-bold ph-credit-card text-xl mr-3"></i> Payments
    </a>
    <a href="staff?action=list" class="flex items-center px-4 py-3.5 text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl font-medium transition-all">
      <i class="ph-bold ph-identification-badge text-xl mr-3"></i> Staff & Admins
    </a>
  </nav>
  <div class="p-5 mb-2 space-y-2">
    <a href="admin-home.jsp" class="flex items-center px-4 py-3 text-sm text-slate-400 hover:text-white font-medium transition-all rounded-xl hover:bg-slate-800">
      <i class="ph-bold ph-arrow-square-out text-lg mr-3"></i> Admin Hub
    </a>
    <form action="auth" method="post" class="m-0">
      <input type="hidden" name="action" value="logout">
      <button type="submit" class="w-full flex items-center px-4 py-3 text-sm text-slate-400 hover:text-white font-medium transition-all rounded-xl hover:bg-slate-800">
        <i class="ph-bold ph-sign-out text-lg mr-3"></i> Logout
      </button>
    </form>
  </div>
</aside>

<div class="flex-1 flex flex-col h-screen overflow-hidden">
  <header class="h-24 bg-white flex items-center justify-between px-10 z-10 border-b border-gray-100">
    <h2 class="text-2xl font-medium text-gray-800">Reservation Management Portal</h2>
    <div class="flex items-center gap-4">
      <span class="text-sm font-medium text-gray-500">Welcome, <%= displayName %></span>
      <div class="w-11 h-11 rounded-full accent-cyan text-white flex items-center justify-center font-bold text-lg shadow-sm">
        <%= displayName.substring(0,1).toUpperCase() %>
      </div>
    </div>
  </header>
  <main class="flex-1 overflow-y-auto p-10">
      <% } else { %>
    <body class="min-h-screen text-gray-800 flex flex-col bg-[#F4F7F6]">
    <nav class="bg-white border-b border-gray-200 sticky top-0 z-20 shadow-sm">
      <div class="max-w-screen-xl mx-auto px-8 h-20 flex items-center justify-between">
        <div class="flex items-center gap-2 text-gray-900">
          <i class="ph-bold ph-car-profile text-3xl text-blue-600"></i>
          <span class="text-2xl font-extrabold tracking-tight">DriveAway</span>
        </div>
        <div class="flex items-center gap-6">
          <a href="customer-home.jsp" class="text-gray-500 hover:text-blue-600 font-bold flex items-center gap-2 transition-colors mr-2">
            <i class="ph-bold ph-house text-xl"></i> Dashboard
          </a>
          <div class="flex items-center gap-3">
            <div class="bg-blue-50 text-blue-600 p-2 rounded-full border border-blue-100"><i class="ph-bold ph-user text-xl"></i></div>
            <div class="hidden sm:block">
              <p class="text-xs text-gray-500 font-semibold uppercase tracking-wider">Welcome Back</p>
              <p class="font-bold text-gray-900 leading-tight"><%= displayName %></p>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <main class="flex-grow max-w-screen-xl mx-auto w-full px-8 py-10 fade-in">
      <% } %>

      <div class="flex justify-between items-end mb-8">
        <div>
          <h3 class="text-3xl font-extrabold text-gray-900 mb-2 tracking-tight"><%= adminMode ? "Active Bookings" : "My Bookings" %></h3>
          <p class="text-gray-500 text-sm font-medium"><%= adminMode ? "Manage, approve, or cancel upcoming rentals." : "View your active rentals, check past transaction history, and modify dates." %></p>
        </div>
        <% if (adminMode) { %>
        <a href="add-booking.jsp" class="flex items-center accent-cyan hover:bg-cyan-600 text-white px-6 py-3.5 rounded-xl font-bold shadow-md transition-all">
          <i class="ph-bold ph-plus mr-2 text-xl"></i> New Booking
        </a>
        <% } else { %>
        <a href="vehicle-dashboard.jsp" class="flex items-center bg-blue-600 hover:bg-blue-700 text-white px-6 py-3.5 rounded-xl font-bold shadow-md transition-all">
          <i class="ph-bold ph-car mr-2 text-xl"></i> Book New Ride
        </a>
        <% } %>
      </div>

      <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
        <table class="w-full text-left border-collapse">
          <thead>
          <tr class="bg-gray-50 border-b border-gray-100">
            <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Booking ID</th>
            <% if (adminMode) { %><th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Customer</th><% } %>
            <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Vehicle ID</th>
            <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Date & Duration</th>
            <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Status</th>
            <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest text-right">Actions</th>
          </tr>
          </thead>
          <tbody>
          <%
            List<Booking> bookingList = (List<Booking>) request.getAttribute("bookingList");
            if (bookingList != null && !bookingList.isEmpty()) {
              for (Booking b : bookingList) {
          %>
          <tr class="hover:bg-blue-50/40 transition-colors group border-b border-gray-50 last:border-none">
            <td class="py-5 px-8 whitespace-nowrap text-sm font-semibold text-gray-500">#<%= b.getTransactionId() %></td>
            <% if (adminMode) { %><td class="py-5 px-8 whitespace-nowrap text-sm font-bold text-gray-900"><%= b.getCustomerName() %></td><% } %>
            <td class="py-5 px-8 whitespace-nowrap text-sm text-gray-600 font-medium"><%= b.getVehicleId() %></td>
            <td class="py-5 px-8 whitespace-nowrap text-sm text-gray-600">
              <%= b.getRentalDate() %> <span class="text-xs text-gray-400 ml-1">(<%= b.getDurationDays() %> Days)</span>
            </td>
            <td class="py-5 px-8 whitespace-nowrap text-sm font-bold">
              <% if ("Active".equalsIgnoreCase(b.getStatus())) { %>
              <span class="bg-blue-50 text-blue-600 px-3 py-1 rounded-lg text-xs font-extrabold uppercase tracking-wide">Active</span>
              <% } else if ("Completed".equalsIgnoreCase(b.getStatus())) { %>
              <span class="bg-green-50 text-green-600 px-3 py-1 rounded-lg text-xs font-extrabold uppercase tracking-wide">Completed</span>
              <% } else { %>
              <span class="bg-gray-100 text-gray-600 px-3 py-1 rounded-lg text-xs font-extrabold uppercase tracking-wide"><%= b.getStatus() %></span>
              <% } %>
            </td>
            <td class="py-5 px-8 whitespace-nowrap text-right text-sm font-medium">
              <div class="flex items-center justify-end gap-3 opacity-0 group-hover:opacity-100 transition-opacity">
                <% if (adminMode) { %>
                <a href="bookings?action=edit&id=<%= b.getTransactionId() %>" class="text-blue-600 hover:text-blue-800 bg-blue-50 hover:bg-blue-100 p-2.5 rounded-lg transition-colors" title="Edit Booking">
                  <i class="ph-bold ph-pencil-simple text-lg"></i>
                </a>
                <% } %>
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
          <tr>
            <td colspan="<%= adminMode ? 6 : 5 %>" class="py-24 text-center">
              <div class="flex flex-col items-center justify-center">
                <div class="bg-gray-50 p-6 rounded-full mb-4">
                  <i class="ph ph-calendar-blank text-5xl text-gray-300"></i>
                </div>
                <p class="text-xl font-bold text-gray-900 mb-2 tracking-tight">No bookings found.</p>
                <p class="text-sm text-gray-500 font-medium">
                  <%= adminMode ? "Get started by creating a new reservation in the system." : "You haven't rented any vehicles yet. Start exploring our fleet!" %>
                </p>
              </div>
            </td>
          </tr>
          <% } %>
          </tbody>
        </table>
      </div>

    </main>
      <% if (adminMode) { %>
</div>
<% } %>
</body>
</html>