<%@ page contentType="text/html; charset=UTF-8" %>
<%
    // Security: Only logged-in customers can see this page!
    if (session.getAttribute("customerLoggedIn") == null || !(Boolean)session.getAttribute("customerLoggedIn")) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My DriveAway Portal</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <style>@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&display=swap'); body { font-family: 'Inter', sans-serif; }</style>
</head>
<body class="bg-gray-50 min-h-screen">

<!-- Customer Top Navigation -->
<nav class="bg-white shadow-sm h-20 flex items-center justify-between px-10 border-b border-gray-200">
    <div class="flex items-center gap-3 text-blue-600">
        <i class="ph-fill ph-car-profile text-3xl"></i>
        <span class="text-xl font-bold tracking-wide text-gray-900">DriveAway</span>
    </div>
    <div class="flex items-center gap-4">
        <div class="flex items-center gap-2 text-gray-600 font-medium">
            <i class="ph-fill ph-user-circle text-2xl"></i>
            <!-- FIXED: Changed to customerUser to match the AuthController -->
            <span>Hello, <%= session.getAttribute("customerUser") %></span>
        </div>
        <!-- FIXED: Removed the dangerous scriptlet and pointed to a dedicated logout page -->
        <a href="logout.jsp" class="border border-red-500 text-red-600 hover:bg-red-50 px-4 py-2 rounded-lg font-bold transition">Logout</a>
    </div>
</nav>

<!-- Customer Content Area -->
<main class="max-w-5xl mx-auto mt-12 px-6 text-center">
    <div class="bg-white rounded-2xl shadow-sm border border-gray-200 p-12 mt-10">
        <i class="ph-fill ph-party-hat text-[80px] text-yellow-500 mb-4 inline-block"></i>
        <h1 class="text-4xl font-extrabold text-gray-900 mb-4 tracking-tight">Welcome to Your Dashboard!</h1>
        <p class="text-xl text-gray-500 max-w-2xl mx-auto mb-8">You have successfully created an account and logged into the system. As a customer, you will be able to view your reservations and browse the vehicle fleet here.</p>

        <button class="bg-blue-600 text-white font-bold px-8 py-4 rounded-xl text-xl hover:bg-blue-700 transition shadow-md inline-flex items-center gap-2">
            <i class="ph-bold ph-car"></i> Browse Available Cars
        </button>
    </div>
</main>

</body>
</html>