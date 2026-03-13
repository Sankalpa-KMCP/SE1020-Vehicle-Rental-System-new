<%@ page contentType="text/html; charset=UTF-8" %>
<%
  // Safely check if the user viewing this page is a logged-in Admin
  Boolean sessionAdmin = (Boolean) session.getAttribute("adminLoggedIn");
  boolean isAdmin = (sessionAdmin != null && sessionAdmin);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>DriveAway | Register Account</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');
    body { font-family: 'Inter', sans-serif; background-color: #F8F9FA; }
    .fade-in { animation: fadeIn 0.4s ease-in-out; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
  </style>
</head>
<body class="min-h-screen flex flex-col items-center justify-center p-6 relative">

<div class="absolute top-6 left-6">
  <a href='<%= isAdmin ? "admin-home.jsp" : "index.jsp" %>' class="flex items-center justify-center gap-2 bg-white px-5 py-2.5 rounded-xl font-bold text-gray-800 shadow-sm border border-gray-200 hover:bg-gray-50 transition">
    <i class="ph-bold ph-arrow-left text-xl"></i>
    <span>Back</span>
  </a>
</div>

<div class="bg-white w-full max-w-2xl rounded-[24px] shadow-lg border border-gray-100 p-8 sm:p-10 mt-12">

  <div class="text-center mb-8">
    <h2 class="text-3xl font-extrabold text-gray-900 tracking-tight">Create an Account</h2>
    <p class="text-gray-500 mt-2 font-medium">Join DriveAway to start booking vehicles instantly.</p>
  </div>

  <% if ("username_taken".equals(request.getParameter("error"))) { %>
  <div class="bg-red-50 border-l-4 border-red-500 p-4 mb-8 rounded-r-xl shadow-sm fade-in">
    <div class="flex items-center">
      <i class="ph-fill ph-warning-circle text-red-500 text-2xl mr-3"></i>
      <p class="text-red-700 font-bold">Registration Failed!</p>
    </div>
    <p class="text-red-600 mt-1 ml-9 text-sm font-medium">That Username is already taken. Please try another one.</p>
  </div>
  <% } %>

  <form action="customers" method="POST" class="space-y-5">
    <input type="hidden" name="action" value="register">

    <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
      <div>
        <label class="block text-gray-700 font-bold mb-2 text-sm">Full Name <span class="text-red-500">*</span></label>
        <input type="text" name="name" required placeholder="John Doe"
               class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] focus:border-[#0D6EFD] outline-none transition-all font-medium text-gray-800">
      </div>

      <div>
        <label class="block text-gray-700 font-bold mb-2 text-sm">Username <span class="text-red-500">*</span></label>
        <div class="relative">
          <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
            <i class="ph-bold ph-at text-gray-400 text-lg"></i>
          </div>
          <input type="text" name="username" required placeholder="johndoe123"
                 class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] focus:border-[#0D6EFD] outline-none transition-all font-medium text-gray-800">
        </div>
      </div>

      <div>
        <label class="block text-gray-700 font-bold mb-2 text-sm">Email Address <span class="text-red-500">*</span></label>
        <input type="email" name="email" required placeholder="john@example.com"
               class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] focus:border-[#0D6EFD] outline-none transition-all font-medium text-gray-800">
      </div>

      <div>
        <label class="block text-gray-700 font-bold mb-2 text-sm">Phone Number <span class="text-red-500">*</span></label>
        <input type="tel" name="phone" pattern="[0-9]{10}" title="Please enter exactly 10 digits" required placeholder="0712345678"
               class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] focus:border-[#0D6EFD] outline-none transition-all font-medium text-gray-800">
      </div>
    </div>

    <div class="mt-4">
      <label class="block text-gray-700 font-bold mb-2 text-sm">Password <span class="text-red-500">*</span></label>
      <div class="relative">
        <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
          <i class="ph-bold ph-lock-key text-gray-400 text-lg"></i>
        </div>
        <input type="password" name="password" required placeholder="••••••••"
               class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] focus:border-[#0D6EFD] outline-none transition-all font-medium text-gray-800">
      </div>
    </div>

    <hr class="border-gray-100 my-6">

    <div class="bg-blue-50 p-4 rounded-xl border border-blue-100 mb-6">
      <h4 class="text-blue-800 font-bold mb-3 text-sm uppercase flex items-center gap-2"><i class="ph-bold ph-shield-check text-lg"></i> Verification Documents (Optional)</h4>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
        <div>
          <label class="block text-gray-700 font-bold mb-1 text-sm">NIC Number</label>
          <input type="text" name="nic" placeholder="e.g. 199912345678"
                 class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] focus:border-[#0D6EFD] outline-none transition-all text-sm font-medium">
        </div>
        <div>
          <label class="block text-gray-700 font-bold mb-1 text-sm">Driver's License</label>
          <input type="text" name="license" placeholder="e.g. B1234567"
                 class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] focus:border-[#0D6EFD] outline-none transition-all text-sm font-medium">
        </div>
      </div>
    </div>

    <button type="submit" name="action" value="register" class="w-full bg-[#0D6EFD] text-white font-bold py-4 rounded-xl hover:bg-blue-700 transition-colors shadow-md flex justify-center items-center gap-2 text-xl mt-6">
      Create Account <i class="ph-bold ph-user-plus"></i>
    </button>
  </form>

  <% if (!isAdmin) { %>
  <div class="mt-8 pt-6 border-t border-gray-100 text-center">
    <p class="text-gray-600 font-medium text-md">
      Already have an account?
      <a href="index.jsp" class="text-[#0D6EFD] font-bold hover:text-blue-800 hover:underline ml-1">Sign In</a>
    </p>
  </div>
  <% } %>

</div>

</body>
</html>