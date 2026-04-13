<%@ page contentType="text/html; charset=UTF-8" %>

<%
  Boolean sessionAdmin = (Boolean) session.getAttribute("adminLoggedIn");
  boolean isAdmin = (sessionAdmin != null && sessionAdmin);
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>DriveAway | Register Account</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <style>
    /* --- FASTER PAGE TRANSITION CSS --- */
    body {
      font-family: 'Inter', sans-serif;
      overflow-x: hidden;
      opacity: 0;
      transition: opacity 0.15s ease-out; /* Reduced from 0.4s to 0.15s */
    }
    body.page-loaded {
      opacity: 1;
    }

    input:-webkit-autofill {
      -webkit-box-shadow: 0 0 0 30px white inset !important;
    }

    #art-container {
      transition: background-color 0.5s ease;
    }
    #art-container.alert-mode {
      background-color: #FFE5E5;
    }

    .mascot-body {
      position: absolute;
      transform-style: preserve-3d;
      will-change: transform;
    }

    /* FIX 1: Changed to relative so flexbox handles the perfect centering */
    .mascot-eyes {
      position: relative;
      transform: translateZ(40px);
      will-change: transform;
      z-index: 2;
    }

    .mascot-prop {
      position: absolute;
      opacity: 0;
      transform: translateY(20px) translateZ(50px) scale(0.5);
      transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
      pointer-events: none;
    }
    .show-prop {
      opacity: 1;
      transform: translateY(0px) translateZ(60px) scale(1);
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

    /* --- THE 4 GUYS DESIGNS --- */
    .shape-boss {
      background: linear-gradient(135deg, #EF4444, #B91C1C);
      box-shadow: inset 4px 4px 15px rgba(255,255,255,0.3), inset -4px -4px 15px rgba(0,0,0,0.3), 10px 20px 30px rgba(239, 68, 68, 0.3);
      width: 8rem; height: 16rem; border-radius: 1rem 1rem 0 0;
      left: -1rem; bottom: 0; z-index: 10;
    }
    .shape-guard-cyan {
      background: linear-gradient(135deg, #00F2FE, #0088FF);
      box-shadow: inset 4px 4px 15px rgba(255,255,255,0.4), inset -4px -4px 15px rgba(0,0,0,0.3), 10px 20px 30px rgba(0, 136, 255, 0.3);
      width: 6rem; height: 12rem; border-radius: 1rem 1rem 0 0;
      left: 8rem; bottom: 0; z-index: 20;
    }
    .shape-rookie {
      background: linear-gradient(135deg, #FB923C, #C2410C);
      box-shadow: inset 4px 4px 15px rgba(255,255,255,0.6), inset -4px -4px 15px rgba(0,0,0,0.2), 10px 20px 30px rgba(251, 146, 60, 0.3);
      width: 7rem; height: 8rem; border-radius: 3rem 3rem 0 1rem;
      left: 14rem; bottom: 0; z-index: 30;
    }
    .shape-tall-black {
      background: linear-gradient(135deg, #2A2D35, #0D0F16);
      box-shadow: inset 4px 4px 15px rgba(255,255,255,0.1), inset -4px -4px 15px rgba(0,0,0,0.6), 10px 20px 30px rgba(0, 0, 0, 0.4);
      width: 6rem; height: 21rem; border-radius: 2rem 2rem 0 0;
      left: 3.5rem; bottom: 0; z-index: 5;
    }

    /* --- THE 2 GIRLS DESIGNS --- */
    .shape-hijab {
      background: linear-gradient(135deg, #7A52FA, #4A12DA);
      box-shadow: inset 4px 4px 15px rgba(255,255,255,0.4), inset -4px -4px 15px rgba(0,0,0,0.5), 10px 20px 30px rgba(90, 50, 250, 0.4);
      width: 5.5rem; height: 11.5rem; border-radius: 3rem 3rem 0 0;
      left: 4.5rem; bottom: 0; z-index: 25;
    }
    .hijab-face {
      background: linear-gradient(135deg, #FFEDD5, #FDBA74);
      box-shadow: inset 0px 5px 10px rgba(0,0,0,0.2), 0px 4px 6px rgba(0,0,0,0.1);
      width: 3.5rem; height: 5rem; border-radius: 2rem 2rem 1.5rem 1.5rem;
      margin-top: 1rem;
    }

    .shape-yellow-girl {
      background: linear-gradient(135deg, #FBBF24, #B45309);
      box-shadow: inset 3px 3px 10px rgba(255,255,255,0.6), inset -3px -3px 10px rgba(0,0,0,0.2), 10px 20px 30px rgba(251, 191, 36, 0.3);
      width: 4.5rem; height: 5.5rem; border-radius: 4rem 4rem 0 0;
      left: 12.5rem; bottom: 0; z-index: 35;
    }

    /* --- GLASSES ACCESSORY CSS --- */
    /* FIX 2: Removed position: relative from here to prevent fighting with .mascot-eyes */
    .glasses-wrapper { display: flex; gap: 0.1rem; align-items: center; justify-content: center; }
    .glasses-frame {
      border: 3px solid #1F2937;
      border-radius: 50%;
      display: flex; justify-content: center; align-items: center;
      background: rgba(255, 255, 255, 0.2);
      box-shadow: 1px 2px 4px rgba(0,0,0,0.15);
    }
    .glasses-lg { width: 1.5rem; height: 1.5rem; }
    .glasses-sm { width: 1.1rem; height: 1.1rem; border-width: 2px; }
    .glasses-bridge {
      position: absolute; width: 0.4rem; height: 3px; background-color: #1F2937;
      left: 50%; transform: translateX(-50%); top: 50%; z-index: 10;
    }
  </style>
</head>
<body class="min-h-screen flex bg-white text-gray-900 relative">

<div class="w-full lg:w-1/2 flex items-center justify-center p-8 sm:p-16 relative min-h-screen z-50 shadow-[20px_0_50px_rgba(0,0,0,0.05)]">

  <div class="absolute top-8 left-8 sm:top-12 sm:left-12">
    <a href='<%= isAdmin ? "admin-home.jsp" : "index.jsp" %>' class="transition-link flex items-center gap-2 text-base font-bold text-gray-500 hover:text-black transition-colors">
      <i class="ph-bold ph-arrow-left text-2xl"></i> Back
    </a>
  </div>

  <div class="w-full max-w-[650px] mt-16 lg:mt-0">
    <div class="mb-12">
      <div class="flex mb-4">
        <div class="bg-gray-100 p-4 rounded-2xl">
          <i class="ph-fill ph-user-plus text-4xl text-[#0D6EFD]"></i>
        </div>
      </div>
      <h2 class="text-[2.5rem] font-extrabold mb-3 tracking-tight leading-tight">Create an Account</h2>
      <p class="text-gray-500 font-medium text-lg">Join DriveAway to start booking instantly.</p>
    </div>

    <% if ("username_taken".equals(request.getParameter("error"))) { %>
    <div id="error-alert" class="bg-red-50 p-5 rounded-xl border border-red-200 text-base text-red-600 font-bold flex items-center gap-3 mb-10 shadow-sm">
      <i class="ph-bold ph-warning-circle text-2xl"></i>
      Username is already taken. Please try another.
    </div>
    <% } %>

    <form action="customers" method="POST" class="space-y-8">
      <input type="hidden" name="action" value="register">

      <div class="grid grid-cols-1 md:grid-cols-2 gap-10">
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Full Name <span class="text-red-500">*</span></label>
          <input type="text" name="name" required placeholder="John Doe"
                 class="inspectable-input w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-xl font-bold bg-transparent">
        </div>

        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Username <span class="text-red-500">*</span></label>
          <input type="text" name="username" required placeholder="johndoe123"
                 class="inspectable-input w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-xl font-bold bg-transparent">
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-10">
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Email Address <span class="text-red-500">*</span></label>
          <input type="email" name="email" required placeholder="john@example.com"
                 class="inspectable-input w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-xl font-bold bg-transparent">
        </div>

        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Phone Number <span class="text-red-500">*</span></label>
          <input type="tel" name="phone" pattern="[0-9]{10}" title="Please enter exactly 10 digits" required placeholder="0712345678"
                 class="inspectable-input w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-xl font-bold bg-transparent">
        </div>
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Password <span class="text-red-500">*</span></label>
        <div class="relative" id="passwordWrapper">
          <input type="password" name="password" id="passwordInput" required placeholder="••••••••"
                 class="w-full border-b-2 border-gray-300 py-3 pr-12 focus:border-[#0D6EFD] outline-none transition-colors text-xl font-bold bg-transparent">
          <i id="togglePasswordBtn" class="ph-bold ph-eye absolute right-2 top-3 text-gray-400 cursor-pointer hover:text-black text-2xl transition-all hover:scale-110"></i>
        </div>
      </div>

      <div class="mt-10 pt-8 border-t border-gray-100">
        <p class="text-sm font-bold text-gray-400 uppercase tracking-wider mb-6">Verification (Optional)</p>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-10">
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">NIC Number</label>
            <input type="text" name="nic" placeholder="e.g. 199912345678"
                   class="inspectable-input w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-xl font-bold bg-transparent">
          </div>
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Driver's License</label>
            <input type="text" name="license" placeholder="e.g. B1234567"
                   class="inspectable-input w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-xl font-bold bg-transparent">
          </div>
        </div>
      </div>

      <button type="submit" class="w-full bg-[#0D6EFD] text-white font-bold py-5 rounded-2xl hover:bg-blue-700 transition-transform transform hover:-translate-y-1 shadow-lg hover:shadow-[#0D6EFD]/30 mt-10 text-xl tracking-wide flex justify-center items-center gap-3">
        Register Account <i class="ph-bold ph-arrow-right"></i>
      </button>
    </form>

    <% if (!isAdmin) { %>
    <div class="mt-12 pt-8 border-t border-gray-100 text-center flex justify-between items-center">
      <p class="text-sm font-medium text-gray-500">
        Already have an account?
        <a href="login.jsp" class="transition-link text-black font-bold hover:underline">Sign In</a>
      </p>
    </div>
    <% } %>
  </div>
</div>

<div id="art-container" class="hidden lg:flex lg:w-1/2 bg-[#F0F2F5] items-end justify-center overflow-hidden pb-16 lg:h-screen lg:sticky lg:top-0">
  <div class="relative w-80 h-80 flex items-end justify-center transform scale-125 xl:scale-[1.3] origin-bottom perspective-[1000px]">

    <div id="mascot-tall-black" class="mascot-body shape-tall-black flex justify-center pt-8">
      <div class="mascot-eyes flex gap-3 drop-shadow-md">
        <div class="w-2 h-2 rounded-full bg-white eye-dot" style="animation-delay: 4s;"></div>
        <div class="w-2 h-2 rounded-full bg-white eye-dot" style="animation-delay: 4s;"></div>
      </div>
    </div>

    <div id="mascot-boss" class="mascot-body shape-boss flex justify-center pt-6">
      <div class="mascot-eyes flex gap-4 drop-shadow-md">
        <div class="w-2.5 h-2.5 rounded-full bg-white eye-dot"></div>
        <div class="w-2.5 h-2.5 rounded-full bg-white eye-dot"></div>
      </div>
    </div>

    <div id="mascot-hijab" class="mascot-body shape-hijab flex justify-center">
      <div class="hijab-face flex justify-center items-center">
        <div class="mascot-eyes glasses-wrapper drop-shadow-sm">
          <div class="glasses-bridge"></div>
          <div class="glasses-frame glasses-lg relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 3s;"></div></div>
          <div class="glasses-frame glasses-lg relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 3s;"></div></div>
        </div>
      </div>
    </div>

    <div id="mascot-guard-cyan" class="mascot-body shape-guard-cyan flex justify-center pt-6">
      <div class="mascot-eyes flex gap-3 drop-shadow-md">
        <div class="w-2.5 h-2.5 rounded-full bg-black eye-dot" style="animation-delay: 1s;"></div>
        <div class="w-2.5 h-2.5 rounded-full bg-black eye-dot" style="animation-delay: 1s;"></div>
      </div>
      <div id="prop-shield" class="mascot-prop text-gray-800 left-12 top-4">
        <i class="ph-fill ph-shield-check text-[5rem] drop-shadow-2xl"></i>
      </div>
    </div>

    <div id="mascot-rookie" class="mascot-body shape-rookie flex justify-center pt-6">
      <div class="mascot-eyes flex gap-2 drop-shadow-sm">
        <div class="w-2 h-2 rounded-full bg-black eye-dot" style="animation-delay: 2s;"></div>
        <div class="w-2 h-2 rounded-full bg-black eye-dot" style="animation-delay: 2s;"></div>
      </div>
    </div>

    <div id="mascot-yellow-girl" class="mascot-body shape-yellow-girl flex justify-center pt-4">
      <div class="mascot-eyes glasses-wrapper drop-shadow-sm">
        <div class="glasses-bridge"></div>
        <div class="glasses-frame glasses-sm relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 1.5s;"></div></div>
        <div class="glasses-frame glasses-sm relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 1.5s;"></div></div>
      </div>
    </div>

  </div>
</div>

<script>
  class Mascot {
    constructor(id, weight, maxRotation, baseZ) {
      this.body = document.getElementById(id);
      this.eyes = this.body.querySelector('.mascot-eyes');
      this.eyeDots = this.body.querySelectorAll('.eye-dot');

      this.weight = weight;
      this.maxRotation = maxRotation;
      this.baseZ = baseZ;

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

      const moveX = this.currentX * 25;
      const moveY = this.currentY * 25;
      const rotX = this.currentY * -(this.maxRotation * 1.5);
      const rotY = this.currentX * (this.maxRotation * 1.5);

      this.body.style.transform = 'translate3d(' + moveX + 'px, ' + moveY + 'px, ' + this.baseZ + 'px) ' +
              'rotateX(' + rotX + 'deg) rotateY(' + rotY + 'deg) ' +
              'scale(' + this.scale + ')';

      this.eyes.style.transform = 'translateZ(45px) ' +
              'translate(' + (this.currentX * 30) + 'px, ' + (this.currentY * 30) + 'px)';
    }
  }

  const tallBlackGuy = new Mascot('mascot-tall-black', 0.015, 10, -20);
  const bossGuy = new Mascot('mascot-boss', 0.03, 15, 0);
  const hijabGirl = new Mascot('mascot-hijab', 0.08, 22, 5);
  const guardCyanGuy = new Mascot('mascot-guard-cyan', 0.05, 20, 10);
  const rookieGuy = new Mascot('mascot-rookie', 0.12, 30, 20);
  const yellowGirl = new Mascot('mascot-yellow-girl', 0.18, 40, 25);

  const mascots = [tallBlackGuy, bossGuy, hijabGirl, guardCyanGuy, rookieGuy, yellowGirl];

  const inspectableInputs = document.querySelectorAll('.inspectable-input');
  const passwordInput = document.getElementById('passwordInput');
  const passwordWrapper = document.getElementById('passwordWrapper');
  const toggleBtn = document.getElementById('togglePasswordBtn');
  const propShield = document.getElementById('prop-shield');
  const artContainer = document.getElementById('art-container');

  let globalMouseX = -0.5;
  let globalMouseY = 0;
  let activeState = 'idle';
  let isHoveringPassword = false;

  if (document.getElementById('error-alert')) {
    activeState = 'sad';
    mascots.forEach(m => { m.lookAt(0, 0.8); m.setSquint(true); });
  }

  passwordWrapper.addEventListener('mouseenter', () => {
    isHoveringPassword = true;
    evaluateState();
  });
  passwordWrapper.addEventListener('mouseleave', () => {
    isHoveringPassword = false;
    evaluateState();
  });

  toggleBtn.addEventListener('click', () => {
    const isHidden = passwordInput.type === 'password';
    passwordInput.type = isHidden ? 'text' : 'password';

    toggleBtn.classList.toggle('ph-eye');
    toggleBtn.classList.toggle('ph-eye-closed');

    evaluateState();
  });

  function evaluateState() {
    if (activeState === 'sad') return;

    propShield.classList.remove('show-prop');
    artContainer.classList.remove('alert-mode');
    mascots.forEach(m => { m.scale = 1; m.setSquint(false); });

    const isInspecting = Array.from(inspectableInputs).includes(document.activeElement);

    if (isInspecting) {
      activeState = 'inspecting';
      updateTypingTrack();

    } else if (document.activeElement === passwordInput || isHoveringPassword) {
      if (passwordInput.type === 'password') {
        activeState = 'shielding';
        propShield.classList.add('show-prop');
        guardCyanGuy.scale = 1.1;

        tallBlackGuy.lookAt(1, -1);
        bossGuy.lookAt(0.8, -0.8);
        hijabGirl.lookAt(0.5, 0);
        guardCyanGuy.lookAt(-0.6, 0);
        rookieGuy.lookAt(0.8, 0.5);
        yellowGirl.lookAt(0.9, 0.9);
      } else {
        activeState = 'peeking';
        artContainer.classList.add('alert-mode');

        mascots.forEach(m => {
          m.scale = 1.05;
          m.lookAt(-0.7, 0.2);
          m.setSquint(true);
        });
      }
    } else {
      activeState = 'idle';
      mascots.forEach(m => m.lookAt(globalMouseX, globalMouseY));
    }
  }

  function updateTypingTrack() {
    if (activeState !== 'inspecting') return;
    const activeInput = document.activeElement;
    const typeProgress = Math.min((activeInput.value || '').length / 20, 1);
    const lookX = -0.6 + (typeProgress * 0.4);
    mascots.forEach(m => m.lookAt(lookX, 0.1));
  }

  inspectableInputs.forEach(input => {
    input.addEventListener('focus', evaluateState);
    input.addEventListener('blur', evaluateState);
    input.addEventListener('input', updateTypingTrack);
    input.addEventListener('input', () => { if(activeState === 'sad') { activeState = 'idle'; evaluateState(); } }, {once: true});
  });

  passwordInput.addEventListener('focus', evaluateState);
  passwordInput.addEventListener('blur', evaluateState);
  passwordInput.addEventListener('input', () => { if(activeState === 'sad') { activeState = 'idle'; evaluateState(); } }, {once: true});

  document.addEventListener('mousemove', (e) => {
    globalMouseX = (e.clientX / window.innerWidth) * 2 - 1;
    globalMouseY = (e.clientY / window.innerHeight) * 2 - 1;

    if (activeState === 'idle') {
      mascots.forEach(m => m.lookAt(globalMouseX, globalMouseY));
    }
  });

  function renderLoop() {
    mascots.forEach(m => m.update());
    requestAnimationFrame(renderLoop);
  }

  requestAnimationFrame(renderLoop);

  // --- FASTER PAGE TRANSITION LOGIC ---
  window.addEventListener('load', () => {
    document.body.classList.add('page-loaded');
  });

  document.querySelectorAll('.transition-link').forEach(link => {
    link.addEventListener('click', function(e) {
      e.preventDefault();
      const targetUrl = this.href;
      document.body.classList.remove('page-loaded');
      // Timeout reduced to 150ms for lightning-fast routing
      setTimeout(() => {
        window.location.href = targetUrl;
      }, 150);
    });
  });
</script>
</body>
</html>