<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Add New Vehicle - DriveAway</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
</head>
<body class="bg-gray-50 flex h-screen text-gray-800">
<!-- Main Content Area (Simplified without sidebar for full focus) -->
<div class="flex-1 flex flex-col h-screen overflow-y-auto">
    <div class="max-w-2xl mx-auto w-full mt-10 p-8 bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="flex items-center mb-6 border-b pb-4">
            <i class="ph ph-car-profile text-3xl text-blue-600 mr-3"></i>
            <h2 class="text-2xl font-bold text-gray-800">Register New Vehicle</h2>
        </div>

        <form action="vehicles" method="post" class="space-y-5">
            <div class="grid grid-cols-2 gap-5">
                <div>
                    <label class="block text-sm font-semibold text-gray-700 mb-1">Brand (e.g., Toyota)</label>
                    <input type="text" name="brand" required class="w-full border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-blue-500 outline-none">
                </div>
                <div>
                    <label class="block text-sm font-semibold text-gray-700 mb-1">Model (e.g., Camry)</label>
                    <input type="text" name="model" required class="w-full border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-blue-500 outline-none">
                </div>
            </div>
            <div class="grid grid-cols-2 gap-5">
                <div>
                    <label class="block text-sm font-semibold text-gray-700 mb-1">Daily Rental Rate ($)</label>
                    <input type="number" step="0.01" name="rate" required class="w-full border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-blue-500 outline-none">
                </div>
                <div>
                    <label class="block text-sm font-semibold text-gray-700 mb-1">Seating Capacity</label>
                    <input type="number" name="capacity" required class="w-full border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-blue-500 outline-none">
                </div>
            </div>

            <div class="pt-4 flex justify-between items-center">
                <a href="vehicles?action=list" class="text-gray-500 hover:text-gray-800 font-medium">Cancel</a>
                <button type="submit" class="bg-blue-600 text-white font-bold py-3 px-6 rounded-lg hover:bg-blue-700 transition flex items-center">
                    <i class="ph ph-plus-circle mr-2 text-xl"></i> Add Vehicle
                </button>
            </div>
        </form>
    </div>
</div>
</body>
</html>