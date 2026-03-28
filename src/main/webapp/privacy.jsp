<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>DriveAway | Privacy Policy</title>
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
        <h1 class="text-4xl md:text-5xl font-extrabold mb-4">Privacy <span class="gradient-text">Policy</span></h1>
        <p class="text-xl text-gray-500 max-w-2xl mx-auto">Your privacy matters to us. Learn how we handle your data.</p>
      </div>
    </div>
  </section>

  <section class="py-20">
    <div class="max-w-[1200px] mx-auto px-6 lg:px-8 reveal">
      <div class="prose prose-lg max-w-none">
        <h2 class="text-2xl font-bold mb-4">Information We Collect</h2>
        <p class="mb-6 text-gray-600">We collect personal information you provide when registering, making a booking, or contacting us. This may include your name, email address, phone number, driver’s license details, and payment information.</p>

        <h2 class="text-2xl font-bold mb-4">How We Use Your Information</h2>
        <p class="mb-6 text-gray-600">We use your data to process reservations, communicate with you, improve our services, and comply with legal obligations. We do not sell your personal information to third parties.</p>

        <h2 class="text-2xl font-bold mb-4">Data Security</h2>
        <p class="mb-6 text-gray-600">We implement industry-standard security measures to protect your data. However, no method of transmission over the Internet is 100% secure.</p>

        <h2 class="text-2xl font-bold mb-4">Cookies</h2>
        <p class="mb-6 text-gray-600">Our site uses cookies to enhance user experience. You can disable cookies in your browser settings, but some features may not function properly.</p>

        <h2 class="text-2xl font-bold mb-4">Your Rights</h2>
        <p class="mb-6 text-gray-600">You may request access to, correction, or deletion of your personal data by contacting us at privacy@driveaway.lk.</p>

        <h2 class="text-2xl font-bold mb-4">Changes to This Policy</h2>
        <p class="mb-6 text-gray-600">We may update this policy occasionally. Changes will be posted on this page with an updated effective date.</p>

        <p class="text-gray-500 text-sm mt-12">Effective date: March 28, 2026</p>
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