<%@ page import="com.example.vehiclerentalsystem01.models.Feedback" %>
<%@ page import="com.example.vehiclerentalsystem01.models.CustomerReview" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    Feedback f = (Feedback) request.getAttribute("review");
    if (f == null) { response.sendRedirect("reviews?action=list"); return; }
    CustomerReview cr = (CustomerReview) f;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Moderate Review</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <style>@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&display=swap'); body { font-family: 'Inter', sans-serif; }</style>
</head>
<body class="bg-[#FAFAFA] flex h-screen items-center justify-center p-4">
<div class="max-w-lg w-full bg-white p-10 rounded-2xl shadow-lg border-[3px] border-[#475569]">
    <div class="text-center mb-8">
        <i class="ph-fill ph-pencil-simple text-5xl text-[#475569] mb-2"></i>
        <h2 class="text-3xl font-bold text-gray-900 tracking-tight">Moderate Review</h2>
    </div>

    <form action="reviews" method="post" class="space-y-5">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="id" value="<%= cr.getReviewId() %>">
        <input type="hidden" name="reviewerName" value="<%= cr.getReviewerName() %>">
        <input type="hidden" name="rating" value="<%= cr.getRating() %>">

        <!-- Read-only context for the admin -->
        <div class="bg-slate-50 p-4 rounded-lg border border-slate-100 mb-4">
            <p class="text-sm text-gray-500 mb-1">Customer: <span class="font-bold text-gray-900"><%= cr.getReviewerName() %></span></p>
            <p class="text-sm text-gray-500">Rating: <span class="font-bold text-yellow-500"><% for(int i=0; i<cr.getRating(); i++) out.print("⭐"); %></span></p>
        </div>

        <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">Review Content (Edit if inappropriate):</label>
            <textarea name="comments" rows="4" required class="w-full border border-gray-300 rounded-lg p-3 outline-none focus:border-[#475569]"><%= cr.getComments() %></textarea>
        </div>

        <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">Moderation Status:</label>
            <select name="status" class="w-full border border-gray-300 rounded-lg p-3 outline-none">
                <option value="PUBLISHED" <%= cr.getStatus().equals("PUBLISHED") ? "selected" : "" %>>PUBLISHED (Visible to Public)</option>
                <option value="FLAGGED" <%= cr.getStatus().equals("FLAGGED") ? "selected" : "" %>>FLAGGED (Hidden / Under Review)</option>
            </select>
        </div>

        <div class="pt-4 flex gap-3">
            <a href="reviews?action=list" class="w-1/3 bg-gray-100 text-gray-700 font-bold py-3 px-4 rounded-lg hover:bg-gray-200 text-center transition">Cancel</a>
            <button type="submit" class="w-2/3 bg-[#475569] text-white font-bold py-3 px-4 rounded-lg hover:bg-slate-800 transition">Save Moderation</button>
        </div>
    </form>
</div>
</body>
</html>