<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.vehiclerentalsystem01.dao.BookingDAO" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>
<%
    // Ensure the user is actually logged in as a customer
    Boolean customerLoggedIn = (Boolean) session.getAttribute("customerLoggedIn");
    if (customerLoggedIn == null || !customerLoggedIn) {
        response.sendRedirect("login.jsp");
        return;
    }

    String sessionUsername = (String) session.getAttribute("username");

    // Attempt to grab the full name, fallback to username, then to 'Customer'
    String displayName = (String) session.getAttribute("name");
    if (displayName == null) {
        displayName = sessionUsername;
        if (displayName == null) {
            displayName = "Customer";
        } else {
            displayName = "@" + displayName; // Add @ if falling back to username
        }
    }

    List<String> bookingNotices = new ArrayList<>();
    BookingDAO bookingDAO = new BookingDAO();
    for (Booking booking : bookingDAO.getAllBookings()) {
        String owner = booking.getCustomerName();
        boolean isOwner = owner != null && sessionUsername != null && owner.trim().equalsIgnoreCase(sessionUsername.trim());
        boolean hasNotice = booking.getStaffMessage() != null && !booking.getStaffMessage().trim().isEmpty();
        boolean decisionStatus = "CONFIRMED".equalsIgnoreCase(booking.getStatus()) || "REJECTED".equalsIgnoreCase(booking.getStatus());
        if (isOwner && hasNotice && decisionStatus) {
            bookingNotices.add("Booking " + booking.getId() + " - " + booking.getStatus() + ": " + booking.getStaffMessage());
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
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; background: radial-gradient(circle at top left, #f0f9ff 0%, #e0f2fe 25%, #f8fafc 50%, #f1f5f9 100%); }
        .fade-in { animation: fadeIn 0.5s ease-out; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
        .soft-card { box-shadow: 0 10px 40px rgba(15, 23, 42, 0.08); }
        .glass-effect { backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px); }
        .hero-pattern { background-image: url('https://www.transparenttextures.com/patterns/cubes.png'); }
        .card-hover { transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1); }
        .card-hover:hover { transform: translateY(-8px) scale(1.02); box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15); }
        .icon-bounce { transition: transform 0.3s ease; }
        .card-hover:hover .icon-bounce { transform: scale(1.1) rotate(5deg); }
    </style>
</head>
<body class="min-h-screen text-gray-800 flex flex-col">

<nav class="bg-white/80 glass-effect border-b border-white/20 sticky top-0 z-30 shadow-lg">
    <div class="max-w-screen-2xl mx-auto px-6 lg:px-8 h-20 flex items-center justify-between">
        <a href="customer-home.jsp" class="flex items-center gap-3 group">
            <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg group-hover:shadow-blue-500/30 transition-all">
                <i class="ph-fill ph-car-profile text-2xl text-white"></i>
            </div>
            <span class="text-2xl font-extrabold tracking-tight bg-gradient-to-r from-gray-900 to-gray-700 bg-clip-text text-transparent">DriveAway</span>
        </a>

        <div class="flex items-center gap-4">
            <div class="hidden md:flex items-center gap-4 bg-gradient-to-r from-blue-50 to-indigo-50 px-5 py-2.5 rounded-2xl border border-blue-100">
                <div class="w-10 h-10 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-full flex items-center justify-center shadow-md">
                    <i class="ph-bold ph-user text-lg text-white"></i>
                </div>
                <div>
                    <p class="text-xs text-blue-600 font-bold uppercase tracking-wider">Welcome</p>
                    <p class="font-bold text-gray-900 text-sm leading-tight truncate max-w-[150px]"><%= displayName %></p>
                </div>
            </div>
            <form action="auth" method="post" class="m-0">
                <input type="hidden" name="action" value="logout">
                <button type="submit" class="bg-gradient-to-r from-rose-500 to-pink-600 hover:from-rose-600 hover:to-pink-700 text-white font-bold px-5 py-2.5 rounded-xl shadow-lg shadow-rose-500/30 hover:shadow-rose-500/50 transition-all flex items-center gap-2">
                    <i class="ph-bold ph-sign-out text-lg"></i>
                    <span class="hidden sm:inline">Logout</span>
                </button>
            </form>
        </div>
    </div>
</nav>

<main class="flex-grow max-w-screen-2xl mx-auto w-full px-6 lg:px-8 py-12 fade-in">

    <div class="bg-gradient-to-br from-blue-600 via-indigo-600 to-purple-700 rounded-3xl p-8 sm:p-12 lg:p-16 text-white shadow-2xl mb-12 relative overflow-hidden hero-pattern">
        <div class="absolute inset-0 bg-gradient-to-r from-blue-600/20 to-purple-600/20"></div>
        <div class="relative z-10 flex flex-col lg:flex-row justify-between items-start lg:items-center gap-8">
            <div class="flex-1">
                <div class="flex items-center gap-3 mb-4">
                    <div class="w-14 h-14 bg-white/20 rounded-2xl flex items-center justify-center backdrop-blur-sm border border-white/30">
                        <i class="ph-fill ph-house text-3xl text-white"></i>
                    </div>
                    <h1 class="text-4xl sm:text-5xl lg:text-6xl font-extrabold tracking-tight">Welcome Back</h1>
                </div>
                <p class="text-blue-100 text-lg sm:text-xl font-medium max-w-2xl leading-relaxed mb-6">Your premium vehicle rental dashboard. Book rides, manage reservations, and explore our fleet with ease.</p>
                <div class="flex flex-wrap gap-3">
                    <a href="browse-cars.jsp" class="inline-flex items-center gap-2 bg-white text-indigo-600 font-bold px-6 py-3 rounded-xl shadow-lg hover:shadow-xl hover:bg-gray-50 transition-all">
                        <i class="ph-bold ph-car"></i> Browse Fleet
                    </a>
                    <a href="my-bookings.jsp" class="inline-flex items-center gap-2 bg-white/10 backdrop-blur-sm text-white font-bold px-6 py-3 rounded-xl border border-white/30 hover:bg-white/20 transition-all">
                        <i class="ph-bold ph-calendar-check"></i> My Bookings
                    </a>
                </div>
            </div>

            <div class="flex gap-4 bg-white/10 p-6 rounded-2xl backdrop-blur-sm border border-white/20">
                <div class="text-center px-6 border-r border-white/20">
                    <div class="w-12 h-12 bg-green-400/20 rounded-xl flex items-center justify-center mx-auto mb-2">
                        <i class="ph-fill ph-shield-check text-2xl text-green-300"></i>
                    </div>
                    <p class="text-blue-100 text-xs font-bold uppercase tracking-wider mb-1">Status</p>
                    <p class="text-white font-extrabold">Active</p>
                </div>
                <div class="text-center px-6">
                    <div class="w-12 h-12 bg-amber-400/20 rounded-xl flex items-center justify-center mx-auto mb-2">
                        <i class="ph-fill ph-crown text-2xl text-amber-300"></i>
                    </div>
                    <p class="text-blue-100 text-xs font-bold uppercase tracking-wider mb-1">Member</p>
                    <p class="text-white font-extrabold">Premium</p>
                </div>
            </div>
        </div>
        <i class="ph-fill ph-car absolute -right-16 -bottom-16 text-[400px] text-white opacity-5 transform rotate-12 pointer-events-none"></i>
    </div>

    <% if (!bookingNotices.isEmpty()) { %>
    <div class="mb-10 bg-gradient-to-r from-amber-50 to-orange-50 border border-amber-200 rounded-2xl p-6 shadow-sm">
        <div class="flex items-center gap-3 mb-4">
            <div class="w-10 h-10 bg-amber-500 rounded-xl flex items-center justify-center shadow-md">
                <i class="ph-fill ph-bell text-xl text-white"></i>
            </div>
            <h3 class="text-lg font-bold text-amber-800">Booking Notifications</h3>
        </div>
        <div class="space-y-3">
            <%
                int start = Math.max(0, bookingNotices.size() - 3);
                for (int i = bookingNotices.size() - 1; i >= start; i--) {
            %>
            <div class="bg-white border border-amber-200 text-amber-900 rounded-xl px-5 py-3 text-sm font-medium shadow-sm flex items-start gap-3">
                <i class="ph-fill ph-info text-amber-500 mt-0.5"></i>
                <%= bookingNotices.get(i) %>
            </div>
            <% } %>
        </div>
    </div>
    <% } %>

    <div class="mb-8">
        <h2 class="text-3xl font-extrabold text-gray-900 mb-2">Quick Actions</h2>
        <p class="text-gray-500 font-medium">Access all your DriveAway services from one place</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">

        <a href="browse-cars.jsp" class="bg-white rounded-3xl p-8 border border-gray-100 soft-card card-hover group flex flex-col h-full relative overflow-hidden">
            <div class="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-blue-100 to-indigo-100 rounded-full -mr-16 -mt-16 opacity-50 group-hover:opacity-70 transition-opacity"></div>
            <div class="relative z-10">
                <div class="flex items-center gap-4 mb-5">
                    <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-blue-500 to-indigo-600 text-white flex items-center justify-center text-3xl shadow-lg icon-bounce">
                        <i class="ph-fill ph-car-profile"></i>
                    </div>
                    <h3 class="text-xl font-extrabold text-gray-900">Rent a Vehicle</h3>
                </div>
                <p class="text-gray-500 font-medium mb-6 flex-grow leading-relaxed">Browse our premium fleet, check real-time availability, pricing, and secure your booking instantly.</p>
                <div class="inline-flex items-center gap-2 bg-blue-50 text-blue-600 font-bold px-4 py-2 rounded-xl group-hover:bg-blue-600 group-hover:text-white transition-all">
                    Explore Fleet <i class="ph-bold ph-arrow-right"></i>
                </div>
            </div>
        </a>

        <a href="my-bookings.jsp" class="bg-white rounded-3xl p-8 border border-gray-100 soft-card card-hover group flex flex-col h-full relative overflow-hidden">
            <div class="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-green-100 to-emerald-100 rounded-full -mr-16 -mt-16 opacity-50 group-hover:opacity-70 transition-opacity"></div>
            <div class="relative z-10">
                <div class="flex items-center gap-4 mb-5">
                    <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-green-500 to-emerald-600 text-white flex items-center justify-center text-3xl shadow-lg icon-bounce">
                        <i class="ph-fill ph-calendar-check"></i>
                    </div>
                    <h3 class="text-xl font-extrabold text-gray-900">My Bookings</h3>
                </div>
                <p class="text-gray-500 font-medium mb-6 flex-grow leading-relaxed">View your active rentals, check past transaction history, modify dates, and manage cancellations.</p>
                <div class="inline-flex items-center gap-2 bg-green-50 text-green-600 font-bold px-4 py-2 rounded-xl group-hover:bg-green-600 group-hover:text-white transition-all">
                    Manage Reservations <i class="ph-bold ph-arrow-right"></i>
                </div>
            </div>
        </a>

        <a href="customer-billing.jsp" class="bg-white rounded-3xl p-8 border border-gray-100 soft-card card-hover group flex flex-col h-full relative overflow-hidden">
            <div class="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-teal-100 to-cyan-100 rounded-full -mr-16 -mt-16 opacity-50 group-hover:opacity-70 transition-opacity"></div>
            <div class="relative z-10">
                <div class="flex items-center gap-4 mb-5">
                    <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-teal-500 to-cyan-600 text-white flex items-center justify-center text-3xl shadow-lg icon-bounce">
                        <i class="ph-fill ph-receipt"></i>
                    </div>
                    <h3 class="text-xl font-extrabold text-gray-900">Billing & Payments</h3>
                </div>
                <p class="text-gray-500 font-medium mb-6 flex-grow leading-relaxed">View your invoices, settle pending balances, and manage your saved payment methods securely.</p>
                <div class="inline-flex items-center gap-2 bg-teal-50 text-teal-600 font-bold px-4 py-2 rounded-xl group-hover:bg-teal-600 group-hover:text-white transition-all">
                    View Invoices <i class="ph-bold ph-arrow-right"></i>
                </div>
            </div>
        </a>

        <a href="edit-customer.jsp" class="bg-white rounded-3xl p-8 border border-gray-100 soft-card card-hover group flex flex-col h-full relative overflow-hidden">
            <div class="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-purple-100 to-violet-100 rounded-full -mr-16 -mt-16 opacity-50 group-hover:opacity-70 transition-opacity"></div>
            <div class="relative z-10">
                <div class="flex items-center gap-4 mb-5">
                    <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-purple-500 to-violet-600 text-white flex items-center justify-center text-3xl shadow-lg icon-bounce">
                        <i class="ph-fill ph-identification-card"></i>
                    </div>
                    <h3 class="text-xl font-extrabold text-gray-900">Profile Settings</h3>
                </div>
                <p class="text-gray-500 font-medium mb-6 flex-grow leading-relaxed">Update your contact information, upload missing verification documents, or change your password.</p>
                <div class="inline-flex items-center gap-2 bg-purple-50 text-purple-600 font-bold px-4 py-2 rounded-xl group-hover:bg-purple-600 group-hover:text-white transition-all">
                    Edit Profile <i class="ph-bold ph-arrow-right"></i>
                </div>
            </div>
        </a>

        <a href="customer-add-review.jsp" class="bg-white rounded-3xl p-8 border border-gray-100 soft-card card-hover group flex flex-col h-full relative overflow-hidden">
            <div class="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-orange-100 to-amber-100 rounded-full -mr-16 -mt-16 opacity-50 group-hover:opacity-70 transition-opacity"></div>
            <div class="relative z-10">
                <div class="flex items-center gap-4 mb-5">
                    <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-orange-500 to-amber-600 text-white flex items-center justify-center text-3xl shadow-lg icon-bounce">
                        <i class="ph-fill ph-star"></i>
                    </div>
                    <h3 class="text-xl font-extrabold text-gray-900">My Reviews</h3>
                </div>
                <p class="text-gray-500 font-medium mb-6 flex-grow leading-relaxed">Rate your previous rentals, leave feedback on our services, and help us improve DriveAway.</p>
                <div class="inline-flex items-center gap-2 bg-orange-50 text-orange-600 font-bold px-4 py-2 rounded-xl group-hover:bg-orange-600 group-hover:text-white transition-all">
                    Write a Review <i class="ph-bold ph-arrow-right"></i>
                </div>
            </div>
        </a>

        <a href="customer-support.jsp" class="bg-white rounded-3xl p-8 border border-gray-100 soft-card card-hover group flex flex-col h-full relative overflow-hidden">
            <div class="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-rose-100 to-pink-100 rounded-full -mr-16 -mt-16 opacity-50 group-hover:opacity-70 transition-opacity"></div>
            <div class="relative z-10">
                <div class="flex items-center gap-4 mb-5">
                    <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-rose-500 to-pink-600 text-white flex items-center justify-center text-3xl shadow-lg icon-bounce">
                        <i class="ph-fill ph-headset"></i>
                    </div>
                    <h3 class="text-xl font-extrabold text-gray-900">Help & Support</h3>
                </div>
                <p class="text-gray-500 font-medium mb-6 flex-grow leading-relaxed">Having trouble? Contact our 24/7 support team, read the FAQs, or report an issue with a vehicle.</p>
                <div class="inline-flex items-center gap-2 bg-rose-50 text-rose-600 font-bold px-4 py-2 rounded-xl group-hover:bg-rose-600 group-hover:text-white transition-all">
                    Get Assistance <i class="ph-bold ph-arrow-right"></i>
                </div>
            </div>
        </a>

    </div>

</main>

</body>
</html>