<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>
<%@ page import="com.example.vehiclerentalsystem01.dao.BookingDAO" %>
<%@ page import="java.util.List" %>

<%
  // 1. Grab the ID from the URL (e.g., edit-booking.jsp?id=BKG-1234)
  String bookingId = request.getParameter("id");
  Booking b = null;

  // 2. Fetch that specific booking from the text file to pre-fill the form
  if (bookingId != null) {
    BookingDAO dao = new BookingDAO();
    List<Booking> allBookings = dao.getAllBookings();
    for (Booking bk : allBookings) {
      if (bk.getId().equals(bookingId)) {
        b = bk;
        break;
      }
    }
  }

  // 3. Security Check: If the booking doesn't exist, kick them back to safety
  if (b == null) {
    response.sendRedirect("customer-home.jsp");
    return;
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Edit Booking - DriveAway</title>
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
    <h2 class="text-3xl font-bold text-[#F59E0B] flex items-center gap-3">
      <i class="ph-bold ph-pencil-simple"></i> Modify Dates
    </h2>
    <button onclick="history.back()" class="text-gray-500 hover:text-gray-800 transition-colors">
      <i class="ph-bold ph-x text-2xl"></i>
    </button>
  </div>

  <form action="bookings" method="POST" class="space-y-6">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="id" value="<%= b.getId() %>">

    <div>
      <label class="block font-bold text-gray-700 mb-1">Customer Name:</label>
      <input type="text" name="customerName" value="<%= b.getCustomerName() %>" readonly
             class="w-full bg-gray-100 text-gray-500 border border-gray-300 rounded-lg p-3 outline-none cursor-not-allowed">
    </div>

    <div>
      <label class="block font-bold text-gray-700 mb-1">Vehicle ID:</label>
      <input type="text" name="vehicleId" value="<%= b.getVehicleId() %>" readonly
             class="w-full bg-gray-100 text-gray-500 border border-gray-300 rounded-lg p-3 outline-none cursor-not-allowed">
    </div>

    <div class="flex gap-4">
      <div class="flex-1">
        <label class="block font-bold text-gray-700 mb-1">New Rental Date:</label>
        <input type="date" name="rentalDate" value="<%= b.getRentalDate() %>" required
               class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#F59E0B] focus:ring-2 focus:ring-yellow-200 transition-all">
      </div>
      <div class="w-1/3">
        <label class="block font-bold text-gray-700 mb-1">Duration (Days):</label>
        <input type="number" name="days" value="<%= b.getDays() %>" min="1" required
               class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#F59E0B] focus:ring-2 focus:ring-yellow-200 transition-all">
      </div>
    </div>

    <button type="submit" class="w-full bg-[#F59E0B] hover:bg-yellow-600 text-white font-bold text-lg py-4 rounded-xl flex justify-center items-center gap-3 mt-8 shadow-md transition-all">
      <i class="ph-bold ph-floppy-disk text-2xl"></i> Save Changes
    </button>
  </form>

</div>

</body>
</html>