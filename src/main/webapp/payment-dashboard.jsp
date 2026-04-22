<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Payment" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
        response.sendRedirect("login.jsp");
        return;
    }

    java.util.List paymentList = (java.util.List) request.getAttribute("paymentList");
    if (paymentList == null) {
        paymentList = new java.util.ArrayList<>();
    }

    int totalTransactions = paymentList.size();
    int paidCount = 0;
    int pendingCount = 0;
    int refundedCount = 0;
    double totalAmount = 0.0;

    for (Object obj : paymentList) {
        Payment payment = (Payment) obj;
        totalAmount += payment.getAmount();
        if ("PAID".equalsIgnoreCase(payment.getStatus())) {
            paidCount++;
        } else if ("PENDING".equalsIgnoreCase(payment.getStatus())) {
            pendingCount++;
        } else if ("REFUNDED".equalsIgnoreCase(payment.getStatus())) {
            refundedCount++;
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Billing & Payments - DriveAway</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; background: radial-gradient(circle at top left, #eef4ff 0%, #f8fafc 45%, #f1f5f9 100%); }
        .fade-in { animation: fadeIn 0.4s ease-out; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
        .soft-card { box-shadow: 0 10px 35px rgba(15, 23, 42, 0.06); }
    </style>
</head>
<body class="min-h-screen text-gray-800 flex flex-col">

<nav class="bg-slate-950/95 border-b border-slate-800 sticky top-0 z-20 shadow-lg backdrop-blur">
    <div class="max-w-screen-xl mx-auto px-8 h-20 flex items-center justify-between">
        <a href="admin-home.jsp" class="flex items-center gap-2 text-white">
            <i class="ph-bold ph-car-profile text-3xl text-blue-400"></i>
            <span class="text-2xl font-extrabold tracking-tight">DriveAway</span>
        </a>
        <div class="flex items-center gap-4">
            <a href="admin-home.jsp" class="text-slate-300 hover:text-white font-semibold transition-colors flex items-center gap-2">
                <i class="ph-bold ph-house"></i> Admin Hub
            </a>
            <a href="login.jsp" class="text-slate-100 hover:text-white font-bold border border-slate-700 bg-slate-900 hover:bg-slate-800 px-5 py-2.5 rounded-xl transition-all flex items-center gap-2 shadow-sm">
                Logout <i class="ph-bold ph-sign-out text-lg"></i>
            </a>
        </div>
    </div>
</nav>

<main class="flex-grow max-w-screen-xl mx-auto w-full px-8 py-10 fade-in">
    <div class="bg-gradient-to-br from-violet-600 via-indigo-600 to-blue-600 rounded-3xl p-10 sm:p-12 text-white shadow-xl mb-10 relative overflow-hidden">
        <div class="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
            <div>
                <h1 class="text-4xl sm:text-5xl font-extrabold mb-4 tracking-tight">Payments & Billing</h1>
                <p class="text-blue-100 text-lg font-medium max-w-2xl leading-relaxed">Track invoices, manage refunds, and view every transaction in a cleaner portal-style layout.</p>
            </div>

            <div class="flex gap-3 bg-white/10 p-4 rounded-2xl backdrop-blur-sm border border-white/20">
                <div class="text-center px-4 border-r border-white/20">
                    <p class="text-blue-100 text-xs font-bold uppercase tracking-wider mb-1">Transactions</p>
                    <p class="text-white font-extrabold"><%= totalTransactions %></p>
                </div>
                <div class="text-center px-4 border-r border-white/20">
                    <p class="text-blue-100 text-xs font-bold uppercase tracking-wider mb-1">Paid</p>
                    <p class="text-white font-extrabold"><%= paidCount %></p>
                </div>
                <div class="text-center px-4">
                    <p class="text-blue-100 text-xs font-bold uppercase tracking-wider mb-1">Pending</p>
                    <p class="text-white font-extrabold"><%= pendingCount %></p>
                </div>
            </div>
        </div>
        <i class="ph-fill ph-receipt absolute -right-10 -bottom-20 text-[300px] text-white opacity-10 transform -rotate-12 pointer-events-none"></i>
        <div class="absolute top-0 left-0 w-full h-full bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10 mix-blend-overlay pointer-events-none"></div>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6 mb-10">
        <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Transactions</p>
            <p class="text-3xl font-extrabold text-gray-900"><%= totalTransactions %></p>
            <p class="text-sm text-gray-500 mt-2">All recorded payments.</p>
        </div>
        <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Paid</p>
            <p class="text-3xl font-extrabold text-green-600"><%= paidCount %></p>
            <p class="text-sm text-gray-500 mt-2">Completed invoices.</p>
        </div>
        <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Refunded</p>
            <p class="text-3xl font-extrabold text-red-600"><%= refundedCount %></p>
            <p class="text-sm text-gray-500 mt-2">Returned amounts.</p>
        </div>
        <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Total Amount</p>
            <p class="text-3xl font-extrabold text-gray-900">$<%= String.format("%.2f", totalAmount) %></p>
            <p class="text-sm text-gray-500 mt-2">Across all listed invoices.</p>
        </div>
    </div>

    <div class="mb-6 flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4">
        <div>
            <h2 class="text-2xl font-bold text-gray-900">Financial Ledger</h2>
            <p class="text-gray-500 mt-1">Track invoice records in a simple, modern table.</p>
        </div>
        <a href="process-payment.jsp" class="bg-[#0D6EFD] hover:bg-blue-700 text-white font-bold px-5 py-3 rounded-xl shadow-sm transition-all flex items-center gap-2 justify-center">
            <i class="ph-bold ph-plus"></i> New Transaction
        </a>
    </div>

    <div class="bg-white rounded-2xl soft-card border border-slate-200 overflow-hidden">
        <table class="w-full text-left border-collapse">
            <thead>
            <tr class="border-b border-slate-100 bg-slate-50/80">
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Invoice ID</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Booking Ref</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Amount</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Method</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Status</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest text-right">Actions</th>
            </tr>
            </thead>
            <tbody>
            <%
                if (!paymentList.isEmpty()) {
                    for (Object obj : paymentList) {
                        Payment p = (Payment) obj;
            %>
            <tr class="hover:bg-indigo-50/40 transition-colors border-b border-gray-50 last:border-none group">
                <td class="py-5 px-8 whitespace-nowrap text-sm font-semibold text-gray-500">#<%= p.getPaymentId() %></td>
                <td class="py-5 px-8 whitespace-nowrap text-sm font-bold text-gray-900"><%= p.getBookingId() %></td>
                <td class="py-5 px-8 whitespace-nowrap text-sm font-bold text-green-600">$<%= String.format("%.2f", p.getAmount()) %></td>
                <td class="py-5 px-8 whitespace-nowrap text-sm text-gray-600"><%= p.getPaymentMethod() %></td>
                <td class="py-5 px-8 whitespace-nowrap text-sm font-bold">
                    <% if ("PAID".equalsIgnoreCase(p.getStatus())) { %>
                    <span class="text-green-700 bg-green-50 border border-green-200 px-3 py-1.5 rounded-lg">PAID</span>
                    <% } else if ("REFUNDED".equalsIgnoreCase(p.getStatus())) { %>
                    <span class="text-red-700 bg-red-50 border border-red-200 px-3 py-1.5 rounded-lg">REFUNDED</span>
                    <% } else { %>
                    <span class="text-yellow-700 bg-yellow-50 border border-yellow-200 px-3 py-1.5 rounded-lg">PENDING</span>
                    <% } %>
                </td>
                <td class="py-5 px-8 whitespace-nowrap text-right text-sm font-medium">
                    <div class="flex items-center justify-end gap-3">
                        <a href="payments?action=edit&id=<%= p.getPaymentId() %>" class="text-blue-700 hover:text-blue-900 bg-blue-50 hover:bg-blue-100 px-3 py-2 rounded-lg transition-colors flex items-center gap-2 font-semibold">
                            <i class="ph-bold ph-pencil-simple"></i> Edit
                        </a>
                        <a href="payments?action=delete&id=<%= p.getPaymentId() %>" class="text-red-600 hover:text-red-800 bg-red-50 hover:bg-red-100 px-3 py-2 rounded-lg transition-colors flex items-center gap-2 font-semibold" onclick="return confirm('Delete this invoice?');">
                            <i class="ph-bold ph-trash"></i> Delete
                        </a>
                    </div>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="6" class="py-24 text-center">
                    <div class="flex flex-col items-center justify-center">
                        <i class="ph-fill ph-receipt-x text-6xl text-gray-300 mb-4"></i>
                        <p class="text-2xl font-bold text-gray-900 mb-2 tracking-tight">No Transactions Found</p>
                        <p class="text-sm text-gray-500 font-medium max-w-md">Log a new payment to get started.</p>
                        <a href="process-payment.jsp" class="mt-6 bg-[#0D6EFD] text-white font-bold px-5 py-3 rounded-xl hover:bg-blue-700 transition">New Transaction</a>
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