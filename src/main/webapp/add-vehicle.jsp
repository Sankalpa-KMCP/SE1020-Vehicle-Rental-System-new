<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Add New Vehicle - DriveAway</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');
        body { font-family: 'Inter', sans-serif; }
    </style>
    <script>
        // AI-Generated logic to swap form fields effortlessly
        function toggleVehicleFields() {
            const type = document.getElementById("vehicleType").value;
            const extraLabel = document.getElementById("extraLabel");
            const extraInput = document.getElementById("extraInput");

            if (type === "Car") {
                extraLabel.innerText = "Seating Capacity";
                extraInput.type = "number";
                extraInput.placeholder = "e.g., 5";
                extraInput.value = "";
            } else {
                extraLabel.innerText = "Includes Helmet? (true/false)";
                extraInput.type = "text";
                extraInput.placeholder = "true or false";
                extraInput.value = "";
            }
        }
    </script>
</head>
<body class="bg-[#F8F9FA] flex h-screen text-gray-800 antialiased">
<div class="flex-1 flex flex-col h-screen overflow-y-auto">

    <header class="h-16 bg-white shadow-sm flex items-center px-8 border-b border-gray-200">
        <a href="vehicles?action=list" class="text-gray-500 hover:text-blue-600 flex items-center transition-colors font-medium">
            <i class="ph-bold ph-arrow-left mr-2"></i> Back to Fleet
        </a>
    </header>

    <div class="max-w-2xl mx-auto w-full mt-12 p-10 bg-white rounded-[24px] shadow-lg border border-gray-100">
        <div class="flex items-center mb-8 border-b border-gray-100 pb-6">
            <div class="bg-blue-50 p-3 rounded-2xl mr-4 text-blue-600">
                <i class="ph-bold ph-car-profile text-3xl"></i>
            </div>
            <div>
                <h2 class="text-2xl font-extrabold text-gray-900 tracking-tight">Register New Vehicle</h2>
                <p class="text-sm text-gray-500 font-medium mt-1">Add a new car or motorcycle to the rental fleet.</p>
            </div>
        </div>

        <% if (request.getParameter("error") != null) { %>
        <div class="mb-6 bg-red-50 text-red-600 text-sm p-4 rounded-xl border border-red-100 font-bold flex items-center">
            <i class="ph-fill ph-warning-circle text-xl mr-2"></i> Invalid data entered. Please check your numbers and try again.
        </div>
        <% } %>

        <form action="vehicles" method="post" class="space-y-6">
            <div>
                <label class="block text-sm font-bold text-gray-700 mb-2">Vehicle Type</label>
                <div class="relative">
                    <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                        <i class="ph-bold ph-steering-wheel text-gray-400 text-lg"></i>
                    </div>
                    <select id="vehicleType" name="type" onchange="toggleVehicleFields()" class="w-full pl-12 pr-4 py-3.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] focus:border-[#0D6EFD] outline-none transition-all font-medium text-gray-800 bg-white appearance-none cursor-pointer">
                        <option value="Car">Car / SUV</option>
                        <option value="Motorcycle">Motorcycle</option>
                    </select>
                    <div class="absolute inset-y-0 right-0 pr-4 flex items-center pointer-events-none">
                        <i class="ph-bold ph-caret-down text-gray-400"></i>
                    </div>
                </div>
            </div>

            <div class="grid grid-cols-2 gap-6">
                <div>
                    <label class="block text-sm font-bold text-gray-700 mb-2">Brand</label>
                    <input type="text" name="brand" required placeholder="eg:- Toyota" class="w-full px-4 py-3.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] outline-none transition-all font-medium text-gray-800">
                </div>
                <div>
                    <label class="block text-sm font-bold text-gray-700 mb-2">Model</label>
                    <input type="text" name="model" required placeholder="eg:- Camry" class="w-full px-4 py-3.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] outline-none transition-all font-medium text-gray-800">
                </div>
            </div>

            <div class="grid grid-cols-2 gap-6">
                <div>
                    <label class="block text-sm font-bold text-gray-700 mb-2">Base Daily Rate (Rs)</label>
                    <div class="relative">
                        <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                            <span class="text-gray-400 font-bold"> Rs </span>
                        </div>
                        <input type="number" step="0.01" name="rate" required placeholder="0.00" class="w-full pl-8 pr-4 py-3.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] outline-none transition-all font-medium text-gray-800">
                    </div>
                </div>
                <div>
                    <label id="extraLabel" class="block text-sm font-bold text-gray-700 mb-2">Seating Capacity</label>
                    <input id="extraInput" type="number" name="extraDetail" required placeholder="eg:- 5" class="w-full px-4 py-3.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-[#0D6EFD] outline-none transition-all font-medium text-gray-800">
                </div>
            </div>

            <div class="pt-6 mt-2 border-t border-gray-100 flex justify-end">
                <button type="submit" class="bg-[#0D6EFD] text-white font-bold py-3.5 px-8 rounded-xl hover:bg-blue-700 transition-colors shadow-md flex items-center text-lg">
                    <i class="ph-bold ph-check-circle mr-2 text-xl"></i> Register Vehicle
                </button>
            </div>
        </form>
    </div>
</div>
</body>
</html>