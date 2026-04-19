<%@ page contentType="text/html; charset=UTF-8" %>
<%
    if (session.getAttribute("adminLoggedIn") == null || !(Boolean)session.getAttribute("adminLoggedIn")) {
        response.sendRedirect("login.jsp"); return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Staff Management</title>

    <!-- Tailwind -->
    <script src="https://cdn.tailwindcss.com"></script>

    <!-- Icons -->
    <script src="https://unpkg.com/@phosphor-icons/web"></script>

    <style>
        body { font-family: 'Inter', sans-serif; background-color: #f5f5f5; }
    </style>
</head>

<body class="p-6">

<div class="max-w-6xl mx-auto">

    <!-- 🔵 TOP NAV -->
    <div class="flex justify-between items-center mb-6">
        <div class="flex gap-3">
            <button onclick="history.back()" class="bg-gray-100 px-4 py-2 rounded-lg font-semibold hover:bg-gray-200">
                ← Back
            </button>

            <a href="admin-home.jsp" class="bg-black text-white px-4 py-2 rounded-lg font-semibold">
                Dashboard Home
            </a>
        </div>
    </div>

    <!-- 🟦 STATS CARD -->
    <div class="bg-white rounded-xl shadow p-6 w-64 mb-6">
        <p class="text-gray-500 font-semibold">TOTAL STAFF</p>
        <h1 class="text-4xl font-bold mt-2">
            <%= request.getAttribute("staffCount") != null ? request.getAttribute("staffCount") : 0 %>
        </h1>
    </div>

    <!-- 🟨 TITLE -->
    <div class="mb-6">
        <h2 class="text-3xl font-bold">Staff Directory</h2>
        <p class="text-gray-500">Manage and onboard employees in the system.</p>
    </div>

    <!-- 🟩 FORM CARD -->
    <div class="bg-white rounded-xl shadow p-6 mb-8">
        <form action="staff" method="POST" class="grid grid-cols-2 gap-4">

            <input type="text" name="manualId" placeholder="Employee ID (EMP-01)"
                   class="border border-gray-200 rounded-lg px-4 py-3 focus:ring-2 focus:ring-red-400 outline-none" required>

            <input type="text" name="name" placeholder="Full Name"
                   class="border border-gray-200 rounded-lg px-4 py-3 focus:ring-2 focus:ring-red-400 outline-none" required>

            <input type="email" name="email" placeholder="Email Address"
                   class="border border-gray-200 rounded-lg px-4 py-3 focus:ring-2 focus:ring-red-400 outline-none" required>

            <input type="text" name="phone" placeholder="Phone Number"
                   class="border border-gray-200 rounded-lg px-4 py-3 focus:ring-2 focus:ring-red-400 outline-none" required>

            <input type="text" name="username" placeholder="Username"
                   class="border border-gray-200 rounded-lg px-4 py-3 focus:ring-2 focus:ring-red-400 outline-none" required>

            <input type="password" name="password" placeholder="Password"
                   class="border border-gray-200 rounded-lg px-4 py-3 focus:ring-2 focus:ring-red-400 outline-none" required>

            <select name="role"
                    class="border border-gray-200 rounded-lg px-4 py-3 col-span-2 focus:ring-2 focus:ring-red-400 outline-none">
                <option value="Support">Support Staff</option>
                <option value="Admin">Admin</option>
            </select>

            <button type="submit" name="action" value="create"
                    class="col-span-2 bg-red-500 hover:bg-red-600 text-white font-semibold py-3 rounded-lg transition flex justify-center items-center gap-2">
                <i class="ph-bold ph-user-plus"></i> Add Employee
            </button>

        </form>
    </div>

    <!-- 🟥 TABLE -->
    <div class="bg-white rounded-xl shadow overflow-hidden">

        <div class="p-4 border-b">
            <h3 class="text-xl font-semibold">Employee List</h3>
        </div>

        <table class="w-full text-left">
            <thead class="bg-gray-100 text-gray-600">
            <tr>
                <th class="p-4">EMP ID</th>
                <th class="p-4">NAME</th>
                <th class="p-4">EMAIL</th>
                <th class="p-4">PHONE</th>
                <th class="p-4">ROLE</th>
                <th class="p-4">ACTIONS</th>
            </tr>
            </thead>

            <tbody>

            <%
                List staffList = (List) request.getAttribute("staffList");
                if (staffList != null && !staffList.isEmpty()) {
                    for (Object obj : staffList) {
                        // assuming Staff class getters
                        com.yourpackage.Staff s = (com.yourpackage.Staff) obj;
            %>

            <tr class="border-t">
                <td class="p-4"><%= s.getManualId() %></td>
                <td class="p-4"><%= s.getName() %></td>
                <td class="p-4"><%= s.getEmail() %></td>
                <td class="p-4"><%= s.getPhone() %></td>
                <td class="p-4"><%= s.getRole() %></td>
                <td class="p-4">
                    <a href="staff?action=edit&id=<%= s.getManualId() %>"
                       class="text-blue-500 font-semibold">Edit</a>

                    <a href="staff?action=delete&id=<%= s.getManualId() %>"
                       onclick="return confirm('Are you sure?')"
                       class="text-red-500 font-semibold ml-3">Delete</a>
                </td>
            </tr>

            <%
                }
            } else {
            %>

            <tr>
                <td colspan="6" class="text-center p-6 text-gray-500">
                    No staff found in the system.
                </td>
            </tr>

            <%
                }
            %>

            </tbody>
        </table>
    </div>

</div>

</body>
</html>