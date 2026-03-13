<%@ page contentType="text/html; charset=UTF-8" %>
<%
    if (session.getAttribute("adminLoggedIn") == null || !(Boolean)session.getAttribute("adminLoggedIn")) {
        response.sendRedirect("index.jsp"); return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register Staff</title>
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
    <!-- RED Accent Border for Staff Module -->
    <div class="w-full max-w-3xl bg-white border-[3px] border-[#DC3545] rounded-xl p-10 shadow-lg">

        <div class="flex flex-col items-center mb-10">
            <i class="ph-fill ph-identification-badge text-[80px] text-[#DC3545] mb-2"></i>
            <h2 class="text-[40px] font-bold text-[#DC3545] tracking-tight">Onboard New Staff</h2>
        </div>

        <form action="staff" method="POST" class="space-y-6">
            <div>
                <label class="block text-gray-900 font-bold mb-2 text-xl">Employee ID (e.g., EMP-01):</label>
                <input type="text" name="manualId" class="w-full border border-gray-200 rounded-xl px-4 py-4 focus:outline-none focus:border-[#DC3545] focus:ring-1 focus:ring-[#DC3545] text-lg" required>
            </div>
            <div>
                <label class="block text-gray-900 font-bold mb-2 text-xl">Full Name:</label>
                <input type="text" name="name" class="w-full border border-gray-200 rounded-xl px-4 py-4 focus:outline-none focus:border-[#DC3545] focus:ring-1 focus:ring-[#DC3545] text-lg" required>
            </div>
            <div>
                <label class="block text-gray-900 font-bold mb-2 text-xl">Email Address:</label>
                <input type="email" name="email" class="w-full border border-gray-200 rounded-xl px-4 py-4 focus:outline-none focus:border-[#DC3545] focus:ring-1 focus:ring-[#DC3545] text-lg" required>
            </div>
            <div>
                <label class="block text-gray-900 font-bold mb-2 text-xl">Phone Number:</label>
                <input type="text" name="phone" class="w-full border border-gray-200 rounded-xl px-4 py-4 focus:outline-none focus:border-[#DC3545] focus:ring-1 focus:ring-[#DC3545] text-lg" required>
            </div>
            <div>
                <label class="block text-gray-900 font-bold mb-2 text-xl">System Username:</label>
                <input type="text" name="username" class="w-full border border-gray-200 rounded-xl px-4 py-4 focus:outline-none focus:border-[#DC3545] focus:ring-1 focus:ring-[#DC3545] text-lg" required>
            </div>
            <div>
                <label class="block text-gray-900 font-bold mb-2 text-xl">Account Password:</label>
                <input type="password" name="password" class="w-full border border-gray-200 rounded-xl px-4 py-4 focus:outline-none focus:border-[#DC3545] focus:ring-1 focus:ring-[#DC3545] text-lg" required>
            </div>
            <div>
                <label class="block text-gray-900 font-bold mb-2 text-xl">System Role:</label>
                <div class="relative">
                    <select name="role" class="w-full border border-gray-200 rounded-xl px-4 py-4 appearance-none bg-white text-gray-700 text-lg focus:outline-none focus:border-[#DC3545] focus:ring-1 focus:ring-[#DC3545]">
                        <option value="Support">Support Staff (Limited Access)</option>
                        <option value="Admin">Admin (Full Access)</option>
                    </select>
                    <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-gray-800">
                        <i class="ph-bold ph-caret-down text-xl"></i>
                    </div>
                </div>
            </div>

            <button type="submit" name="action" value="create" class="w-full bg-[#DC3545] hover:bg-red-700 text-white font-bold py-4 rounded-xl text-2xl flex justify-center items-center gap-3 mt-10 shadow-md transition-all">
                <i class="ph-bold ph-user-circle-plus text-3xl"></i> Add Employee
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
                    <i class="ph-bold ph-user-minus text-xl"></i> Delete
                </button>
                <a href="staff?action=list" class="flex items-center gap-2 px-6 py-2.5 border-2 border-[#198754] text-[#198754] font-bold rounded-lg hover:bg-green-50 transition-colors text-lg">
                    <i class="ph-bold ph-list-dashes text-xl"></i> Staff Directory
                </a>
            </div>
        </form>
    </div>
</div>

<script>
    function handleSearch() { window.location.href = "staff?action=list"; }
    function handleEdit() {
        let id = prompt("Enter the Employee ID you want to Edit:");
        if (id) window.location.href = "staff?action=edit&id=" + id.trim();
    }
    function handleDelete() {
        let id = prompt("Enter the Employee ID you want to Delete:");
        if (id && confirm("Terminate this employee? This cannot be undone.")) {
            window.location.href = "staff?action=delete&id=" + id.trim();
        }
    }
</script>
</body>
</html>