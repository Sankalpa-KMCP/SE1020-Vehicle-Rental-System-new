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

  List<Customer> customerList = (List<Customer>) request.getAttribute("customerList");
  int totalGuests = customerList != null ? customerList.size() : 0;
  int verifiedGuests = 0;
  int unverifiedGuests = 0;
  if (customerList != null) {
    for (Customer c : customerList) {
      if (c.isVerified()) verifiedGuests++;
      else unverifiedGuests++;
    }
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Customer Management - DriveAway</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <style>
    body { font-family: 'Inter', sans-serif; background: radial-gradient(circle at top right, #dbeafe 0%, #f8fafc 45%, #f1f5f9 100%); }
    .fade-in { animation: fadeIn 0.4s ease-out; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
    .soft-card { box-shadow: 0 10px 35px rgba(15, 23, 42, 0.06); }
    .card-hover { transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); }
    .card-hover:hover { transform: translateY(-5px); box-shadow: 0 20px 40px rgba(13, 110, 253, 0.15); }
  </style>
</head>
<body class="min-h-screen text-gray-800 flex flex-col">

<nav class="bg-gradient-to-r from-blue-900 via-indigo-900 to-purple-900 border-b border-white/10 sticky top-0 z-20 shadow-lg backdrop-blur">
  <div class="max-w-screen-xl mx-auto px-8 h-20 flex items-center justify-between">
    <a href="admin-home.jsp" class="flex items-center gap-2 text-white">
      <div class="bg-white/10 p-2.5 rounded-xl backdrop-blur-sm">
        <i class="ph-fill ph-users-three text-2xl text-blue-300"></i>
      </div>
      <span class="text-2xl font-extrabold tracking-tight">Customer Hub</span>
    </a>

    <div class="flex items-center gap-4">
      <a href="admin-home.jsp" class="text-blue-200 hover:text-white font-semibold transition-colors flex items-center gap-2">
        <i class="ph-bold ph-house"></i> Admin Hub
      </a>
      <a href="login.jsp" class="text-white hover:text-blue-200 font-bold border border-white/20 bg-white/10 hover:bg-white/20 px-5 py-2.5 rounded-xl transition-all flex items-center gap-2 shadow-sm">
        Logout <i class="ph-bold ph-sign-out text-lg"></i>
      </a>
    </div>
  </div>
</nav>

<main class="flex-grow max-w-screen-xl mx-auto w-full px-8 py-10 fade-in">
  <div class="bg-gradient-to-br from-blue-600 via-indigo-600 to-purple-700 rounded-3xl p-10 sm:p-12 text-white shadow-xl mb-10 relative overflow-hidden">
    <div class="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10 mix-blend-overlay pointer-events-none"></div>
    <div class="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
      <div>
        <h1 class="text-4xl sm:text-5xl font-extrabold mb-4 tracking-tight">Customer Directory</h1>
        <p class="text-blue-100 text-lg font-medium max-w-2xl leading-relaxed">Manage customer records, verification status, and booking history from one clean workspace.</p>
      </div>

      <div class="flex gap-3 bg-white/10 p-4 rounded-2xl backdrop-blur-sm border border-white/20">
        <div class="text-center px-4 border-r border-white/20">
          <p class="text-blue-100 text-xs font-bold uppercase tracking-wider mb-1">Total</p>
          <p class="text-white font-extrabold"><%= totalGuests %></p>
        </div>
        <div class="text-center px-4 border-r border-white/20">
          <p class="text-blue-100 text-xs font-bold uppercase tracking-wider mb-1">Verified</p>
          <p class="text-white font-extrabold"><%= verifiedGuests %></p>
        </div>
        <div class="text-center px-4">
          <p class="text-blue-100 text-xs font-bold uppercase tracking-wider mb-1">Pending</p>
          <p class="text-white font-extrabold"><%= unverifiedGuests %></p>
        </div>
      </div>
    </div>
    <i class="ph-fill ph-users-three absolute -right-10 -bottom-20 text-[300px] text-white opacity-10 transform -rotate-12 pointer-events-none"></i>
  </div>

  <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6 mb-10">
    <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card card-hover">
      <div class="flex items-center justify-between mb-4">
        <div class="bg-blue-50 p-3 rounded-xl">
          <i class="ph-fill ph-users-three text-2xl text-blue-600"></i>
        </div>
        <span class="text-xs font-bold text-blue-600 bg-blue-50 px-2 py-1 rounded-lg">TOTAL</span>
      </div>
      <p class="text-3xl font-extrabold text-gray-900"><%= totalGuests %></p>
      <p class="text-sm text-gray-500 mt-2">Registered customers</p>
    </div>

    <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card card-hover">
      <div class="flex items-center justify-between mb-4">
        <div class="bg-green-50 p-3 rounded-xl">
          <i class="ph-fill ph-shield-check text-2xl text-green-600"></i>
        </div>
        <span class="text-xs font-bold text-green-600 bg-green-50 px-2 py-1 rounded-lg">VERIFIED</span>
      </div>
      <p class="text-3xl font-extrabold text-gray-900"><%= verifiedGuests %></p>
      <p class="text-sm text-gray-500 mt-2">With complete docs</p>
    </div>

    <div class="bg-white rounded-2xl p-6 border border-slate-100 soft-card card-hover">
      <div class="flex items-center justify-between mb-4">
        <div class="bg-amber-50 p-3 rounded-xl">
          <i class="ph-fill ph-warning text-2xl text-amber-600"></i>
        </div>
        <span class="text-xs font-bold text-amber-600 bg-amber-50 px-2 py-1 rounded-lg">PENDING</span>
      </div>
      <p class="text-3xl font-extrabold text-gray-900"><%= unverifiedGuests %></p>
      <p class="text-sm text-gray-500 mt-2">Missing documents</p>
    </div>

    <div class="bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl p-6 text-white shadow-lg card-hover">
      <div class="flex items-center justify-between mb-4">
        <div class="bg-white/20 p-3 rounded-xl backdrop-blur-sm">
          <i class="ph-fill ph-user-plus text-2xl text-white"></i>
        </div>
        <span class="text-xs font-bold text-white bg-white/20 px-2 py-1 rounded-lg backdrop-blur-sm">ACTION</span>
      </div>
      <p class="text-lg font-extrabold mb-2">Add New</p>
      <p class="text-sm text-blue-100 mb-4">Register a customer</p>
      <a href="register.jsp" class="inline-flex items-center gap-2 bg-white text-blue-600 font-bold px-4 py-2 rounded-xl hover:bg-blue-50 transition-colors text-sm">
        <i class="ph-bold ph-plus"></i> Register
      </a>
    </div>
  </div>

  <div class="w-full max-w-screen-xl mx-auto pb-16">
    <div class="bg-white border border-slate-200 rounded-3xl p-10 soft-card shadow-lg">

      <% if ("created".equals(request.getParameter("msg"))) { %>
      <div class="mb-8 bg-green-50 text-green-700 p-4 rounded-xl border border-green-200 font-semibold flex items-center gap-2">
        <i class="ph-bold ph-check-circle text-xl"></i> Customer registered successfully.
      </div>
      <% } else if ("updated".equals(request.getParameter("msg"))) { %>
      <div class="mb-8 bg-green-50 text-green-700 p-4 rounded-xl border border-green-200 font-semibold flex items-center gap-2">
        <i class="ph-bold ph-check-circle text-xl"></i> Customer profile updated successfully.
      </div>
      <% } else if ("deleted".equals(request.getParameter("msg"))) { %>
      <div class="mb-8 bg-green-50 text-green-700 p-4 rounded-xl border border-green-200 font-semibold flex items-center gap-2">
        <i class="ph-bold ph-check-circle text-xl"></i> Customer deleted successfully.
      </div>
      <% } else if ("not_found".equals(request.getParameter("error"))) { %>
      <div class="mb-8 bg-red-50 text-red-700 p-4 rounded-xl border border-red-200 font-semibold flex items-center gap-2">
        <i class="ph-bold ph-warning-circle text-xl"></i> Customer not found.
      </div>
      <% } %>

      <div id="listSection" class="fade-in block">
        <div class="flex justify-between items-end mb-8">
          <div>
            <h2 class="text-3xl font-extrabold text-gray-900 tracking-tight">Guest Directory</h2>
            <p class="text-gray-500 text-lg mt-1 font-medium">Manage and verify your registered rental customers.</p>
          </div>
        </div>

        <div class="overflow-x-auto rounded-2xl border border-slate-200 bg-white soft-card">
          <table class="w-full text-left border-collapse">
            <thead>
            <tr class="bg-slate-50 text-gray-500 text-sm uppercase tracking-wider border-b border-slate-200">
              <th class="py-4 px-6 font-bold">Username</th>
              <th class="py-4 px-6 font-bold">Name</th>
              <th class="py-4 px-6 font-bold">Email</th>
              <th class="py-4 px-6 font-bold">Verification</th>
              <th class="py-4 px-6 font-bold text-right">Actions</th>
            </tr>
            </thead>
            <tbody class="text-gray-700 text-base">
            <%
              if (customerList != null && !customerList.isEmpty()) {
                for(Customer c : customerList) {
                  boolean isVerified = c.isVerified();
            %>
            <tr class="border-b border-slate-100 hover:bg-blue-50/50 transition-colors group">
              <td class="py-4 px-6 font-bold text-gray-900 text-lg">@<%= c.getUsername() %></td>
              <td class="py-4 px-6 font-semibold"><%= c.getName() %></td>
              <td class="py-4 px-6 text-gray-500"><%= c.getEmail() %></td>
              <td class="py-4 px-6">
                <% if (isVerified) { %>
                <span class="bg-green-50 text-green-700 border border-green-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max"><i class="ph-bold ph-check-circle text-lg"></i> Verified</span>
                <% } else { %>
                <span class="bg-amber-50 text-amber-700 border border-amber-200 font-bold px-3 py-1.5 rounded-lg text-sm flex items-center gap-1.5 w-max"><i class="ph-bold ph-warning text-lg"></i> Pending</span>
                <% } %>
              </td>
              <td class="py-4 px-6 text-right">
                <div class="flex items-center justify-end gap-2">
                  <a href="customers?action=edit&id=<%= c.getId() %>" class="text-blue-600 bg-blue-50 hover:bg-blue-600 hover:text-white p-2 rounded-xl transition-colors shadow-sm" title="Edit Profile">
                    <i class="ph-bold ph-pencil-simple text-lg"></i>
                  </a>
                  <a href="customers?action=delete&id=<%= c.getId() %>" onclick="return confirm('WARNING: Are you sure you want to delete @<%= c.getUsername() %>?');" class="text-red-500 bg-red-50 hover:bg-red-600 hover:text-white p-2 rounded-xl transition-colors shadow-sm" title="Delete Profile">
                    <i class="ph-bold ph-trash text-lg"></i>
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
            <input type="text" name="searchUsername" value="<%= request.getParameter("searchUsername") != null ? request.getParameter("searchUsername") : "" %>" placeholder="Enter Username (e.g., johndoe123)..." class="w-full pl-14 pr-6 py-4 border border-gray-300 rounded-2xl focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500 transition-shadow text-gray-900 text-lg font-medium" required>
          </div>
          <button type="submit" class="bg-blue-600 hover:bg-blue-700 text-white font-bold px-10 py-4 rounded-2xl flex items-center justify-center gap-2 shadow-md transition-colors shrink-0 text-lg">
            Find Guest
          </button>
        </form>

        <%
          Customer foundCustomer = (Customer) request.getAttribute("foundCustomer");
          if (foundCustomer != null) {
            boolean foundIsVerified = foundCustomer.isVerified();
        %>
        <div class="bg-white rounded-2xl border border-gray-200 soft-card overflow-hidden">

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

            <div class="overflow-x-auto rounded-xl border border-gray-200 bg-white soft-card">
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

      <hr class="my-10 border-slate-200">

      <div class="flex justify-center flex-wrap gap-4">
        <button id="btn-list" onclick="switchView('list')" class="flex items-center gap-2 px-8 py-3.5 text-lg font-bold rounded-xl transition-colors shadow-sm bg-blue-50 border-2 border-blue-500 text-blue-700 hover:bg-blue-100">
          <i class="ph-bold ph-list-dashes text-xl"></i> View Directory
        </button>
        <button id="btn-search" onclick="switchView('search')" class="flex items-center gap-2 px-8 py-3.5 text-lg font-bold rounded-xl transition-colors shadow-sm bg-white border-2 border-slate-200 text-gray-700 hover:bg-slate-50">
          <i class="ph-bold ph-magnifying-glass text-xl"></i> Find Guest
        </button>
      </div>
    </div>
  </div>
</main>

<script>
  function switchView(view) {
    const listSection = document.getElementById('listSection');
    const searchSection = document.getElementById('searchSection');
    const btnList = document.getElementById('btn-list');
    const btnSearch = document.getElementById('btn-search');

    listSection.classList.add('hidden');
    searchSection.classList.add('hidden');

    const activeClass = "bg-blue-50 border-blue-500 text-blue-700 hover:bg-blue-100".split(" ");
    const inactiveClass = "bg-white border-slate-200 text-gray-700 hover:bg-slate-50".split(" ");

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