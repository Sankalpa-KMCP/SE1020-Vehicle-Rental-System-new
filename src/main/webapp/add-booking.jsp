<%@ page contentType="text/html; charset=UTF-8" %>
<%
  // Ensure only authorized users can view this page
  Boolean isLoggedIn = (Boolean) session.getAttribute("adminLoggedIn");
  boolean isAdmin = (isLoggedIn != null && isLoggedIn);
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

<div class="w-full max-w-3xl flex gap-4 mb-6 mt-4">
  <button onclick="history.back()"
          class="flex items-center justify-center gap-2 bg-white px-5 py-2.5 rounded-xl font-bold text-gray-800 shadow-sm border border-gray-200 hover:bg-gray-50 transition">
    <div class="bg-gray-500 rounded-full text-white p-0.5 flex items-center justify-center h-6 w-6">
      <i class="ph-bold ph-arrow-left text-sm"></i>
    </div>
    <span class="text-lg">Back</span>
  </button>
  <a href="<%= isAdmin ? "admin-home.jsp" : "index.jsp" %>" class="flex items-center justify-center gap-2
      bg-[#1C2024] px-5 py-2.5 rounded-xl font-bold text-white shadow-sm hover:bg-black transition">
    <i class="ph-fill ph-house text-[#FFC107] text-2xl"></i>
    <span class="text-lg">Home</span>
  </a>
</div>

<div class="w-full max-w-3xl bg-white border-[3px] border-[#0D6EFD] rounded-[24px] p-10 md:p-12 shadow-sm mb-12">

  <div class="flex flex-col items-center mb-10">
    <div class="bg-[#EBF4FF] text-[#0D6EFD] w-20 h-20 flex items-center justify-center rounded-full mb-4">
      <i class="ph-fill ph-calendar-check text-4xl"></i>
    </div>
    <h2 class="text-[36px] font-extrabold text-[#0D6EFD] tracking-tight mb-1">Book Your Vehicle</h2>
    <p class="text-gray-500 text-lg font-medium">Experience luxury and comfort on your next journey.</p>
  </div>

  <form action="bookings" method="POST" class="space-y-6">

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div>
        <label class="block text-gray-700 font-bold mb-2 text-sm">Customer Full Name</label>
        <div class="relative">
          <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-gray-400">
            <i class="ph-bold ph-user text-xl"></i>
          </div>
          <input type="text" name="customerName" required placeholder="John Doe"
                 class="w-full border border-gray-200 rounded-xl pl-12 pr-4 py-3.5 focus:outline-none focus:border-[#0D6EFD] focus:ring-1 focus:ring-[#0D6EFD] text-lg font-medium text-gray-800 transition">
        </div>
      </div>

      <div>
        <label class="block text-gray-700 font-bold mb-2 text-sm">Vehicle ID</label>
        <div class="relative">
          <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-gray-400">
            <i class="ph-bold ph-car-profile text-xl"></i>
          </div>
          <input type="text" name="vehicleId" required placeholder="E.g., V-101"
                 class="w-full border border-gray-200 rounded-xl pl-12 pr-4 py-3.5 focus:outline-none focus:border-[#0D6EFD] focus:ring-1 focus:ring-[#0D6EFD] text-lg font-medium text-gray-800 transition">
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div>
        <label class="block text-gray-700 font-bold mb-2 text-sm">Pick-Up Date</label>
        <input type="date" name="rentalDate" required
               class="w-full border border-gray-200 rounded-xl px-4 py-3.5 focus:outline-none focus:border-[#0D6EFD] focus:ring-1 focus:ring-[#0D6EFD] text-lg font-medium text-gray-800 transition text-gray-500">
      </div>
      <div>
        <label class="block text-gray-700 font-bold mb-2 text-sm">Return Date</label>
        <input type="date" name="returnDate" required
               class="w-full border border-gray-200 rounded-xl px-4 py-3.5 focus:outline-none focus:border-[#0D6EFD] focus:ring-1 focus:ring-[#0D6EFD] text-lg font-medium text-gray-800 transition text-gray-500">
      </div>
    </div>

    <div>
      <label class="block text-gray-700 font-bold mb-2 text-sm">Purpose of Rental (Booking Type)</label>
      <div class="relative">
        <select name="bookingType"
                class="w-full border border-gray-200 rounded-xl px-4 py-3.5 appearance-none focus:outline-none focus:border-[#0D6EFD] focus:ring-1 focus:ring-[#0D6EFD] text-lg font-medium text-gray-800 transition bg-white">
          <option>Personal / Vacation</option>
          <option>Business Trip</option>
          <option>Relocation</option>
        </select>
        <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-gray-500">
          <i class="ph-bold ph-caret-down text-xl"></i>
        </div>
      </div>
    </div>

    <div class="border border-gray-200 rounded-xl p-4 mt-2">
      <div class="flex items-center gap-2 mb-1.5">
        <i class="ph-bold ph-clock text-[#0D6EFD] text-lg"></i>
        <label class="text-[#0D6EFD] font-bold text-sm">Duration (Days)</label>
      </div>
      <input type="number" name="days" min="1" required placeholder="1" value="1"
             class="w-full bg-transparent border-none focus:outline-none text-xl font-bold text-gray-900">
    </div>

    <button type="submit" name="action" value="create"
            class="w-full bg-[#0D6EFD] hover:bg-blue-700 text-white font-bold py-4 rounded-xl text-xl flex justify-center items-center gap-3 mt-6 shadow-md transition-all">
      <i class="ph-bold ph-calendar-plus text-2xl"></i> Confirm Reservation
    </button>

    <hr class="my-8 border-gray-100">

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 max-w-xl mx-auto">
      <a href="search-booking.jsp"
         class="flex justify-center items-center gap-2 px-6 py-3 border border-[#06B6D4] text-[#06B6D4] font-bold rounded-2xl hover:bg-cyan-50 transition-colors text-lg">
        <i class="ph-bold ph-magnifying-glass text-2xl"></i> Search Booking
      </a>
      <a href="cancel-booking.jsp"
         class="flex justify-center items-center gap-2 px-6 py-3 border border-[#EF4444] text-[#EF4444] font-bold rounded-2xl hover:bg-red-50 transition-colors text-lg">
        <i class="ph-bold ph-trash text-2xl"></i> Cancel Booking
      </a>
      <a href="edit-booking.jsp"
         class="flex justify-center items-center gap-2 px-6 py-3 border border-[#F59E0B] text-[#F59E0B] font-bold rounded-2xl hover:bg-yellow-50 transition-colors text-lg">
        <i class="ph-bold ph-pencil-simple text-2xl"></i> Edit Booking
      </a>
      <a href="bookings?action=view-all"
         class="flex justify-center items-center gap-2 px-6 py-3 border border-[#10B981] text-[#10B981] font-bold rounded-2xl hover:bg-green-50 transition-colors text-lg">
        <i class="ph-bold ph-list-dashes text-2xl"></i> View All
      </a>
    </div>

  </form>
</div>

</body>

</html>