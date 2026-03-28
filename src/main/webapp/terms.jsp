<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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

    /* --- FASTER PAGE TRANSITION CSS --- */
    body {
      font-family: 'Inter', sans-serif;
      background-color: #FAFAFA;
      overflow-x: hidden;
      opacity: 0;
      transition: opacity 0.15s ease-out;
    }
    body.page-loaded { opacity: 1; }

    .gradient-text {
      background: linear-gradient(135deg, #0D6EFD 0%, #4A12DA 100%);
      -webkit-background-clip: text;
      background-clip: text;
      color: transparent;
    }

    .reveal { opacity: 0; transform: translateY(30px); transition: all 0.8s cubic-bezier(0.2,0.8,0.2,1); }
    .reveal.active { opacity: 1; transform: translateY(0); }

    /* --- LEGAL MASCOT CSS --- */
    .mascot-body {
      position: absolute;
      transform-style: preserve-3d;
      will-change: transform;
      width: 8rem;
      height: 10rem; /* Slightly shorter for the sidebar */
    }
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
    .eye-dot {
      animation: blink 5s infinite;
      transform-origin: center;
      transition: transform 0.2s ease;
    }

    /* Red Boss Body */
    .shape-boss {
      background: linear-gradient(135deg, #EF4444, #B91C1C);
      box-shadow: inset 4px 4px 15px rgba(255,255,255,0.3), inset -4px -4px 15px rgba(0,0,0,0.3), 10px 20px 30px rgba(239, 68, 68, 0.3);
      border-radius: 2rem 2rem 0 0;
    }

    /* Glasses for the Lawyer/Auditor Look! */
    .glasses-wrapper { display: flex; gap: 0.1rem; position: relative; align-items: center; justify-content: center; }
    .glasses-frame {
      border: 3px solid #1F2937;
      border-radius: 50%;
      display: flex; justify-content: center; align-items: center;
      background: rgba(255, 255, 255, 0.2);
      box-shadow: 1px 2px 4px rgba(0,0,0,0.15);
    }
    .glasses-lg { width: 1.6rem; height: 1.6rem; }
    .glasses-bridge {
      position: absolute; width: 0.4rem; height: 3px; background-color: #1F2937;
      left: 50%; transform: translateX(-50%); top: 50%; z-index: 10;
    }

    /* Section Cards */
    .tos-card {
      background: white;
      border-radius: 2rem;
      padding: 2.5rem;
      border: 1px solid #F3F4F6;
      box-shadow: 0 10px 30px rgba(0,0,0,0.02);
      transition: all 0.3s ease;
    }
    .tos-card:hover {
      box-shadow: 0 20px 40px rgba(13,110,253,0.05);
      border-color: #E5E7EB;
    }
  </style>
</head>
<body class="text-gray-900 antialiased">

<jsp:include page="navbar.jsp" />

<main class="relative">

  <section class="pt-40 pb-16 relative z-10 bg-white border-b border-gray-100">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 text-center">
      <div class="reveal">
        <div class="inline-flex items-center justify-center bg-gray-100 p-4 rounded-2xl mb-6">
          <i class="ph-fill ph-scales text-4xl text-gray-800"></i>
        </div>
        <h1 class="text-[3.5rem] md:text-[4.5rem] font-extrabold mb-4 tracking-tight leading-tight">Terms of <span class="gradient-text">Service</span></h1>
        <p class="text-xl text-gray-500 max-w-2xl mx-auto font-medium">Clear, transparent rules for a smooth journey. Please read these terms carefully.</p>
        <p class="text-sm font-bold text-gray-400 uppercase tracking-widest mt-6">Last Updated: March 28, 2026</p>
      </div>
    </div>
  </section>

  <section class="py-20 relative z-10">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 grid lg:grid-cols-12 gap-16 items-start">

      <div class="hidden lg:block lg:col-span-4 sticky top-32 reveal">
        <div class="bg-white p-8 rounded-[2.5rem] shadow-[0_20px_50px_rgba(0,0,0,0.04)] border border-gray-100 text-center flex flex-col items-center">

          <div class="h-48 w-full bg-[#F0F2F5] rounded-3xl relative flex justify-center items-end overflow-hidden perspective-[1000px] mb-8">
            <div id="mascot-lawyer" class="mascot-body shape-boss">
              <div class="mascot-eyes glasses-wrapper drop-shadow-sm absolute inset-x-0 mx-auto w-fit top-4">
                <div class="glasses-bridge"></div>
                <div class="glasses-frame glasses-lg relative z-20"><div class="w-2 h-2 rounded-full bg-black eye-dot" style="animation-delay: 2s;"></div></div>
                <div class="glasses-frame glasses-lg relative z-20"><div class="w-2 h-2 rounded-full bg-black eye-dot" style="animation-delay: 2s;"></div></div>
              </div>
            </div>
          </div>

          <h3 class="text-2xl font-extrabold text-gray-900 mb-2">Legal Team</h3>
          <p class="text-gray-500 font-medium mb-6">Our compliance officer is making sure you read the fine print.</p>

          <div class="w-full h-px bg-gray-100 mb-6"></div>

          <ul class="text-left space-y-4 w-full">
            <li><a href="#section-1" class="text-gray-500 font-bold hover:text-[#0D6EFD] transition-colors flex items-center gap-2"><i class="ph-bold ph-caret-right"></i> Acceptance</a></li>
            <li><a href="#section-2" class="text-gray-500 font-bold hover:text-[#0D6EFD] transition-colors flex items-center gap-2"><i class="ph-bold ph-caret-right"></i> Eligibility</a></li>
            <li><a href="#section-3" class="text-gray-500 font-bold hover:text-[#0D6EFD] transition-colors flex items-center gap-2"><i class="ph-bold ph-caret-right"></i> Booking Rules</a></li>
            <li><a href="#section-4" class="text-gray-500 font-bold hover:text-[#0D6EFD] transition-colors flex items-center gap-2"><i class="ph-bold ph-caret-right"></i> Vehicle Usage</a></li>
          </ul>
        </div>
      </div>

      <div class="lg:col-span-8 space-y-8">

        <div id="section-1" class="tos-card reveal">
          <div class="flex items-center gap-4 mb-6">
            <div class="bg-blue-50 p-3 rounded-xl"><i class="ph-fill ph-check-circle text-2xl text-[#0D6EFD]"></i></div>
            <h2 class="text-2xl font-extrabold text-gray-900">1. Acceptance of Terms</h2>
          </div>
          <p class="text-lg text-gray-600 leading-relaxed font-medium">By accessing or using the DriveAway website, mobile application, and physical rental services, you agree to be bound by these Terms of Service. If you do not agree with any part of these terms, you are prohibited from utilizing our platform or renting our vehicles.</p>
        </div>

        <div id="section-2" class="tos-card reveal">
          <div class="flex items-center gap-4 mb-6">
            <div class="bg-blue-50 p-3 rounded-xl"><i class="ph-fill ph-identification-card text-2xl text-[#0D6EFD]"></i></div>
            <h2 class="text-2xl font-extrabold text-gray-900">2. Rental Eligibility</h2>
          </div>
          <p class="text-lg text-gray-600 leading-relaxed font-medium mb-4">To successfully rent and operate a vehicle from DriveAway, you must meet the following mandatory criteria:</p>
          <ul class="list-disc list-inside text-lg text-gray-600 leading-relaxed font-medium space-y-2 ml-4">
            <li>Be at least 21 years of age at the time of booking.</li>
            <li>Possess a valid, unexpired National Identity Card (NIC) or Passport.</li>
            <li>Possess a valid Sri Lankan Driver's License appropriate for the vehicle class.</li>
            <li>Provide a valid form of digital or physical payment for the security deposit.</li>
          </ul>
          <p class="text-lg text-gray-600 leading-relaxed font-medium mt-4">We reserve the right to refuse service to anyone at our discretion if security or safety concerns arise.</p>
        </div>

        <div id="section-3" class="tos-card reveal">
          <div class="flex items-center gap-4 mb-6">
            <div class="bg-blue-50 p-3 rounded-xl"><i class="ph-fill ph-calendar-x text-2xl text-[#0D6EFD]"></i></div>
            <h2 class="text-2xl font-extrabold text-gray-900">3. Booking & Cancellation</h2>
          </div>
          <p class="text-lg text-gray-600 leading-relaxed font-medium mb-4">Reservations can be made securely through our online portal. Once a vehicle is booked, it is removed from public inventory.</p>
          <p class="text-lg text-gray-600 leading-relaxed font-medium">Cancellation policies vary based on the specific vehicle class and high-demand seasons. Generally, cancellations made less than 24 hours before the scheduled pickup time will incur a late-cancellation fee equivalent to one day's rental cost. Please refer to your booking confirmation email for specific details.</p>
        </div>

        <div id="section-4" class="tos-card reveal">
          <div class="flex items-center gap-4 mb-6">
            <div class="bg-blue-50 p-3 rounded-xl"><i class="ph-fill ph-steering-wheel text-2xl text-[#0D6EFD]"></i></div>
            <h2 class="text-2xl font-extrabold text-gray-900">4. Vehicle Usage</h2>
          </div>
          <p class="text-lg text-gray-600 leading-relaxed font-medium mb-4">DriveAway vehicles are provided in fully serviced, road-worthy condition. Renters must adhere to the following usage rules:</p>
          <ul class="list-disc list-inside text-lg text-gray-600 leading-relaxed font-medium space-y-2 ml-4">
            <li>Vehicles must be operated in strict accordance with all Sri Lankan traffic laws.</li>
            <li>Smoking, vaping, or the transportation of hazardous materials inside the vehicle is strictly prohibited.</li>
            <li>The renter assumes full financial responsibility for any traffic fines, minor cosmetic damages, or incidents resulting from negligence during the rental period.</li>
          </ul>
        </div>

        <div id="section-5" class="tos-card reveal">
          <div class="flex items-center gap-4 mb-6">
            <div class="bg-blue-50 p-3 rounded-xl"><i class="ph-fill ph-shield-warning text-2xl text-[#0D6EFD]"></i></div>
            <h2 class="text-2xl font-extrabold text-gray-900">5. Liability & Modifications</h2>
          </div>
          <p class="text-lg text-gray-600 leading-relaxed font-medium mb-4">DriveAway is not liable for any indirect, incidental, or consequential damages arising from the use of our vehicles or online services. Our total liability is legally limited to the rental amount paid by the customer.</p>
          <p class="text-lg text-gray-600 leading-relaxed font-medium">We reserve the right to update or modify these terms at any time. Continued use of our services following any changes constitutes your formal acceptance of the updated terms.</p>
        </div>

        <div class="reveal pt-8 border-t border-gray-200">
          <button id="agreeBtn" class="w-full md:w-auto bg-[#1A1C23] text-white font-bold py-5 px-12 rounded-2xl hover:bg-black transition-all transform hover:-translate-y-1 shadow-xl text-xl tracking-wide flex justify-center items-center gap-3 mx-auto">
            I Understand & Agree <i class="ph-bold ph-arrow-right"></i>
          </button>
          <div id="successMsg" class="hidden justify-center items-center gap-3 text-green-600 font-bold text-xl mt-6 text-center">
            <i class="ph-fill ph-check-circle text-3xl"></i> Thank you. You may now proceed.
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
      entries.forEach(entry => { if (entry.isIntersecting) entry.target.classList.add('active'); });
    }, { threshold: 0.1 });
    document.querySelectorAll('.reveal').forEach(el => observer.observe(el));
  });

  // 2. Interactive "Auditor" Mascot Logic
  class Mascot {
    constructor(id, weight, maxRotation) {
      this.body = document.getElementById(id);
      this.eyes = this.body.querySelector('.mascot-eyes');
      this.eyeDots = this.body.querySelectorAll('.eye-dot');
      this.weight = weight;
      this.maxRotation = maxRotation;
      this.currentX = 0; this.currentY = 0;
      this.targetX = 0;  this.targetY = 0;
    }

    lookAt(x, y) {
      this.targetX = x;
      this.targetY = y;
    }

    setSquint(squint) {
      this.eyeDots.forEach(dot => {
        if (squint) {
          dot.style.transform = 'scaleY(0.15) translateY(2px)';
          dot.style.animation = 'none';
        } else {
          dot.style.transform = '';
          dot.style.animation = '';
        }
      });
    }

    update() {
      this.currentX += (this.targetX - this.currentX) * this.weight;
      this.currentY += (this.targetY - this.currentY) * this.weight;

      const moveX = this.currentX * 15;
      const moveY = this.currentY * 15;
      const rotX = this.currentY * -(this.maxRotation * 1.2);
      const rotY = this.currentX * (this.maxRotation * 1.2);

      this.body.style.transform = 'translate3d(' + moveX + 'px, ' + moveY + 'px, 0) ' +
              'rotateX(' + rotX + 'deg) rotateY(' + rotY + 'deg)';

      this.eyes.style.transform = 'translateZ(40px) translate(' + (this.currentX * 25) + 'px, ' + (this.currentY * 25) + 'px)';
    }
  }

  // Initialize the Legal Mascot only if on desktop (he's hidden on mobile)
  let lawyerMascot = null;
  if(document.getElementById('mascot-lawyer')) {
    lawyerMascot = new Mascot('mascot-lawyer', 0.08, 25);
  }

  let isHoveringButton = false;

  // Track mouse to make him watch you read the document
  document.addEventListener('mousemove', (e) => {
    if(!lawyerMascot || isHoveringButton) return;

    const mouseX = (e.clientX / window.innerWidth) * 2 - 1;
    const mouseY = (e.clientY / window.innerHeight) * 2 - 1;

    lawyerMascot.lookAt(mouseX, mouseY);
  });

  // Start animation loop
  function renderLoop() {
    if(lawyerMascot) lawyerMascot.update();
    requestAnimationFrame(renderLoop);
  }
  requestAnimationFrame(renderLoop);

  // 3. Interactive Agreement Button
  const agreeBtn = document.getElementById('agreeBtn');
  const successMsg = document.getElementById('successMsg');

  agreeBtn.addEventListener('mouseenter', () => {
    isHoveringButton = true;
    if(lawyerMascot) {
      lawyerMascot.lookAt(0.5, 0.8); // Look down toward the button
    }
  });

  agreeBtn.addEventListener('mouseleave', () => {
    isHoveringButton = false;
  });

  agreeBtn.addEventListener('click', () => {
    // Hide button, show success
    agreeBtn.style.display = 'none';
    successMsg.classList.remove('hidden');
    successMsg.classList.add('flex');

    // Mascot does a happy squint when you agree to the terms!
    if(lawyerMascot) {
      lawyerMascot.lookAt(0, 0); // Look straight at user
      lawyerMascot.setSquint(true);
    }
  });

  // 4. Page Transition Logic
  window.addEventListener('load', () => {
    document.body.classList.add('page-loaded');
  });
  document.querySelectorAll('.transition-link, .nav-link').forEach(link => {
    link.addEventListener('click', function(e) {
      if(this.href.includes('#')) return; // Ignore anchor links used in the ToS sidebar
      e.preventDefault();
      const targetUrl = this.href;
      document.body.classList.remove('page-loaded');
      setTimeout(() => { window.location.href = targetUrl; }, 150);
    });
  });
</script>
</body>
</html>