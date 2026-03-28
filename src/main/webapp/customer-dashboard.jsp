<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Customer" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>

<%
  Boolean isLoggedIn = (Boolean) session.getAttribute("adminLoggedIn");
  if (isLoggedIn == null || !isLoggedIn) {
    response.sendRedirect("admin-login.jsp");
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
<body class="min-h-screen p-8 relative text-gray-800">

<div class="flex gap-4 mb-8 max-w-7xl mx-auto">
  <button onclick="history.back()" class="flex items-center justify-center gap-2 bg-white px-6 py-3 rounded-xl font-bold text-gray-700 shadow-sm border border-gray-200 hover:bg-gray-50 transition text-lg">
    <i class="ph-bold ph-arrow-left text-xl text-gray-500"></i>
    <span>Back</span>
  </button>
  <a href="admin-home.jsp" class="flex items-center justify-center gap-2 bg-gray-900 px-6 py-3 rounded-xl font-bold text-white shadow-sm hover:bg-black transition text-lg">
    <i class="ph-fill ph-house text-gray-300 text-2xl"></i>
    <span>Dashboard Home</span>
  </a>
</div>

<div class="w-full max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-8 mb-10">
  <div class="bg-white rounded-2xl p-8 shadow-sm border border-gray-200 flex justify-between items-center hover:shadow-md transition-shadow">
    <div>
      <p class="text-gray-500 font-bold tracking-wide text-base uppercase">Total Guests</p>
      <h3 class="text-5xl font-extrabold text-gray-900 mt-2"><%= request.getAttribute("totalGuests") != null ? request.getAttribute("totalGuests") : 0 %></h3>
    </div>
    <div class="bg-blue-50 p-4 rounded-2xl text-blue-600">
      <i class="ph-fill ph-users-three text-5xl"></i>
    </div>
  </div>

  <div class="bg-white rounded-2xl p-8 shadow-sm border border-gray-200 flex justify-between items-center hover:shadow-md transition-shadow">
    <div>
      <p class="text-gray-500 font-bold tracking-wide text-base uppercase">Verified Docs</p>
      <h3 class="text-5xl font-extrabold text-gray-900 mt-2"><%= request.getAttribute("verifiedGuests") != null ? request.getAttribute("verifiedGuests") : 0 %></h3>
    </div>
    <div class="bg-green-50 p-4 rounded-2xl text-green-600">
      <i class="ph-fill ph-shield-check text-5xl"></i>
    </div>
  </div>

  <div class="bg-white rounded-2xl p-8 shadow-sm border border-gray-200 flex justify-between items-center hover:shadow-md transition-shadow">
    <div>
      <p class="text-gray-500 font-bold tracking-wide text-base uppercase">Missing Docs</p>
      <h3 class="text-5xl font-extrabold text-gray-900 mt-2"><%= request.getAttribute("unverifiedGuests") != null ? request.getAttribute("unverifiedGuests") : 0 %></h3>
    </div>
    <div class="bg-red-50 p-4 rounded-2xl text-red-600">
      <i class="ph-fill ph-warning-circle text-5xl"></i>
    </div>
  </div>
</div>

<div class="flex justify-center items-start pb-16">
  <div class="w-full max-w-7xl bg-white border border-gray-200 rounded-3xl p-10 shadow-sm">

    <div id="listSection" class="fade-in block">
      <div class="flex justify-between items-end mb-8">
        <div>
          <h2 class="text-3xl font-extrabold text-gray-900 tracking-tight">Guest Directory</h2>
          <p class="text-gray-500 text-lg mt-1 font-medium">Manage and verify your registered rental customers.</p>
        </div>
      </div>

      <div class="overflow-x-auto rounded-2xl border border-gray-200 bg-white shadow-sm">
        <table class="w-full text-left border-collapse">
          <thead>
          <tr class="bg-gray-50 text-gray-500 text-sm uppercase tracking-wider border-b border-gray-200">
            <th class="py-5 px-8 font-bold">Username</th>
            <th class="py-5 px-8 font-bold">Name</th>
            <th class="py-5 px-8 font-bold">Email</th>
            <th class="py-5 px-8 font-bold">Verification</th>
            <th class="py-5 px-8 font-bold text-right">Actions</th>
          </tr>
          </thead>
          <tbody class="text-gray-700 text-base">
          <%
            List<Customer> customerList = (List<Customer>) request.getAttribute("customerList");
            if (customerList != null && !customerList.isEmpty()) {
              for(Customer c : customerList) {
                boolean isVerified = c.getNic() != null && !c.getNic().equals("N/A") && !c.getNic().isEmpty() &&
                        c.getLicenseNumber() != null && !c.getLicenseNumber().equals("N/A") && !c.getLicenseNumber().isEmpty();
          %>
          <tr class="border-b border-gray-100 hover:bg-blue-50/40 transition-colors group">
            <td class="py-5 px-8 font-bold text-gray-900 text-lg">@<%= c.getUsername() %></td>
            <td class="py-5 px-8 font-semibold"><%= c.getName() %></td>
            <td class="py-5 px-8 text-gray-500"><%= c.getEmail() %></td>
            <td class="py-5 px-8">
              <% if (isVerified) { %>
              <span class="bg-green-50 text-green-700 border border-green-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max"><i class="ph-bold ph-check-circle text-lg"></i> Verified</span>
              <% } else { %>
              <span class="bg-red-50 text-red-700 border border-red-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max"><i class="ph-bold ph-warning text-lg"></i> Missing Docs</span>
              <% } %>
            </td>
            <td class="py-5 px-8 text-right">
              <div class="flex items-center justify-end gap-3 opacity-0 group-hover:opacity-100 transition-opacity">
                <a href="customers?action=edit&id=<%= c.getId() %>" class="text-blue-600 bg-blue-50 hover:bg-blue-600 hover:text-white p-2.5 rounded-xl transition-colors shadow-sm" title="Edit Profile">
                  <i class="ph-bold ph-pencil-simple text-xl"></i>
                </a>
                <a href="customers?action=delete&id=<%= c.getId() %>" onclick="return confirm('WARNING: Are you sure you want to delete @<%= c.getUsername() %>?');" class="text-red-500 bg-red-50 hover:bg-red-600 hover:text-white p-2.5 rounded-xl transition-colors shadow-sm" title="Delete Profile">
                  <i class="ph-bold ph-trash text-xl"></i>
                </a>
              </div>
            </td>
          </tr>
          <%
            }
          } else {
          %>
          <tr><td colspan="5" class="py-10 text-center text-gray-500 font-medium text-lg">No registered guests found in the system.</td></tr>
          <% } %>
          </tbody>
        </table>
      </div>
    </div>

    <div id="searchSection" class="fade-in hidden">
      <div class="mb-8">
        <h2 id="searchTitle" class="text-3xl font-extrabold text-gray-900 tracking-tight">Search & Verify Guest</h2>
        <p class="text-gray-500 text-lg mt-1 font-medium">Look up a guest by their username to update documents or view history.</p>
      </div>

      <form action="customers" method="GET" class="flex flex-col sm:flex-row gap-4 mb-10">
        <input type="hidden" name="action" value="search">
        <div class="relative flex-grow">
          <div class="absolute inset-y-0 left-0 pl-5 flex items-center pointer-events-none">
            <i class="ph-bold ph-magnifying-glass text-gray-400 text-xl"></i>
          </div>
          <input type="text" name="searchUsername" placeholder="Enter Username (e.g., johndoe123)..." class="w-full pl-14 pr-6 py-4 border border-gray-300 rounded-2xl focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500 transition-shadow text-gray-900 text-lg font-medium" required>
        </div>
        <button type="submit" class="bg-blue-600 hover:bg-blue-700 text-white font-bold px-10 py-4 rounded-2xl flex items-center justify-center gap-2 shadow-md transition-colors shrink-0 text-lg">
          Find Guest
        </button>
      </form>

      <%
        Customer foundCustomer = (Customer) request.getAttribute("foundCustomer");
        if (foundCustomer != null) {
          boolean foundIsVerified = foundCustomer.getNic() != null && !foundCustomer.getNic().equals("N/A") && !foundCustomer.getNic().isEmpty() &&
                  foundCustomer.getLicenseNumber() != null && !foundCustomer.getLicenseNumber().equals("N/A") && !foundCustomer.getLicenseNumber().isEmpty();
      %>
      <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">

        <div class="bg-gray-50 border-b border-gray-200 px-8 py-5 flex justify-between items-center">
          <div>
            <p class="text-sm font-bold text-gray-500 uppercase tracking-wider">Guest Profile</p>
            <h3 class="text-2xl font-extrabold text-gray-900 mt-1">@<%= foundCustomer.getUsername() %></h3>
          </div>
          <% if (foundIsVerified) { %>
          <span class="bg-green-50 text-green-700 border border-green-200 font-bold px-4 py-2 rounded-lg text-base flex items-center gap-2"><i class="ph-bold ph-shield-check text-xl"></i> Verified</span>
          <% } else { %>
          <span class="bg-red-50 text-red-700 border border-red-200 font-bold px-4 py-2 rounded-lg text-base flex items-center gap-2"><i class="ph-bold ph-warning-circle text-xl"></i> Missing Docs</span>
          <% } %>
        </div>

        <div class="p-8">
          <form action="customers" method="POST" class="space-y-8">
            <input type="hidden" name="action" id="manageAction" value="update">
            <input type="hidden" name="id" value="<%= foundCustomer.getId() %>">
            <input type="hidden" name="username" value="<%= foundCustomer.getUsername() %>">
            <input type="hidden" name="password" value="<%= foundCustomer.getPassword() %>">

            <div class="grid grid-cols-1 md:grid-cols-2 gap-10">
              <div class="space-y-5">
                <h4 class="font-bold text-gray-900 text-lg border-b border-gray-100 pb-3">Contact Information</h4>
                <div>
                  <label class="block text-gray-600 font-bold text-sm mb-2">Full Name</label>
                  <input type="text" name="name" value="<%= foundCustomer.getName() %>" class="w-full border border-gray-300 rounded-xl px-5 py-3 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500 transition-shadow text-base font-medium" required>
                </div>
                <div>
                  <label class="block text-gray-600 font-bold text-sm mb-2">Email Address</label>
                  <input type="email" name="email" value="<%= foundCustomer.getEmail() %>" class="w-full border border-gray-300 rounded-xl px-5 py-3 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500 transition-shadow text-base font-medium" required>
                </div>
                <div>
                  <label class="block text-gray-600 font-bold text-sm mb-2">Phone Number</label>
                  <input type="text" name="phone" value="<%= foundCustomer.getPhoneNumber() %>" class="w-full border border-gray-300 rounded-xl px-5 py-3 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500 transition-shadow text-base font-medium" required>
                </div>
              </div>

              <div class="space-y-5">
                <h4 class="font-bold text-gray-900 text-lg border-b border-gray-100 pb-3">Identity Documents</h4>
                <div>
                  <label class="block text-gray-600 font-bold text-sm mb-2">NIC Number</label>
                  <input type="text" name="nic" value="<%= foundCustomer.getNic() %>" class="w-full border border-gray-300 rounded-xl px-5 py-3 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500 transition-shadow text-base font-medium">
                  <p class="text-sm text-gray-400 mt-2 font-medium">Leave as N/A if missing.</p>
                </div>
                <div>
                  <label class="block text-gray-600 font-bold text-sm mb-2">Driver's License</label>
                  <input type="text" name="license" value="<%= foundCustomer.getLicenseNumber() %>" class="w-full border border-gray-300 rounded-xl px-5 py-3 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500 transition-shadow text-base font-medium">
                  <p class="text-sm text-gray-400 mt-2 font-medium">Leave as N/A if missing.</p>
                </div>
              </div>
            </div>

            <div class="flex flex-col sm:flex-row gap-4 pt-6">
              <button type="submit" onclick="document.getElementById('manageAction').value='update'" class="flex-1 bg-gray-900 hover:bg-black text-white font-bold py-4 px-6 rounded-xl flex justify-center items-center gap-2 shadow-md transition-colors text-lg">
                <i class="ph-bold ph-floppy-disk text-xl"></i> Save Changes
              </button>
              <button type="submit" onclick="document.getElementById('manageAction').value='delete'" class="sm:w-auto bg-red-50 hover:bg-red-100 text-red-600 border border-red-200 font-bold py-4 px-8 rounded-xl flex justify-center items-center gap-2 transition-colors text-lg">
                <i class="ph-bold ph-trash text-xl"></i> Delete Guest
              </button>
            </div>
          </form>
        </div>

        <div class="bg-gray-50 border-t border-gray-200 p-8">
          <h4 class="font-bold text-gray-900 text-lg mb-4 flex items-center gap-2">
            <i class="ph-bold ph-clock-counter-clockwise text-gray-500"></i> Rental History
          </h4>

          <div class="overflow-x-auto rounded-xl border border-gray-200 bg-white shadow-sm">
            <table class="w-full text-left border-collapse">
              <thead>
              <tr class="bg-gray-50 text-gray-500 text-sm uppercase tracking-wider border-b border-gray-200">
                <th class="py-4 px-6 font-bold">Transaction ID</th>
                <th class="py-4 px-6 font-bold">Vehicle</th>
                <th class="py-4 px-6 font-bold">Date</th>
                <th class="py-4 px-6 font-bold">Days</th>
                <th class="py-4 px-6 font-bold">Status</th>
              </tr>
              </thead>
              <tbody class="text-gray-700 text-base">
              <%
                List<Booking> customerBookings = (List<Booking>) request.getAttribute("customerBookings");
                if (customerBookings != null && !customerBookings.isEmpty()) {
                  for(Booking b : customerBookings) {
              %>
              <tr class="border-b border-gray-100 hover:bg-gray-50 transition">
                <td class="py-4 px-6 font-bold text-gray-900">#<%= b.getTransactionId() %></td>
                <td class="py-4 px-6"><%= b.getVehicleId() %></td>
                <td class="py-4 px-6 text-gray-500"><%= b.getRentalDate() %></td>
                <td class="py-4 px-6"><%= b.getDurationDays() %></td>
                <td class="py-4 px-6">
                  <% if ("Active".equalsIgnoreCase(b.getStatus())) { %>
                  <span class="bg-blue-50 text-blue-700 border border-blue-200 font-bold px-3 py-1 rounded-md text-sm">Active</span>
                  <% } else if ("Completed".equalsIgnoreCase(b.getStatus())) { %>
                  <span class="bg-green-50 text-green-700 border border-green-200 font-bold px-3 py-1 rounded-md text-sm">Completed</span>
                  <% } else if ("Pending".equalsIgnoreCase(b.getStatus())) { %>
                  <span class="bg-yellow-50 text-yellow-700 border border-yellow-200 font-bold px-3 py-1 rounded-md text-sm">Pending</span>
                  <% } else { %>
                  <span class="bg-gray-100 text-gray-700 border border-gray-200 font-bold px-3 py-1 rounded-md text-sm"><%= b.getStatus() %></span>
                  <% } %>
                </td>
              </tr>
              <%
                }
              } else {
              %>
              <tr><td colspan="5" class="py-8 text-center text-gray-500 font-medium text-lg">No past rentals found for this guest.</td></tr>
              <% } %>
              </tbody>
            </table>
          </div>
        </div>

      </div>
      <% } else if ("search".equals(request.getParameter("action"))) { %>
      <div class="bg-red-50 text-red-700 p-5 rounded-2xl border border-red-200 text-center font-bold text-lg flex items-center justify-center gap-3 shadow-sm">
        <i class="ph-fill ph-warning-circle text-2xl"></i> No customer found with that username.
      </div>
      <% } %>
    </div>

    <hr class="my-10 border-gray-200">

    <div class="flex justify-center flex-wrap gap-5">
      <button id="btn-list" onclick="switchView('list')" class="flex items-center gap-2 px-8 py-3.5 text-lg font-bold rounded-xl transition-colors shadow-sm bg-blue-50 border-2 border-blue-500 text-blue-700">
        <i class="ph-bold ph-list-dashes text-xl"></i> View Directory
      </button>
      <button id="btn-search" onclick="switchView('search')" class="flex items-center gap-2 px-8 py-3.5 text-lg font-bold rounded-xl transition-colors shadow-sm bg-white border-2 border-gray-200 text-gray-700 hover:bg-gray-50">
        <i class="ph-bold ph-magnifying-glass text-xl"></i> Find Guest
      </button>
    </div>
  </div>
</div>

<script>
  function switchView(view) {
    const listSection = document.getElementById('listSection');
    const searchSection = document.getElementById('searchSection');
    const btnList = document.getElementById('btn-list');
    const btnSearch = document.getElementById('btn-search');

    listSection.classList.add('hidden');
    searchSection.classList.add('hidden');

    const activeClass = "bg-blue-50 border-blue-500 text-blue-700 hover:bg-blue-50".split(" ");
    const inactiveClass = "bg-white border-gray-200 text-gray-700 hover:bg-gray-50".split(" ");

    btnList.classList.remove(...activeClass);
    btnList.classList.add(...inactiveClass);
    btnSearch.classList.remove(...activeClass);
    btnSearch.classList.add(...inactiveClass);

    if (view === 'list') {
      listSection.classList.remove('hidden');
      btnList.classList.remove(...inactiveClass);
      btnList.classList.add(...activeClass);
    } else {
      searchSection.classList.remove('hidden');
      btnSearch.classList.remove(...inactiveClass);
      btnSearch.classList.add(...activeClass);
    }
  }

  <% if (foundCustomer != null || "search".equals(request.getParameter("action"))) { %>
  switchView('search');
  <% } else { %>
  switchView('list');
  <% } %>
</script>

</body>
</html>