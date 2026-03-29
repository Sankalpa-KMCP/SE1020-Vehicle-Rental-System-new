<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Feedback" %>
<%@ page import="com.example.vehiclerentalsystem01.models.CustomerReview" %>
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
    <title>Customer Reviews - DriveAway Pro</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
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
        <a href="staff?action=list" class="flex items-center px-4 py-3.5 text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl font-medium transition-all">
            <i class="ph-bold ph-identification-badge text-xl mr-3"></i> Staff & Admins
        </a>
        <!-- Active Tab for Student 6 (Slate Accent) -->
        <a href="reviews?action=list" class="flex items-center px-4 py-3.5 bg-[#475569] text-white rounded-xl font-bold shadow-md transition-all">
            <i class="ph-bold ph-star text-xl mr-3"></i> Reviews
        </a>
    </nav>
    <div class="p-5 mb-2 space-y-2">
        <a href="admin-home.jsp" class="flex items-center px-4 py-3 text-sm text-slate-400 hover:text-white font-medium transition-all rounded-xl hover:bg-slate-800">
            <i class="ph-bold ph-arrow-square-out text-lg mr-3"></i> Admin Hub
        </a>
        <a href="login.jsp" class="flex items-center px-4 py-3 text-sm text-slate-400 hover:text-white font-medium transition-all rounded-xl hover:bg-slate-800">
            <i class="ph-bold ph-sign-out text-lg mr-3"></i> Logout
        </a>
    </div>
</aside>

<!-- MAIN CONTENT -->
<main class="flex-1 flex flex-col h-screen overflow-hidden bg-[#F8F9FA]">
    <header class="h-24 bg-white flex items-center justify-between px-10 z-10 border-b border-gray-100">
        <h2 class="text-2xl font-medium text-gray-800">Feedback & Moderation Portal</h2>
    </header>

    <div class="flex-1 overflow-y-auto p-10">
        <div class="flex justify-between items-end mb-8">
            <div>
                <h3 class="text-3xl font-extrabold text-gray-900 mb-2 tracking-tight">Customer Reviews</h3>
                <p class="text-gray-500 text-sm font-medium">Read testimonials, moderate feedback, and manage reputation.</p>
            </div>
            <a href="add-review.jsp" class="flex items-center bg-[#475569] hover:bg-slate-700 text-white px-6 py-3.5 rounded-xl font-bold shadow-md transition-all">
                <i class="ph-bold ph-plus mr-2 text-xl"></i> Log Review
            </a>
        </div>

        <!-- Seamless Data Table Card -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
            <table class="w-full text-left border-collapse">
                <thead>
                <tr class="border-b border-gray-100">
                    <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Review ID</th>
                    <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Customer</th>
                    <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Rating</th>
                    <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Comment Snippet</th>
                    <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Status</th>
                    <th class="py-6 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest text-right">Actions</th>
                </tr>
                </thead>
                <tbody>
                <%
                    List<Feedback> reviewList = (List<Feedback>) request.getAttribute("reviewList");
                    if (reviewList != null && !reviewList.isEmpty()) {
                        for (Feedback f : reviewList) {
                            CustomerReview cr = (CustomerReview) f;
                %>
                <tr class="hover:bg-slate-50 transition-colors group border-b border-gray-50 last:border-none">
                    <td class="py-5 px-8 whitespace-nowrap text-sm font-semibold text-gray-500">#<%= cr.getReviewId() %></td>
                    <td class="py-5 px-8 whitespace-nowrap text-sm font-bold text-gray-900"><%= cr.getReviewerName() %></td>
                    <td class="py-5 px-8 whitespace-nowrap text-sm text-yellow-500 font-bold">
                        <% for(int i=0; i<cr.getRating(); i++) { out.print("⭐"); } %>
                    </td>
                    <td class="py-5 px-8 whitespace-nowrap text-sm text-gray-600 max-w-xs truncate overflow-hidden">
                        "<%= cr.getComments() %>"
                    </td>
                    <td class="py-5 px-8 whitespace-nowrap text-sm font-bold">
                        <% if(cr.getStatus().equals("PUBLISHED")) { %>
                        <span class="text-green-600 bg-green-50 px-3 py-1 rounded-lg text-xs tracking-wide">PUBLISHED</span>
                        <% } else { %>
                        <span class="text-red-600 bg-red-50 px-3 py-1 rounded-lg text-xs tracking-wide">FLAGGED</span>
                        <% } %>
                    </td>
                    <td class="py-5 px-8 whitespace-nowrap text-right text-sm font-medium">
                        <div class="flex items-center justify-end gap-3 opacity-0 group-hover:opacity-100 transition-opacity">
                            <a href="reviews?action=edit&id=<%= cr.getReviewId() %>" class="text-blue-600 hover:text-blue-800 bg-blue-50 p-2.5 rounded-lg transition-colors"><i class="ph-bold ph-pencil-simple text-lg"></i></a>
                            <a href="reviews?action=delete&id=<%= cr.getReviewId() %>" class="text-red-500 hover:text-red-700 bg-red-50 p-2.5 rounded-lg transition-colors" onclick="return confirm('Delete this review permanently?');"><i class="ph-bold ph-trash text-lg"></i></a>
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
                            <i class="ph ph-chat-circle-text text-5xl text-gray-300 mb-4"></i>
                            <p class="text-xl font-bold text-gray-900 mb-2 tracking-tight">No Reviews Found</p>
                            <p class="text-sm text-gray-500 font-medium">Once customers submit feedback, it will appear here.</p>
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