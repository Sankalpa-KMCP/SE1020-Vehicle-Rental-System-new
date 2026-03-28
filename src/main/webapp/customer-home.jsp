<%@ page contentType="text/html; charset=UTF-8" %>
<%
    // Ensure the user is actually logged in as a customer
    Boolean customerLoggedIn = (Boolean) session.getAttribute("customerLoggedIn");
    if (customerLoggedIn == null || !customerLoggedIn) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Attempt to grab the full name, fallback to username, then to 'Customer'
    String displayName = (String) session.getAttribute("name");
    if (displayName == null) {
        displayName = (String) session.getAttribute("username");
        if (displayName == null) {
            displayName = "Customer";
        } else {
            displayName = "@" + displayName; // Add @ if falling back to username
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DriveAway | My Portal</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');
        body { font-family: 'Inter', sans-serif; background-color: #F4F7F6; }
        .fade-in { animation: fadeIn 0.4s ease-out; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
    </style>
</head>
<body class="min-h-screen text-gray-800 flex flex-col">

<nav class="bg-white border-b border-gray-200 sticky top-0 z-20 shadow-sm">
    <div class="max-w-screen-xl mx-auto px-8 h-20 flex items-center justify-between">
        <div class="flex items-center gap-2 text-gray-900">
            <i class="ph-bold ph-car-profile text-3xl text-blue-600"></i>
            <span class="text-2xl font-extrabold tracking-tight">DriveAway</span>
        </div>

        <div class="flex items-center gap-6">
            <div class="flex items-center gap-3">
                <div class="bg-blue-50 text-blue-600 p-2 rounded-full border border-blue-100">
                    <i class="ph-bold ph-user text-xl"></i>
                </div>
                <div class="hidden sm:block">
                    <p class="text-xs text-gray-500 font-semibold uppercase tracking-wider">Welcome Back</p>
                    <p class="font-bold text-gray-900 leading-tight"><%= displayName %></p>
                </div>
            </div>
            <div class="w-px h-8 bg-gray-200 hidden sm:block"></div>
            <form action="auth" method="post" class="m-0">
                <input type="hidden" name="action" value="logout">
                <button type="submit" class="text-gray-600 hover:text-red-700 font-bold border border-gray-200 bg-white hover:bg-red-50 hover:border-red-200 px-5 py-2.5 rounded-xl transition-all flex items-center gap-2 shadow-sm">
                    Logout <i class="ph-bold ph-sign-out text-lg"></i>
                </button>
            </form>
        </div>
    </div>
</nav>

<main class="flex-grow max-w-screen-xl mx-auto w-full px-8 py-10 fade-in">

    <div class="bg-gradient-to-br from-[#0D6EFD] to-[#0a58ca] rounded-3xl p-10 sm:p-12 text-white shadow-lg mb-10 relative overflow-hidden">
        <div class="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
            <div>
                <h1 class="text-4xl sm:text-5xl font-extrabold mb-4 tracking-tight">Your Personal Portal</h1>
                <p class="text-blue-100 text-lg font-medium max-w-2xl leading-relaxed">Everything you need to manage your DriveAway experience. Book new rides, track your rental history, and manage your account settings all in one place.</p>
            </div>

            <div class="flex gap-3 bg-white/10 p-4 rounded-2xl backdrop-blur-sm border border-white/20">
                <div class="text-center px-4 border-r border-white/20">
                    <p class="text-blue-100 text-xs font-bold uppercase tracking-wider mb-1">Status</p>
                    <p class="text-white font-extrabold flex items-center gap-1"><i class="ph-fill ph-shield-check text-green-400"></i> Active</p>
                </div>
                <div class="text-center px-4">
                    <p class="text-blue-100 text-xs font-bold uppercase tracking-wider mb-1">Member Since</p>
                    <p class="text-white font-extrabold">2026</p>
                </div>
            </div>
        </div>
        <i class="ph-fill ph-steering-wheel absolute -right-10 -bottom-20 text-[300px] text-white opacity-10 transform -rotate-12 pointer-events-none"></i>
        <div class="absolute top-0 left-0 w-full h-full bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10 mix-blend-overlay pointer-events-none"></div>
    </div>

    <div class="mb-6 flex items-center gap-3">
        <i class="ph-fill ph-squares-four text-2xl text-gray-400"></i>
        <h2 class="text-2xl font-bold text-gray-900">Dashboard Modules</h2>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">

        <a href="vehicle-dashboard.jsp" class="bg-white rounded-2xl p-8 border border-gray-100 shadow-sm hover:shadow-xl hover:-translate-y-1.5 transition-all duration-300 group flex flex-col h-full relative overflow-hidden">
            <div class="absolute top-0 left-0 w-full h-1.5 bg-blue-500 transform origin-left scale-x-0 group-hover:scale-x-100 transition-transform duration-300"></div>
            <div class="flex items-center gap-5 mb-5">
                <div class="w-16 h-16 rounded-2xl bg-blue-50 text-blue-600 flex items-center justify-center text-3xl group-hover:bg-blue-600 group-hover:text-white transition-colors shadow-inner">
                    <i class="ph-fill ph-car-profile"></i>
                </div>
                <h3 class="text-xl font-extrabold text-gray-900">Rent a Vehicle</h3>
            </div>
            <p class="text-gray-500 font-medium mb-8 flex-grow leading-relaxed">Browse our premium fleet, check real-time availability, pricing, and secure your booking instantly.</p>
            <div class="text-blue-600 font-bold flex items-center gap-2 group-hover:gap-3 transition-all">
                Explore Fleet <i class="ph-bold ph-arrow-right"></i>
            </div>
        </a>

        <a href="booking-dashboard.jsp" class="bg-white rounded-2xl p-8 border border-gray-100 shadow-sm hover:shadow-xl hover:-translate-y-1.5 transition-all duration-300 group flex flex-col h-full relative overflow-hidden">
            <div class="absolute top-0 left-0 w-full h-1.5 bg-green-500 transform origin-left scale-x-0 group-hover:scale-x-100 transition-transform duration-300"></div>
            <div class="flex items-center gap-5 mb-5">
                <div class="w-16 h-16 rounded-2xl bg-green-50 text-green-600 flex items-center justify-center text-3xl group-hover:bg-green-600 group-hover:text-white transition-colors shadow-inner">
                    <i class="ph-fill ph-calendar-check"></i>
                </div>
                <h3 class="text-xl font-extrabold text-gray-900">My Bookings</h3>
            </div>
            <p class="text-gray-500 font-medium mb-8 flex-grow leading-relaxed">View your active rentals, check past transaction history, modify dates, and manage cancellations.</p>
            <div class="text-green-600 font-bold flex items-center gap-2 group-hover:gap-3 transition-all">
                Manage Reservations <i class="ph-bold ph-arrow-right"></i>
            </div>
        </a>

        <a href="payment-dashboard.jsp" class="bg-white rounded-2xl p-8 border border-gray-100 shadow-sm hover:shadow-xl hover:-translate-y-1.5 transition-all duration-300 group flex flex-col h-full relative overflow-hidden">
            <div class="absolute top-0 left-0 w-full h-1.5 bg-teal-500 transform origin-left scale-x-0 group-hover:scale-x-100 transition-transform duration-300"></div>
            <div class="flex items-center gap-5 mb-5">
                <div class="w-16 h-16 rounded-2xl bg-teal-50 text-teal-600 flex items-center justify-center text-3xl group-hover:bg-teal-600 group-hover:text-white transition-colors shadow-inner">
                    <i class="ph-fill ph-receipt"></i>
                </div>
                <h3 class="text-xl font-extrabold text-gray-900">Billing & Payments</h3>
            </div>
            <p class="text-gray-500 font-medium mb-8 flex-grow leading-relaxed">View your invoices, settle pending balances, and manage your saved payment methods securely.</p>
            <div class="text-teal-600 font-bold flex items-center gap-2 group-hover:gap-3 transition-all">
                View Invoices <i class="ph-bold ph-arrow-right"></i>
            </div>
        </a>

        <a href="edit-customer.jsp" class="bg-white rounded-2xl p-8 border border-gray-100 shadow-sm hover:shadow-xl hover:-translate-y-1.5 transition-all duration-300 group flex flex-col h-full relative overflow-hidden">
            <div class="absolute top-0 left-0 w-full h-1.5 bg-purple-500 transform origin-left scale-x-0 group-hover:scale-x-100 transition-transform duration-300"></div>
            <div class="flex items-center gap-5 mb-5">
                <div class="w-16 h-16 rounded-2xl bg-purple-50 text-purple-600 flex items-center justify-center text-3xl group-hover:bg-purple-600 group-hover:text-white transition-colors shadow-inner">
                    <i class="ph-fill ph-identification-card"></i>
                </div>
                <h3 class="text-xl font-extrabold text-gray-900">Profile Settings</h3>
            </div>
            <p class="text-gray-500 font-medium mb-8 flex-grow leading-relaxed">Update your contact information, upload missing verification documents, or change your password.</p>
            <div class="text-purple-600 font-bold flex items-center gap-2 group-hover:gap-3 transition-all">
                Edit Profile <i class="ph-bold ph-arrow-right"></i>
            </div>
        </a>

        <a href="review-dashboard.jsp" class="bg-white rounded-2xl p-8 border border-gray-100 shadow-sm hover:shadow-xl hover:-translate-y-1.5 transition-all duration-300 group flex flex-col h-full relative overflow-hidden">
            <div class="absolute top-0 left-0 w-full h-1.5 bg-orange-500 transform origin-left scale-x-0 group-hover:scale-x-100 transition-transform duration-300"></div>
            <div class="flex items-center gap-5 mb-5">
                <div class="w-16 h-16 rounded-2xl bg-orange-50 text-orange-600 flex items-center justify-center text-3xl group-hover:bg-orange-600 group-hover:text-white transition-colors shadow-inner">
                    <i class="ph-fill ph-star"></i>
                </div>
                <h3 class="text-xl font-extrabold text-gray-900">My Reviews</h3>
            </div>
            <p class="text-gray-500 font-medium mb-8 flex-grow leading-relaxed">Rate your previous rentals, leave feedback on our services, and help us improve DriveAway.</p>
            <div class="text-orange-600 font-bold flex items-center gap-2 group-hover:gap-3 transition-all">
                Write a Review <i class="ph-bold ph-arrow-right"></i>
            </div>
        </a>

        <a href="#" class="bg-white rounded-2xl p-8 border border-gray-100 shadow-sm hover:shadow-xl hover:-translate-y-1.5 transition-all duration-300 group flex flex-col h-full relative overflow-hidden">
            <div class="absolute top-0 left-0 w-full h-1.5 bg-rose-500 transform origin-left scale-x-0 group-hover:scale-x-100 transition-transform duration-300"></div>
            <div class="flex items-center gap-5 mb-5">
                <div class="w-16 h-16 rounded-2xl bg-rose-50 text-rose-600 flex items-center justify-center text-3xl group-hover:bg-rose-600 group-hover:text-white transition-colors shadow-inner">
                    <i class="ph-fill ph-headset"></i>
                </div>
                <h3 class="text-xl font-extrabold text-gray-900">Help & Support</h3>
            </div>
            <p class="text-gray-500 font-medium mb-8 flex-grow leading-relaxed">Having trouble? Contact our 24/7 support team, read the FAQs, or report an issue with a vehicle.</p>
            <div class="text-rose-600 font-bold flex items-center gap-2 group-hover:gap-3 transition-all">
                Get Assistance <i class="ph-bold ph-arrow-right"></i>
            </div>
        </a>

    </div>

</main>

</body>
</html>