terms<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>DriveAway | Terms of Service</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap');
    body { font-family: 'Inter', sans-serif; background-color: #ffffff; }
    .gradient-text {
      background: linear-gradient(135deg, #0D6EFD 0%, #4A12DA 100%);
      -webkit-background-clip: text;
      background-clip: text;
      color: transparent;
    }
    .reveal { opacity: 0; transform: translateY(30px); transition: all 0.8s cubic-bezier(0.2,0.8,0.2,1); }
    .reveal.active { opacity: 1; transform: translateY(0); }
  </style>
</head>
<body class="text-gray-900 antialiased">

<jsp:include page="navbar.jsp" />

<main>
  <section class="pt-32 pb-20 bg-gradient-to-br from-[#F8FAFF] to-white">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 text-center">
      <div class="reveal">
        <h1 class="text-4xl md:text-5xl font-extrabold mb-4">Terms of <span class="gradient-text">Service</span></h1>
        <p class="text-xl text-gray-500 max-w-2xl mx-auto">Please read these terms carefully before using our services.</p>
      </div>
    </div>
  </section>

  <section class="py-20">
    <div class="max-w-[1200px] mx-auto px-6 lg:px-8 reveal">
      <div class="prose prose-lg max-w-none">
        <h2 class="text-2xl font-bold mb-4">1. Acceptance of Terms</h2>
        <p class="mb-6 text-gray-600">By accessing or using the DriveAway website and services, you agree to be bound by these Terms of Service. If you do not agree, please do not use our services.</p>

        <h2 class="text-2xl font-bold mb-4">2. Rental Eligibility</h2>
        <p class="mb-6 text-gray-600">To rent a vehicle, you must be at least 21 years old, possess a valid driver’s license, and provide a valid form of payment. We reserve the right to refuse service to anyone at our discretion.</p>

        <h2 class="text-2xl font-bold mb-4">3. Booking & Cancellation</h2>
        <p class="mb-6 text-gray-600">Reservations can be made online. Cancellation policies vary by vehicle and season; please refer to your booking confirmation for details. Cancellations made less than 24 hours before pickup may incur a fee.</p>

        <h2 class="text-2xl font-bold mb-4">4. Vehicle Usage</h2>
        <p class="mb-6 text-gray-600">Vehicles must be used in accordance with all traffic laws. The renter is responsible for any fines, damages, or incidents that occur during the rental period. Smoking in vehicles is strictly prohibited.</p>

        <h2 class="text-2xl font-bold mb-4">5. Liability</h2>
        <p class="mb-6 text-gray-600">DriveAway is not liable for any indirect, incidental, or consequential damages arising from the use of our vehicles or services. Our total liability is limited to the rental amount paid.</p>

        <h2 class="text-2xl font-bold mb-4">6. Changes to Terms</h2>
        <p class="mb-6 text-gray-600">We may update these terms from time to time. Continued use of our services constitutes acceptance of the updated terms.</p>

        <p class="text-gray-500 text-sm mt-12">Last updated: March 28, 2026</p>
      </div>
    </div>
  </section>
</main>

<jsp:include page="footer.jsp" />

<script>
  document.addEventListener('DOMContentLoaded', () => {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => { if (entry.isIntersecting) entry.target.classList.add('active'); });
    }, { threshold: 0.1 });
    document.querySelectorAll('.reveal').forEach(el => observer.observe(el));
  });
</script>
</body>
</html>