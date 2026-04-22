<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Payment" %>
<%
    Boolean customerLoggedIn = (Boolean) session.getAttribute("customerLoggedIn");
    if (customerLoggedIn == null || !customerLoggedIn) {
        response.sendRedirect("index.jsp");
        return;
    }

    Booking booking = (Booking) request.getAttribute("booking");
    Payment payment = (Payment) request.getAttribute("payment");
    if (booking == null || payment == null) {
        response.sendRedirect("my-bookings.jsp?payment=missing_invoice");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fake Checkout</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50 min-h-screen p-8 text-gray-800">
<div class="max-w-2xl mx-auto">
    <a href="my-bookings.jsp" class="inline-block mb-6 text-sm font-bold text-blue-700">&larr; Back to My Bookings</a>

    <div class="bg-white border border-gray-200 rounded-2xl shadow-sm p-8">
        <h1 class="text-3xl font-extrabold mb-2">Fake Payment Window</h1>
        <p class="text-gray-500 mb-8">Your booking is confirmed. Payment is required before vehicle handover.</p>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
            <div class="bg-gray-50 border border-gray-200 rounded-xl p-4">
                <p class="text-xs text-gray-500 font-bold uppercase">Booking ID</p>
                <p class="text-lg font-extrabold"><%= booking.getId() %></p>
            </div>
            <div class="bg-gray-50 border border-gray-200 rounded-xl p-4">
                <p class="text-xs text-gray-500 font-bold uppercase">Vehicle ID</p>
                <p class="text-lg font-extrabold"><%= booking.getVehicleId() %></p>
            </div>
            <div class="bg-gray-50 border border-gray-200 rounded-xl p-4 md:col-span-2">
                <p class="text-xs text-gray-500 font-bold uppercase">Amount Due</p>
                <p class="text-3xl font-extrabold text-green-700">$<%= String.format("%.2f", payment.getAmount()) %></p>
            </div>
        </div>

        <form action="payments" method="post" class="space-y-5">
            <input type="hidden" name="action" value="customer_pay">
            <input type="hidden" name="bookingId" value="<%= booking.getId() %>">

            <div>
                <label class="block text-sm font-bold text-gray-700 mb-2">Payment Method</label>
                <select name="method" class="w-full border border-gray-300 rounded-xl px-4 py-3" required>
                    <option value="Card">Card (Fake Gateway)</option>
                    <option value="Cash">Cash</option>
                </select>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <label class="block text-sm font-bold text-gray-700 mb-2">Card Number (Demo)</label>
                    <input type="text" maxlength="19" placeholder="4111 1111 1111 1111" class="w-full border border-gray-300 rounded-xl px-4 py-3">
                </div>
                <div>
                    <label class="block text-sm font-bold text-gray-700 mb-2">CVV (Demo)</label>
                    <input type="text" maxlength="4" placeholder="123" class="w-full border border-gray-300 rounded-xl px-4 py-3">
                </div>
            </div>

            <button type="submit" class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3.5 rounded-xl">
                Pay Now (Fake)
            </button>
        </form>
    </div>
</div>
</body>
</html>

