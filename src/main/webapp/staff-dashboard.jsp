<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Staff" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    if (session.getAttribute("adminLoggedIn") == null || !(Boolean)session.getAttribute("adminLoggedIn")) {
        response.sendRedirect("login.jsp"); return;
    }

    List<Staff> staffList = (List<Staff>) request.getAttribute("staffList");
    int totalStaff = staffList != null ? staffList.size() : 0;
    int adminCount = 0;
    int supportCount = 0;

    if (staffList != null) {
        for (Staff s : staffList) {
            if ("Admin".equals(s.getRole())) {
                adminCount++;
            } else {
                supportCount++;
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff Directory - DriveAway</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; background: radial-gradient(circle at top right, #fef2f2 0%, #f8fafc 45%, #f1f5f9 100%); }
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
    <div class="bg-gradient-to-br from-rose-500 via-red-500 to-orange-500 rounded-3xl p-10 sm:p-12 text-white shadow-xl mb-10 relative overflow-hidden">
        <div class="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
            <div>
                <h1 class="text-4xl sm:text-5xl font-extrabold mb-4 tracking-tight">Staff Directory</h1>
                <p class="text-red-100 text-lg font-medium max-w-2xl leading-relaxed">Manage corporate users, admins, and support staff in one place.</p>
            </div>
            <div class="flex gap-3 bg-white/10 p-4 rounded-2xl backdrop-blur-sm border border-white/20">
                <div class="text-center px-4 border-r border-white/20">
                    <p class="text-red-100 text-xs font-bold uppercase tracking-wider mb-1">Total</p>
                    <p class="text-white font-extrabold"><%= totalStaff %></p>
                </div>
                <div class="text-center px-4 border-r border-white/20">
                    <p class="text-red-100 text-xs font-bold uppercase tracking-wider mb-1">Admins</p>
                    <p class="text-white font-extrabold"><%= adminCount %></p>
                </div>
                <div class="text-center px-4">
                    <p class="text-red-100 text-xs font-bold uppercase tracking-wider mb-1">Support</p>
                    <p class="text-white font-extrabold"><%= supportCount %></p>
                </div>
            </div>
        </div>
        <i class="ph-fill ph-identification-badge absolute -right-10 -bottom-20 text-[300px] text-white opacity-10 transform -rotate-12 pointer-events-none"></i>
        <div class="absolute top-0 left-0 w-full h-full bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10 mix-blend-overlay pointer-events-none"></div>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6 mb-10">
        <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Total Staff</p>
            <p class="text-3xl font-extrabold text-gray-900"><%= totalStaff %></p>
            <p class="text-sm text-gray-500 mt-2">All employees in system.</p>
        </div>
        <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Administrators</p>
            <p class="text-3xl font-extrabold text-red-600"><%= adminCount %></p>
            <p class="text-sm text-gray-500 mt-2">Full system access users.</p>
        </div>
        <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Support Staff</p>
            <p class="text-3xl font-extrabold text-orange-500"><%= supportCount %></p>
            <p class="text-sm text-gray-500 mt-2">Customer service team.</p>
        </div>
        <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Departments</p>
            <p class="text-3xl font-extrabold text-blue-600">2</p>
            <p class="text-sm text-gray-500 mt-2">Active organizational units.</p>
        </div>
    </div>

    <div class="mb-6 flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4">
        <div>
            <h2 class="text-2xl font-bold text-gray-900">Employee Management</h2>
            <p class="text-gray-500 mt-1">Manage all corporate users, roles, and permissions.</p>
        </div>
        <a href="add-staff.jsp" class="bg-[#DC3545] hover:bg-red-700 text-white font-bold px-5 py-3 rounded-xl shadow-sm transition-all flex items-center gap-2 justify-center">
            <i class="ph-bold ph-user-plus"></i> Add Employee
        </a>
    </div>

    <div class="bg-white rounded-2xl soft-card border border-slate-200 overflow-hidden">
        <table class="w-full text-left border-collapse">
            <thead>
            <tr class="border-b border-slate-100 bg-slate-50/80">
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Employee ID</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Full Name</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Email Address</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Phone</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">System Role</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest text-right">Actions</th>
            </tr>
            </thead>
            <tbody>
            <%
                if (staffList != null && !staffList.isEmpty()) {
                    for (Staff s : staffList) {
            %>
            <tr class="hover:bg-red-50/40 transition-colors border-b border-gray-50 last:border-none group">
                <td class="py-5 px-8 whitespace-nowrap text-sm font-mono text-gray-500">#<%= s.getStaffId() %></td>
                <td class="py-5 px-8 whitespace-nowrap text-sm font-bold text-gray-900"><%= s.getName() %></td>
                <td class="py-5 px-8 whitespace-nowrap text-sm text-gray-600"><%= s.getEmail() %></td>
                <td class="py-5 px-8 whitespace-nowrap text-sm text-gray-600"><%= s.getPhone() %></td>
                <td class="py-5 px-8 whitespace-nowrap text-sm">
                    <% if("Admin".equals(s.getRole())) { %>
                    <span class="bg-red-50 text-red-700 border border-red-100 px-3 py-1.5 rounded-lg text-xs font-bold">ADMIN</span>
                    <% } else { %>
                    <span class="bg-gray-50 text-gray-700 border border-gray-200 px-3 py-1.5 rounded-lg text-xs font-bold">SUPPORT</span>
                    <% } %>
                </td>
                <td class="py-5 px-8 whitespace-nowrap text-right text-sm font-medium">
                    <div class="flex items-center justify-end gap-2">
                        <a href="staff?action=edit&id=<%= s.getStaffId() %>" class="text-blue-700 hover:text-blue-900 bg-blue-50 hover:bg-blue-100 px-3 py-2 rounded-lg transition-colors flex items-center gap-1 font-semibold">
                            <i class="ph-bold ph-pencil-simple"></i> Edit
                        </a>
                        <a href="staff?action=delete&id=<%= s.getStaffId() %>" class="text-red-600 hover:text-red-800 bg-red-50 hover:bg-red-100 px-3 py-2 rounded-lg transition-colors flex items-center gap-1 font-semibold" onclick="return confirm('Terminate this employee?');">
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
                        <i class="ph-fill ph-identification-badge text-6xl text-gray-300 mb-4"></i>
                        <p class="text-2xl font-bold text-gray-900 mb-2 tracking-tight">No Employees Found</p>
                        <p class="text-sm text-gray-500 font-medium max-w-md">Add a staff member to see them listed here.</p>
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