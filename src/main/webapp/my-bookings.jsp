<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>
<%@ page import="com.example.vehiclerentalsystem01.dao.BookingDAO" %>

<%
    // Security Check
    Boolean customerLoggedIn = (Boolean) session.getAttribute("customerLoggedIn");
    if (customerLoggedIn == null || !customerLoggedIn) {
        response.sendRedirect("index.jsp");
        return;
    }

    String currentUser = (String) session.getAttribute("username");
    if (currentUser == null) currentUser = (String) session.getAttribute("name");
    if (currentUser == null) currentUser = "";

    // Fetch ONLY the bookings that belong to this specific customer
    BookingDAO bookingDAO = new BookingDAO();
    List<Booking> allBookings = bookingDAO.getAllBookings();
    List<Booking> myBookings = new ArrayList<>();

    if (allBookings != null) {
        for (Booking b : allBookings) {
            if (b.getCustomerName() != null && b.getCustomerName().trim().equalsIgnoreCase(currentUser.trim())) {
                myBookings.add(b);
            }
        }
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DriveAway | My Bookings</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');
        body { font-family: 'Inter', sans-serif; background-color: #F3F4F6; }
    </style>
</head>
<body class="min-h-screen p-6 md:p-10">

<div class="max-w-5xl mx-auto flex justify-between items-center mb-10">
    <a href="customer-home.jsp" class="flex items-center gap-2 bg-white px-5 py-2.5 rounded-xl font-bold text-gray-800 shadow-sm border border-gray-200 hover:bg-gray-50 transition">
        <i class="ph-bold ph-arrow-left text-xl"></i> Back to Portal
    </a>
    <h1 class="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
        <i class="ph-fill ph-calendar-check text-[#0D6EFD]"></i> My Reservations
    </h1>
</div>

<div class="max-w-5xl mx-auto space-y-6">
    <% if (myBookings.isEmpty()) { %>
    <div class="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-200 border-dashed">
        <i class="ph-fill ph-calendar-x text-6xl text-gray-300 mb-4"></i>
        <h3 class="text-2xl font-bold text-gray-700">No Active Rentals</h3>
        <p class="text-gray-500 mt-2 mb-6">You haven't booked any vehicles yet.</p>
        <a href="browse-cars.jsp" class="bg-[#0D6EFD] text-white font-bold py-3 px-8 rounded-xl hover:bg-blue-700 transition">Browse Fleet</a>
    </div>
    <% } else {
        for (Booking b : myBookings) {
    %>
    <div class="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 flex flex-col md:flex-row items-center justify-between gap-6 hover:shadow-md transition">

        <div class="flex items-center gap-6 w-full md:w-auto">
            <div class="w-16 h-16 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center text-3xl shrink-0">
                <i class="ph-fill ph-car"></i>
            </div>
            <div>
                <p class="text-sm text-gray-500 font-bold mb-1">Transaction ID: <%= b.getId() %></p>
                <h2 class="text-xl font-extrabold text-gray-900">Vehicle ID: <%= b.getVehicleId() %></h2>
                <div class="flex gap-4 mt-2 text-sm font-medium">
                            <span class="flex items-center gap-1.5 text-gray-600 bg-gray-100 px-3 py-1 rounded-lg">
                                <i class="ph-bold ph-calendar"></i> <%= b.getRentalDate() %> (<%= b.getDays() %> Days)
                            </span>

                    <% if("CONFIRMED".equalsIgnoreCase(b.getStatus())) { %>
                    <span class="flex items-center gap-1.5 text-green-700 bg-green-50 px-3 py-1 rounded-lg border border-green-200">
                                    <i class="ph-bold ph-check-circle"></i> Confirmed
                                </span>
                    <% } else if("CANCELLED".equalsIgnoreCase(b.getStatus())) { %>
                    <span class="flex items-center gap-1.5 text-red-700 bg-red-50 px-3 py-1 rounded-lg border border-red-200">
                                    <i class="ph-bold ph-x-circle"></i> Cancelled
                                </span>
                    <% } else { %>
                    <span class="flex items-center gap-1.5 text-yellow-700 bg-yellow-50 px-3 py-1 rounded-lg border border-yellow-200">
                                    <i class="ph-bold ph-clock"></i> <%= b.getStatus() %>
                                </span>
                    <% } %>
                </div>
            </div>
        </div>

        <div class="flex gap-3 w-full md:w-auto">
            <a href="edit-booking.jsp?id=<%= b.getId() %>" class="flex-1 md:flex-none text-center bg-gray-100 text-gray-700 font-bold py-2.5 px-5 rounded-xl hover:bg-gray-200 transition">
                Modify Dates
            </a>
            <a href="cancel-booking.jsp?id=<%= b.getId() %>" class="flex-1 md:flex-none text-center bg-red-50 text-red-600 font-bold py-2.5 px-5 rounded-xl hover:bg-red-100 transition border border-red-100">
                Cancel Trip
            </a>
        </div>

    </div>
    <%
            }
        }
    %>
</div>

</body>
</html>