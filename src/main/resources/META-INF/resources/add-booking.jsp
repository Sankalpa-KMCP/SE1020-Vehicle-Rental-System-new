<%@ page contentType="text/html; charset=UTF-8" %>
<%
  // Grab the logged-in user's username to lock it in the form
  String sessionUsername = (String) session.getAttribute("username");
  if (sessionUsername == null) {
    sessionUsername = ""; // Fallback
  }

  String preselectedVehicleId = request.getParameter("vehicleId");
  if (preselectedVehicleId == null) {
    preselectedVehicleId = "";
  }
%>
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Book Your Vehicle - DriveAway</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

    body {
      font-family: 'Inter', sans-serif;
      background-color: #FAFAFA;
    }

    /* Stylizes the date picker icon slightly */
    input[type="date"]::-webkit-calendar-picker-indicator {
      color: #6B7280;
      cursor: pointer;
      opacity: 0.6;
    }
  </style>
</head>

<body class="min-h-screen p-6 relative flex flex-col items-center">

<div class="w-full max-w-2xl bg-white p-10 rounded-3xl shadow-lg border border-gray-200 mt-10">
  <div class="flex justify-between items-center mb-8">
    <h2 class="text-3xl font-extrabold text-[#06B6D4] flex items-center gap-3">
      <i class="ph-bold ph-car-profile"></i> New Reservation
    </h2>
    <button onclick="history.back()" class="text-gray-500 hover:text-gray-800 transition-colors">
      <i class="ph-bold ph-x text-2xl"></i>
    </button>
  </div>

  <form action="bookings" method="POST" class="space-y-6">
    <input type="hidden" name="action" value="create">

    <% if ("vehicle_unavailable".equals(request.getParameter("error"))) { %>
    <div class="bg-red-50 border border-red-200 text-red-700 font-semibold px-4 py-3 rounded-xl">
      This vehicle is currently unavailable based on booking decisions. Please choose a different vehicle.
    </div>
    <% } %>

    <div>
      <label class="block font-bold text-gray-700 mb-2">Customer Name</label>
      <input type="text" name="customerName" value="<%= sessionUsername %>" readonly
             class="w-full bg-gray-100 border border-gray-300 rounded-xl p-4 outline-none cursor-not-allowed text-gray-600 font-medium">
      <p class="text-sm text-gray-400 mt-1"><i class="ph-fill ph-lock-key"></i> Locked to your account for security.</p>
    </div>

    <div>
      <label class="block font-bold text-gray-700 mb-2">Vehicle ID</label>
      <input type="text" name="vehicleId" value="<%= preselectedVehicleId %>" required placeholder="e.g., V-101"
             class="w-full border border-gray-300 rounded-xl p-4 outline-none focus:border-[#06B6D4] focus:ring-2 focus:ring-cyan-200 transition-all">
    </div>

    <div class="flex gap-4">
      <div class="flex-1">
        <label class="block font-bold text-gray-700 mb-2">Rental Date</label>
        <input type="date" name="rentalDate" required
               class="w-full border border-gray-300 rounded-xl p-4 outline-none focus:border-[#06B6D4] focus:ring-2 focus:ring-cyan-200 transition-all">
      </div>
      <div class="w-1/3">
        <label class="block font-bold text-gray-700 mb-2">Days</label>
        <input type="number" name="days" min="1" value="1" required
               class="w-full border border-gray-300 rounded-xl p-4 outline-none focus:border-[#06B6D4] focus:ring-2 focus:ring-cyan-200 transition-all">
      </div>
    </div>

    <button type="submit" class="w-full bg-[#06B6D4] hover:bg-cyan-600 text-white font-bold text-lg py-4 rounded-xl flex justify-center items-center gap-3 mt-8 shadow-md transition-all">
      <i class="ph-bold ph-calendar-plus text-2xl"></i> Confirm Reservation
    </button>
  </form>

  <hr class="my-8 border-gray-100">

  <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mx-auto">
    <a href="search-booking.jsp" class="flex justify-center items-center gap-2 px-4 py-3 border border-[#06B6D4] text-[#06B6D4] font-bold rounded-2xl hover:bg-cyan-50 transition-colors">
      <i class="ph-bold ph-magnifying-glass text-xl"></i> Search
    </a>
    <a href="cancel-booking.jsp" class="flex justify-center items-center gap-2 px-4 py-3 border border-[#EF4444] text-[#EF4444] font-bold rounded-2xl hover:bg-red-50 transition-colors">
      <i class="ph-bold ph-trash text-xl"></i> Cancel
    </a>
    <a href="edit-booking.jsp" class="flex justify-center items-center gap-2 px-4 py-3 border border-[#F59E0B] text-[#F59E0B] font-bold rounded-2xl hover:bg-yellow-50 transition-colors">
      <i class="ph-bold ph-pencil-simple text-xl"></i> Edit
    </a>
  </div>
</div>

</body>
</html>

