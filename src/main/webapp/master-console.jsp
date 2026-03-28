<%@ page import="java.io.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    // Security Mechanism: Kick out anyone trying to access this page via URL directly!
    Boolean isMaster = (Boolean) session.getAttribute("isMasterAdmin");
    if (isMaster == null || !isMaster) {
        response.sendRedirect("admin-home.jsp");
        return;
    }

    // Read the pending requests from the file
    String FILE_PATH = System.getProperty("user.home") + File.separator + "RentalData" + File.separator + "pending_admins.txt";
    List<String[]> requestsList = new ArrayList<>();
    File file = new File(FILE_PATH);
    if (file.exists()) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length >= 3 && "PENDING".equals(data[2])) {
                    requestsList.add(data);
                }
            }
        } catch(Exception e) { e.printStackTrace(); }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Master Security Clearance</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
</head>
<body class="bg-[#F8F9FA] min-h-screen text-gray-800">

<!-- Dark Master Header -->
<header class="bg-[#1A1A1A] h-20 w-full flex items-center justify-between px-10 shadow-lg">
    <div class="text-[#FFC107] text-2xl font-bold flex items-center gap-3">
        <i class="ph-fill ph-shield-check text-3xl"></i>
        Master Admin Console
    </div>
    <div class="flex items-center gap-6">
        <span class="text-white font-medium">Logged in as: <span class="font-bold text-xl"><%= session.getAttribute("adminUser") %></span></span>

        <a href="admin-home.jsp" class="border border-white hover:bg-white hover:text-black text-white font-semibold py-2 px-5 rounded-md transition-colors">
            Go to Main Dashboard
        </a>

        <a href="index.jsp" class="bg-[#DC3545] hover:bg-[#c82333] text-white font-semibold py-2 px-5 rounded-md flex items-center gap-2 transition-colors">
            <i class="ph ph-sign-out text-lg"></i> Logout
        </a>
    </div>
</header>

<!-- Content Area -->
<main class="max-w-5xl mx-auto mt-16 px-6">
    <h1 class="text-4xl font-extrabold text-gray-900 text-center mb-10 tracking-tight">Pending Access Requests</h1>

    <div class="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
        <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-[#1A1A1A]">
            <tr>
                <th class="px-6 py-4 text-left text-sm font-bold text-white tracking-wider rounded-tl-lg">Username</th>
                <th class="px-6 py-4 text-left text-sm font-bold text-white tracking-wider">Requested Role</th>
                <th class="px-6 py-4 text-left text-sm font-bold text-white tracking-wider">Status</th>
                <th class="px-6 py-4 text-right text-sm font-bold text-white tracking-wider rounded-tr-lg">Action</th>
            </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
            <% if (requestsList.isEmpty()) { %>
            <tr>
                <td colspan="4" class="px-6 py-12 text-center text-gray-500 font-medium">
                    <i class="ph ph-check-circle text-4xl text-green-500 mb-2"></i><br>
                    No pending requests at this time.
                </td>
            </tr>
            <% } else {
                for (String[] req : requestsList) {
            %>
            <tr class="hover:bg-gray-50 transition-colors">
                <td class="px-6 py-5 whitespace-nowrap">
                    <div class="flex items-center gap-3">
                        <i class="ph-fill ph-user text-gray-400 text-xl"></i>
                        <span class="font-bold text-gray-900 text-lg"><%= req[0] %></span>
                    </div>
                </td>
                <td class="px-6 py-5 whitespace-nowrap">
                    <span class="bg-blue-500 text-white text-xs font-bold px-3 py-1 rounded-full">ADMIN</span>
                </td>
                <td class="px-6 py-5 whitespace-nowrap">
                                <span class="bg-[#FFC107] text-gray-900 text-xs font-bold px-3 py-1 rounded-full flex items-center w-max gap-1">
                                    <i class="ph ph-clock text-sm"></i> PENDING
                                </span>
                </td>
                <td class="px-6 py-5 whitespace-nowrap text-right">
                    <button class="bg-[#198754] hover:bg-green-700 text-white font-bold py-2 px-4 rounded-md transition-colors shadow-sm">
                        <i class="ph ph-check mr-1"></i> Approve Access
                    </button>
                </td>
            </tr>
            <%
                    }
                }
            %>
            </tbody>
        </table>
    </div>
</main>

</body>
</html>