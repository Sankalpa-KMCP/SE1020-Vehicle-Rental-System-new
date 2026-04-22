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
    body { font-family: 'Inter', sans-serif; background-color: #ffffff; overflow-x: hidden; }

    .gradient-text {
      background: linear-gradient(135deg, #0D6EFD 0%, #4A12DA 100%);
      -webkit-background-clip: text;
      background-clip: text;
      color: transparent;
    }
    .reveal { opacity: 0; transform: translateY(30px); transition: all 0.8s cubic-bezier(0.2,0.8,0.2,1); }
    .reveal.active { opacity: 1; transform: translateY(0); }

    /* --- UNIFORM TEAM MASCOT CSS --- */
    .mascot-body {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      margin: 0 auto;
      transform-style: preserve-3d;
      will-change: transform;
      /* ALL MASCOTS ARE NOW THE EXACT SAME SIZE */
      width: 7rem;
      height: 9rem;
    }

    /* Eyes are safely centered using CSS inset margins */
    .mascot-eyes {
      position: absolute;
      transform: translateZ(40px);
      will-change: transform;
      z-index: 2;
    }

    @keyframes blink {
      0%, 92%, 98%, 100% { transform: scaleY(1); }
      95% { transform: scaleY(0.1); }
    }
    .eye-dot { animation: blink 5s infinite; transform-origin: center; transition: transform 0.2s ease; }

    /* The 6 Shapes (Colors and Radii preserved, sizes unified) */
    .shape-tall-black {
      background: linear-gradient(135deg, #2A2D35, #0D0F16);
      box-shadow: inset 4px 4px 15px rgba(255,255,255,0.1), inset -4px -4px 15px rgba(0,0,0,0.6), 10px 20px 30px rgba(0, 0, 0, 0.4);
      border-radius: 3.5rem 3.5rem 0 0;
    }
    .shape-guard-cyan {
      background: linear-gradient(135deg, #00F2FE, #0088FF);
      box-shadow: inset 4px 4px 15px rgba(255,255,255,0.4), inset -4px -4px 15px rgba(0,0,0,0.3), 10px 20px 30px rgba(0, 136, 255, 0.3);
      border-radius: 1.5rem 1.5rem 0 0;
    }
    .shape-hijab {
      background: linear-gradient(135deg, #7A52FA, #4A12DA);
      box-shadow: inset 4px 4px 15px rgba(255,255,255,0.4), inset -4px -4px 15px rgba(0,0,0,0.5), 10px 20px 30px rgba(90, 50, 250, 0.4);
      border-radius: 3.5rem 3.5rem 0 0;
    }
    .hijab-face {
      background: linear-gradient(135deg, #FFEDD5, #FDBA74);
      box-shadow: inset 0px 5px 10px rgba(0,0,0,0.2), 0px 4px 6px rgba(0,0,0,0.1);
      width: 4.5rem; height: 5.5rem; border-radius: 2rem 2rem 1.5rem 1.5rem;
      margin-top: 1rem;
    }
    .shape-yellow-girl {
      background: linear-gradient(135deg, #FFE044, #D6A200);
      box-shadow: inset 3px 3px 10px rgba(255,255,255,0.6), inset -3px -3px 10px rgba(0,0,0,0.2), 10px 20px 30px rgba(251, 207, 20, 0.3);
      border-radius: 3.5rem 3.5rem 0 0;
    }
    .shape-boss {
      background: linear-gradient(135deg, #EF4444, #B91C1C);
      box-shadow: inset 4px 4px 15px rgba(255,255,255,0.3), inset -4px -4px 15px rgba(0,0,0,0.3), 10px 20px 30px rgba(239, 68, 68, 0.3);
      border-radius: 1.5rem 1.5rem 0 0;
    }
    .shape-rookie {
      background: linear-gradient(135deg, #FF9A44, #D65A00);
      box-shadow: inset 4px 4px 15px rgba(255,255,255,0.6), inset -4px -4px 15px rgba(0,0,0,0.2), 10px 20px 30px rgba(255, 154, 68, 0.3);
      border-radius: 3.5rem 3.5rem 0 1.5rem;
    }

    /* Glasses CSS */
    .glasses-wrapper { display: flex; gap: 0.1rem; position: relative; align-items: center; justify-content: center; }
    .glasses-frame { border: 3px solid #1F2937; border-radius: 50%; display: flex; justify-content: center; align-items: center; background: rgba(255, 255, 255, 0.2); box-shadow: 1px 2px 4px rgba(0,0,0,0.15); }
    .glasses-lg { width: 1.5rem; height: 1.5rem; }
    .glasses-sm { width: 1.1rem; height: 1.1rem; border-width: 2px; }
    .glasses-bridge { position: absolute; width: 0.4rem; height: 3px; background-color: #1F2937; left: 50%; transform: translateX(-50%); top: 50%; z-index: 10; }

    /* Card Styling */
    .team-card {
      background: white;
      border-radius: 2rem;
      transition: all 0.4s ease;
      border: 1px solid #f3f4f6;
    }
    .team-card:hover {
      transform: translateY(-8px);
      box-shadow: 0 25px 40px -12px rgba(0,0,0,0.1);
    }
  </style>
</head>
<body class="text-gray-900 antialiased">

<jsp:include page="navbar.jsp" />

<main>
  <section class="pt-32 pb-20 bg-gradient-to-br from-[#F8FAFF] to-white">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 text-center">
      <div class="reveal">
        <h1 class="text-4xl md:text-5xl font-extrabold mb-4">About <span class="gradient-text">DriveAway</span></h1>
        <p class="text-xl text-gray-500 max-w-2xl mx-auto">We’re redefining car rentals in Sri Lanka – making it seamless, transparent, and enjoyable.</p>
      </div>
    </div>
  </section>

  <section class="py-20">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 grid md:grid-cols-2 gap-12 items-center">
      <div class="reveal order-2 md:order-1">
        <div class="inline-block bg-gray-100 px-4 py-2 rounded-full text-sm font-bold text-[#0D6EFD] mb-6">Our Mission</div>
        <h2 class="text-3xl font-bold mb-6">Empowering journeys, one drive at a time.</h2>
        <p class="text-gray-600 text-lg mb-6">At DriveAway, we believe renting a car should be as exciting as the journey itself. We combine technology with a passion for travel to give you the best vehicles, transparent pricing, and unparalleled support.</p>
        <p class="text-gray-600 text-lg">Our vision is to become the most trusted car rental platform in Sri Lanka, connecting travellers with a fleet of quality, well-maintained vehicles – all with a few clicks.</p>
      </div>
      <div class="reveal order-1 md:order-2">
        <img src="https://images.unsplash.com/photo-1555215695-3004980ad54e?q=80&w=800&auto=format" alt="Road trip" class="rounded-2xl shadow-xl w-full">
      </div>
    </div>
  </section>

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

  <section class="py-20">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">
      <div class="reveal mb-16 text-center">
        <h2 class="text-[2.5rem] font-extrabold mb-4 tracking-tight">The People Behind DriveAway</h2>
        <p class="text-gray-500 text-lg font-medium">A dedicated team making your rental experience exceptional.</p>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-10">

        <div class="team-card reveal overflow-hidden group flex flex-col">
          <div class="h-48 bg-[#F0F2F5] relative flex justify-center items-end pt-8 overflow-hidden group-hover:bg-[#E4E7EC] transition-colors duration-300 perspective-[1000px]">
            <div id="avatar-1" class="mascot-body shape-tall-black">
              <div class="mascot-eyes flex gap-3 drop-shadow-md absolute inset-x-0 mx-auto w-fit top-6">
                <div class="w-2 h-2 rounded-full bg-white eye-dot" style="animation-delay: 1s;"></div>
                <div class="w-2 h-2 rounded-full bg-white eye-dot" style="animation-delay: 1s;"></div>
              </div>
            </div>
          </div>
          <div class="p-8 text-center bg-white z-10 relative">
            <h3 class="text-xl font-extrabold text-gray-900 mb-1">Sankalpa K.M.C.P</h3>
            <p class="text-gray-400 text-sm font-bold tracking-widest mb-4">IT25100142</p>
            <div class="inline-block bg-blue-50 text-[#0D6EFD] font-bold px-4 py-1.5 rounded-lg text-sm">Founder & CEO</div>
          </div>
        </div>

        <div class="team-card reveal overflow-hidden group flex flex-col" style="transition-delay: 0.1s;">
          <div class="h-48 bg-[#F0F2F5] relative flex justify-center items-end pt-8 overflow-hidden group-hover:bg-[#E4E7EC] transition-colors duration-300 perspective-[1000px]">
            <div id="avatar-2" class="mascot-body shape-guard-cyan">
              <div class="mascot-eyes flex gap-3 drop-shadow-md absolute inset-x-0 mx-auto w-fit top-6">
                <div class="w-2.5 h-2.5 rounded-full bg-black eye-dot" style="animation-delay: 2s;"></div>
                <div class="w-2.5 h-2.5 rounded-full bg-black eye-dot" style="animation-delay: 2s;"></div>
              </div>
            </div>
          </div>
          <div class="p-8 text-center bg-white z-10 relative">
            <h3 class="text-xl font-extrabold text-gray-900 mb-1">Weerakoon W.M.Y.B</h3>
            <p class="text-gray-400 text-sm font-bold tracking-widest mb-4">IT25100565</p>
            <div class="inline-block bg-blue-50 text-[#0D6EFD] font-bold px-4 py-1.5 rounded-lg text-sm">Senior Developer</div>
          </div>
        </div>

        <div class="team-card reveal overflow-hidden group flex flex-col" style="transition-delay: 0.2s;">
          <div class="h-48 bg-[#F0F2F5] relative flex justify-center items-end pt-8 overflow-hidden group-hover:bg-[#E4E7EC] transition-colors duration-300 perspective-[1000px]">
            <div id="avatar-3" class="mascot-body shape-hijab flex justify-center">
              <div class="hijab-face relative">
                <div class="mascot-eyes glasses-wrapper drop-shadow-sm absolute inset-x-0 mx-auto inset-y-0 my-auto w-fit h-fit">
                  <div class="glasses-bridge"></div>
                  <div class="glasses-frame glasses-lg relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 1.5s;"></div></div>
                  <div class="glasses-frame glasses-lg relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 1.5s;"></div></div>
                </div>
              </div>
            </div>
          </div>
          <div class="p-8 text-center bg-white z-10 relative">
            <h3 class="text-xl font-extrabold text-gray-900 mb-1">Miftha N.S</h3>
            <p class="text-gray-400 text-sm font-bold tracking-widest mb-4">IT24100879</p>
            <div class="inline-block bg-blue-50 text-[#0D6EFD] font-bold px-4 py-1.5 rounded-lg text-sm">Operations Director</div>
          </div>
        </div>

        <div class="team-card reveal overflow-hidden group flex flex-col">
          <div class="h-48 bg-[#F0F2F5] relative flex justify-center items-end pt-8 overflow-hidden group-hover:bg-[#E4E7EC] transition-colors duration-300 perspective-[1000px]">
            <div id="avatar-4" class="mascot-body shape-yellow-girl">
              <div class="mascot-eyes glasses-wrapper drop-shadow-sm absolute inset-x-0 mx-auto w-fit top-6">
                <div class="glasses-bridge"></div>
                <div class="glasses-frame glasses-sm relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 3s;"></div></div>
                <div class="glasses-frame glasses-sm relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 3s;"></div></div>
              </div>
            </div>
          </div>
          <div class="p-8 text-center bg-white z-10 relative">
            <h3 class="text-xl font-extrabold text-gray-900 mb-1">Rajapakse J.A.C.D.</h3>
            <p class="text-gray-400 text-sm font-bold tracking-widest mb-4">IT25100385</p>
            <div class="inline-block bg-blue-50 text-[#0D6EFD] font-bold px-4 py-1.5 rounded-lg text-sm">Customer Experience</div>
          </div>
        </div>

        <div class="team-card reveal overflow-hidden group flex flex-col" style="transition-delay: 0.1s;">
          <div class="h-48 bg-[#F0F2F5] relative flex justify-center items-end pt-8 overflow-hidden group-hover:bg-[#E4E7EC] transition-colors duration-300 perspective-[1000px]">
            <div id="avatar-5" class="mascot-body shape-boss">
              <div class="mascot-eyes flex gap-4 drop-shadow-md absolute inset-x-0 mx-auto w-fit top-6">
                <div class="w-2.5 h-2.5 rounded-full bg-white eye-dot" style="animation-delay: 0.5s;"></div>
                <div class="w-2.5 h-2.5 rounded-full bg-white eye-dot" style="animation-delay: 0.5s;"></div>
              </div>
            </div>
          </div>
          <div class="p-8 text-center bg-white z-10 relative">
            <h3 class="text-xl font-extrabold text-gray-900 mb-1">Liyanage H.K</h3>
            <p class="text-gray-400 text-sm font-bold tracking-widest mb-4">IT25100429</p>
            <div class="inline-block bg-blue-50 text-[#0D6EFD] font-bold px-4 py-1.5 rounded-lg text-sm">Technical Director</div>
          </div>
        </div>

        <div class="team-card reveal overflow-hidden group flex flex-col" style="transition-delay: 0.2s;">
          <div class="h-48 bg-[#F0F2F5] relative flex justify-center items-end pt-8 overflow-hidden group-hover:bg-[#E4E7EC] transition-colors duration-300 perspective-[1000px]">
            <div id="avatar-6" class="mascot-body shape-rookie">
              <div class="mascot-eyes flex gap-2 drop-shadow-sm absolute inset-x-0 mx-auto w-fit top-6">
                <div class="w-2 h-2 rounded-full bg-black eye-dot" style="animation-delay: 2.5s;"></div>
                <div class="w-2 h-2 rounded-full bg-black eye-dot" style="animation-delay: 2.5s;"></div>
              </div>
            </div>
          </div>
          <div class="p-8 text-center bg-white z-10 relative">
            <h3 class="text-xl font-extrabold text-gray-900 mb-1">Arusha H M D K</h3>
            <p class="text-gray-400 text-sm font-bold tracking-widest mb-4">IT25100445</p>
            <div class="inline-block bg-blue-50 text-[#0D6EFD] font-bold px-4 py-1.5 rounded-lg text-sm">Marketing Lead</div>
          </div>
        </div>

      </div>
    </div>
  </section>
</main>

<jsp:include page="footer.jsp" />

<script>
  // 1. Scroll Reveal Logic
  document.addEventListener('DOMContentLoaded', () => {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) entry.target.classList.add('active');
      });
    }, { threshold: 0.1 });
    document.querySelectorAll('.reveal').forEach(el => observer.observe(el));
  });

  // 2. Interactive 3D Mascot Logic
  class Mascot {
    constructor(id, weight, maxRotation) {
      this.body = document.getElementById(id);
      this.eyes = this.body.querySelector('.mascot-eyes');
      this.weight = weight;
      this.maxRotation = maxRotation;
      this.currentX = 0; this.currentY = 0;
      this.targetX = 0;  this.targetY = 0;
    }

    lookAt(x, y) {
      this.targetX = x;
      this.targetY = y;
    }

    update() {
      this.currentX += (this.targetX - this.currentX) * this.weight;
      this.currentY += (this.targetY - this.currentY) * this.weight;

      // Calculate rotations
      const moveX = this.currentX * 15;
      const moveY = this.currentY * 15;
      const rotX = this.currentY * -this.maxRotation;
      const rotY = this.currentX * this.maxRotation;

      // Apply to body
      this.body.style.transform = 'translate3d(' + moveX + 'px, ' + moveY + 'px, 0) ' +
              'rotateX(' + rotX + 'deg) rotateY(' + rotY + 'deg)';

      // Apply independent eye tracking
      this.eyes.style.transform = 'translateZ(40px) translate(' + (this.currentX * 20) + 'px, ' + (this.currentY * 20) + 'px)';
    }
  }

  // Initialize all 6 avatars
  const m1 = new Mascot('avatar-1', 0.05, 20);
  const m2 = new Mascot('avatar-2', 0.06, 15);
  const m3 = new Mascot('avatar-3', 0.04, 25);
  const m4 = new Mascot('avatar-4', 0.07, 30);
  const m5 = new Mascot('avatar-5', 0.05, 15);
  const m6 = new Mascot('avatar-6', 0.06, 20);
  const mascots = [m1, m2, m3, m4, m5, m6];

  // Track mouse movements globally
  document.addEventListener('mousemove', (e) => {
    const mouseX = (e.clientX / window.innerWidth) * 2 - 1;
    const mouseY = (e.clientY / window.innerHeight) * 2 - 1;

    mascots.forEach(m => m.lookAt(mouseX, mouseY));
  });

  // Start animation loop
  function renderLoop() {
    mascots.forEach(m => m.update());
    requestAnimationFrame(renderLoop);
  }
  requestAnimationFrame(renderLoop);
</script>
</body>
</html>