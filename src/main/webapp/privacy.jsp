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

    /* --- PAGE TRANSITION CSS --- */
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

    /* --- CYAN GUARD MASCOT CSS --- */
    .mascot-body {
      position: absolute;
      transform-style: preserve-3d;
      will-change: transform;
      width: 8rem;
      height: 10rem;
    }
    .mascot-eyes {
      position: absolute;
      transform: translateZ(40px);
      will-change: transform;
      z-index: 2;
    }
    .mascot-prop {
      position: absolute;
      transform: translateY(10px) translateZ(60px) scale(1);
      transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
      pointer-events: none;
    }
    @keyframes blink {
      0%, 92%, 98%, 100% { transform: scaleY(1); }
      95% { transform: scaleY(0.1); }
    }
    .eye-dot { animation: blink 5s infinite; transform-origin: center; transition: transform 0.2s ease; }

    .shape-guard-cyan {
      background: linear-gradient(135deg, #00F2FE, #0088FF);
      box-shadow: inset 4px 4px 15px rgba(255,255,255,0.4), inset -4px -4px 15px rgba(0,0,0,0.3), 10px 20px 30px rgba(0, 136, 255, 0.3);
      border-radius: 2rem 2rem 0 0;
    }

    /* --- SCROLL SPY NAV STYLING --- */
    .spy-link {
      transition: all 0.3s ease;
      border-left: 3px solid transparent;
    }
    .spy-link.active {
      border-left-color: #0D6EFD;
      color: #0D6EFD;
      background-color: #EFF6FF;
      font-weight: 700;
    }

    /* Clean Card Styling */
    .privacy-card {
      background: white;
      border-radius: 1.5rem;
      padding: 2.5rem;
      border: 1px solid #F3F4F6;
      box-shadow: 0 4px 20px rgba(0,0,0,0.02);
      transition: transform 0.3s ease, box-shadow 0.3s ease;
      scroll-margin-top: 8rem;
    }
    .privacy-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 10px 30px rgba(13,110,253,0.05);
    }
  </style>
</head>
<body class="text-gray-900 antialiased relative">

<jsp:include page="navbar.jsp" />

<main class="relative">

  <section class="pt-40 pb-12 relative z-10 bg-white border-b border-gray-100">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 flex flex-col md:flex-row justify-between items-end gap-8">
      <div class="reveal max-w-2xl text-left">
        <div class="inline-flex items-center justify-center bg-gray-100 p-3 rounded-2xl mb-6">
          <i class="ph-fill ph-shield-check text-3xl text-gray-800"></i>
        </div>
        <h1 class="text-[3.5rem] md:text-[4.5rem] font-extrabold mb-2 tracking-tight leading-tight">Privacy <span class="gradient-text">Policy</span></h1>
        <p class="text-xl text-gray-500 font-medium">Your data is yours. We treat your security as our highest priority.</p>
      </div>

      <div class="reveal bg-[#F8FAFF] p-5 rounded-2xl border border-blue-100 shadow-sm flex flex-col gap-3 min-w-[280px]">
        <h4 class="text-xs font-bold text-gray-400 uppercase tracking-widest">Live Security Status</h4>
        <div class="flex items-center justify-between">
          <span class="text-sm font-bold text-gray-700 flex items-center gap-2"><i class="ph-bold ph-lock-key text-blue-500"></i> SSL Encryption</span>
          <span class="flex items-center gap-1.5 text-xs font-bold text-green-600 bg-green-50 px-2 py-1 rounded-md"><div class="w-1.5 h-1.5 rounded-full bg-green-500 animate-pulse"></div> Active</span>
        </div>
        <div class="flex items-center justify-between">
          <span class="text-sm font-bold text-gray-700 flex items-center gap-2"><i class="ph-bold ph-database text-blue-500"></i> Data Storage</span>
          <span class="flex items-center gap-1.5 text-xs font-bold text-green-600 bg-green-50 px-2 py-1 rounded-md"><div class="w-1.5 h-1.5 rounded-full bg-green-500 animate-pulse"></div> Secured</span>
        </div>
      </div>
    </div>
  </section>

  <section class="py-16 relative z-10">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 grid lg:grid-cols-12 gap-12 items-start">

      <div class="hidden lg:block lg:col-span-3 sticky top-32 reveal">

        <div class="bg-white p-4 rounded-3xl shadow-sm border border-gray-100 mb-8">
          <h4 class="text-xs font-bold text-gray-400 uppercase tracking-widest mb-4 px-4 pt-2">Contents</h4>
          <nav id="toc" class="flex flex-col text-sm font-medium text-gray-600">
            <a href="#collection" class="spy-link px-4 py-3 rounded-r-xl hover:bg-gray-50">1. Information Collection</a>
            <a href="#usage" class="spy-link px-4 py-3 rounded-r-xl hover:bg-gray-50">2. Data Utilization</a>
            <a href="#security" class="spy-link px-4 py-3 rounded-r-xl hover:bg-gray-50">3. Infrastructure Security</a>
            <a href="#cookies" class="spy-link px-4 py-3 rounded-r-xl hover:bg-gray-50">4. Cookie Protocol</a>
            <a href="#rights" class="spy-link px-4 py-3 rounded-r-xl hover:bg-gray-50">5. Digital Rights</a>
          </nav>
        </div>

        <div class="bg-white p-6 rounded-3xl shadow-sm border border-gray-100 text-center flex flex-col items-center">
          <div class="h-40 w-full bg-[#F0F2F5] rounded-2xl relative flex justify-center items-end overflow-hidden perspective-[1000px] mb-4">
            <div id="mascot-guard" class="mascot-body shape-guard-cyan">
              <div class="mascot-eyes flex gap-3 drop-shadow-md absolute inset-x-0 mx-auto w-fit top-4">
                <div class="w-2.5 h-2.5 rounded-full bg-black eye-dot" style="animation-delay: 1s;"></div>
                <div class="w-2.5 h-2.5 rounded-full bg-black eye-dot" style="animation-delay: 1s;"></div>
              </div>
              <div class="mascot-prop text-gray-800 left-8 top-4">
                <i class="ph-fill ph-shield-check text-[6rem] drop-shadow-2xl"></i>
              </div>
            </div>
          </div>
          <h3 class="text-lg font-extrabold text-gray-900 mb-1">Data Guardian</h3>
          <p class="text-gray-500 text-sm font-medium">Monitoring your session.</p>
        </div>
      </div>

      <div class="lg:col-span-9 space-y-8 pb-32">

        <div id="collection" class="privacy-card reveal section-block">
          <div class="flex items-center gap-4 mb-4">
            <div class="bg-gray-100 p-2.5 rounded-xl text-gray-800"><i class="ph-bold ph-database text-2xl"></i></div>
            <h2 class="text-2xl font-extrabold text-gray-900">1. Information Collection</h2>
          </div>
          <p class="text-lg text-gray-600 leading-relaxed font-medium">We collect personal information you explicitly provide when registering an account, making a vehicle reservation, or contacting our support team. This strictly includes your legal name, email address, contact number, driver’s license verification, and secure payment gateway tokens.</p>
        </div>

        <div id="usage" class="privacy-card reveal section-block">
          <div class="flex items-center gap-4 mb-4">
            <div class="bg-gray-100 p-2.5 rounded-xl text-gray-800"><i class="ph-bold ph-cpu text-2xl"></i></div>
            <h2 class="text-2xl font-extrabold text-gray-900">2. Data Utilization</h2>
          </div>
          <p class="text-lg text-gray-600 leading-relaxed font-medium">Your data is utilized exclusively to process your reservations, verify your identity for legal compliance, and improve our digital fleet management services. We maintain a strict zero-tolerance policy against data brokering—your personal information is never sold to third-party advertising agencies.</p>
        </div>

        <div id="security" class="privacy-card reveal section-block">
          <div class="flex items-center gap-4 mb-4">
            <div class="bg-gray-100 p-2.5 rounded-xl text-gray-800"><i class="ph-bold ph-lock-key text-2xl"></i></div>
            <h2 class="text-2xl font-extrabold text-gray-900">3. Infrastructure Security</h2>
          </div>
          <p class="text-lg text-gray-600 leading-relaxed font-medium">All sensitive information routing through the DriveAway infrastructure is protected using enterprise-grade AES-256 encryption. We enforce stringent firewall protocols and regular internal audits to safeguard your profile against unauthorized access or breaches.</p>
        </div>

        <div id="cookies" class="privacy-card reveal section-block">
          <div class="flex items-center gap-4 mb-4">
            <div class="bg-gray-100 p-2.5 rounded-xl text-gray-800"><i class="ph-bold ph-cookie text-2xl"></i></div>
            <h2 class="text-2xl font-extrabold text-gray-900">4. Cookie Protocol</h2>
          </div>
          <p class="text-lg text-gray-600 leading-relaxed font-medium">We utilize essential session cookies strictly to maintain your active login state and preserve your vehicle search preferences. You maintain total control and may disable non-essential trackers via your browser settings without penalty.</p>
        </div>

        <div id="rights" class="privacy-card reveal section-block">
          <div class="flex items-center gap-4 mb-4">
            <div class="bg-gray-100 p-2.5 rounded-xl text-gray-800"><i class="ph-bold ph-user-focus text-2xl"></i></div>
            <h2 class="text-2xl font-extrabold text-gray-900">5. Your Digital Rights</h2>
          </div>
          <p class="text-lg text-gray-600 leading-relaxed font-medium">You have absolute sovereignty over your digital footprint. You possess the right to request a full export of your personal data file, execute corrections, or invoke the "Right to be Forgotten" by initiating a secure deletion request through privacy@driveaway.lk.</p>
        </div>

        <div class="pt-8 border-t border-gray-200 flex justify-between items-center reveal">
          <p class="text-gray-400 font-bold text-sm uppercase tracking-widest">Document Version: 2.1.0</p>
          <p class="text-gray-400 font-bold text-sm uppercase tracking-widest">March 2026</p>
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

  // 2. Interactive Scroll-Spy Navigation (The "Wow" Factor UX)
  const sections = document.querySelectorAll(".section-block");
  const navLinks = document.querySelectorAll(".spy-link");

  const spyOptions = {
    root: null,
    rootMargin: "-20% 0px -70% 0px",
    threshold: 0
  };

  const spyObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        navLinks.forEach(link => link.classList.remove("active"));
        const id = entry.target.getAttribute("id");
        // FIX: Replaced template literals to prevent Tomcat 500 errors
        const activeLink = document.querySelector('.spy-link[href="#' + id + '"]');
        if(activeLink) activeLink.classList.add("active");
      }
    });
  }, spyOptions);

  sections.forEach(section => {
    spyObserver.observe(section);
  });

  // 3. Interactive "Guard" Mascot Logic
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

      const moveX = this.currentX * 10;
      const moveY = this.currentY * 10;
      const rotX = this.currentY * -this.maxRotation;
      const rotY = this.currentX * this.maxRotation;

      // FIX: Replaced template literals to prevent Tomcat 500 errors
      this.body.style.transform = 'translate3d(' + moveX + 'px, ' + moveY + 'px, 0) rotateX(' + rotX + 'deg) rotateY(' + rotY + 'deg)';
      this.eyes.style.transform = 'translateZ(40px) translate(' + (this.currentX * 20) + 'px, ' + (this.currentY * 20) + 'px)';
    }
  }

  let guardMascot = null;
  if(document.getElementById('mascot-guard')) {
    guardMascot = new Mascot('mascot-guard', 0.08, 20);
  }

  document.addEventListener('mousemove', (e) => {
    if(!guardMascot) return;
    const mouseX = (e.clientX / window.innerWidth) * 2 - 1;
    const mouseY = (e.clientY / window.innerHeight) * 2 - 1;
    guardMascot.lookAt(mouseX, mouseY);
  });

  function renderLoop() {
    if(guardMascot) guardMascot.update();
    requestAnimationFrame(renderLoop);
  }
  requestAnimationFrame(renderLoop);

  // 4. Page Transition Logic
  window.addEventListener('load', () => { document.body.classList.add('page-loaded'); });
  document.querySelectorAll('.transition-link, .nav-link').forEach(link => {
    link.addEventListener('click', function(e) {
      if(this.href.includes('#')) return;
      e.preventDefault();
      const targetUrl = this.href;
      document.body.classList.remove('page-loaded');
      setTimeout(() => { window.location.href = targetUrl; }, 150);
    });
  });
</script>
</body>
</html>