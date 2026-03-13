<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Secure Staff Portal</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
</head>
<body class="bg-[#111111] min-h-screen flex items-center justify-center p-4">

<!-- White Main Card -->
<div class="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden">

  <!-- Header Area -->
  <div class="pt-10 pb-6 px-8 text-center">
    <!-- Simulated Shield with Keyhole Icon -->
    <div class="relative inline-block mb-4">
      <i class="ph-fill ph-shield text-[#FFC107] text-6xl drop-shadow-sm"></i>
      <i class="ph-fill ph-keyhole text-white text-2xl absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 mt-[2px]"></i>
    </div>
    <h2 class="text-3xl font-bold text-gray-900 tracking-tight">Secure Staff Portal</h2>
    <p class="text-gray-500 text-sm mt-2">Authorized Personnel Only</p>
  </div>

  <!-- Error/Success Messages -->
  <% if ("invalid".equals(request.getParameter("error"))) { %>
  <div class="mx-8 mb-4 bg-red-50 text-red-600 text-sm p-3 rounded-lg border border-red-100 text-center font-semibold">
    Invalid Username or Password. Access Denied.
  </div>
  <% } else if ("requested".equals(request.getParameter("success"))) { %>
  <div class="mx-8 mb-4 bg-green-50 text-green-700 text-sm p-3 rounded-lg border border-green-100 text-center font-semibold">
    Request sent to Master Admin successfully.
  </div>
  <% } %>

  <!-- Tabs Navigation -->
  <div class="flex border-b border-gray-200">
    <button id="btnLoginTab" onclick="switchTab('login')" class="w-1/2 py-4 text-center font-bold text-gray-900 border-b-[3px] border-[#FFC107] bg-white transition-colors">
      Staff Login
    </button>
    <button id="btnRequestTab" onclick="switchTab('request')" class="w-1/2 py-4 text-center font-bold text-gray-500 border-b-[3px] border-transparent hover:text-gray-700 bg-gray-50 hover:bg-white transition-colors">
      Request Access
    </button>
  </div>

  <div class="p-8">
    <!-- TAB 1: LOGIN FORM -->
    <form id="loginForm" action="auth" method="post" class="space-y-5 block">
      <input type="hidden" name="action" value="login">
      <div>
        <label class="block text-sm font-bold text-slate-500 mb-1.5">Admin Username</label>
        <input type="text" name="username" required class="w-full border border-gray-300 rounded-lg p-3 text-gray-900 focus:ring-2 focus:ring-[#FFC107] focus:border-[#FFC107] outline-none transition-all">
      </div>
      <div>
        <label class="block text-sm font-bold text-slate-500 mb-1.5">Password</label>
        <input type="password" name="password" required class="w-full border border-gray-300 rounded-lg p-3 text-gray-900 focus:ring-2 focus:ring-[#FFC107] focus:border-[#FFC107] outline-none transition-all">
      </div>
      <button type="submit" class="w-full bg-[#1A202C] text-white font-bold py-3.5 rounded-lg hover:bg-black transition-colors flex items-center justify-center gap-2 mt-2">
        Secure Login <i class="ph-bold ph-sign-in text-lg"></i>
      </button>
    </form>

    <!-- TAB 2: REQUEST ACCESS FORM (Hidden by default) -->
    <form id="requestForm" action="auth" method="post" class="space-y-5 hidden">
      <input type="hidden" name="action" value="request_access">

      <!-- Yellow Info Box -->
      <div class="bg-[#FFF8E1] border border-[#FFE082] rounded-lg p-3 flex gap-2 text-[#8D6E18] text-sm font-medium">
        <i class="ph-fill ph-info text-lg mt-0.5"></i>
        <p>New accounts require Master Admin approval.</p>
      </div>

      <div>
        <label class="block text-sm font-bold text-slate-500 mb-1.5">Choose Username</label>
        <input type="text" name="new_username" required class="w-full border border-gray-300 rounded-lg p-3 text-gray-900 focus:ring-2 focus:ring-[#FFC107] focus:border-[#FFC107] outline-none transition-all">
      </div>
      <div>
        <label class="block text-sm font-bold text-slate-500 mb-1.5">Create Password</label>
        <input type="password" name="new_password" required class="w-full border border-gray-300 rounded-lg p-3 text-gray-900 focus:ring-2 focus:ring-[#FFC107] focus:border-[#FFC107] outline-none transition-all">
      </div>
      <button type="submit" class="w-full bg-[#FFC107] text-gray-900 font-extrabold py-3.5 rounded-lg hover:bg-[#FFB300] transition-colors mt-2">
        Submit Request
      </button>
    </form>
  </div>

  <!-- Footer Area -->
  <div class="bg-gray-50 p-5 text-center border-t border-gray-100">
    <a href="index.jsp" class="text-slate-500 hover:text-slate-800 text-sm font-medium flex items-center justify-center gap-1 transition-colors">
      <i class="ph ph-arrow-left"></i> Return to Public Site
    </a>
  </div>
</div>

<!-- JavaScript to handle Tab Switching -->
<script>
  function switchTab(tabName) {
    const loginForm = document.getElementById('loginForm');
    const requestForm = document.getElementById('requestForm');
    const btnLoginTab = document.getElementById('btnLoginTab');
    const btnRequestTab = document.getElementById('btnRequestTab');

    if (tabName === 'login') {
      loginForm.classList.remove('hidden');
      requestForm.classList.add('hidden');

      // Style Active Login Tab
      btnLoginTab.className = "w-1/2 py-4 text-center font-bold text-gray-900 border-b-[3px] border-[#FFC107] bg-white transition-colors";
      btnRequestTab.className = "w-1/2 py-4 text-center font-bold text-gray-500 border-b-[3px] border-transparent hover:text-gray-700 bg-gray-50 hover:bg-white transition-colors";
    } else {
      loginForm.classList.add('hidden');
      requestForm.classList.remove('hidden');

      // Style Active Request Tab
      btnRequestTab.className = "w-1/2 py-4 text-center font-bold text-gray-900 border-b-[3px] border-[#FFC107] bg-white transition-colors";
      btnLoginTab.className = "w-1/2 py-4 text-center font-bold text-gray-500 border-b-[3px] border-transparent hover:text-gray-700 bg-gray-50 hover:bg-white transition-colors";
    }
  }
</script>
</body>
</html>