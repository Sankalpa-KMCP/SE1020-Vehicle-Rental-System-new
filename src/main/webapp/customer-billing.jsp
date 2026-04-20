<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.example.vehiclerentalsystem01.dao.BookingDAO" %>
<%@ page import="com.example.vehiclerentalsystem01.dao.PaymentDAO" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Payment" %>

<%
    Boolean customerLoggedIn = (Boolean) session.getAttribute("customerLoggedIn");
    if (customerLoggedIn == null || !customerLoggedIn) {
        response.sendRedirect("index.jsp");
        return;
    }

    String displayName = (String) session.getAttribute("name");
    String currentUser = displayName;
    if (currentUser == null || currentUser.trim().isEmpty()) {
        currentUser = (String) session.getAttribute("username");
        if (currentUser != null && !currentUser.trim().isEmpty()) {
            displayName = "@" + currentUser;
        }
    }
    if (displayName == null || displayName.trim().isEmpty()) {
        displayName = "Customer";
    }
    if (currentUser == null) {
        currentUser = "";
    }

    BookingDAO bookingDAO = new BookingDAO();
    PaymentDAO paymentDAO = new PaymentDAO();

    List<Booking> allBookings = bookingDAO.getAllBookings();
    List<Booking> myBookings = new ArrayList<>();
    Set<String> myBookingIds = new HashSet<>();

    if (allBookings != null) {
        for (Booking b : allBookings) {
            if (b.getCustomerName() != null && b.getCustomerName().trim().equalsIgnoreCase(currentUser.trim())) {
                myBookings.add(b);
                myBookingIds.add(b.getTransactionId());
            }
        }
    }

    List<Payment> allPayments = paymentDAO.getAllPayments();
    List<Payment> myPayments = new ArrayList<>();
    double paidTotal = 0.0;
    double pendingTotal = 0.0;
    double refundedTotal = 0.0;

    if (allPayments != null) {
        for (Payment p : allPayments) {
            if (p.getBookingId() != null && myBookingIds.contains(p.getBookingId())) {
                myPayments.add(p);
                if ("PAID".equalsIgnoreCase(p.getStatus())) {
                    paidTotal += p.getAmount();
                } else if ("PENDING".equalsIgnoreCase(p.getStatus())) {
                    pendingTotal += p.getAmount();
                } else if ("REFUNDED".equalsIgnoreCase(p.getStatus())) {
                    refundedTotal += p.getAmount();
                }
            }
        }
    }

    double totalBilled = paidTotal + pendingTotal + refundedTotal;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DriveAway | Billing & Payments</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #F4F7F6; }
        .fade-in { animation: fadeIn 0.35s ease-out; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
    </style>
</head>
<body class="min-h-screen text-gray-800">

<nav class="bg-white border-b border-gray-200 sticky top-0 z-20 shadow-sm">
    <div class="max-w-screen-xl mx-auto px-8 h-20 flex items-center justify-between">
        <a href="customer-home.jsp" class="flex items-center gap-2 text-gray-900">
            <i class="ph-bold ph-car-profile text-3xl text-blue-600"></i>
            <span class="text-2xl font-extrabold tracking-tight">DriveAway</span>
        </a>

        <div class="flex items-center gap-6">
            <div class="flex items-center gap-3">
                <div class="bg-blue-50 text-blue-600 p-2 rounded-full border border-blue-100">
                    <i class="ph-bold ph-receipt text-xl"></i>
                </div>
                <div class="hidden sm:block">
                    <p class="text-xs text-gray-500 font-semibold uppercase tracking-wider">Billing Account</p>
                    <p class="font-bold text-gray-900 leading-tight"><%= displayName %></p>
                </div>
            </div>
            <div class="w-px h-8 bg-gray-200 hidden sm:block"></div>
            <form action="auth" method="post" class="m-0">
                <input type="hidden" name="action" value="logout">
                <button type="submit" class="text-gray-600 hover:text-red-700 font-bold border border-gray-200 bg-white hover:bg-red-50 hover:border-red-200 px-5 py-2.5 rounded-xl transition-all flex items-center gap-2 shadow-sm">
                    Logout <i class="ph-bold ph-sign-out text-lg"></i>
                </button>
            </form>
        </div>
    </div>
</nav>

<main class="max-w-screen-xl mx-auto w-full px-8 py-10 fade-in">
    <div class="bg-gradient-to-br from-[#0D6EFD] to-[#0a58ca] rounded-3xl p-10 sm:p-12 text-white shadow-lg mb-10 relative overflow-hidden">
        <div class="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
            <div>
                <h1 class="text-4xl sm:text-5xl font-extrabold mb-4 tracking-tight">Billing & Payments</h1>
                <p class="text-blue-100 text-lg font-medium max-w-2xl leading-relaxed">Track the invoices linked to your bookings, review payment status, and keep an eye on any pending balances from your DriveAway reservations.</p>
            </div>

            <div class="flex gap-3 bg-white/10 p-4 rounded-2xl backdrop-blur-sm border border-white/20">
                <div class="text-center px-4 border-r border-white/20">
                    <p class="text-blue-100 text-xs font-bold uppercase tracking-wider mb-1">Bookings</p>
                    <p class="text-white font-extrabold"><%= myBookings.size() %></p>
                </div>
                <div class="text-center px-4">
                    <p class="text-blue-100 text-xs font-bold uppercase tracking-wider mb-1">Invoices</p>
                    <p class="text-white font-extrabold"><%= myPayments.size() %></p>
                </div>
            </div>
        </div>
        <i class="ph-fill ph-receipt absolute -right-10 -bottom-20 text-[300px] text-white opacity-10 transform -rotate-12 pointer-events-none"></i>
        <div class="absolute top-0 left-0 w-full h-full bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10 mix-blend-overlay pointer-events-none"></div>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6 mb-10">
        <div class="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Total Invoices</p>
            <p class="text-3xl font-extrabold text-gray-900"><%= myPayments.size() %></p>
            <p class="text-sm text-gray-500 mt-2">All billing records linked to your bookings.</p>
        </div>
        <div class="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Paid Total</p>
            <p class="text-3xl font-extrabold text-green-600">$<%= String.format("%.2f", paidTotal) %></p>
            <p class="text-sm text-gray-500 mt-2">Completed transactions.</p>
        </div>
        <div class="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Pending Total</p>
            <p class="text-3xl font-extrabold text-yellow-600">$<%= String.format("%.2f", pendingTotal) %></p>
            <p class="text-sm text-gray-500 mt-2">Waiting to be settled.</p>
        </div>
        <div class="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Total Billed</p>
            <p class="text-3xl font-extrabold text-gray-900">$<%= String.format("%.2f", totalBilled) %></p>
            <p class="text-sm text-gray-500 mt-2">All invoices linked to your account. Refunds total $<%= String.format("%.2f", refundedTotal) %>.</p>
        </div>
    </div>

    <div class="mb-6 flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4">
        <div>
            <h2 class="text-2xl font-bold text-gray-900">Your Invoice Ledger</h2>
            <p class="text-gray-500 mt-1">Invoices are matched using the bookings on your account.</p>
        </div>
        <div class="flex gap-3">
            <a href="my-bookings.jsp" class="bg-white border border-gray-200 text-gray-700 font-bold px-5 py-3 rounded-xl hover:bg-gray-50 transition shadow-sm">My Bookings</a>
            <a href="browse-cars.jsp" class="bg-[#0D6EFD] text-white font-bold px-5 py-3 rounded-xl hover:bg-blue-700 transition shadow-sm">Browse Fleet</a>
        </div>
    </div>

    <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
        <table class="w-full text-left border-collapse">
            <thead>
            <tr class="border-b border-gray-100 bg-gray-50">
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Invoice ID</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Booking Ref</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Amount</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Method</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Status</th>
            </tr>
            </thead>
            <tbody>
            <%
                if (!myPayments.isEmpty()) {
                    for (Payment p : myPayments) {
            %>
            <tr class="hover:bg-blue-50/30 transition-colors border-b border-gray-50 last:border-none">
                <td class="py-5 px-8 whitespace-nowrap font-bold text-gray-900">#<%= p.getPaymentId() %></td>
                <td class="py-5 px-8 whitespace-nowrap text-gray-600 font-medium"><%= p.getBookingId() %></td>
                <td class="py-5 px-8 whitespace-nowrap font-bold text-green-600">$<%= String.format("%.2f", p.getAmount()) %></td>
                <td class="py-5 px-8 whitespace-nowrap text-gray-700"><%= p.getPaymentMethod() %></td>
                <td class="py-5 px-8 whitespace-nowrap font-bold">
                    <% if ("PAID".equalsIgnoreCase(p.getStatus())) { %>
                    <span class="text-green-700 bg-green-50 border border-green-200 px-3 py-1.5 rounded-lg">PAID</span>
                    <% } else if ("PENDING".equalsIgnoreCase(p.getStatus())) { %>
                    <span class="text-yellow-700 bg-yellow-50 border border-yellow-200 px-3 py-1.5 rounded-lg">PENDING</span>
                    <% } else if ("REFUNDED".equalsIgnoreCase(p.getStatus())) { %>
                    <span class="text-red-700 bg-red-50 border border-red-200 px-3 py-1.5 rounded-lg">REFUNDED</span>
                    <% } else { %>
                    <span class="text-gray-700 bg-gray-100 border border-gray-200 px-3 py-1.5 rounded-lg"><%= p.getStatus() %></span>
                    <% } %>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="5" class="py-20 text-center">
                    <div class="flex flex-col items-center justify-center">
                        <i class="ph-fill ph-receipt-x text-6xl text-gray-300 mb-4"></i>
                        <p class="text-2xl font-bold text-gray-900 mb-2 tracking-tight">No Invoices Yet</p>
                        <p class="text-sm text-gray-500 font-medium max-w-md">Your payment records will appear here once an admin creates a transaction for one of your bookings.</p>
                        <div class="mt-6 flex gap-3">
                            <a href="my-bookings.jsp" class="bg-[#0D6EFD] text-white font-bold px-5 py-3 rounded-xl hover:bg-blue-700 transition">Check My Bookings</a>
                            <a href="customer-home.jsp" class="bg-gray-100 text-gray-700 font-bold px-5 py-3 rounded-xl hover:bg-gray-200 transition">Back to Portal</a>
                        </div>
                    </div>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</main>

</body>
</html>

