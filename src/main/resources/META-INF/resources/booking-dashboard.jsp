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
  int totalBookings = bookingList != null ? bookingList.size() : 0;
  int cancellationRequests = 0;
  int pendingBookings = 0;
  if (bookingList != null) {
    for (Booking b : bookingList) {
      if ("CANCELLATION_REQUESTED".equalsIgnoreCase(b.getStatus())) {
        cancellationRequests++;
      }
      if ("PENDING".equalsIgnoreCase(b.getStatus())) {
        pendingBookings++;
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
    .card-hover { transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); }
    .card-hover:hover { transform: translateY(-5px); box-shadow: 0 20px 40px rgba(6, 182, 212, 0.15); }
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
  <div class="bg-gradient-to-br from-cyan-600 via-teal-600 to-emerald-600 rounded-3xl p-10 sm:p-12 text-white shadow-xl mb-10 relative overflow-hidden">
    <div class="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10 mix-blend-overlay pointer-events-none"></div>
    <div class="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
      <div>
        <h1 class="text-4xl sm:text-5xl font-extrabold mb-4 tracking-tight">Reservation Directory</h1>
        <p class="text-cyan-100 text-lg font-medium max-w-2xl leading-relaxed">Monitor and manage all vehicle rentals across the system.</p>
      </div>

      <div class="flex gap-3 bg-white/10 p-4 rounded-2xl backdrop-blur-sm border border-white/20">
        <div class="text-center px-4 border-r border-white/20">
          <p class="text-cyan-100 text-xs font-bold uppercase tracking-wider mb-1">Total</p>
          <p class="text-white font-extrabold"><%= totalBookings %></p>
        </div>
        <div class="text-center px-4 border-r border-white/20">
          <p class="text-cyan-100 text-xs font-bold uppercase tracking-wider mb-1">Pending</p>
          <p class="text-white font-extrabold"><%= pendingBookings %></p>
        </div>
        <div class="text-center px-4">
          <p class="text-cyan-100 text-xs font-bold uppercase tracking-wider mb-1">Cancel Req</p>
          <p class="text-white font-extrabold"><%= cancellationRequests %></p>
        </div>
      </div>
    </div>
    <i class="ph-fill ph-calendar-check absolute -right-10 -bottom-20 text-[300px] text-white opacity-10 transform -rotate-12 pointer-events-none"></i>
  </div>

  <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6 mb-10">
    <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card card-hover">
      <div class="flex items-center justify-between mb-4">
        <div class="bg-cyan-50 p-3 rounded-xl">
          <i class="ph-fill ph-calendar-check text-2xl text-cyan-600"></i>
        </div>
        <span class="text-xs font-bold text-cyan-600 bg-cyan-50 px-2 py-1 rounded-lg">TOTAL</span>
      </div>
      <p class="text-3xl font-extrabold text-gray-900"><%= totalBookings %></p>
      <p class="text-sm text-gray-500 mt-2">All reservations</p>
    </div>

    <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card card-hover">
      <div class="flex items-center justify-between mb-4">
        <div class="bg-amber-50 p-3 rounded-xl">
          <i class="ph-fill ph-clock text-2xl text-amber-600"></i>
        </div>
        <span class="text-xs font-bold text-amber-600 bg-amber-50 px-2 py-1 rounded-lg">PENDING</span>
      </div>
      <p class="text-3xl font-extrabold text-gray-900"><%= pendingBookings %></p>
      <p class="text-sm text-gray-500 mt-2">Awaiting approval</p>
    </div>

    <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card card-hover">
      <div class="flex items-center justify-between mb-4">
        <div class="bg-red-50 p-3 rounded-xl">
          <i class="ph-fill ph-warning-circle text-2xl text-red-600"></i>
        </div>
        <span class="text-xs font-bold text-red-600 bg-red-50 px-2 py-1 rounded-lg">CANCEL</span>
      </div>
      <p class="text-3xl font-extrabold text-gray-900"><%= cancellationRequests %></p>
      <p class="text-sm text-gray-500 mt-2">Cancel requests</p>
    </div>

    <div class="bg-gradient-to-br from-cyan-500 to-teal-600 rounded-2xl p-6 text-white shadow-lg card-hover">
      <div class="flex items-center justify-between mb-4">
        <div class="bg-white/20 p-3 rounded-xl backdrop-blur-sm">
          <i class="ph-fill ph-plus text-2xl text-white"></i>
        </div>
        <span class="text-xs font-bold text-white bg-white/20 px-2 py-1 rounded-lg backdrop-blur-sm">ACTION</span>
      </div>
      <p class="text-lg font-extrabold mb-2">New Booking</p>
      <p class="text-sm text-cyan-100 mb-4">Create reservation</p>
      <a href="add-booking.jsp" class="inline-flex items-center gap-2 bg-white text-cyan-600 font-bold px-4 py-2 rounded-xl hover:bg-cyan-50 transition-colors text-sm">
        <i class="ph-bold ph-plus"></i> Add Booking
      </a>
    </div>
  </div>

  <div class="w-full max-w-screen-xl mx-auto pb-16">
    <div class="bg-white border border-slate-200 rounded-3xl p-10 soft-card shadow-lg">

      <div class="flex justify-between items-end mb-8">
        <div>
          <h2 class="text-3xl font-extrabold text-gray-900 tracking-tight">Reservation Directory</h2>
          <p class="text-gray-500 text-lg mt-1 font-medium">Monitor and manage all vehicle rentals across the system.</p>
        </div>
      </div>

      <div class="overflow-x-auto rounded-2xl border border-slate-200 bg-white soft-card">
        <table class="w-full text-left border-collapse">
          <thead>
          <tr class="bg-slate-50 text-gray-500 text-sm uppercase tracking-wider border-b border-slate-200">
            <th class="py-4 px-6 font-bold">Booking ID</th>
            <th class="py-4 px-6 font-bold">Customer</th>
            <th class="py-4 px-6 font-bold">Vehicle ID</th>
            <th class="py-4 px-6 font-bold">Rental Date</th>
            <th class="py-4 px-6 font-bold">Duration</th>
            <th class="py-4 px-6 font-bold">Status</th>
            <th class="py-4 px-6 font-bold text-right">Actions</th>
          </tr>
          </thead>
          <tbody class="text-gray-700 text-base">
          <%
            PaymentDAO paymentDAO = new PaymentDAO();
            if (bookingList != null && !bookingList.isEmpty()) {
              for(Booking b : bookingList) {
          %>
          <tr class="border-b border-slate-100 hover:bg-cyan-50/50 transition-colors group">
            <td class="py-4 px-6 font-bold text-gray-900 text-lg">#<%= b.getId() %></td>
            <td class="py-4 px-6 font-semibold"><%= b.getCustomerName() %></td>
            <td class="py-4 px-6 font-semibold text-gray-600"><%= b.getVehicleId() %></td>
            <td class="py-4 px-6 text-gray-500"><%= b.getRentalDate() %></td>
            <td class="py-4 px-6 font-medium"><%= b.getDays() %> Days</td>
            <td class="py-4 px-6">
              <% if ("CONFIRMED".equalsIgnoreCase(b.getStatus()) || "UPDATED".equalsIgnoreCase(b.getStatus())) { %>
              <% if (paymentDAO.hasPaidPaymentForBooking(b.getId())) { %>
              <span class="bg-green-50 text-green-700 border border-green-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max">Confirmed + Paid</span>
              <% } else { %>
              <span class="bg-amber-50 text-amber-700 border border-amber-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max">Confirmed - Unpaid</span>
              <% } %>
              <% } else if ("PENDING".equalsIgnoreCase(b.getStatus())) { %>
              <span class="bg-yellow-50 text-yellow-700 border border-yellow-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max">Pending</span>
              <% } else if ("REJECTED".equalsIgnoreCase(b.getStatus())) { %>
              <span class="bg-red-50 text-red-700 border border-red-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max">Rejected</span>
              <% } else if ("CANCELLATION_REQUESTED".equalsIgnoreCase(b.getStatus())) { %>
              <span class="bg-amber-50 text-amber-700 border border-amber-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max">Cancel Requested</span>
              <% } else if ("CANCELLED".equalsIgnoreCase(b.getStatus())) { %>
              <span class="bg-gray-100 text-gray-700 border border-gray-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max">Cancelled</span>
              <% } else { %>
              <span class="bg-gray-100 text-gray-700 border border-gray-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max"><%= b.getStatus() %></span>
              <% } %>
            </td>
            <td class="py-4 px-6 text-right">
              <% if ("PENDING".equalsIgnoreCase(b.getStatus())) { %>
              <form action="bookings" method="POST" class="m-0 p-0 flex flex-col items-end gap-2">
                <input type="hidden" name="id" value="<%= b.getId() %>">
                <input type="text" name="staffMessage" maxlength="120" placeholder="Message to customer (optional)" class="w-64 border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-cyan-500 focus:ring-2 focus:ring-cyan-500">
                <div class="flex items-center justify-end gap-2">
                  <button type="submit" name="action" value="accept" class="text-green-700 bg-green-50 hover:bg-green-600 hover:text-white px-3 py-2 rounded-lg transition-colors shadow-sm text-sm font-bold" title="Accept Booking">
                    Accept
                  </button>
                  <button type="submit" name="action" value="decline" class="text-red-700 bg-red-50 hover:bg-red-600 hover:text-white px-3 py-2 rounded-lg transition-colors shadow-sm text-sm font-bold" title="Decline Booking">
                    Decline
                  </button>
                  <button type="submit" name="action" value="delete" onclick="return confirm('WARNING: Are you sure you want to delete <%= b.getId() %>?');" class="text-red-500 bg-red-50 hover:bg-red-600 hover:text-white p-2 rounded-lg transition-colors shadow-sm" title="Delete Booking">
                    <i class="ph-bold ph-trash text-lg"></i>
                  </button>
                </div>
              </form>
              <% } else if ("CANCELLATION_REQUESTED".equalsIgnoreCase(b.getStatus())) { %>
              <form action="bookings" method="POST" class="m-0 p-0 flex flex-col items-end gap-2">
                <input type="hidden" name="id" value="<%= b.getId() %>">
                <input type="text" name="staffMessage" maxlength="120" placeholder="Decision note (optional)" class="w-64 border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-cyan-500 focus:ring-2 focus:ring-cyan-500">
                <div class="flex items-center justify-end gap-2">
                  <button type="submit" name="action" value="approve_cancel" class="text-red-700 bg-red-50 hover:bg-red-600 hover:text-white px-3 py-2 rounded-lg transition-colors shadow-sm text-sm font-bold" title="Approve cancellation">
                    Approve Cancel
                  </button>
                  <button type="submit" name="action" value="reject_cancel" class="text-green-700 bg-green-50 hover:bg-green-600 hover:text-white px-3 py-2 rounded-lg transition-colors shadow-sm text-sm font-bold" title="Reject cancellation">
                    Reject Cancel
                  </button>
                </div>
              </form>
              <% } else { %>
              <form action="bookings" method="POST" class="m-0 p-0 flex items-center justify-end">
                <input type="hidden" name="id" value="<%= b.getId() %>">
                <button type="submit" name="action" value="delete" onclick="return confirm('WARNING: Are you sure you want to delete <%= b.getId() %>?');" class="text-red-500 bg-red-50 hover:bg-red-600 hover:text-white p-2 rounded-lg transition-colors shadow-sm" title="Delete Booking">
                  <i class="ph-bold ph-trash text-lg"></i>
                </button>
              </form>
              <% } %>
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
</main>
</body>
</html>

