<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.controllers.SupportController.SupportTicket" %>
<%
    Boolean isLoggedIn = (Boolean) session.getAttribute("adminLoggedIn");
    if (isLoggedIn == null || !isLoggedIn) {
        response.sendRedirect("admin-login.jsp");
        return;
    }

    List<SupportTicket> tickets = (List<SupportTicket>) request.getAttribute("tickets");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Support Tickets</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
</head>
<body class="bg-gray-50 min-h-screen p-8 text-gray-800">
<div class="max-w-7xl mx-auto">
    <div class="flex justify-between items-center mb-8">
        <div>
            <h1 class="text-3xl font-extrabold">Support Ticket Queue</h1>
            <p class="text-gray-500">Open and resolved customer support requests.</p>
        </div>
        <a href="admin-home.jsp" class="bg-gray-900 text-white font-bold px-5 py-3 rounded-xl hover:bg-black transition">Back to Dashboard</a>
    </div>

    <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
        <table class="w-full text-left border-collapse">
            <thead>
            <tr class="bg-gray-50 border-b border-gray-200 text-xs uppercase tracking-widest text-gray-500">
                <th class="px-5 py-4">Ticket ID</th>
                <th class="px-5 py-4">Customer</th>
                <th class="px-5 py-4">Subject</th>
                <th class="px-5 py-4">Message</th>
                <th class="px-5 py-4">Status</th>
                <th class="px-5 py-4">Action</th>
            </tr>
            </thead>
            <tbody>
            <%
                if (tickets != null && !tickets.isEmpty()) {
                    for (SupportTicket ticket : tickets) {
            %>
            <tr class="border-b border-gray-100 align-top">
                <td class="px-5 py-4 font-mono text-xs"><%= ticket.id %></td>
                <td class="px-5 py-4 font-semibold"><%= ticket.username %></td>
                <td class="px-5 py-4"><%= ticket.subject %></td>
                <td class="px-5 py-4 max-w-md"><%= ticket.message %></td>
                <td class="px-5 py-4">
                    <% if ("RESOLVED".equalsIgnoreCase(ticket.status)) { %>
                    <span class="px-3 py-1 rounded-lg bg-green-50 border border-green-200 text-green-700 font-bold text-xs">RESOLVED</span>
                    <div class="text-xs text-gray-500 mt-2">
                        by <%= ticket.resolvedBy %><br>
                        <%= ticket.resolvedAt %>
                    </div>
                    <% } else { %>
                    <span class="px-3 py-1 rounded-lg bg-amber-50 border border-amber-200 text-amber-700 font-bold text-xs">OPEN</span>
                    <% } %>
                </td>
                <td class="px-5 py-4">
                    <% if (!"RESOLVED".equalsIgnoreCase(ticket.status)) { %>
                    <form action="support" method="POST" class="space-y-2">
                        <input type="hidden" name="action" value="resolve_ticket">
                        <input type="hidden" name="ticketId" value="<%= ticket.id %>">
                        <textarea name="resolutionNote" rows="2" maxlength="160" placeholder="Resolution note (optional)" class="w-56 border border-gray-300 rounded-lg px-3 py-2 text-xs"></textarea>
                        <button type="submit" class="bg-green-600 hover:bg-green-700 text-white font-bold px-3 py-2 rounded-lg text-xs">Mark Resolved</button>
                    </form>
                    <% } else { %>
                    <span class="text-xs text-gray-500"><%= ticket.resolutionNote %></span>
                    <% } %>
                </td>
            </tr>
            <%      }
                } else {
            %>
            <tr>
                <td colspan="6" class="px-5 py-12 text-center text-gray-500 font-medium">No tickets found.</td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>

