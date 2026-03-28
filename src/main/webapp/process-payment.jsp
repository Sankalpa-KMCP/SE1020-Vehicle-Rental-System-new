<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Process Payment - DriveAway</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');
        body { font-family: 'Inter', sans-serif; background-color: #FAFAFA; }
    </style>
</head>
<body class="min-h-screen p-6 relative">

<div class="flex gap-4 mb-8">
    <button onclick="history.back()" class="flex items-center justify-center gap-2 bg-white px-5 py-2.5 rounded-xl font-bold text-black shadow-sm border border-gray-200 hover:bg-gray-50 transition">
        <div class="bg-gray-500 rounded-full text-white p-0.5 flex items-center justify-center h-6 w-6">
            <i class="ph-bold ph-arrow-left text-sm"></i>
        </div>
        <span class="text-lg">Back</span>
    </button>
    <a href="admin-home.jsp" class="flex items-center justify-center gap-2 bg-[#1C2024] px-5 py-2.5 rounded-xl font-bold text-white shadow-sm hover:bg-black transition">
        <i class="ph-fill ph-house text-[#FFD43B] text-2xl"></i>
        <span class="text-lg">Home</span>
    </a>
</div>

<div class="flex justify-center items-center pb-12">
    <div class="w-full max-w-3xl bg-white border-[3px] border-[#0D6EFD] rounded-xl p-10 shadow-lg">

        <div class="flex flex-col items-center mb-10">
            <i class="ph-fill ph-receipt text-[80px] text-[#0D6EFD] mb-2"></i>
            <h2 class="text-[40px] font-bold text-[#0D6EFD] tracking-tight">Process Payment</h2>
        </div>

        <form action="payments" method="POST" class="space-y-6">

            <div>
                <label class="block text-gray-900 font-bold mb-2 text-xl">Invoice ID (Optional):</label>
                <input type="text" name="manualId" placeholder="e.g., INV-001" class="w-full border border-gray-200 rounded-xl px-4 py-4 focus:outline-none focus:border-[#0D6EFD] focus:ring-1 focus:ring-[#0D6EFD] text-lg">
            </div>

            <div>
                <label class="block text-gray-900 font-bold mb-2 text-xl">Booking Reference ID:</label>
                <input type="text" name="bookingId" required placeholder="e.g., BKG-XYZ" class="w-full border border-gray-200 rounded-xl px-4 py-4 focus:outline-none focus:border-[#0D6EFD] focus:ring-1 focus:ring-[#0D6EFD] text-lg">
            </div>

            <div>
                <label class="block text-gray-900 font-bold mb-2 text-xl">Total Amount ($):</label>
                <input type="number" step="0.01" name="amount" required placeholder="0.00" class="w-full border border-gray-200 rounded-xl px-4 py-4 focus:outline-none focus:border-[#0D6EFD] focus:ring-1 focus:ring-[#0D6EFD] text-lg">
            </div>

            <div class="grid grid-cols-2 gap-6">
                <div>
                    <label class="block text-gray-900 font-bold mb-2 text-xl">Method:</label>
                    <div class="relative">
                        <select name="method" class="w-full border border-gray-200 rounded-xl px-4 py-4 appearance-none bg-white text-gray-700 text-lg focus:outline-none focus:border-[#0D6EFD] focus:ring-1 focus:ring-[#0D6EFD]">
                            <option value="Card">Credit/Debit Card</option>
                            <option value="Cash">Cash Transaction</option>
                        </select>
                        <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-gray-800">
                            <i class="ph-bold ph-caret-down text-xl"></i>
                        </div>
                    </div>
                </div>
                <div>
                    <label class="block text-gray-900 font-bold mb-2 text-xl">Status:</label>
                    <div class="relative">
                        <select name="status" class="w-full border border-gray-200 rounded-xl px-4 py-4 appearance-none bg-white text-gray-700 text-lg focus:outline-none focus:border-[#0D6EFD] focus:ring-1 focus:ring-[#0D6EFD]">
                            <option value="PAID">PAID</option>
                            <option value="PENDING">PENDING</option>
                            <option value="REFUNDED">REFUNDED</option>
                        </select>
                        <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-gray-800">
                            <i class="ph-bold ph-caret-down text-xl"></i>
                        </div>
                    </div>
                </div>
            </div>

            <button type="submit" name="action" value="create" class="w-full bg-[#0D6EFD] hover:bg-blue-700 text-white font-bold py-4 rounded-xl text-2xl flex justify-center items-center gap-3 mt-10 shadow-md transition-all">
                <i class="ph-bold ph-credit-card text-3xl"></i> Save Transaction
            </button>

            <hr class="my-10 border-gray-200">

            <div class="flex justify-center flex-wrap gap-4">
                <button type="button" onclick="handleSearch()" class="flex items-center gap-2 px-6 py-2.5 border-2 border-[#00B4D8] text-[#00B4D8] font-bold rounded-lg hover:bg-cyan-50 transition-colors text-lg">
                    <i class="ph-bold ph-magnifying-glass text-xl"></i> Search
                </button>
                <button type="button" onclick="handleEdit()" class="flex items-center gap-2 px-6 py-2.5 border-2 border-[#FFC107] text-gray-800 font-bold rounded-lg hover:bg-yellow-50 transition-colors text-lg">
                    <i class="ph-bold ph-pencil-simple text-xl"></i> Edit
                </button>
                <button type="button" onclick="handleDelete()" class="flex items-center gap-2 px-6 py-2.5 border-2 border-[#DC3545] text-[#DC3545] font-bold rounded-lg hover:bg-red-50 transition-colors text-lg">
                    <i class="ph-bold ph-trash text-xl"></i> Delete
                </button>
                <a href="payments?action=list" class="flex items-center gap-2 px-6 py-2.5 border-2 border-[#198754] text-[#198754] font-bold rounded-lg hover:bg-green-50 transition-colors text-lg">
                    <i class="ph-bold ph-list-dashes text-xl"></i> Billing Ledger
                </a>
            </div>
        </form>
    </div>
</div>

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