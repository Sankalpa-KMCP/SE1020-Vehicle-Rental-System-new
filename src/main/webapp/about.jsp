<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>DriveAway | About Us</title>
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

    /* Simple shape container */
    .simple-shape {
      width: 120px;
      height: 120px;
      margin: 0 auto 1rem;
      border-radius: 30% 70% 70% 30% / 30% 30% 70% 70%;
      transition: transform 0.3s ease, box-shadow 0.3s ease;
      animation: float 3s ease-in-out infinite;
      box-shadow: 0 10px 20px rgba(0,0,0,0.1);
    }
    .team-card:hover .simple-shape {
      transform: translateY(-8px) scale(1.02);
      box-shadow: 0 20px 30px rgba(0,0,0,0.15);
      animation: none;
    }
    @keyframes float {
      0%, 100% { transform: translateY(0px); }
      50% { transform: translateY(-6px); }
    }

    /* Color variants */
    .shape-black { background: linear-gradient(135deg, #2A2D35, #0D0F16); }
    .shape-cyan { background: linear-gradient(135deg, #00F2FE, #0088FF); }
    .shape-purple { background: linear-gradient(135deg, #7A52FA, #4A12DA); }
    .shape-yellow { background: linear-gradient(135deg, #FFE044, #D6A200); }
    .shape-red { background: linear-gradient(135deg, #EF4444, #B91C1C); }
    .shape-orange { background: linear-gradient(135deg, #FF9A44, #D65A00); }

    .team-card {
      background: white;
      border-radius: 1.5rem;
      transition: all 0.3s ease;
    }
    .team-card:hover {
      transform: translateY(-4px);
      box-shadow: 0 20px 30px -12px rgba(0,0,0,0.15);
    }
  </style>
</head>
<body class="text-gray-900 antialiased">

<jsp:include page="navbar.jsp" />

<main>
  <!-- Hero Section -->
  <section class="pt-32 pb-20 bg-gradient-to-br from-[#F8FAFF] to-white">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 text-center">
      <div class="reveal">
        <h1 class="text-4xl md:text-5xl font-extrabold mb-4">About <span class="gradient-text">DriveAway</span></h1>
        <p class="text-xl text-gray-500 max-w-2xl mx-auto">We’re redefining car rentals in Sri Lanka – making it seamless, transparent, and enjoyable.</p>
      </div>
    </div>
  </section>

  <!-- Mission & Vision -->
  <section class="py-20">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 grid md:grid-cols-2 gap-12 items-center">
      <div class="reveal order-2 md:order-1">
        <div class="inline-block bg-gray-100 px-4 py-2 rounded-full text-sm font-bold text-[#0D6EFD] mb-6">Our Mission</div>
        <h2 class="text-3xl font-bold mb-6">Empowering journeys, one drive at a time.</h2>
        <p class="text-gray-600 text-lg mb-6">At DriveAway, we believe renting a car should be as exciting as the journey itself. We combine technology with a passion for travel to give you the best vehicles, transparent pricing, and unparalleled support.</p>
        <p class="text-gray-600 text-lg">Our vision is to become the most trusted car rental platform in Sri Lanka, connecting travellers with a fleet of quality, well‑maintained vehicles – all with a few clicks.</p>
      </div>
      <div class="reveal order-1 md:order-2">
        <img src="https://images.unsplash.com/photo-1555215695-3004980ad54e?q=80&w=800&auto=format" alt="Road trip" class="rounded-2xl shadow-xl w-full">
      </div>
    </div>
  </section>

  <!-- Stats -->
  <section class="py-20 bg-gray-50">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 text-center">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div class="reveal p-6">
          <i class="ph-fill ph-car text-4xl text-[#0D6EFD] mb-4"></i>
          <div class="text-4xl font-black mb-2">250+</div>
          <div class="text-gray-600 font-medium">Vehicles in Fleet</div>
        </div>
        <div class="reveal p-6">
          <i class="ph-fill ph-smiley text-4xl text-[#0D6EFD] mb-4"></i>
          <div class="text-4xl font-black mb-2">1,200+</div>
          <div class="text-gray-600 font-medium">Happy Customers</div>
        </div>
        <div class="reveal p-6">
          <i class="ph-fill ph-star text-4xl text-[#0D6EFD] mb-4"></i>
          <div class="text-4xl font-black mb-2">4.9★</div>
          <div class="text-gray-600 font-medium">Average Rating</div>
        </div>
      </div>
    </div>
  </section>

  <!-- Team Section (simple shapes, no faces, no blob) -->
  <section class="py-20">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">
      <div class="reveal mb-12 text-center">
        <h2 class="text-3xl font-bold mb-3">The People Behind DriveAway</h2>
        <p class="text-gray-500 text-lg">A dedicated team making your rental experience exceptional.</p>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-10">
        <!-- 1. IT25100142 - Sankalpa K.M.C.P (Black) -->
        <div class="team-card reveal bg-white p-6 rounded-2xl shadow-md text-center">
          <div class="simple-shape shape-black mx-auto"></div>
          <h3 class="text-xl font-bold mt-4">Sankalpa K.M.C.P</h3>
          <p class="text-gray-500">IT25100142</p>
          <p class="text-gray-600 font-medium mt-2">Founder & CEO</p>
        </div>

        <!-- 2. IT25100565 - Weerakoon W.M.Y.B (Light blue / Cyan) -->
        <div class="team-card reveal bg-white p-6 rounded-2xl shadow-md text-center">
          <div class="simple-shape shape-cyan mx-auto"></div>
          <h3 class="text-xl font-bold mt-4">Weerakoon W.M.Y.B</h3>
          <p class="text-gray-500">IT25100565</p>
          <p class="text-gray-600 font-medium mt-2">Senior Developer</p>
        </div>

        <!-- 3. IT24100879 - Miftha N.S (Purple) -->
        <div class="team-card reveal bg-white p-6 rounded-2xl shadow-md text-center">
          <div class="simple-shape shape-purple mx-auto"></div>
          <h3 class="text-xl font-bold mt-4">Miftha N.S</h3>
          <p class="text-gray-500">IT24100879</p>
          <p class="text-gray-600 font-medium mt-2">Operations Director</p>
        </div>

        <!-- 4. IT25100385 - Rajapakse J. A. C. D. (Yellow) -->
        <div class="team-card reveal bg-white p-6 rounded-2xl shadow-md text-center">
          <div class="simple-shape shape-yellow mx-auto"></div>
          <h3 class="text-xl font-bold mt-4">Rajapakse J. A. C. D.</h3>
          <p class="text-gray-500">IT25100385</p>
          <p class="text-gray-600 font-medium mt-2">Customer Experience Lead</p>
        </div>

        <!-- 5. IT25100429 - Liyanage H.K (Red) -->
        <div class="team-card reveal bg-white p-6 rounded-2xl shadow-md text-center">
          <div class="simple-shape shape-red mx-auto"></div>
          <h3 class="text-xl font-bold mt-4">Liyanage H.K</h3>
          <p class="text-gray-500">IT25100429</p>
          <p class="text-gray-600 font-medium mt-2">Technical Director</p>
        </div>

        <!-- 6. IT25100445 - Arusha H M D K (Orange) -->
        <div class="team-card reveal bg-white p-6 rounded-2xl shadow-md text-center">
          <div class="simple-shape shape-orange mx-auto"></div>
          <h3 class="text-xl font-bold mt-4">Arusha H M D K</h3>
          <p class="text-gray-500">IT25100445</p>
          <p class="text-gray-600 font-medium mt-2">Marketing Lead</p>
        </div>
      </div>
    </div>
  </section>
</main>

<jsp:include page="footer.jsp" />

<script>
  document.addEventListener('DOMContentLoaded', () => {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) entry.target.classList.add('active');
      });
    }, { threshold: 0.1 });
    document.querySelectorAll('.reveal').forEach(el => observer.observe(el));
  });
</script>
</body>
</html>