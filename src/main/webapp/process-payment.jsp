<%@ page contentType="text/html; charset=UTF-8" %>
<%
    String error = request.getParameter("error");
    String prefillBookingId = request.getParameter("bookingId") == null ? "" : request.getParameter("bookingId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Process Payment - DriveAway</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #F4F7F6; }
        .fade-in { animation: fadeIn 0.4s ease-out; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
    </style>
</head>
<body class="min-h-screen text-gray-800 flex flex-col">

<nav class="bg-white border-b border-gray-200 sticky top-0 z-20 shadow-sm">
    <div class="max-w-screen-xl mx-auto px-8 h-20 flex items-center justify-between">
        <a href="payments?action=list" class="flex items-center gap-2 text-gray-900">
            <i class="ph-bold ph-receipt text-3xl text-blue-600"></i>
            <span class="text-2xl font-extrabold tracking-tight">DriveAway</span>
        </a>
        <div class="flex items-center gap-3">
            <button onclick="history.back()" class="text-gray-600 hover:text-blue-600 font-semibold transition-colors flex items-center gap-2">
                <i class="ph-bold ph-arrow-left"></i> Back
            </button>
            <a href="admin-home.jsp" class="text-gray-600 hover:text-blue-600 font-semibold transition-colors flex items-center gap-2">
                <i class="ph-bold ph-house"></i> Home
            </a>
        </div>
    </div>
</nav>

<main class="flex-grow max-w-screen-md mx-auto w-full px-8 py-10 fade-in">
    <div class="bg-gradient-to-br from-[#0D6EFD] to-[#0a58ca] rounded-3xl p-10 sm:p-12 text-white shadow-lg mb-8 relative overflow-hidden">
        <div class="relative z-10">
            <h1 class="text-4xl sm:text-5xl font-extrabold mb-4 tracking-tight">Process Payment</h1>
            <p class="text-blue-100 text-lg font-medium max-w-2xl leading-relaxed">Create a transaction with a cleaner form that matches the customer portal style.</p>
        </div>
        <i class="ph-fill ph-receipt absolute -right-8 -bottom-16 text-[220px] text-white opacity-10 transform -rotate-12 pointer-events-none"></i>
    </div>

    <div class="bg-white rounded-3xl p-10 sm:p-12 shadow-sm border border-gray-100">
        <% if ("booking_not_confirmed".equals(error)) { %>
        <div class="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl font-semibold">
            Payment blocked: only <strong>CONFIRMED</strong> bookings can be paid.
        </div>
        <% } else if ("invalid_amount".equals(error)) { %>
        <div class="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl font-semibold">
            Invalid amount. Enter a valid numeric payment amount.
        </div>
        <% } else if ("missing_booking".equals(error)) { %>
        <div class="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl font-semibold">
            Booking reference is required.
        </div>
        <% } %>

        <div class="flex items-center gap-4 mb-8">
            <div class="w-14 h-14 rounded-2xl bg-blue-50 text-blue-600 flex items-center justify-center text-2xl border border-blue-100">
                <i class="ph ph-credit-card"></i>
            </div>
            <div>
                <h2 class="text-2xl font-extrabold text-gray-900 tracking-tight">Transaction Details</h2>
                <p class="text-gray-500 font-medium">Enter the invoice details and save the payment.</p>
            </div>
        </div>

        <form action="payments" method="POST" class="space-y-6">
            <div>
                <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Invoice ID (Optional)</label>
                <input id="manualId" type="text" name="manualId" placeholder="e.g., INV-001" class="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all font-medium text-gray-900 shadow-inner">
            </div>

            <div>
                <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Booking Reference ID</label>
                <input id="bookingId" type="text" name="bookingId" value="<%= prefillBookingId %>" required placeholder="e.g., BKG-XYZ" class="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all font-medium text-gray-900 shadow-inner">
                <p class="text-xs text-gray-500 mt-2 font-medium">Only bookings in CONFIRMED status are payable.</p>
            </div>

            <div>
                <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Total Amount ($)</label>
                <input id="amount" type="number" step="0.01" name="amount" required placeholder="0.00" class="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all font-medium text-gray-900 shadow-inner">
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                    <label for="method" class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Method</label>
                    <select id="method" name="method" class="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all font-medium text-gray-900 shadow-inner">
                        <option value="Card">Credit/Debit Card</option>
                        <option value="Cash">Cash Transaction</option>
                    </select>
                </div>
                <div>
                    <label for="status" class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Status</label>
                    <select id="status" name="status" class="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all font-medium text-gray-900 shadow-inner">
                        <option value="PAID">PAID</option>
                        <option value="PENDING">PENDING</option>
                        <option value="REFUNDED">REFUNDED</option>
                    </select>
                </div>
            </div>

            <button type="submit" name="action" value="create" class="w-full bg-[#0D6EFD] hover:bg-blue-700 text-white font-bold px-8 py-3.5 rounded-xl shadow-lg shadow-blue-600/20 hover:shadow-blue-600/40 transition-all flex justify-center items-center gap-2 transform hover:-translate-y-0.5">
                <i class="ph-bold ph-credit-card text-lg"></i> Save Transaction
            </button>

            <div class="pt-2 grid grid-cols-1 sm:grid-cols-2 gap-3">
                <button type="button" onclick="handleSearch()" class="flex items-center justify-center gap-2 px-5 py-3 border-2 border-blue-200 text-blue-700 font-bold rounded-xl hover:bg-blue-50 transition-colors">
                    <i class="ph-bold ph-magnifying-glass"></i> View Ledger
                </button>
                <button type="button" onclick="handleEdit()" class="flex items-center justify-center gap-2 px-5 py-3 border-2 border-yellow-200 text-yellow-700 font-bold rounded-xl hover:bg-yellow-50 transition-colors">
                    <i class="ph-bold ph-pencil-simple"></i> Edit Invoice
                </button>
                <button type="button" onclick="handleDelete()" class="flex items-center justify-center gap-2 px-5 py-3 border-2 border-red-200 text-red-700 font-bold rounded-xl hover:bg-red-50 transition-colors">
                    <i class="ph-bold ph-trash"></i> Delete Invoice
                </button>
                <a href="payments?action=list" class="flex items-center justify-center gap-2 px-5 py-3 border-2 border-green-200 text-green-700 font-bold rounded-xl hover:bg-green-50 transition-colors">
                    <i class="ph-bold ph-list-dashes"></i> Billing Ledger
                </a>
            </div>
        </form>
    </div>
</main>

<script>
    function handleSearch() { window.location.href = "payments?action=list"; }
    function handleEdit() {
        let id = prompt("Enter the Invoice ID you want to Edit:");
        if (id) window.location.href = "payments?action=edit&id=" + id.trim();
    }
    function handleDelete() {
        let id = prompt("Enter the Invoice ID you want to Delete:");
        if (id && confirm("Permanently delete invoice " + id + "?")) {
            window.location.href = "payments?action=delete&id=" + id.trim();
        }
    }
</script>
</body>
</html>