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

    /* Input overrides matching auth pages */
    input:-webkit-autofill, textarea:-webkit-autofill {
      -webkit-box-shadow: 0 0 0 30px white inset !important;
    }

    /* --- INTERACTIVE MASCOT CSS --- */
    .mascot-body {
      position: absolute;
      transform-style: preserve-3d;
      will-change: transform;
      width: 7rem;
      height: 9rem;
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

    .shape-yellow-girl {
      background: linear-gradient(135deg, #FFE044, #D6A200);
      box-shadow: inset 3px 3px 10px rgba(255,255,255,0.6), inset -3px -3px 10px rgba(0,0,0,0.2), 10px 20px 30px rgba(251, 207, 20, 0.3);
      border-radius: 3.5rem 3.5rem 0 0;
    }
    .glasses-wrapper { display: flex; gap: 0.1rem; position: relative; align-items: center; justify-content: center; }
    .glasses-frame {
      border: 3px solid #1F2937;
      border-radius: 50%;
      display: flex; justify-content: center; align-items: center;
      background: rgba(255, 255, 255, 0.2);
      box-shadow: 1px 2px 4px rgba(0,0,0,0.15);
    }
    .glasses-sm { width: 1.1rem; height: 1.1rem; border-width: 2px; }
    .glasses-bridge {
      position: absolute; width: 0.4rem; height: 3px; background-color: #1F2937;
      left: 50%; transform: translateX(-50%); top: 50%; z-index: 10;
    }

    /* Floating Background Art */
    .brand-shape { position: absolute; opacity: 0.15; z-index: 0; pointer-events: none; animation: float 6s ease-in-out infinite; }
    @keyframes float { 0%, 100% { transform: translateY(0) rotate(0deg); } 50% { transform: translateY(-20px) rotate(2deg); } }
  </style>
</head>
<body class="text-gray-900 antialiased">

<jsp:include page="navbar.jsp" />

<main class="relative">
  <div class="brand-shape w-64 h-64 rounded-[4rem] bg-[#0D6EFD] top-20 left-[-5%] transform -rotate-12"></div>
  <div class="brand-shape w-40 h-40 rounded-full bg-[#7A52FA] top-40 right-[10%] transform rotate-12" style="animation-delay: 2s;"></div>

  <section class="pt-40 pb-16 relative z-10">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 text-center">
      <div class="reveal">
        <div class="inline-flex items-center justify-center bg-blue-50 p-4 rounded-2xl mb-6">
          <i class="ph-fill ph-paper-plane-tilt text-4xl text-[#0D6EFD]"></i>
        </div>
        <h1 class="text-[3.5rem] md:text-[4.5rem] font-extrabold mb-4 tracking-tight leading-tight">Get in <span class="gradient-text">Touch</span></h1>
        <p class="text-xl text-gray-500 max-w-2xl mx-auto font-medium">Have a question about our fleet? Need support with a booking? Our team is ready to help.</p>
      </div>
    </div>
  </section>

  <section class="pb-32 relative z-10">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 grid lg:grid-cols-12 gap-16 items-start">

      <div class="lg:col-span-5 reveal">
        <h2 class="text-3xl font-extrabold mb-8 tracking-tight text-gray-900">Contact Information</h2>

        <div class="space-y-6">
          <div class="bg-white p-6 rounded-3xl shadow-[0_10px_30px_rgba(0,0,0,0.03)] border border-gray-100 flex items-start gap-5 hover:shadow-[0_20px_40px_rgba(13,110,253,0.08)] transition-all duration-300 group cursor-pointer">
            <div class="bg-blue-50 p-4 rounded-2xl group-hover:bg-[#0D6EFD] transition-colors duration-300">
              <i class="ph-fill ph-map-pin text-2xl text-[#0D6EFD] group-hover:text-white transition-colors duration-300"></i>
            </div>
            <div>
              <h4 class="text-sm font-bold text-gray-400 uppercase tracking-wider mb-1">Our Headquarters</h4>
              <p class="text-lg font-bold text-gray-900">123 Galle Road,<br>Colombo 03, Sri Lanka</p>
            </div>
          </div>

          <div class="bg-white p-6 rounded-3xl shadow-[0_10px_30px_rgba(0,0,0,0.03)] border border-gray-100 flex items-start gap-5 hover:shadow-[0_20px_40px_rgba(13,110,253,0.08)] transition-all duration-300 group cursor-pointer">
            <div class="bg-blue-50 p-4 rounded-2xl group-hover:bg-[#0D6EFD] transition-colors duration-300">
              <i class="ph-fill ph-phone text-2xl text-[#0D6EFD] group-hover:text-white transition-colors duration-300"></i>
            </div>
            <div>
              <h4 class="text-sm font-bold text-gray-400 uppercase tracking-wider mb-1">Call Us</h4>
              <p class="text-lg font-bold text-gray-900">+94 11 234 5678</p>
              <p class="text-sm text-gray-500 font-medium mt-1">Mon - Sat: 8:00 AM - 6:00 PM</p>
            </div>
          </div>

          <div class="bg-white p-6 rounded-3xl shadow-[0_10px_30px_rgba(0,0,0,0.03)] border border-gray-100 flex items-start gap-5 hover:shadow-[0_20px_40px_rgba(13,110,253,0.08)] transition-all duration-300 group cursor-pointer">
            <div class="bg-blue-50 p-4 rounded-2xl group-hover:bg-[#0D6EFD] transition-colors duration-300">
              <i class="ph-fill ph-envelope text-2xl text-[#0D6EFD] group-hover:text-white transition-colors duration-300"></i>
            </div>
            <div>
              <h4 class="text-sm font-bold text-gray-400 uppercase tracking-wider mb-1">Email Us</h4>
              <p class="text-lg font-bold text-gray-900">hello@driveaway.lk</p>
              <p class="text-sm text-gray-500 font-medium mt-1">Expect a reply within 2 hours</p>
            </div>
          </div>
        </div>
      </div>

      <div class="lg:col-span-7 reveal relative pt-12">

        <div class="absolute -top-8 right-16 z-0 perspective-[1000px]">
          <div id="mascot-support" class="mascot-body shape-yellow-girl">
            <div class="mascot-eyes glasses-wrapper drop-shadow-sm absolute inset-x-0 mx-auto w-fit top-4">
              <div class="glasses-bridge"></div>
              <div class="glasses-frame glasses-sm relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 1.5s;"></div></div>
              <div class="glasses-frame glasses-sm relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 1.5s;"></div></div>
            </div>
          </div>
        </div>

        <div class="bg-white p-10 md:p-12 rounded-[2.5rem] shadow-[0_30px_60px_rgba(0,0,0,0.08)] border border-gray-100 relative z-10">
          <h2 class="text-3xl font-extrabold mb-8 tracking-tight">Send a Message</h2>

          <form action="#" method="POST" id="contactForm" class="space-y-8">

            <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
              <div>
                <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Full Name</label>
                <input type="text" name="name" required placeholder="John Doe"
                       class="inspectable-input w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-xl font-bold bg-transparent">
              </div>
              <div>
                <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Email Address</label>
                <input type="email" name="email" required placeholder="john@example.com"
                       class="inspectable-input w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-xl font-bold bg-transparent">
              </div>
            </div>

            <div>
              <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Message</label>
              <textarea name="message" rows="4" required placeholder="How can we help you today?"
                        class="inspectable-input w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-xl font-bold bg-transparent resize-none"></textarea>
            </div>

            <button type="submit" class="w-full bg-[#0D6EFD] text-white font-bold py-5 rounded-2xl hover:bg-blue-700 transition-transform transform hover:-translate-y-1 shadow-lg hover:shadow-[#0D6EFD]/30 mt-4 text-xl tracking-wide flex justify-center items-center gap-3">
              Send Message <i class="ph-bold ph-paper-plane-right"></i>
            </button>
          </form>

          <div id="formStatus" class="mt-6 p-4 rounded-xl bg-green-50 border border-green-200 text-green-700 font-bold text-center hidden items-center justify-center gap-2">
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

  // 2. Interactive Support Mascot Logic
  class Mascot {
    constructor(id, weight, maxRotation) {
      this.body = document.getElementById(id);
      this.eyes = this.body.querySelector('.mascot-eyes');
      this.eyeDots = this.body.querySelectorAll('.eye-dot');
      this.weight = weight;
      this.maxRotation = maxRotation;
      this.currentX = 0; this.currentY = 0;
      this.targetX = 0;  this.targetY = 0;
      this.scale = 1;
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
              'rotateX(' + rotX + 'deg) rotateY(' + rotY + 'deg) ' +
              'scale(' + this.scale + ')';

      this.eyes.style.transform = 'translateZ(40px) translate(' + (this.currentX * 25) + 'px, ' + (this.currentY * 25) + 'px)';
    }
  }

  // Initialize the Support Mascot
  const supportMascot = new Mascot('mascot-support', 0.08, 25);
  const inspectableInputs = document.querySelectorAll('.inspectable-input');

  let globalMouseX = -0.5; // She looks slightly left by default
  let globalMouseY = 0;
  let activeState = 'idle';

  function evaluateState() {
    const isInspecting = Array.from(inspectableInputs).includes(document.activeElement);

    if (isInspecting) {
      activeState = 'inspecting';
      supportMascot.scale = 1.05; // Leans in slightly
      supportMascot.setSquint(false);
      updateTypingTrack();
    } else {
      activeState = 'idle';
      supportMascot.scale = 1;
      supportMascot.setSquint(false);
      supportMascot.lookAt(globalMouseX, globalMouseY);
    }
  }

  function updateTypingTrack() {
    if (activeState !== 'inspecting') return;
    const activeInput = document.activeElement;

    // Calculate how far along the input they have typed to track the text
    const typeProgress = Math.min((activeInput.value || '').length / 25, 1);

    // She looks DOWN at the form (Y = 0.5) and sweeps LEFT to RIGHT as you type
    const lookX = -0.6 + (typeProgress * 0.8);
    supportMascot.lookAt(lookX, 0.5);
  }

  // Attach listeners to all inputs and textareas
  inspectableInputs.forEach(input => {
    input.addEventListener('focus', evaluateState);
    input.addEventListener('blur', evaluateState);
    input.addEventListener('input', updateTypingTrack);
  });

  // Track mouse globally
  document.addEventListener('mousemove', (e) => {
    globalMouseX = (e.clientX / window.innerWidth) * 2 - 1;
    globalMouseY = (e.clientY / window.innerHeight) * 2 - 1;

    if (activeState === 'idle') {
      supportMascot.lookAt(globalMouseX, globalMouseY);
    }
  });

  function renderLoop() {
    supportMascot.update();
    requestAnimationFrame(renderLoop);
  }
  requestAnimationFrame(renderLoop);

  // 3. Simple Form Submission UI State
  const form = document.getElementById('contactForm');
  const status = document.getElementById('formStatus');

  form.addEventListener('submit', (e) => {
    e.preventDefault();

    // Mascot looks incredibly happy/squints when submitted!
    activeState = 'submitted';
    supportMascot.scale = 1.1;
    supportMascot.lookAt(0, 0.8); // Looks straight down at the success message
    supportMascot.setSquint(true);

    status.classList.remove('hidden');
    status.classList.add('flex');
    status.innerHTML = '<i class="ph-fill ph-check-circle text-2xl"></i> Thank you! We will get back to you soon.';
    form.reset();

    setTimeout(() => {
      status.classList.remove('flex');
      status.classList.add('hidden');
      activeState = 'idle';
      evaluateState();
    }, 4000);
  });

  // 4. Page Transition Logic
  window.addEventListener('load', () => {
    document.body.classList.add('page-loaded');
  });
  document.querySelectorAll('.transition-link').forEach(link => {
    link.addEventListener('click', function(e) {
      e.preventDefault();
      const targetUrl = this.href;
      document.body.classList.remove('page-loaded');
      setTimeout(() => { window.location.href = targetUrl; }, 150);
    });
  });
</script>
</body>
</html>