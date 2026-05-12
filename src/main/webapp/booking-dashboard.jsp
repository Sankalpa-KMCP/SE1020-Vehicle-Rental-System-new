<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.dao.PaymentDAO" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>

<%
  // Allow access to booking dashboard without admin login
  // Boolean isLoggedIn = (Boolean) session.getAttribute("adminLoggedIn");
  // if (isLoggedIn == null || !isLoggedIn) {
  //   response.sendRedirect("admin-login.jsp");
  //   return;
  // }

  List<Booking> bookingList = (List<Booking>) request.getAttribute("bookingList");

  // STATS COUNTERS
  int totalBookings = bookingList != null ? bookingList.size() : 0;
  int cancellationRequests = 0;
  int pendingBookings = 0;
  int updateRequests = 0;

  if (bookingList != null) {
    for (Booking b : bookingList) {
      if ("CANCELLATION_REQUESTED".equalsIgnoreCase(b.getStatus())) {
        cancellationRequests++;
      } else if ("PENDING".equalsIgnoreCase(b.getStatus())) {
        pendingBookings++;
      } else if ("UPDATE_REQUESTED".equalsIgnoreCase(b.getStatus())) {
        updateRequests++;
      }
    }
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Reservation Management - DriveAway</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <style>
    body { font-family: 'Inter', sans-serif; background: radial-gradient(circle at top right, #e0f2fe 0%, #f8fafc 45%, #f1f5f9 100%); }
    .fade-in { animation: fadeIn 0.4s ease-out; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
    .soft-card { box-shadow: 0 10px 35px rgba(15, 23, 42, 0.06); }
  </style>
</head>
<body class="min-h-screen text-gray-800 flex flex-col">

<nav class="bg-gradient-to-r from-cyan-900 via-teal-900 to-emerald-900 border-b border-white/10 sticky top-0 z-20 shadow-lg backdrop-blur">
  <div class="max-w-screen-xl mx-auto px-8 h-20 flex items-center justify-between">
    <a href="admin-home.jsp" class="flex items-center gap-2 text-white">
      <div class="bg-white/10 p-2.5 rounded-xl backdrop-blur-sm">
        <i class="ph-fill ph-calendar-check text-2xl text-cyan-300"></i>
      </div>
      <span class="text-2xl font-extrabold tracking-tight">Reservations</span>
    </a>

    <div class="flex items-center gap-4">
      <a href="admin-home.jsp" class="text-cyan-200 hover:text-white font-semibold transition-colors flex items-center gap-2">
        <i class="ph-bold ph-house"></i> Admin Hub
      </a>
      <a href="login.jsp" class="text-white hover:text-cyan-200 font-bold border border-white/20 bg-white/10 hover:bg-white/20 px-5 py-2.5 rounded-xl transition-all flex items-center gap-2 shadow-sm">
        Logout <i class="ph-bold ph-sign-out text-lg"></i>
      </a>
    </div>
  </div>
</nav>

<main class="flex-grow max-w-screen-xl mx-auto w-full px-8 py-10 fade-in">

  <div class="bg-gradient-to-br from-cyan-600 via-teal-600 to-emerald-600 rounded-3xl p-10 text-white shadow-xl mb-10 relative overflow-hidden">
    <div class="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
      <div>
        <h1 class="text-4xl font-extrabold mb-2 tracking-tight">Reservation Directory</h1>
        <p class="text-cyan-100 text-lg font-medium max-w-2xl">Monitor and manage all customer rental requests.</p>
      </div>

      <div class="flex gap-3 bg-white/10 p-4 rounded-2xl backdrop-blur-sm border border-white/20">
        <div class="text-center px-4 border-r border-white/20">
          <p class="text-cyan-100 text-xs font-bold uppercase mb-1">Total</p>
          <p class="text-white font-extrabold text-xl"><%= totalBookings %></p>
        </div>
        <div class="text-center px-4 border-r border-white/20">
          <p class="text-cyan-100 text-xs font-bold uppercase mb-1">Pending</p>
          <p class="text-white font-extrabold text-xl"><%= pendingBookings %></p>
        </div>
        <div class="text-center px-4 border-r border-white/20">
          <p class="text-cyan-100 text-xs font-bold uppercase mb-1">Edit Req</p>
          <p class="text-white font-extrabold text-xl"><%= updateRequests %></p>
        </div>
        <div class="text-center px-4">
          <p class="text-cyan-100 text-xs font-bold uppercase mb-1">Cancel Req</p>
          <p class="text-white font-extrabold text-xl"><%= cancellationRequests %></p>
        </div>
      </div>
    </div>
  </div>

  <div class="bg-white border border-slate-200 rounded-3xl p-8 soft-card shadow-lg">
    <div class="overflow-x-auto">
      <table class="w-full text-left border-collapse">
        <thead>
        <tr class="bg-slate-50 text-gray-500 text-xs uppercase tracking-widest border-b border-slate-200">
          <th class="py-4 px-6 font-bold">Booking ID</th>
          <th class="py-4 px-6 font-bold">Customer</th>
          <th class="py-4 px-6 font-bold">Vehicle</th>
          <th class="py-4 px-6 font-bold">Current Date/Days</th>
          <th class="py-4 px-6 font-bold">Status</th>
          <th class="py-4 px-6 font-bold text-right">Actions</th>
        </tr>
        </thead>
        <tbody class="text-gray-700">
        <%
          PaymentDAO paymentDAO = new PaymentDAO();
          if (bookingList != null && !bookingList.isEmpty()) {
            for(Booking b : bookingList) {
        %>
        <tr class="border-b border-slate-100 hover:bg-slate-50 transition-colors group">
          <td class="py-5 px-6 font-bold text-gray-900">#<%= b.getId() %></td>
          <td class="py-5 px-6 font-semibold"><%= b.getCustomerName() %></td>
          <td class="py-5 px-6"><span class="bg-gray-100 px-2 py-1 rounded text-sm font-mono"><%= b.getVehicleId() %></span></td>
          <td class="py-5 px-6 text-sm">
            <p class="font-medium"><%= b.getRentalDate() %></p>
            <p class="text-gray-400 text-xs"><%= b.getDays() %> Days Duration</p>
          </td>

          <td class="py-5 px-6">
            <% if ("CONFIRMED".equalsIgnoreCase(b.getStatus())) { %>
            <span class="bg-green-50 text-green-700 border border-green-200 font-bold px-3 py-1 rounded-lg text-xs">Confirmed</span>
            <% } else if ("PENDING".equalsIgnoreCase(b.getStatus())) { %>
            <span class="bg-yellow-50 text-yellow-700 border border-yellow-200 font-bold px-3 py-1 rounded-lg text-xs">New Pending</span>
            <% } else if ("UPDATE_REQUESTED".equalsIgnoreCase(b.getStatus())) { %>
            <div class="flex flex-col gap-1">
              <span class="bg-blue-50 text-blue-700 border border-blue-200 font-bold px-3 py-1 rounded-lg text-xs w-max">Edit Requested</span>
              <p class="text-[10px] text-blue-600 font-bold">REQ: <%= b.getStaffMessage().replace("|", " / ") %> Days</p>
            </div>
            <% } else if ("CANCELLATION_REQUESTED".equalsIgnoreCase(b.getStatus())) { %>
            <span class="bg-red-50 text-red-700 border border-red-200 font-bold px-3 py-1 rounded-lg text-xs">Cancel Req</span>
            <% } else if ("CANCELLED".equalsIgnoreCase(b.getStatus())) { %>
            <span class="bg-gray-100 text-gray-500 font-bold px-3 py-1 rounded-lg text-xs">Cancelled</span>
            <% } else { %>
            <span class="bg-gray-50 text-gray-600 px-3 py-1 rounded-lg text-xs"><%= b.getStatus() %></span>
            <% } %>
          </td>

          <td class="py-5 px-6 text-right">
            <form action="bookings" method="POST" class="flex justify-end gap-2">
              <input type="hidden" name="id" value="<%= b.getId() %>">

              <% if ("PENDING".equalsIgnoreCase(b.getStatus())) { %>
              <button type="submit" name="action" value="accept" class="bg-green-600 hover:bg-green-700 text-white text-xs font-bold px-3 py-2 rounded-lg transition-all">Approve</button>
              <button type="submit" name="action" value="decline" class="bg-red-50 text-red-600 hover:bg-red-600 hover:text-white text-xs font-bold px-3 py-2 rounded-lg transition-all">Reject</button>

              <% } else if ("UPDATE_REQUESTED".equalsIgnoreCase(b.getStatus())) { %>
              <button type="submit" name="action" value="approve_update" class="bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold px-3 py-2 rounded-lg transition-all">Accept Edit</button>
              <button type="submit" name="action" value="reject_update" class="bg-gray-100 text-gray-600 hover:bg-gray-200 text-xs font-bold px-3 py-2 rounded-lg transition-all">Deny Edit</button>

              <% } else if ("CANCELLATION_REQUESTED".equalsIgnoreCase(b.getStatus())) { %>
              <button type="submit" name="action" value="approve_cancel" class="bg-red-600 hover:bg-red-700 text-white text-xs font-bold px-3 py-2 rounded-lg transition-all">Confirm Cancel</button>
              <button type="submit" name="action" value="reject_cancel" class="bg-gray-100 text-gray-600 hover:bg-gray-200 text-xs font-bold px-3 py-2 rounded-lg transition-all">Deny Cancel</button>

              <% } else { %>
              <button type="submit" name="action" value="delete" onclick="return confirm('Delete this record forever?');" class="text-gray-400 hover:text-red-600 p-2 transition-colors">
                <i class="ph-bold ph-trash text-lg"></i>
              </button>
              <% } %>
            </form>
          </td>
        </tr>
        <%
          }
        } else {
        %>
        <tr><td colspan="6" class="py-12 text-center text-gray-400 font-medium">No reservations found in the system.</td></tr>
        <% } %>
        </tbody>
      </table>
    </div>
  </div>
</main>

</body>
</html>