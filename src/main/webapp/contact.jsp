<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>DriveAway | Contact Us</title>
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
        <h1 class="text-4xl md:text-5xl font-extrabold mb-4">Get in <span class="gradient-text">Touch</span></h1>
        <p class="text-xl text-gray-500 max-w-2xl mx-auto">We’re here to help. Reach out with any questions or feedback.</p>
      </div>
    </div>
  </section>

  <section class="py-20">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 grid md:grid-cols-2 gap-12">
      <!-- Contact Info -->
      <div class="reveal">
        <h2 class="text-2xl font-bold mb-6">Contact Information</h2>
        <div class="space-y-4">
          <div class="flex items-center gap-4">
            <i class="ph-fill ph-map-pin text-2xl text-[#0D6EFD]"></i>
            <div>123 Galle Road, Colombo 03, Sri Lanka</div>
          </div>
          <div class="flex items-center gap-4">
            <i class="ph-fill ph-phone text-2xl text-[#0D6EFD]"></i>
            <div>+94 11 234 5678</div>
          </div>
          <div class="flex items-center gap-4">
            <i class="ph-fill ph-envelope text-2xl text-[#0D6EFD]"></i>
            <div>hello@driveaway.lk</div>
          </div>
          <div class="flex items-center gap-4">
            <i class="ph-fill ph-clock text-2xl text-[#0D6EFD]"></i>
            <div>Mon - Sat: 8:00 AM - 6:00 PM</div>
          </div>
        </div>
        <!-- Simple Map Placeholder (can embed Google Maps later) -->
        <div class="mt-8 bg-gray-200 h-48 rounded-xl flex items-center justify-center text-gray-500">Map placeholder</div>
      </div>

      <!-- Contact Form -->
      <div class="reveal bg-white p-8 rounded-2xl shadow-xl border border-gray-100">
        <h2 class="text-2xl font-bold mb-6">Send a Message</h2>
        <form action="#" method="POST" id="contactForm">
          <div class="mb-4">
            <label class="block text-sm font-bold text-gray-700 mb-2">Full Name</label>
            <input type="text" name="name" required class="w-full border-b-2 border-gray-300 py-2 focus:border-[#0D6EFD] outline-none transition">
          </div>
          <div class="mb-4">
            <label class="block text-sm font-bold text-gray-700 mb-2">Email Address</label>
            <input type="email" name="email" required class="w-full border-b-2 border-gray-300 py-2 focus:border-[#0D6EFD] outline-none transition">
          </div>
          <div class="mb-6">
            <label class="block text-sm font-bold text-gray-700 mb-2">Message</label>
            <textarea name="message" rows="5" required class="w-full border-b-2 border-gray-300 py-2 focus:border-[#0D6EFD] outline-none transition"></textarea>
          </div>
          <button type="submit" class="w-full bg-[#0D6EFD] text-white font-bold py-3 rounded-xl hover:bg-blue-700 transition">Send Message</button>
        </form>
        <div id="formStatus" class="mt-4 text-sm text-center hidden"></div>
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

    // Simple form handler (demo)
    const form = document.getElementById('contactForm');
    const status = document.getElementById('formStatus');
    form.addEventListener('submit', (e) => {
      e.preventDefault();
      status.classList.remove('hidden');
      status.innerHTML = '<span class="text-green-600"><i class="ph-fill ph-check-circle"></i> Thank you! We\'ll get back to you soon.</span>';
      form.reset();
      setTimeout(() => status.classList.add('hidden'), 4000);
    });
  });
</script>
</body>
</html>