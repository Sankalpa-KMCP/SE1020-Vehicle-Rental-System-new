<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Customer" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>

<%
  Boolean isLoggedIn = (Boolean) session.getAttribute("adminLoggedIn");
  if (isLoggedIn == null || !isLoggedIn) {
    response.sendRedirect("index.jsp");
    return;
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Guest Management Dashboard</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');
    body { font-family: 'Inter', sans-serif; background-color: #FAFAFA; }
    .fade-in { animation: fadeIn 0.3s ease-in-out; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
  </style>
</head>
<body class="min-h-screen p-6 relative">

<div class="flex gap-4 mb-8">
  <button onclick="history.back()" class="flex items-center justify-center gap-2 bg-white px-5 py-2.5 rounded-xl font-bold text-black shadow-sm border border-gray-200 hover:bg-gray-50 transition">
    <div class="bg-gray-500 rounded-full text-white p-0.5 flex items-center justify-center h-6 w-6">
      <i class="ph-bold ph-arrow-left text-sm"></i>
    </div>
    <span class="text-lg">Back</span>
  </button>
  <a href="admin-home.jsp" class="flex items-center justify-center gap-2 bg-[#1C2024] px-5 py-2.5 rounded-xl font-bold text-white shadow-sm hover:bg-black transition">
    <i class="ph-fill ph-house text-[#FFD43B] text-2xl"></i>
    <span class="text-lg">Home</span>
  </a>
</div>

<div class="w-full max-w-4xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
  <div class="bg-white rounded-xl p-6 shadow-md border-b-4 border-blue-500 flex items-center gap-4 hover:-translate-y-1 transition-transform">
    <div class="bg-blue-100 p-4 rounded-full text-blue-600">
      <i class="ph-fill ph-users-three text-4xl"></i>
    </div>
    <div>
      <p class="text-gray-500 font-bold text-sm uppercase tracking-wider">Total Guests</p>
      <h3 class="text-4xl font-extrabold text-gray-900"><%= request.getAttribute("totalGuests") != null ? request.getAttribute("totalGuests") : 0 %></h3>
    </div>
  </div>

  <div class="bg-white rounded-xl p-6 shadow-md border-b-4 border-green-500 flex items-center gap-4 hover:-translate-y-1 transition-transform">
    <div class="bg-green-100 p-4 rounded-full text-green-600">
      <i class="ph-fill ph-shield-check text-4xl"></i>
    </div>
    <div>
      <p class="text-gray-500 font-bold text-sm uppercase tracking-wider">Verified Docs</p>
      <h3 class="text-4xl font-extrabold text-gray-900"><%= request.getAttribute("verifiedGuests") != null ? request.getAttribute("verifiedGuests") : 0 %></h3>
    </div>
  </div>

  <div class="bg-white rounded-xl p-6 shadow-md border-b-4 border-red-500 flex items-center gap-4 hover:-translate-y-1 transition-transform">
    <div class="bg-red-100 p-4 rounded-full text-red-600">
      <i class="ph-fill ph-warning-circle text-4xl"></i>
    </div>
    <div>
      <p class="text-gray-500 font-bold text-sm uppercase tracking-wider">Missing Docs</p>
      <h3 class="text-4xl font-extrabold text-gray-900"><%= request.getAttribute("unverifiedGuests") != null ? request.getAttribute("unverifiedGuests") : 0 %></h3>
    </div>
  </div>
</div>

<div class="flex justify-center items-start pb-12">
  <div class="w-full max-w-4xl bg-white border-[3px] border-[#0D6EFD] rounded-xl p-10 shadow-lg">

    <div id="listSection" class="fade-in block">
      <div class="flex flex-col items-center mb-8">
        <h2 class="text-[32px] font-bold text-[#198754] tracking-tight border-b-4 border-[#198754] pb-2 mb-2">Guest Directory</h2>
      </div>

      <div class="overflow-x-auto rounded-xl border border-gray-200 shadow-sm">
        <table class="w-full text-left border-collapse">
          <thead>
          <tr class="bg-gray-100 text-gray-700 text-lg border-b border-gray-200">
            <th class="py-4 px-6 font-bold">Username</th>
            <th class="py-4 px-6 font-bold">Name</th>
            <th class="py-4 px-6 font-bold">Email</th>
            <th class="py-4 px-6 font-bold">Verification</th>
          </tr>
          </thead>
          <tbody class="text-gray-600 text-lg">
          <%
            List<Customer> customerList = (List<Customer>) request.getAttribute("customerList");
            if (customerList != null && !customerList.isEmpty()) {
              for(Customer c : customerList) {
                boolean isVerified = c.getNic() != null && !c.getNic().equals("N/A") && !c.getNic().isEmpty() &&
                        c.getLicenseNumber() != null && !c.getLicenseNumber().equals("N/A") && !c.getLicenseNumber().isEmpty();
          %>
          <tr class="border-b border-gray-100 hover:bg-gray-50 transition">
            <td class="py-4 px-6 font-bold text-[#0D6EFD]">@<%= c.getUsername() %></td>
            <td class="py-4 px-6 font-semibold"><%= c.getName() %></td>
            <td class="py-4 px-6"><%= c.getEmail() %></td>
            <td class="py-4 px-6">
              <% if (isVerified) { %>
              <span class="bg-green-100 text-green-700 font-bold px-3 py-1.5 rounded-full text-sm flex items-center gap-1 w-max"><i class="ph-bold ph-check-circle"></i> Verified</span>
              <% } else { %>
              <span class="bg-red-100 text-red-700 font-bold px-3 py-1.5 rounded-full text-sm flex items-center gap-1 w-max"><i class="ph-bold ph-warning"></i> Missing Docs</span>
              <% } %>
            </td>
          </tr>
          <%
            }
          } else {
          %>
          <tr><td colspan="4" class="py-8 text-center text-gray-500 font-bold">No registered guests found.</td></tr>
          <% } %>
          </tbody>
        </table>
      </div>
    </div>

    <div id="searchSection" class="fade-in hidden">
      <div class="flex flex-col items-center mb-8">
        <i id="searchIcon" class="ph-fill ph-magnifying-glass text-[60px] text-[#00B4D8] mb-2"></i>
        <h2 id="searchTitle" class="text-[32px] font-bold text-[#00B4D8] tracking-tight">Search Guest</h2>
      </div>

      <form action="customers" method="GET" class="flex gap-4 mb-10">
        <input type="hidden" name="action" value="search">
        <input type="text" name="searchUsername" placeholder="Enter Username (e.g., johndoe123)..." class="w-full border-2 border-gray-300 rounded-xl px-6 py-4 focus:outline-none focus:border-[#00B4D8] focus:ring-1 focus:ring-[#00B4D8] text-xl font-medium" required>
        <button type="submit" class="bg-[#00B4D8] hover:bg-cyan-600 text-white font-bold px-8 py-4 rounded-xl text-xl flex items-center gap-2 shadow-md transition-all shrink-0">
          <i class="ph-bold ph-magnifying-glass text-2xl"></i> Find
        </button>
      </form>

      <%
        Customer foundCustomer = (Customer) request.getAttribute("foundCustomer");
        if (foundCustomer != null) {
          boolean foundIsVerified = foundCustomer.getNic() != null && !foundCustomer.getNic().equals("N/A") && !foundCustomer.getNic().isEmpty() &&
                  foundCustomer.getLicenseNumber() != null && !foundCustomer.getLicenseNumber().equals("N/A") && !foundCustomer.getLicenseNumber().isEmpty();
      %>
      <div class="bg-gray-50 p-6 rounded-xl border border-gray-200">
        <div class="flex justify-between items-center border-b pb-4 mb-4">
          <h3 class="text-2xl font-bold text-gray-800">Guest Found: <span class="text-[#0D6EFD]">@<%= foundCustomer.getUsername() %></span></h3>
          <% if (foundIsVerified) { %>
          <span class="bg-green-100 text-green-700 font-bold px-4 py-2 rounded-full text-md flex items-center gap-2"><i class="ph-bold ph-shield-check text-xl"></i> Verified</span>
          <% } else { %>
          <span class="bg-red-100 text-red-700 font-bold px-4 py-2 rounded-full text-md flex items-center gap-2"><i class="ph-bold ph-warning-circle text-xl"></i> Missing Docs</span>
          <% } %>
        </div>

        <form action="customers" method="POST" class="space-y-4">
          <input type="hidden" name="action" id="manageAction" value="update">
          <input type="hidden" name="editGuestId" value="<%= foundCustomer.getId() %>">

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="space-y-4 bg-white p-4 rounded-xl border border-gray-200">
              <h4 class="font-bold text-gray-500 mb-2 uppercase text-sm">Contact Info</h4>
              <div>
                <label class="block text-gray-700 font-bold mb-1">Full Name:</label>
                <input type="text" name="editFullName" value="<%= foundCustomer.getName() %>" class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:border-[#FFC107]" required>
              </div>
              <div>
                <label class="block text-gray-700 font-bold mb-1">Email:</label>
                <input type="email" name="editEmail" value="<%= foundCustomer.getEmail() %>" class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:border-[#FFC107]" required>
              </div>
              <div>
                <label class="block text-gray-700 font-bold mb-1">Phone:</label>
                <input type="text" name="editPhone" value="<%= foundCustomer.getPhoneNumber() %>" class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:border-[#FFC107]" required>
              </div>
            </div>

            <div class="space-y-4 bg-white p-4 rounded-xl border border-gray-200">
              <h4 class="font-bold text-gray-500 mb-2 uppercase text-sm">Identity Documents</h4>
              <div>
                <label class="block text-gray-700 font-bold mb-1">NIC Number:</label>
                <input type="text" name="editNic" value="<%= foundCustomer.getNic() %>" class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:border-[#198754]">
                <p class="text-xs text-gray-400 mt-1">Leave as N/A if missing.</p>
              </div>
              <div>
                <label class="block text-gray-700 font-bold mb-1">Driver's License:</label>
                <input type="text" name="editLicense" value="<%= foundCustomer.getLicenseNumber() %>" class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:border-[#198754]">
                <p class="text-xs text-gray-400 mt-1">Leave as N/A if missing.</p>
              </div>
            </div>
          </div>

          <div class="flex gap-4 mt-6">
            <button type="submit" onclick="document.getElementById('manageAction').value='update'" class="flex-1 bg-[#FFC107] hover:bg-yellow-500 text-gray-900 font-bold py-3 rounded-xl text-xl flex justify-center items-center gap-2 shadow-sm transition-all">
              <i class="ph-bold ph-floppy-disk text-2xl"></i> Save & Verify
            </button>
            <button type="submit" onclick="document.getElementById('manageAction').value='delete'" class="flex-1 bg-[#DC3545] hover:bg-red-700 text-white font-bold py-3 rounded-xl text-xl flex justify-center items-center gap-2 shadow-sm transition-all">
              <i class="ph-bold ph-trash text-2xl"></i> Delete Guest
            </button>
          </div>
        </form>

        <div class="mt-10 pt-8 border-t border-gray-200">
          <h3 class="text-2xl font-bold text-gray-800 mb-4 flex items-center gap-2">
            <i class="ph-bold ph-car-profile text-[#0D6EFD]"></i> Rental History
          </h3>

          <div class="overflow-x-auto rounded-xl border border-gray-200 shadow-sm bg-white">
            <table class="w-full text-left border-collapse">
              <thead>
              <tr class="bg-gray-100 text-gray-700 text-md border-b border-gray-200">
                <th class="py-3 px-4 font-bold">Transaction ID</th>
                <th class="py-3 px-4 font-bold">Vehicle</th>
                <th class="py-3 px-4 font-bold">Date</th>
                <th class="py-3 px-4 font-bold">Days</th>
                <th class="py-3 px-4 font-bold">Status</th>
              </tr>
              </thead>
              <tbody class="text-gray-600 text-md">
              <%
                List<Booking> customerBookings = (List<Booking>) request.getAttribute("customerBookings");
                if (customerBookings != null && !customerBookings.isEmpty()) {
                  for(Booking b : customerBookings) {
              %>
              <tr class="border-b border-gray-100 hover:bg-gray-50 transition">
                <td class="py-3 px-4 font-bold text-gray-800">#<%= b.getTransactionId() %></td>
                <td class="py-3 px-4 font-semibold text-[#0D6EFD]"><%= b.getVehicleId() %></td>
                <td class="py-3 px-4"><%= b.getRentalDate() %></td>
                <td class="py-3 px-4"><%= b.getDurationDays() %></td>
                <td class="py-3 px-4">
                  <% if ("Active".equalsIgnoreCase(b.getStatus())) { %>
                  <span class="bg-blue-100 text-blue-700 font-bold px-3 py-1 rounded-full text-xs">Active</span>
                  <% } else if ("Completed".equalsIgnoreCase(b.getStatus())) { %>
                  <span class="bg-green-100 text-green-700 font-bold px-3 py-1 rounded-full text-xs">Completed</span>
                  <% } else if ("Pending".equalsIgnoreCase(b.getStatus())) { %>
                  <span class="bg-yellow-100 text-yellow-700 font-bold px-3 py-1 rounded-full text-xs">Pending</span>
                  <% } else { %>
                  <span class="bg-gray-100 text-gray-700 font-bold px-3 py-1 rounded-full text-xs"><%= b.getStatus() %></span>
                  <% } %>
                </td>
              </tr>
              <%
                }
              } else {
              %>
              <tr><td colspan="5" class="py-6 text-center text-gray-500 font-medium">No past rentals found for this guest.</td></tr>
              <% } %>
              </tbody>
            </table>
          </div>
        </div>

      </div>
      <% } else if ("search".equals(request.getParameter("action"))) { %>
      <div class="bg-red-50 text-red-600 p-4 rounded-xl border border-red-200 text-center font-bold text-lg">
        No customer found with that username.
      </div>
      <% } %>
    </div>

    <hr class="my-10 border-gray-200">
    <div class="flex justify-center flex-wrap gap-4">
      <button onclick="switchView('list')" class="flex items-center gap-2 px-6 py-2.5 border-2 border-[#198754] text-[#198754] font-bold rounded-lg hover:bg-green-50 transition-colors text-lg">
        <i class="ph-bold ph-list-dashes text-xl"></i> Guest List
      </button>
      <button onclick="switchView('search')" class="flex items-center gap-2 px-6 py-2.5 border-2 border-[#00B4D8] text-[#00B4D8] font-bold rounded-lg hover:bg-cyan-50 transition-colors text-lg">
        <i class="ph-bold ph-magnifying-glass text-xl"></i> Search Guest
      </button>
    </div>
  </div>
</div>

<script>
  function switchView(view) {
    document.getElementById('listSection').classList.add('hidden');
    document.getElementById('searchSection').classList.add('hidden');

    if (view === 'list') {
      document.getElementById('listSection').classList.remove('hidden');
    } else {
      document.getElementById('searchSection').classList.remove('hidden');
      let title = document.getElementById('searchTitle');
      let icon = document.getElementById('searchIcon');

      if (view === 'search') {
        title.innerText = "Search & Verify Guest";
        title.className = "text-[32px] font-bold text-[#00B4D8] tracking-tight";
        icon.className = "ph-fill ph-magnifying-glass text-[60px] text-[#00B4D8] mb-2";
      }
    }
  }

  <% if (foundCustomer != null || "search".equals(request.getParameter("action"))) { %>
  switchView('search');
  <% } %>
</script>

</body>
</html>