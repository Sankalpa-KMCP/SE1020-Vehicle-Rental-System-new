<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DriveAway | Login</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');
        body { font-family: 'Inter', sans-serif; background-color: #F8F9FA; }

        /* Added a smooth slide-down animation for the alerts */
        .animate-slide-down {
            animation: slideDown 0.4s ease-out forwards;
        }
        @keyframes slideDown {
            from { opacity: 0; transform: translateY(-15px); }
            to { opacity: 1; transform: translateY(0); }
        }
    </style>
</head>
<body class="min-h-screen flex items-center justify-center p-4">

<div class="bg-white w-full max-w-md rounded-[24px] shadow-lg border border-gray-100 p-8 sm:p-10 relative">

    <div class="flex justify-center items-center gap-3 mb-8 text-gray-900">
        <i class="ph-bold ph-car-profile text-4xl text-[#0D6EFD]"></i>
        <span class="text-3xl font-extrabold tracking-tight">DriveAway</span>
    </div>

    <div class="mb-8 text-center">
        <h2 class="text-3xl font-extrabold text-gray-900 mb-2 tracking-tight">Welcome Back</h2>
        <p class="text-gray-500 font-medium">Sign in to manage your bookings and rentals.</p>
    </div>

    <form action="auth" method="post" class="space-y-5">
        <input type="hidden" name="action" value="customer_login">

        <% if ("invalid".equals(request.getParameter("error"))) { %>
        <div class="bg-red-50 p-4 rounded-xl border border-red-200 flex items-start gap-3 animate-slide-down shadow-sm mb-6">
            <div class="bg-red-100 p-2 rounded-lg text-red-600 mt-0.5">
                <i class="ph-bold ph-warning-circle text-xl"></i>
            </div>
            <div>
                <p class="font-bold text-red-800">Login Failed</p>
                <p class="text-sm text-red-600 font-medium mt-0.5">Invalid username or password. Please try again.</p>
            </div>
        </div>
        <% } else if ("registered".equals(request.getParameter("success"))) { %>
        <div class="bg-green-50 p-4 rounded-xl border border-green-200 flex items-start gap-3 animate-slide-down shadow-sm mb-6">
            <div class="bg-green-100 p-2 rounded-lg text-green-600 mt-0.5">
                <i class="ph-bold ph-check-circle text-xl"></i>
            </div>
            <div>
                <p class="font-bold text-green-800">Account Created!</p>
                <p class="text-sm text-green-700 font-medium mt-0.5">Your registration was successful. You can now sign in.</p>
            </div>
        </div>
        <% } %>

        <div>
            <label class="block text-sm font-bold text-gray-700 mb-1.5">Username</label>
            <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                    <i class="ph-bold ph-user text-gray-400 text-xl"></i>
                </div>
                <input type="text" name="username" required placeholder="johndoe123"
                       class="w-full pl-12 pr-4 py-4 border border-gray-200 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] focus:border-[#0D6EFD] outline-none transition-all font-medium text-gray-800 text-lg">
            </div>
        </div>

        <div>
            <div class="flex justify-between items-center mb-1.5">
                <label class="block text-sm font-bold text-gray-700">Password</label>
                <a href="#" class="text-sm text-[#0D6EFD] hover:text-blue-800 font-bold hover:underline transition-colors">Forgot password?</a>
            </div>
            <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                    <i class="ph-bold ph-lock-key text-gray-400 text-xl"></i>
                </div>
                <input type="password" name="password" required placeholder="••••••••"
                       class="w-full pl-12 pr-4 py-4 border border-gray-200 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] focus:border-[#0D6EFD] outline-none transition-all font-medium text-gray-800 text-lg">
            </div>
        </div>

        <button type="submit" class="w-full bg-[#0D6EFD] text-white font-bold py-4 rounded-xl hover:bg-blue-700 transition-colors shadow-md flex justify-center items-center gap-2 mt-4 text-xl">
            Sign In <i class="ph-bold ph-arrow-right"></i>
        </button>
    </form>

    <div class="mt-8 pt-6 border-t border-gray-100 text-center">
        <p class="text-gray-600 font-medium text-lg">
            New to DriveAway?
            <a href="register.jsp" class="text-[#0D6EFD] font-bold hover:text-blue-800 hover:underline ml-1">Create an account</a>
        </p>
    </div>

    <div class="mt-8 text-center">
        <a href="admin-login.jsp" class="text-red-500 hover:text-red-700 transition-colors inline-flex items-center justify-center gap-1.5 text-sm font-bold tracking-wide bg-red-50 px-4 py-2 rounded-lg" title="Staff Portal">
            <i class="ph-bold ph-shield-check text-lg"></i> Staff Access
        </a>
    </div>
</div>

</body>
</html>