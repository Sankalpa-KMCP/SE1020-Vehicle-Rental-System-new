<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Feedback" %>
<%@ page import="com.example.vehiclerentalsystem01.models.CustomerReview" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    // Allow access to review dashboard without admin login
    // if (session.getAttribute("adminLoggedIn") == null || !(Boolean)session.getAttribute("adminLoggedIn")) {
    //     response.sendRedirect("login.jsp"); return;
    // }

    List<Feedback> reviewList = (List<Feedback>) request.getAttribute("reviewList");
    int totalReviews = reviewList != null ? reviewList.size() : 0;
    int publishedCount = 0;
    int flaggedCount = 0;
    double avgRating = 0.0;
    int totalRating = 0;

    if (reviewList != null) {
        for (Feedback f : reviewList) {
            CustomerReview cr = (CustomerReview) f;
            if ("PUBLISHED".equals(cr.getStatus())) {
                publishedCount++;
            } else {
                flaggedCount++;
            }
            totalRating += cr.getRating();
        }
        if (totalReviews > 0) {
            avgRating = (double) totalRating / totalReviews;
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Reviews - DriveAway</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; background: radial-gradient(circle at top right, #eef4ff 0%, #f8fafc 45%, #f1f5f9 100%); }
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
    <div class="bg-gradient-to-br from-amber-500 via-orange-500 to-rose-500 rounded-3xl p-10 sm:p-12 text-white shadow-xl mb-10 relative overflow-hidden">
        <div class="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
            <div>
                <h1 class="text-4xl sm:text-5xl font-extrabold mb-4 tracking-tight">Customer Reviews</h1>
                <p class="text-orange-100 text-lg font-medium max-w-2xl leading-relaxed">Read testimonials, moderate feedback, and manage your reputation.</p>
            </div>
            <div class="flex gap-3 bg-white/10 p-4 rounded-2xl backdrop-blur-sm border border-white/20">
                <div class="text-center px-4 border-r border-white/20">
                    <p class="text-orange-100 text-xs font-bold uppercase tracking-wider mb-1">Total</p>
                    <p class="text-white font-extrabold"><%= totalReviews %></p>
                </div>
                <div class="text-center px-4 border-r border-white/20">
                    <p class="text-orange-100 text-xs font-bold uppercase tracking-wider mb-1">Published</p>
                    <p class="text-white font-extrabold"><%= publishedCount %></p>
                </div>
                <div class="text-center px-4">
                    <p class="text-orange-100 text-xs font-bold uppercase tracking-wider mb-1">Flagged</p>
                    <p class="text-white font-extrabold"><%= flaggedCount %></p>
                </div>
            </div>
        </div>
        <i class="ph-fill ph-star absolute -right-10 -bottom-20 text-[300px] text-white opacity-10 transform -rotate-12 pointer-events-none"></i>
        <div class="absolute top-0 left-0 w-full h-full bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10 mix-blend-overlay pointer-events-none"></div>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6 mb-10">
        <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Total Reviews</p>
            <p class="text-3xl font-extrabold text-gray-900"><%= totalReviews %></p>
            <p class="text-sm text-gray-500 mt-2">All customer feedback entries.</p>
        </div>
        <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Average Rating</p>
            <p class="text-3xl font-extrabold text-amber-500"><%= String.format("%.1f", avgRating) %> <span class="text-lg text-gray-400">/ 5</span></p>
            <p class="text-sm text-gray-500 mt-2">Overall customer satisfaction.</p>
        </div>
        <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Published</p>
            <p class="text-3xl font-extrabold text-green-600"><%= publishedCount %></p>
            <p class="text-sm text-gray-500 mt-2">Visible on customer portal.</p>
        </div>
        <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card">
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">Flagged</p>
            <p class="text-3xl font-extrabold text-rose-600"><%= flaggedCount %></p>
            <p class="text-sm text-gray-500 mt-2">Require moderation attention.</p>
        </div>
    </div>

    <div class="mb-6 flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4">
        <div>
            <h2 class="text-2xl font-bold text-gray-900">Review Management</h2>
            <p class="text-gray-500 mt-1">Monitor and moderate all customer feedback and replies.</p>
        </div>
        <a href="add-review.jsp" class="bg-[#0D6EFD] hover:bg-blue-700 text-white font-bold px-5 py-3 rounded-xl shadow-sm transition-all flex items-center gap-2 justify-center">
            <i class="ph-bold ph-plus"></i> Log Review
        </a>
    </div>

    <div class="bg-white rounded-2xl soft-card border border-slate-200 overflow-hidden">
        <table class="w-full text-left border-collapse">
            <thead>
            <tr class="border-b border-slate-100 bg-slate-50/80">
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Review ID</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Customer</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Rating</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Comment</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Status</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest">Staff Reply</th>
                <th class="py-5 px-8 text-xs font-extrabold text-gray-500 uppercase tracking-widest text-right">Actions</th>
            </tr>
            </thead>
            <tbody>
            <%
                if (reviewList != null && !reviewList.isEmpty()) {
                    for (Feedback f : reviewList) {
                        CustomerReview cr = (CustomerReview) f;
            %>
            <tr class="hover:bg-orange-50/40 transition-colors border-b border-gray-50 last:border-none group">
                <td class="py-5 px-8 whitespace-nowrap text-sm font-mono text-gray-500">#<%= cr.getReviewId() %></td>
                <td class="py-5 px-8 whitespace-nowrap text-sm font-bold text-gray-900"><%= cr.getReviewerName() %></td>
                <td class="py-5 px-8 whitespace-nowrap text-sm text-amber-500 font-bold">
                    <% for(int i=0; i<cr.getRating(); i++) { out.print("&#9733;"); } %>
                </td>
                <td class="py-5 px-8 text-sm text-gray-600 max-w-xs truncate" title="<%= cr.getComments().replace("\"", "&quot;") %>">
                    "<%= cr.getComments() %>"
                </td>
                <td class="py-5 px-8 whitespace-nowrap text-sm">
                    <% if("PUBLISHED".equals(cr.getStatus())) { %>
                    <span class="bg-green-50 text-green-700 border border-green-100 px-3 py-1.5 rounded-lg text-xs font-bold">PUBLISHED</span>
                    <% } else { %>
                    <span class="bg-rose-50 text-rose-700 border border-rose-100 px-3 py-1.5 rounded-lg text-xs font-bold">FLAGGED</span>
                    <% } %>
                </td>
                <td class="py-5 px-8 text-sm text-gray-600 max-w-sm">
                    <% if (cr.getStaffReply() != null && !cr.getStaffReply().trim().isEmpty()) { %>
                    <div class="bg-blue-50 border border-blue-100 rounded-lg px-3 py-2">
                        <div class="text-xs font-bold text-blue-700 mb-1">Reply by <%= cr.getRepliedBy() %></div>
                        <div class="text-sm text-blue-900"><%= cr.getStaffReply() %></div>
                    </div>
                    <% } else { %>
                    <span class="text-xs text-gray-400 font-semibold">No reply yet</span>
                    <% } %>
                </td>
                <td class="py-5 px-8 whitespace-nowrap text-right text-sm font-medium">
                    <div class="flex items-center justify-end gap-2">
                        <a href="reviews?action=edit&id=<%= cr.getReviewId() %>" class="text-blue-700 hover:text-blue-900 bg-blue-50 hover:bg-blue-100 px-3 py-2 rounded-lg transition-colors flex items-center gap-1 font-semibold">
                            <i class="ph-bold ph-pencil-simple"></i> Edit
                        </a>
                        <a href="reviews?action=delete&id=<%= cr.getReviewId() %>" class="text-red-600 hover:text-red-800 bg-red-50 hover:bg-red-100 px-3 py-2 rounded-lg transition-colors flex items-center gap-1 font-semibold" onclick="return confirm('Delete this review permanently?');">
                            <i class="ph-bold ph-trash"></i> Delete
                        </a>
                    </div>
                    <form action="reviews" method="post" class="flex items-center justify-end gap-2 mt-2">
                        <input type="hidden" name="action" value="reply">
                        <input type="hidden" name="id" value="<%= cr.getReviewId() %>">
                        <input type="text" name="staffReply" maxlength="160" placeholder="Reply to customer" class="w-48 border border-gray-300 rounded-lg px-3 py-1.5 text-xs">
                        <button type="submit" class="bg-slate-700 text-white text-xs font-bold px-3 py-1.5 rounded-lg hover:bg-slate-900 transition">Reply</button>
                    </form>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="7" class="py-24 text-center">
                    <div class="flex flex-col items-center justify-center">
                        <i class="ph-fill ph-chat-circle-text text-6xl text-gray-300 mb-4"></i>
                        <p class="text-2xl font-bold text-gray-900 mb-2 tracking-tight">No Reviews Found</p>
                        <p class="text-sm text-gray-500 font-medium max-w-md">Once customers submit feedback, it will appear here.</p>
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