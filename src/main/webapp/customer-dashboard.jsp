<%@ page contentType="text/html; charset=UTF-8" %>
<%
  // Ensure the user is actually logged in as a customer
  Boolean customerLoggedIn = (Boolean) session.getAttribute("customerLoggedIn");
  if (customerLoggedIn == null || !customerLoggedIn) {
    response.sendRedirect("index.jsp");
    return;
  }

  // Grab the username from the session (fallback to 'Guest' if null)
  String username = (String) session.getAttribute("username");
  if (username == null) username = "Customer";
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>DriveAway | My Portal</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');
    body { font-family: 'Inter', sans-serif; background-color: #F8F9FA; }
    .fade-in { animation: fadeIn 0.4s ease-out; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
  </style>
</head>
<body class="min-h-screen text-gray-800 flex flex-col">

<nav class="bg-white border-b border-gray-200 sticky top-0 z-10 shadow-sm">
  <div class="max-w-6xl mx-auto px-6 h-20 flex items-center justify-between">
    <div class="flex items-center gap-2 text-gray-900">
      <i class="ph-bold ph-car-profile text-3xl text-blue-600"></i>
      <span class="text-2xl font-extrabold tracking-tight">DriveAway</span>
    </div>

    <div class="flex items-center gap-6">
      <div class="flex items-center gap-3">
        <div class="bg-blue-50 text-blue-600 p-2 rounded-full">
          <i class="ph-bold ph-user text-xl"></i>
        </div>
        <span class="font-semibold text-gray-700">Hello, <%= username %></span>
      </div>
      <div class="w-px h-8 bg-gray-200"></div>
      <form action="auth" method="post" class="m-0">
        <input type="hidden" name="action" value="logout">
        <button type="submit" class="text-red-600 hover:text-red-800 font-bold border border-red-200 bg-red-50 hover:bg-red-100 px-5 py-2.5 rounded-lg transition-colors flex items-center gap-2 shadow-sm">
          Logout <i class="ph-bold ph-sign-out"></i>
        </button>
      </form>
    </div>
  </div>
</nav>

<main class="flex-grow max-w-6xl mx-auto w-full px-6 py-10 fade-in">

  <div class="bg-gradient-to-r from-blue-600 to-cyan-500 rounded-2xl p-10 text-white shadow-md mb-8 relative overflow-hidden">
    <div class="relative z-10">
      <h1 class="text-4xl font-extrabold mb-3">Welcome to Your Dashboard!</h1>
      <p class="text-blue-50 text-lg font-medium max-w-2xl">You have successfully logged into the system. From here, you can browse our premium vehicle fleet, manage your active reservations, and update your profile.</p>
    </div>
    <i class="ph-fill ph-steering-wheel absolute -right-10 -bottom-16 text-[250px] text-white opacity-10 transform -rotate-12"></i>
  </div>

  <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">

    <div class="bg-white p-8 rounded-2xl border border-gray-200 shadow-sm hover:shadow-lg hover:border-blue-300 transition-all group flex flex-col h-full">
      <div class="bg-blue-50 w-16 h-16 rounded-xl flex items-center justify-center text-blue-600 mb-6 group-hover:scale-110 transition-transform">
        <i class="ph-fill ph-car text-3xl"></i>
      </div>
      <h3 class="text-xl font-bold text-gray-900 mb-2">Rent a Vehicle</h3>
      <p class="text-gray-500 font-medium mb-8 flex-grow">Browse our available cars, check pricing, and book your next ride instantly.</p>
      <a href="vehicle-dashboard.jsp" class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3.5 rounded-xl flex justify-center items-center gap-2 shadow-sm transition-colors">
        Browse Cars <i class="ph-bold ph-arrow-right"></i>
      </a>
    </div>

    <div class="bg-white p-8 rounded-2xl border border-gray-200 shadow-sm hover:shadow-lg hover:border-green-300 transition-all group flex flex-col h-full">
      <div class="bg-green-50 w-16 h-16 rounded-xl flex items-center justify-center text-green-600 mb-6 group-hover:scale-110 transition-transform">
        <i class="ph-fill ph-calendar-check text-3xl"></i>
      </div>
      <h3 class="text-xl font-bold text-gray-900 mb-2">My Bookings</h3>
      <p class="text-gray-500 font-medium mb-8 flex-grow">View your active rentals, check past transaction history, and manage cancellations.</p>
      <a href="booking-dashboard.jsp" class="w-full bg-white border-2 border-green-600 text-green-700 hover:bg-green-50 font-bold py-3 rounded-xl flex justify-center items-center gap-2 shadow-sm transition-colors">
        View Bookings
      </a>
    </div>

    <div class="bg-white p-8 rounded-2xl border border-gray-200 shadow-sm hover:shadow-lg hover:border-purple-300 transition-all group flex flex-col h-full">
      <div class="bg-purple-50 w-16 h-16 rounded-xl flex items-center justify-center text-purple-600 mb-6 group-hover:scale-110 transition-transform">
        <i class="ph-fill ph-identification-card text-3xl"></i>
      </div>
      <h3 class="text-xl font-bold text-gray-900 mb-2">Account Settings</h3>
      <p class="text-gray-500 font-medium mb-8 flex-grow">Update your contact information, upload missing verification documents, or change your password.</p>
      <a href="edit-customer.jsp" class="w-full bg-white border-2 border-purple-600 text-purple-700 hover:bg-purple-50 font-bold py-3 rounded-xl flex justify-center items-center gap-2 shadow-sm transition-colors">
        Manage Profile
      </a>
    </div>

  </div>

</main>

</body>
</html>