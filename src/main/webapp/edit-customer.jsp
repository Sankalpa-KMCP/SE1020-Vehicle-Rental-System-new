<%@ page import="com.example.vehiclerentalsystem01.models.Customer" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
  Customer customer = (Customer) request.getAttribute("customer");
  Boolean customerModeAttr = (Boolean) request.getAttribute("isCustomerMode");
  boolean isCustomerMode = customerModeAttr != null && customerModeAttr;

  if (customer == null) {
    Boolean customerLoggedIn = (Boolean) session.getAttribute("customerLoggedIn");
    if (customerLoggedIn != null && customerLoggedIn) {
      response.sendRedirect("customers?action=editSelf");
    } else {
      response.sendRedirect("login.jsp");
    }
    return;
  }

  String nicValue = customer.getNic() != null && !"N/A".equalsIgnoreCase(customer.getNic()) ? customer.getNic() : "";
  String licenseValue = customer.getLicenseNumber() != null && !"N/A".equalsIgnoreCase(customer.getLicenseNumber()) ? customer.getLicenseNumber() : "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>DriveAway | Profile Settings</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <style>
    body { font-family: 'Inter', sans-serif; background: radial-gradient(circle at top right, #dbeafe 0%, #f8fafc 45%, #f1f5f9 100%); }
    .fade-in { animation: fadeIn 0.4s ease-out; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
    .card-hover { transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); }
    .card-hover:hover { transform: translateY(-5px); box-shadow: 0 20px 40px rgba(13, 110, 253, 0.15); }
  </style>
</head>
<body class="min-h-screen flex items-center justify-center p-6 text-gray-800">

<div class="max-w-2xl w-full bg-white rounded-3xl shadow-xl border border-slate-200 p-8 sm:p-10 fade-in relative overflow-hidden card-hover">

  <div class="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600"></div>

  <div class="flex items-center gap-4 mb-8 border-b border-slate-100 pb-6">
    <div class="w-16 h-16 bg-gradient-to-br from-blue-500 to-indigo-600 text-white rounded-2xl flex items-center justify-center shadow-lg">
      <i class="ph-fill ph-user-circle-gear text-4xl"></i>
    </div>
    <div>
      <h2 class="text-3xl font-extrabold text-gray-900 tracking-tight"><%= isCustomerMode ? "My Profile Settings" : "Edit Guest Profile" %></h2>
      <p class="text-gray-500 font-medium mt-1">Update contact details and identity verification documents.</p>
    </div>
  </div>

  <% if ("updated".equals(request.getParameter("msg"))) { %>
  <div class="mb-6 bg-green-50 p-4 rounded-xl border border-green-200 text-green-700 font-semibold flex items-center gap-2 shadow-sm">
    <i class="ph-bold ph-check-circle text-xl"></i> Profile updated successfully.
  </div>
  <% } %>

  <% if ("missing_fields".equals(request.getParameter("error"))) { %>
  <div class="mb-6 bg-red-50 p-4 rounded-xl border border-red-200 text-red-700 font-semibold flex items-center gap-2 shadow-sm">
    <i class="ph-bold ph-warning-circle text-xl"></i> Please fill in all required fields.
  </div>
  <% } %>

  <form action="customers" method="post" class="space-y-6">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="id" value="<%= customer.getId() %>">

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">

      <div class="md:col-span-2 bg-gradient-to-r from-slate-50 to-blue-50 p-5 rounded-xl border border-slate-200 flex items-center justify-between">
        <div>
          <p class="text-xs font-bold text-gray-500 uppercase tracking-wider mb-1">Account Username</p>
          <p class="text-lg font-bold text-gray-900">@<%= customer.getUsername() %></p>
          <input type="hidden" name="username" value="<%= customer.getUsername() %>">
        </div>
        <div class="bg-white p-2 rounded-lg shadow-sm">
          <i class="ph-fill ph-lock-key text-2xl text-blue-500" title="Username cannot be changed"></i>
        </div>
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-700 mb-2">Full Name</label>
        <input type="text" name="name" value="<%= customer.getName() %>" required class="w-full border border-slate-300 rounded-xl p-3.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none font-medium text-gray-800 transition-shadow">
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-700 mb-2">Email Address</label>
        <input type="email" name="email" value="<%= customer.getEmail() %>" required class="w-full border border-slate-300 rounded-xl p-3.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none font-medium text-gray-800 transition-shadow">
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-700 mb-2">Phone Number</label>
        <input type="text" name="phone" value="<%= customer.getPhoneNumber() %>" required class="w-full border border-slate-300 rounded-xl p-3.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none font-medium text-gray-800 transition-shadow">
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-700 mb-2">Account Password</label>
        <input type="password" name="password" value="<%= customer.getPassword() %>" required class="w-full border border-slate-300 rounded-xl p-3.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none font-medium text-gray-800 transition-shadow">
      </div>

      <div class="md:col-span-2 pt-4">
        <h4 class="text-sm font-bold text-gray-500 uppercase tracking-wider mb-4 border-b border-slate-100 pb-2 flex items-center gap-2">
          <i class="ph-fill ph-shield-check text-blue-500"></i> Verification Documents
        </h4>
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-700 mb-2">NIC Number</label>
        <input type="text" name="nic" value="<%= nicValue %>" placeholder="Leave blank if none" class="w-full border border-slate-300 rounded-xl p-3.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none font-medium text-gray-800 transition-shadow">
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-700 mb-2">Driver's License</label>
        <input type="text" name="license" value="<%= licenseValue %>" placeholder="Leave blank if none" class="w-full border border-slate-300 rounded-xl p-3.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none font-medium text-gray-800 transition-shadow">
      </div>

    </div>

    <div class="pt-6 flex gap-4 mt-8 border-t border-slate-100">
      <a href="<%= isCustomerMode ? "customer-home.jsp" : "customers?action=view_directory" %>" class="flex-1 bg-white border-2 border-slate-200 text-gray-700 font-bold py-4 px-4 rounded-xl hover:bg-slate-50 hover:border-slate-300 transition-all text-center flex items-center justify-center gap-2 text-lg shadow-sm">
        <i class="ph-bold ph-x"></i> Cancel
      </a>
      <button type="submit" class="flex-[2] bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-bold py-4 px-4 rounded-xl hover:from-blue-700 hover:to-indigo-700 transition-all flex justify-center items-center gap-2 shadow-lg shadow-blue-500/30 text-lg">
        <i class="ph-bold ph-floppy-disk"></i> Save Changes
      </button>
    </div>
  </form>
</div>

</body>
</html>