<%@ page import="com.example.vehiclerentalsystem01.models.Customer" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
  Customer customer = (Customer) request.getAttribute("customer");
  if (customer == null) {
    response.sendRedirect("customers?action=list");
    return;
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Edit Customer Profile</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
</head>
<body class="bg-gray-50 flex h-screen items-center justify-center p-4">
<div class="max-w-lg w-full bg-white p-8 rounded-xl shadow-lg border border-gray-100">
  <div class="flex items-center gap-3 mb-6 border-b pb-4">
    <i class="ph-fill ph-user-circle-gear text-3xl text-blue-600"></i>
    <h2 class="text-2xl font-bold text-gray-800">Update Customer Profile</h2>
  </div>

  <form action="customers" method="post" class="space-y-4">
    <!-- Hidden field to tell the Servlet this is an Update -->
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="id" value="<%= customer.getId() %>">

    <div>
      <label class="block text-sm font-semibold text-gray-700 mb-1">Full Name</label>
      <input type="text" name="name" value="<%= customer.getName() %>" required class="w-full border border-gray-300 rounded-lg shadow-sm p-3 focus:ring-2 focus:ring-blue-500 outline-none">
    </div>
    <div>
      <label class="block text-sm font-semibold text-gray-700 mb-1">Email Address</label>
      <input type="email" name="email" value="<%= customer.getEmail() %>" required class="w-full border border-gray-300 rounded-lg shadow-sm p-3 focus:ring-2 focus:ring-blue-500 outline-none">
    </div>
    <div>
      <label class="block text-sm font-semibold text-gray-700 mb-1">Driver's License Number <span class="text-gray-400 font-normal italic">(Optional)</span></label>
      <input type="text" name="license" value="<%= customer.getLicenseNumber().equals("Not Provided") ? "" : customer.getLicenseNumber() %>" class="w-full border border-gray-300 rounded-lg shadow-sm p-3 focus:ring-2 focus:ring-blue-500 outline-none">
    </div>
    <div>
      <label class="block text-sm font-semibold text-gray-700 mb-1">Phone Number</label>
      <input type="text" name="phone" value="<%= customer.getPhoneNumber() %>" required class="w-full border border-gray-300 rounded-lg shadow-sm p-3 focus:ring-2 focus:ring-blue-500 outline-none">
    </div>

    <div class="pt-4 flex gap-3">
      <a href="customers?action=list" class="w-1/3 bg-gray-100 text-gray-700 font-bold py-3 px-4 rounded-lg hover:bg-gray-200 transition text-center">Cancel</a>
      <button type="submit" class="w-2/3 bg-blue-600 text-white font-bold py-3 px-4 rounded-lg hover:bg-blue-700 transition flex justify-center items-center gap-2 shadow-md">
        <i class="ph-bold ph-floppy-disk"></i> Save Changes
      </button>
    </div>
  </form>
</div>
</body>
</html>