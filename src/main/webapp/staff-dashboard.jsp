<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Staff" %>
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
    <title>Staff Directory - DriveAway Pro</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');
        body { font-family: 'Inter', sans-serif; background-color: #F8F9FA; }
        .sidebar { background-color: #0F172A; }
    </style>
</head>
<body class="flex h-screen overflow-hidden text-gray-800">

<!-- PROFESSIONAL SIDEBAR -->
<aside class="sidebar w-64 text-white flex flex-col shrink-0 z-20 shadow-xl">
    <div class="h-24 flex items-center px-8">
        <i class="ph-bold ph-car-profile text-3xl text-[#0D6EFD] mr-3"></i>
        <h1 class="text-xl font-bold tracking-wide">DriveAway Pro</h1>
    </div>
    <nav class="flex-1 px-5 py-2 space-y-2 overflow-y-auto">
        <a href="customers?action=list" class="flex items-center px-4 py-3.5 text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl font-medium transition-all">
            <i class="ph-bold ph-users text-xl mr-3"></i> Customers
        </a>
        <a href="vehicles?action=list" class="flex items-center px-4 py-3.5 text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl font-medium transition-all">
            <i class="ph-bold ph-car text-xl mr-3"></i> Vehicle Fleet
        </a>
        <a href="bookings?action=list" class="flex items-center px-4 py-3.5 text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl font-medium transition-all">
            <i class="ph-bold ph-calendar-check text-xl mr-3"></i> Bookings
        </a>
        <a href="payments?action=list" class="flex items-center px-4 py-3.5 text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl font-medium transition-all">
            <i class="ph-bold ph-credit-card text-xl mr-3"></i> Payments
        </a>
        <!-- Active Tab for Student 5 (Red Accent) -->
        <a href="staff?action=list" class="flex items-center px-4 py-3.5 bg-[#DC3545] text-white rounded-xl font-bold shadow-md transition-all">
            <i class="ph-bold ph-identification-badge text-xl mr-3"></i> Staff & Admins
        </a>
        <a href="#" class="flex items-center px-4 py-3.5 text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl font-medium transition-all">
            <i class="ph-bold ph-star text-xl mr-3"></i> Reviews
        </a>
    </nav>
    <div class="p-5 mb-2 space-y-2">
        <a href="admin-home.jsp" class="flex items-center px-4 py-3 text-sm text-slate-400 hover:text-white font-medium transition-all rounded-xl hover:bg-slate-800">
            <i class="ph-bold ph-arrow-square-out text-lg mr-3"></i> Admin Hub
        </a>
        <a href="index.jsp" class="flex items-center px-4 py-3 text-sm text-slate-400 hover:text-white font-medium transition-all rounded-xl hover:bg-slate-800">
            <i class="ph-bold ph-sign-out text-lg mr-3"></i> Logout
        </a>
    </div>
</aside>

<!-- MAIN CONTENT -->
<main class="flex-1 flex flex-col h-screen overflow-hidden bg-[#F8F9FA]">
    <header class="h-24 bg-white flex items-center justify-between px-10 z-10 border-b border-gray-100">
        <h2 class="text-2xl font-medium text-gray-800">Employee Management Portal</h2>
    </header>

    <div class="flex-1 overflow-y-auto p-10">
        <div class="flex justify-between items-end mb-8">
            <div>
                <h3 class="text-3xl font-extrabold text-gray-900 mb-2 tracking-tight">Staff Directory</h3>
                <p class="text-gray-500 text-sm font-medium">Manage corporate users, admins, and support staff.</p>
            </div>
            <a href="add-staff.jsp" class="flex items-center bg-[#DC3545] hover:bg-red-700 text-white px-6 py-3.5 rounded-xl font-bold shadow-md transition-all">
                <i class="ph-bold ph-user-plus mr-2 text-xl"></i> Add Employee
            </a>
        </div>

        <!-- Seamless Data Table Card -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
            <table class="w-full text-left border-collapse">
                <thead>
                <tr class="border-b border-gray-100">
                    <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Employee ID</th>
                    <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Full Name</th>
                    <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Email Address</th>
                    <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Phone</th>
                    <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">System Role</th>
                    <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest text-right">Actions</th>
                </tr>
                </thead>
                <tbody>
                <%
                    List<Staff> staffList = (List<Staff>) request.getAttribute("staffList");
                    if (staffList != null && !staffList.isEmpty()) {
                        for (Staff s : staffList) {
                %>
                <tr class="hover:bg-red-50/40 transition-colors group border-b border-gray-50 last:border-none">
                    <td class="py-5 px-8 whitespace-nowrap text-sm font-semibold text-gray-500">#<%= s.getStaffId() %></td>
                    <td class="py-5 px-8 whitespace-nowrap text-sm font-bold text-gray-900"><%= s.getName() %></td>
                    <td class="py-5 px-8 whitespace-nowrap text-sm text-gray-600"><%= s.getEmail() %></td>
                    <td class="py-5 px-8 whitespace-nowrap text-sm text-gray-600"><%= s.getPhone() %></td>
                    <td class="py-5 px-8 whitespace-nowrap text-sm font-bold">
                        <% if(s.getRole().equals("Admin")) { %>
                        <span class="text-white bg-[#DC3545] px-3 py-1 rounded-lg text-xs tracking-wide">ADMIN</span>
                        <% } else { %>
                        <span class="text-gray-700 bg-gray-200 px-3 py-1 rounded-lg text-xs tracking-wide">SUPPORT</span>
                        <% } %>
                    </td>
                    <td class="py-5 px-8 whitespace-nowrap text-right text-sm font-medium">
                        <div class="flex items-center justify-end gap-3 opacity-0 group-hover:opacity-100 transition-opacity">
                            <a href="staff?action=edit&id=<%= s.getStaffId() %>" class="text-blue-600 hover:text-blue-800 bg-blue-50 p-2.5 rounded-lg transition-colors"><i class="ph-bold ph-pencil-simple text-lg"></i></a>
                            <a href="staff?action=delete&id=<%= s.getStaffId() %>" class="text-red-500 hover:text-red-700 bg-red-50 p-2.5 rounded-lg transition-colors" onclick="return confirm('Terminate this employee?');"><i class="ph-bold ph-trash text-lg"></i></a>
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
                            <i class="ph ph-identification-badge text-5xl text-gray-300 mb-4"></i>
                            <p class="text-xl font-bold text-gray-900 mb-2 tracking-tight">No Employees Found</p>
                            <p class="text-sm text-gray-500 font-medium">Add a staff member to see them listed here.</p>
                        </div>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</main>
</body>
</html>