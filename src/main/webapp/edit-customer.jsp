<%@ page import="com.example.vehiclerentalsystem01.models.Customer" %>
<%@ page import="com.example.vehiclerentalsystem01.dao.CustomerDAO" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
  Customer customer = (Customer) request.getAttribute("customer");
  boolean isCustomerMode = false;

  // If the controller didn't send a customer (Admin mode), check if a Customer is editing themselves
  if (customer == null) {
    Boolean customerLoggedIn = (Boolean) session.getAttribute("customerLoggedIn");
    if (customerLoggedIn != null && customerLoggedIn) {
      String sessionUser = (String) session.getAttribute("username");
      CustomerDAO dao = new CustomerDAO();
      for(Customer c : dao.getAllCustomers()) {
        if(c.getUsername().equals(sessionUser)) {
          customer = c;
          isCustomerMode = true;
          break;
        }
      }
    }
  }

  // If absolutely no profile is found, kick back to login
  if (customer == null) {
    response.sendRedirect("login.jsp");
    return;
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>DriveAway | Profile Settings</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');
    body { font-family: 'Inter', sans-serif; background-color: #F4F7F6; }
    .fade-in { animation: fadeIn 0.4s ease-out; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
  </style>
</head>
<body class="min-h-screen flex items-center justify-center p-6 text-gray-800">

<div class="max-w-2xl w-full bg-white rounded-3xl shadow-xl border border-gray-100 p-8 sm:p-10 fade-in relative overflow-hidden">

  <div class="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-blue-500 to-cyan-400"></div>

  <div class="flex items-center gap-4 mb-8 border-b border-gray-100 pb-6">
    <div class="w-16 h-16 bg-blue-50 text-blue-600 rounded-2xl flex items-center justify-center shadow-inner">
      <i class="ph-fill ph-user-circle-gear text-4xl"></i>
    </div>
    <div>
      <h2 class="text-3xl font-extrabold text-gray-900 tracking-tight"><%= isCustomerMode ? "My Profile Settings" : "Edit Guest Profile" %></h2>
      <p class="text-gray-500 font-medium mt-1">Update contact details and identity verification documents.</p>
    </div>
  </div>

  <form action="customers" method="post" class="space-y-6">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="id" value="<%= customer.getId() %>">

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">

      <div class="md:col-span-2 bg-gray-50 p-5 rounded-xl border border-gray-200 flex items-center justify-between">
        <div>
          <p class="text-xs font-bold text-gray-500 uppercase tracking-wider mb-1">Account Username</p>
          <p class="text-lg font-bold text-gray-900">@<%= customer.getUsername() %></p>
          <input type="hidden" name="username" value="<%= customer.getUsername() %>">
        </div>
        <i class="ph-fill ph-lock-key text-2xl text-gray-400" title="Username cannot be changed"></i>
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-700 mb-2">Full Name</label>
        <input type="text" name="name" value="<%= customer.getName() %>" required class="w-full border border-gray-300 rounded-xl p-3.5 focus:ring-2 focus:ring-blue-500 outline-none font-medium text-gray-800 transition-shadow">
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-700 mb-2">Email Address</label>
        <input type="email" name="email" value="<%= customer.getEmail() %>" required class="w-full border border-gray-300 rounded-xl p-3.5 focus:ring-2 focus:ring-blue-500 outline-none font-medium text-gray-800 transition-shadow">
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-700 mb-2">Phone Number</label>
        <input type="text" name="phone" value="<%= customer.getPhoneNumber() %>" required class="w-full border border-gray-300 rounded-xl p-3.5 focus:ring-2 focus:ring-blue-500 outline-none font-medium text-gray-800 transition-shadow">
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-700 mb-2">Account Password</label>
        <input type="password" name="password" value="<%= customer.getPassword() %>" required class="w-full border border-gray-300 rounded-xl p-3.5 focus:ring-2 focus:ring-blue-500 outline-none font-medium text-gray-800 transition-shadow">
      </div>

      <div class="md:col-span-2 pt-4">
        <h4 class="text-sm font-bold text-gray-500 uppercase tracking-wider mb-4 border-b border-gray-100 pb-2">Verification Documents</h4>
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-700 mb-2">NIC Number</label>
        <input type="text" name="nic" value="<%= customer.getNic().equals("N/A") ? "" : customer.getNic() %>" placeholder="Leave blank if none" class="w-full border border-gray-300 rounded-xl p-3.5 focus:ring-2 focus:ring-blue-500 outline-none font-medium text-gray-800 transition-shadow">
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-700 mb-2">Driver's License</label>
        <input type="text" name="license" value="<%= customer.getLicenseNumber().equals("N/A") ? "" : customer.getLicenseNumber() %>" placeholder="Leave blank if none" class="w-full border border-gray-300 rounded-xl p-3.5 focus:ring-2 focus:ring-blue-500 outline-none font-medium text-gray-800 transition-shadow">
      </div>

    </div>

    <div class="pt-6 flex gap-4 mt-8 border-t border-gray-100">
      <a href="<%= isCustomerMode ? "customer-home.jsp" : "customers?action=view_directory" %>" class="flex-1 bg-white border-2 border-gray-200 text-gray-700 font-bold py-4 px-4 rounded-xl hover:bg-gray-50 hover:border-gray-300 transition-all text-center flex items-center justify-center gap-2 text-lg shadow-sm">
        <i class="ph-bold ph-x"></i> Cancel
      </a>
      <button type="submit" class="flex-[2] bg-blue-600 text-white font-bold py-4 px-4 rounded-xl hover:bg-blue-700 transition-colors flex justify-center items-center gap-2 shadow-md text-lg">
        <i class="ph-bold ph-floppy-disk"></i> Save Changes
      </button>
    </div>
  </form>
</div>

</body>
</html>