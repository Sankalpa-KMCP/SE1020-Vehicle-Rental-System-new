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
    body { font-family: 'Inter', sans-serif; background-color: #F8F9FA; }
    .fade-in { animation: fadeIn 0.3s ease-in-out; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
  </style>
</head>
<body class="min-h-screen p-6 relative text-gray-800">

<div class="flex gap-4 mb-8 max-w-5xl mx-auto">
  <button onclick="history.back()" class="flex items-center justify-center gap-2 bg-white px-5 py-2.5 rounded-xl font-semibold text-gray-700 shadow-sm border border-gray-200 hover:bg-gray-50 transition">
    <i class="ph-bold ph-arrow-left text-lg text-gray-500"></i>
    <span>Back</span>
  </button>
  <a href="admin-home.jsp" class="flex items-center justify-center gap-2 bg-gray-900 px-5 py-2.5 rounded-xl font-semibold text-white shadow-sm hover:bg-black transition">
    <i class="ph-fill ph-house text-gray-300 text-xl"></i>
    <span>Dashboard Home</span>
  </a>
</div>

<div class="w-full max-w-5xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
  <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-200 flex justify-between items-center hover:shadow-md transition-shadow">
    <div>
      <p class="text-gray-500 font-medium text-sm">Total Guests</p>
      <h3 class="text-3xl font-bold text-gray-900 mt-1"><%= request.getAttribute("totalGuests") != null ? request.getAttribute("totalGuests") : 0 %></h3>
    </div>
    <div class="bg-blue-50 p-3 rounded-lg text-blue-600">
      <i class="ph-fill ph-users-three text-3xl"></i>
    </div>
  </div>

  <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-200 flex justify-between items-center hover:shadow-md transition-shadow">
    <div>
      <p class="text-gray-500 font-medium text-sm">Verified Docs</p>
      <h3 class="text-3xl font-bold text-gray-900 mt-1"><%= request.getAttribute("verifiedGuests") != null ? request.getAttribute("verifiedGuests") : 0 %></h3>
    </div>
    <div class="bg-green-50 p-3 rounded-lg text-green-600">
      <i class="ph-fill ph-shield-check text-3xl"></i>
    </div>
  </div>

  <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-200 flex justify-between items-center hover:shadow-md transition-shadow">
    <div>
      <p class="text-gray-500 font-medium text-sm">Missing Docs</p>
      <h3 class="text-3xl font-bold text-gray-900 mt-1"><%= request.getAttribute("unverifiedGuests") != null ? request.getAttribute("unverifiedGuests") : 0 %></h3>
    </div>
    <div class="bg-red-50 p-3 rounded-lg text-red-600">
      <i class="ph-fill ph-warning-circle text-3xl"></i>
    </div>
  </div>
</div>

<div class="flex justify-center items-start pb-12">
  <div class="w-full max-w-5xl bg-white border border-gray-200 rounded-2xl p-8 shadow-sm">

    <div id="listSection" class="fade-in block">
      <div class="flex justify-between items-end mb-6">
        <div>
          <h2 class="text-2xl font-bold text-gray-900 tracking-tight">Guest List</h2>
          <p class="text-gray-500 text-sm mt-1">Manage and verify your registered rental customers.</p>
        </div>
      </div>

      <div class="overflow-x-auto rounded-xl border border-gray-200">
        <table class="w-full text-left border-collapse">
          <thead>
          <tr class="bg-gray-50 text-gray-500 text-sm uppercase tracking-wider border-b border-gray-200">
            <th class="py-4 px-6 font-semibold">Username</th>
            <th class="py-4 px-6 font-semibold">Name</th>
            <th class="py-4 px-6 font-semibold">Email</th>
            <th class="py-4 px-6 font-semibold">Verification</th>
          </tr>
          </thead>
          <tbody class="text-gray-700 text-sm">
          <%
            List<Customer> customerList = (List<Customer>) request.getAttribute("customerList");
            if (customerList != null && !customerList.isEmpty()) {
              for(Customer c : customerList) {
                boolean isVerified = c.getNic() != null && !c.getNic().equals("N/A") && !c.getNic().isEmpty() &&
                        c.getLicenseNumber() != null && !c.getLicenseNumber().equals("N/A") && !c.getLicenseNumber().isEmpty();
          %>
          <tr class="border-b border-gray-100 hover:bg-gray-50 transition">
            <td class="py-4 px-6 font-medium text-gray-900">@<%= c.getUsername() %></td>
            <td class="py-4 px-6"><%= c.getName() %></td>
            <td class="py-4 px-6 text-gray-500"><%= c.getEmail() %></td>
            <td class="py-4 px-6">
              <% if (isVerified) { %>
              <span class="bg-green-50 text-green-700 border border-green-200 font-medium px-2.5 py-1 rounded-md text-xs flex items-center gap-1 w-max"><i class="ph-bold ph-check-circle"></i> Verified</span>
              <% } else { %>
              <span class="bg-red-50 text-red-700 border border-red-200 font-medium px-2.5 py-1 rounded-md text-xs flex items-center gap-1 w-max"><i class="ph-bold ph-warning"></i> Missing Docs</span>
              <% } %>
            </td>
          </tr>
          <%
            }
          } else {
          %>
          <tr><td colspan="4" class="py-8 text-center text-gray-500">No registered guests found.</td></tr>
          <% } %>
          </tbody>
        </table>
      </div>
    </div>

    <div id="searchSection" class="fade-in hidden">
      <div class="mb-6">
        <h2 id="searchTitle" class="text-2xl font-bold text-gray-900 tracking-tight">Search & Verify Guest</h2>
        <p class="text-gray-500 text-sm mt-1">Look up a guest by their username to update documents or view history.</p>
      </div>

      <form action="customers" method="GET" class="flex flex-col sm:flex-row gap-3 mb-8">
        <input type="hidden" name="action" value="search">
        <div class="relative flex-grow">
          <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
            <i class="ph-bold ph-magnifying-glass text-gray-400"></i>
          </div>
          <input type="text" name="searchUsername" placeholder="Enter Username (e.g., johndoe123)..." class="w-full pl-11 pr-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-shadow text-gray-800" required>
        </div>
        <button type="submit" class="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-8 py-3 rounded-xl flex items-center justify-center gap-2 shadow-sm transition-colors shrink-0">
          Find Guest
        </button>
      </form>

      <%
        Customer foundCustomer = (Customer) request.getAttribute("foundCustomer");
        if (foundCustomer != null) {
          boolean foundIsVerified = foundCustomer.getNic() != null && !foundCustomer.getNic().equals("N/A") && !foundCustomer.getNic().isEmpty() &&
                  foundCustomer.getLicenseNumber() != null && !foundCustomer.getLicenseNumber().equals("N/A") && !foundCustomer.getLicenseNumber().isEmpty();
      %>
      <div class="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">

        <div class="bg-gray-50 border-b border-gray-200 px-6 py-4 flex justify-between items-center">
          <div>
            <p class="text-xs font-semibold text-gray-500 uppercase tracking-wider">Guest Profile</p>
            <h3 class="text-xl font-bold text-gray-900 mt-1">@<%= foundCustomer.getUsername() %></h3>
          </div>
          <% if (foundIsVerified) { %>
          <span class="bg-green-50 text-green-700 border border-green-200 font-medium px-3 py-1.5 rounded-md text-sm flex items-center gap-1.5"><i class="ph-bold ph-shield-check text-lg"></i> Verified</span>
          <% } else { %>
          <span class="bg-red-50 text-red-700 border border-red-200 font-medium px-3 py-1.5 rounded-md text-sm flex items-center gap-1.5"><i class="ph-bold ph-warning-circle text-lg"></i> Missing Docs</span>
          <% } %>
        </div>

        <div class="p-6">
          <form action="customers" method="POST" class="space-y-6">
            <input type="hidden" name="action" id="manageAction" value="update">
            <input type="hidden" name="editGuestId" value="<%= foundCustomer.getId() %>">

            <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
              <div class="space-y-4">
                <h4 class="font-semibold text-gray-800 border-b border-gray-100 pb-2">Contact Information</h4>
                <div>
                  <label class="block text-gray-600 font-medium text-sm mb-1.5">Full Name</label>
                  <input type="text" name="editFullName" value="<%= foundCustomer.getName() %>" class="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-shadow" required>
                </div>
                <div>
                  <label class="block text-gray-600 font-medium text-sm mb-1.5">Email Address</label>
                  <input type="email" name="editEmail" value="<%= foundCustomer.getEmail() %>" class="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-shadow" required>
                </div>
                <div>
                  <label class="block text-gray-600 font-medium text-sm mb-1.5">Phone Number</label>
                  <input type="text" name="editPhone" value="<%= foundCustomer.getPhoneNumber() %>" class="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-shadow" required>
                </div>
              </div>

              <div class="space-y-4">
                <h4 class="font-semibold text-gray-800 border-b border-gray-100 pb-2">Identity Documents</h4>
                <div>
                  <label class="block text-gray-600 font-medium text-sm mb-1.5">NIC Number</label>
                  <input type="text" name="editNic" value="<%= foundCustomer.getNic() %>" class="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-shadow">
                  <p class="text-xs text-gray-400 mt-1.5">Leave as N/A if missing.</p>
                </div>
                <div>
                  <label class="block text-gray-600 font-medium text-sm mb-1.5">Driver's License</label>
                  <input type="text" name="editLicense" value="<%= foundCustomer.getLicenseNumber() %>" class="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-shadow">
                  <p class="text-xs text-gray-400 mt-1.5">Leave as N/A if missing.</p>
                </div>
              </div>
            </div>

            <div class="flex flex-col sm:flex-row gap-3 pt-4">
              <button type="submit" onclick="document.getElementById('manageAction').value='update'" class="flex-1 bg-gray-900 hover:bg-black text-white font-semibold py-3 px-4 rounded-xl flex justify-center items-center gap-2 shadow-sm transition-colors">
                <i class="ph-bold ph-floppy-disk text-lg"></i> Save Changes
              </button>
              <button type="submit" onclick="document.getElementById('manageAction').value='delete'" class="sm:w-auto bg-red-50 hover:bg-red-100 text-red-600 border border-red-200 font-semibold py-3 px-6 rounded-xl flex justify-center items-center gap-2 transition-colors">
                <i class="ph-bold ph-trash text-lg"></i> Delete Guest
              </button>
            </div>
          </form>
        </div>

        <div class="bg-gray-50 border-t border-gray-200 p-6">
          <h4 class="font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <i class="ph-bold ph-clock-counter-clockwise text-gray-500"></i> Rental History
          </h4>

          <div class="overflow-x-auto rounded-xl border border-gray-200 bg-white shadow-sm">
            <table class="w-full text-left border-collapse">
              <thead>
              <tr class="bg-gray-50 text-gray-500 text-xs uppercase tracking-wider border-b border-gray-200">
                <th class="py-3 px-4 font-semibold">Transaction ID</th>
                <th class="py-3 px-4 font-semibold">Vehicle</th>
                <th class="py-3 px-4 font-semibold">Date</th>
                <th class="py-3 px-4 font-semibold">Days</th>
                <th class="py-3 px-4 font-semibold">Status</th>
              </tr>
              </thead>
              <tbody class="text-gray-700 text-sm">
              <%
                List<Booking> customerBookings = (List<Booking>) request.getAttribute("customerBookings");
                if (customerBookings != null && !customerBookings.isEmpty()) {
                  for(Booking b : customerBookings) {
              %>
              <tr class="border-b border-gray-100 hover:bg-gray-50 transition">
                <td class="py-3 px-4 font-medium text-gray-900">#<%= b.getTransactionId() %></td>
                <td class="py-3 px-4"><%= b.getVehicleId() %></td>
                <td class="py-3 px-4 text-gray-500"><%= b.getRentalDate() %></td>
                <td class="py-3 px-4"><%= b.getDurationDays() %></td>
                <td class="py-3 px-4">
                  <% if ("Active".equalsIgnoreCase(b.getStatus())) { %>
                  <span class="bg-blue-50 text-blue-700 border border-blue-200 font-medium px-2 py-0.5 rounded text-xs">Active</span>
                  <% } else if ("Completed".equalsIgnoreCase(b.getStatus())) { %>
                  <span class="bg-green-50 text-green-700 border border-green-200 font-medium px-2 py-0.5 rounded text-xs">Completed</span>
                  <% } else if ("Pending".equalsIgnoreCase(b.getStatus())) { %>
                  <span class="bg-yellow-50 text-yellow-700 border border-yellow-200 font-medium px-2 py-0.5 rounded text-xs">Pending</span>
                  <% } else { %>
                  <span class="bg-gray-100 text-gray-700 border border-gray-200 font-medium px-2 py-0.5 rounded text-xs"><%= b.getStatus() %></span>
                  <% } %>
                </td>
              </tr>
              <%
                }
              } else {
              %>
              <tr><td colspan="5" class="py-6 text-center text-gray-500 text-sm">No past rentals found for this guest.</td></tr>
              <% } %>
              </tbody>
            </table>
          </div>
        </div>

      </div>
      <% } else if ("search".equals(request.getParameter("action"))) { %>
      <div class="bg-red-50 text-red-700 p-4 rounded-xl border border-red-200 text-center font-medium flex items-center justify-center gap-2">
        <i class="ph-fill ph-warning-circle text-xl"></i> No customer found with that username.
      </div>
      <% } %>
    </div>

    <hr class="my-8 border-gray-200">

    <div class="flex justify-center flex-wrap gap-4">
      <button onclick="switchView('list')" class="flex items-center gap-2 px-6 py-2.5 bg-white border border-gray-300 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 hover:text-gray-900 transition-colors shadow-sm">
        <i class="ph-bold ph-list-dashes text-lg"></i> View Directory
      </button>
      <button onclick="switchView('search')" class="flex items-center gap-2 px-6 py-2.5 bg-white border border-gray-300 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 hover:text-gray-900 transition-colors shadow-sm">
        <i class="ph-bold ph-magnifying-glass text-lg"></i> Find Guest
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
    }
  }

  <% if (foundCustomer != null || "search".equals(request.getParameter("action"))) { %>
  switchView('search');
  <% } %>
</script>

</body>
</html>