<%@ page import="com.example.vehiclerentalsystem01.models.Staff" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    Staff s = (Staff) request.getAttribute("staff");
    if (s == null) { response.sendRedirect("staff?action=list"); return; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Employee</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <style>@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&display=swap'); body { font-family: 'Inter', sans-serif; }</style>
</head>
<body class="bg-[#FAFAFA] flex h-screen items-center justify-center p-4">
<div class="max-w-lg w-full bg-white p-10 rounded-2xl shadow-lg border-[3px] border-[#DC3545]">
    <div class="text-center mb-8">
        <i class="ph-fill ph-pencil-simple text-5xl text-[#DC3545] mb-2"></i>
        <h2 class="text-3xl font-bold text-gray-900 tracking-tight">Edit Employee Profile</h2>
    </div>

    <form action="staff" method="post" class="space-y-5">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="id" value="<%= s.getStaffId() %>">

        <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">Full Name:</label>
            <input type="text" name="name" value="<%= s.getName() %>" required class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#DC3545]">
        </div>
        <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">Email Address:</label>
            <input type="email" name="email" value="<%= s.getEmail() %>" required class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#DC3545]">
        </div>
        <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">Phone Number:</label>
            <input type="text" name="phone" value="<%= s.getPhone() %>" required class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#DC3545]">
        </div>
        <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">System Username:</label>
            <input type="text" name="username" value="<%= s.getUsername() %>" required class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#DC3545]">
        </div>
        <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">Account Password:</label>
            <input type="text" name="password" value="<%= s.getPassword() %>" required class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#DC3545]">
        </div>
        <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">System Role:</label>
            <select name="role" class="w-full border border-gray-300 rounded-lg p-3 outline-none">
                <option value="Admin" <%= s.getRole().equals("Admin") ? "selected" : "" %>>Admin (Full Access)</option>
                <option value="Support" <%= s.getRole().equals("Support") ? "selected" : "" %>>Support Staff (Limited Access)</option>
            </select>
        </div>
        <div class="pt-4 flex gap-3">
            <a href="staff?action=list" class="w-1/3 bg-gray-100 text-gray-700 font-bold py-3 px-4 rounded-lg hover:bg-gray-200 text-center transition">Cancel</a>
            <button type="submit" class="w-2/3 bg-[#DC3545] text-white font-bold py-3 px-4 rounded-lg hover:bg-red-700 transition">Update Profile</button>
        </div>
    </form>
</div>
</body>
</html>