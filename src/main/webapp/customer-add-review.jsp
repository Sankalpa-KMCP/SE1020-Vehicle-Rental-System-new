<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    Boolean customerLoggedIn = (Boolean) session.getAttribute("customerLoggedIn");
    if (customerLoggedIn == null || !customerLoggedIn) {
        response.sendRedirect("/login.jsp");
        return;
    }
    String err = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DriveAway | Write a review</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <style>body { font-family: 'Inter', sans-serif; }</style>
</head>
<body class="min-h-screen bg-slate-50 text-slate-900 p-6">
<div class="max-w-xl mx-auto">
    <div class="flex gap-3 mb-8">
        <a href="/customer-home.jsp" class="inline-flex items-center gap-2 text-slate-600 font-semibold hover:text-slate-900">
            <i class="ph-bold ph-arrow-left"></i> Back to home
        </a>
    </div>

    <% if ("missing_fields".equals(err)) { %>
        <p class="mb-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-red-900 text-sm">Please choose a rating and enter comments before submitting.</p>
    <% } else if ("account_limit".equals(err)) { %>
        <p class="mb-4 rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-amber-900 text-sm">You already submitted a general review. Add a booking ID to review a specific trip.</p>
    <% } else if ("booking_limit".equals(err)) { %>
        <p class="mb-4 rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-amber-900 text-sm">You already left two reviews for that booking.</p>
    <% } else if ("invalid_booking".equals(err)) { %>
        <p class="mb-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-red-900 text-sm">Booking not found or it does not belong to your account.</p>
    <% } %>

    <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-8">
        <h1 class="text-2xl font-bold mb-2 flex items-center gap-2">
            <i class="ph-fill ph-star text-amber-500"></i> Write a review
        </h1>
        <p class="text-slate-600 text-sm mb-6">Share feedback on a rental. Optionally tie this review to a booking ID from My Bookings.</p>

        <form action="/reviews" method="post" class="space-y-5">
            <input type="hidden" name="source" value="customer">
            <input type="hidden" name="action" value="create">
            <input type="hidden" name="status" value="PUBLISHED">
            <c:set var="rn" value="${sessionScope.customerUsername}" />
            <c:if test="${empty rn}"><c:set var="rn" value="${sessionScope.username}" /></c:if>
            <c:if test="${empty rn}"><c:set var="rn" value="${sessionScope.name}" /></c:if>
            <c:if test="${empty rn}"><c:set var="rn" value="Customer" /></c:if>
            <input type="hidden" name="reviewerName" value="<c:out value='${rn}'/>">

            <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1">Booking ID (optional)</label>
                <input type="text" name="bookingId" placeholder="e.g. BKG-AB12"
                       class="w-full rounded-xl border border-slate-200 px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none">
            </div>

            <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1">Rating</label>
                <select name="rating" required class="w-full rounded-xl border border-slate-200 px-4 py-3 bg-white">
                    <option value="5">5 — Excellent</option>
                    <option value="4">4 — Good</option>
                    <option value="3">3 — OK</option>
                    <option value="2">2 — Poor</option>
                    <option value="1">1 — Very poor</option>
                </select>
            </div>

            <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1">Comments</label>
                <textarea name="comments" rows="4" required maxlength="2000"
                          class="w-full rounded-xl border border-slate-200 px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
                          placeholder="Tell us about your experience"></textarea>
            </div>

            <button type="submit" class="w-full rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 shadow-sm transition">
                Submit review
            </button>
        </form>
    </div>
</div>
</body>
</html>
