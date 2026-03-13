<%@ page import="com.example.vehiclerentalsystem01.models.Payment" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    Payment p = (Payment) request.getAttribute("payment");
    if (p == null) { response.sendRedirect("payments?action=list"); return; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Payment</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <style>@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&display=swap'); body { font-family: 'Inter', sans-serif; }</style>
</head>
<body class="bg-[#FAFAFA] flex h-screen items-center justify-center p-4">
<div class="max-w-lg w-full bg-white p-10 rounded-2xl shadow-lg border-[3px] border-[#FFC107]">
    <div class="text-center mb-8">
        <i class="ph-fill ph-pencil-simple text-5xl text-[#FFC107] mb-2"></i>
        <h2 class="text-3xl font-bold text-gray-900 tracking-tight">Edit Transaction</h2>
    </div>

    <form action="payments" method="post" class="space-y-5">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="id" value="<%= p.getPaymentId() %>">

        <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">Booking Ref:</label>
            <input type="text" name="bookingId" value="<%= p.getBookingId() %>" required class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#FFC107]">
        </div>
        <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">Amount ($):</label>
            <input type="number" step="0.01" name="amount" value="<%= p.getAmount() %>" required class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#FFC107]">
        </div>
        <div class="grid grid-cols-2 gap-4">
            <div>
                <label class="block text-sm font-bold text-gray-700 mb-1">Method:</label>
                <select name="method" class="w-full border border-gray-300 rounded-lg p-3 outline-none">
                    <option value="Card" <%= p.getPaymentMethod().contains("Card") ? "selected" : "" %>>Card</option>
                    <option value="Cash" <%= p.getPaymentMethod().contains("Cash") ? "selected" : "" %>>Cash</option>
                </select>
            </div>
            <div>
                <label class="block text-sm font-bold text-gray-700 mb-1">Status:</label>
                <select name="status" class="w-full border border-gray-300 rounded-lg p-3 outline-none">
                    <option value="PAID" <%= p.getStatus().equals("PAID") ? "selected" : "" %>>PAID</option>
                    <option value="PENDING" <%= p.getStatus().equals("PENDING") ? "selected" : "" %>>PENDING</option>
                    <option value="REFUNDED" <%= p.getStatus().equals("REFUNDED") ? "selected" : "" %>>REFUNDED</option>
                </select>
            </div>
        </div>
        <div class="pt-4 flex gap-3">
            <a href="payments?action=list" class="w-1/3 bg-gray-100 text-gray-700 font-bold py-3 px-4 rounded-lg hover:bg-gray-200 text-center">Cancel</a>
            <button type="submit" class="w-2/3 bg-[#FFC107] text-gray-900 font-bold py-3 px-4 rounded-lg hover:bg-[#e0a800]">Update Data</button>
        </div>
    </form>
</div>
</body>
</html>