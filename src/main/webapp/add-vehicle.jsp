<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Vehicle - DriveAway</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <script>
        function toggleVehicleFields() {
            const type = document.getElementById('vehicleType').value;
            document.getElementById('carFields').classList.toggle('hidden', type !== 'Car');
            document.getElementById('motorcycleFields').classList.toggle('hidden', type !== 'Motorcycle');
        }
        window.addEventListener('DOMContentLoaded', toggleVehicleFields);
    </script>
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #F4F7F6; }
        .fade-in { animation: fadeIn 0.4s ease-out; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
    </style>
</head>
<body class="min-h-screen text-gray-800 flex flex-col">

<nav class="bg-white border-b border-gray-200 sticky top-0 z-20 shadow-sm">
    <div class="max-w-screen-xl mx-auto px-8 h-20 flex items-center justify-between">
        <a href="vehicles?action=list" class="flex items-center gap-2 text-gray-900">
            <i class="ph-bold ph-car-profile text-3xl text-blue-600"></i>
            <span class="text-2xl font-extrabold tracking-tight">DriveAway</span>
        </a>
        <a href="vehicles?action=list" class="text-gray-600 hover:text-blue-600 font-semibold transition-colors flex items-center gap-2">
            <i class="ph-bold ph-arrow-left"></i> Back to Fleet
        </a>
    </div>
</nav>

<main class="flex-grow max-w-screen-md mx-auto w-full px-8 py-10 fade-in">
    <div class="bg-gradient-to-br from-[#0D6EFD] to-[#0a58ca] rounded-3xl p-10 sm:p-12 text-white shadow-lg mb-8 relative overflow-hidden">
        <div class="relative z-10">
            <h1 class="text-4xl sm:text-5xl font-extrabold mb-4 tracking-tight">Register New Vehicle</h1>
            <p class="text-blue-100 text-lg font-medium max-w-2xl leading-relaxed">Create a new fleet item using the same polished style as the customer pages.</p>
        </div>
        <i class="ph-fill ph-car absolute -right-8 -bottom-16 text-[220px] text-white opacity-10 transform -rotate-12 pointer-events-none"></i>
    </div>

    <div class="bg-white rounded-3xl p-10 sm:p-12 shadow-sm border border-gray-100">
        <div class="flex items-center gap-4 mb-8">
            <div class="w-14 h-14 rounded-2xl bg-blue-50 text-blue-600 flex items-center justify-center text-2xl border border-blue-100">
                <i class="ph ph-car-profile"></i>
            </div>
            <div>
                <h2 class="text-2xl font-extrabold text-gray-900 tracking-tight">Vehicle Details</h2>
                <p class="text-gray-500 font-medium">Choose the vehicle type and fill in the relevant fields.</p>
            </div>
        </div>

        <form action="vehicles" method="post" class="space-y-6">
            <input type="hidden" name="action" value="create">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                    <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Brand</label>
                    <input type="text" name="brand" required class="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all font-medium text-gray-900 shadow-inner">
                </div>
                <div>
                    <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Model</label>
                    <input type="text" name="model" required class="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all font-medium text-gray-900 shadow-inner">
                </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                    <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Daily Rental Rate ($)</label>
                    <input type="number" step="0.01" name="rate" required class="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all font-medium text-gray-900 shadow-inner">
                </div>
                <div>
                    <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Vehicle Type</label>
                    <select id="vehicleType" name="vehicleType" onchange="toggleVehicleFields()" class="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all font-medium text-gray-900 shadow-inner">
                        <option value="Car">Car / SUV</option>
                        <option value="Motorcycle">Motorcycle</option>
                    </select>
                </div>
            </div>

            <div id="carFields">
                <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Seating Capacity</label>
                <input type="number" name="capacity" min="1" value="4" class="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all font-medium text-gray-900 shadow-inner">
            </div>

            <div id="motorcycleFields" class="hidden">
                <label class="inline-flex items-center gap-3 text-sm font-bold text-gray-700">
                    <input type="checkbox" name="helmetIncluded" class="rounded border-gray-300 text-blue-600 focus:ring-blue-500">
                    Helmet included with rental
                </label>
            </div>

            <div class="pt-6 flex flex-col sm:flex-row justify-between items-center gap-4">
                <a href="vehicles?action=list" class="text-gray-500 hover:text-gray-800 font-bold px-4 py-2 transition-colors order-2 sm:order-1">Cancel</a>
                <button type="submit" class="w-full sm:w-auto bg-[#0D6EFD] hover:bg-blue-700 text-white font-bold px-8 py-3.5 rounded-xl shadow-lg shadow-blue-600/20 hover:shadow-blue-600/40 transition-all flex justify-center items-center gap-2 transform hover:-translate-y-0.5 order-1 sm:order-2">
                    <i class="ph-bold ph-plus-circle text-lg"></i> Add Vehicle
                </button>
            </div>
        </form>
    </div>
</main>
</body>
</html>