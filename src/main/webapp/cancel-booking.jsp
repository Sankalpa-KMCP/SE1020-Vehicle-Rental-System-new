<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Cancel Booking</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <style>body { font-family: 'Inter', sans-serif; }</style>
</head>
<body class="bg-[#FAFAFA] min-h-screen flex items-center justify-center p-6">
<div class="w-full max-w-lg bg-white p-10 rounded-[24px] shadow-lg border-[3px] border-[#EF4444]">
  <div class="flex justify-between items-center mb-6">
    <h2 class="text-3xl font-bold text-[#EF4444] flex items-center gap-3"><i class="ph-bold ph-trash"></i> Cancel Booking</h2>
    <a href="add-booking.jsp" class="text-gray-500 hover:text-gray-800 font-bold">Close ✕</a>
  </div>
  <p class="text-gray-600 mb-8 font-medium">Enter your Booking ID to send a cancellation request for staff approval.</p>

  <form action="bookings" method="POST" class="space-y-6">
    <input type="hidden" name="action" value="request_cancel">
    <div>
      <input type="text" name="id" placeholder="Booking ID (e.g. BKG-101)" required class="w-full border border-gray-300 rounded-xl px-4 py-4 text-lg outline-none focus:border-[#EF4444]">
    </div>
    <div>
      <textarea name="cancelReason" rows="3" placeholder="Reason (optional)" class="w-full border border-gray-300 rounded-xl px-4 py-3 outline-none focus:border-[#EF4444]"></textarea>
    </div>
    <button type="submit" onclick="return confirm('Submit this cancellation request?');" class="w-full bg-[#EF4444] text-white py-4 rounded-xl font-bold text-xl hover:bg-red-600 transition shadow-md">
      Submit Cancellation Request
    </button>
  </form>
</div>
</body>
</html>