<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DriveAway | Secure Login</title>
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

        .mascot-eyes {
            position: absolute;
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
        .glasses-wrapper { display: flex; gap: 0.1rem; position: relative; align-items: center; justify-content: center; }
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

        /* --- SUCCESS ANIMATION CSS --- */
        #success-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(255, 255, 255, 0.98);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            z-index: 9999;
            opacity: 0;
            visibility: hidden;
            transition: opacity 0.4s ease, visibility 0.4s ease;
        }

        #success-overlay.active {
            opacity: 1;
            visibility: visible;
        }

        .success-checkmark {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            background: #10B981;
            display: flex;
            align-items: center;
            justify-content: center;
            transform: scale(0.8);
            opacity: 0;
            transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
            transition-delay: 0.1s;
        }

        #success-overlay.active .success-checkmark {
            transform: scale(1);
            opacity: 1;
        }

        .checkmark-icon {
            width: 40px;
            height: 40px;
            stroke: white;
            stroke-width: 3;
            fill: none;
            stroke-dasharray: 50;
            stroke-dashoffset: 50;
            transition: stroke-dashoffset 0.5s ease;
            transition-delay: 0.4s;
        }

        #success-overlay.active .checkmark-icon {
            stroke-dashoffset: 0;
        }

        .success-text {
            color: #1F2937;
            font-size: 1.5rem;
            font-weight: 600;
            margin-top: 1.5rem;
            opacity: 0;
            transform: translateY(10px);
            transition: all 0.4s ease;
            transition-delay: 0.6s;
        }

        #success-overlay.active .success-text {
            opacity: 1;
            transform: translateY(0);
        }

        .success-subtext {
            color: #6B7280;
            font-size: 0.95rem;
            font-weight: 400;
            margin-top: 0.5rem;
            opacity: 0;
            transition: opacity 0.4s ease;
            transition-delay: 0.8s;
        }

        #success-overlay.active .success-subtext {
            opacity: 1;
        }

        .loading-dots {
            display: flex;
            gap: 6px;
            margin-top: 1.5rem;
            opacity: 0;
            transition: opacity 0.4s ease;
            transition-delay: 1s;
        }

        #success-overlay.active .loading-dots {
            opacity: 1;
        }

        .loading-dots span {
            width: 8px;
            height: 8px;
            background: #10B981;
            border-radius: 50%;
            animation: pulse 1.4s ease-in-out infinite;
        }

        .loading-dots span:nth-child(2) {
            animation-delay: 0.2s;
        }

        .loading-dots span:nth-child(3) {
            animation-delay: 0.4s;
        }

        @keyframes pulse {
            0%, 80%, 100% {
                transform: scale(0.6);
                opacity: 0.4;
            }
            40% {
                transform: scale(1);
                opacity: 1;
            }
        }
    </style>
</head>
<body class="min-h-screen flex bg-white text-gray-900">
<div id="art-container" class="hidden lg:flex lg:w-1/2 bg-[#F0F2F5] relative items-end justify-center overflow-hidden pb-16">
    <div class="relative w-80 h-80 flex items-end justify-center transform scale-125 xl:scale-[1.3] origin-bottom perspective-[1000px]">

        <div id="mascot-tall-black" class="mascot-body shape-tall-black">
            <div class="mascot-eyes flex gap-3 drop-shadow-md absolute inset-x-0 mx-auto w-fit top-8">
                <div class="w-2 h-2 rounded-full bg-white eye-dot" style="animation-delay: 4s;"></div>
                <div class="w-2 h-2 rounded-full bg-white eye-dot" style="animation-delay: 4s;"></div>
            </div>
        </div>

        <div id="mascot-boss" class="mascot-body shape-boss">
            <div class="mascot-eyes flex gap-4 drop-shadow-md absolute inset-x-0 mx-auto w-fit top-6">
                <div class="w-2.5 h-2.5 rounded-full bg-white eye-dot"></div>
                <div class="w-2.5 h-2.5 rounded-full bg-white eye-dot"></div>
            </div>
        </div>

        <div id="mascot-hijab" class="mascot-body shape-hijab flex justify-center">
            <div class="hijab-face relative">
                <div class="mascot-eyes glasses-wrapper drop-shadow-sm absolute inset-x-0 mx-auto inset-y-0 my-auto w-fit h-fit">
                    <div class="glasses-bridge"></div>
                    <div class="glasses-frame glasses-lg relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 3s;"></div></div>
                    <div class="glasses-frame glasses-lg relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 3s;"></div></div>
                </div>
            </div>
        </div>

        <div id="mascot-guard-cyan" class="mascot-body shape-guard-cyan">
            <div class="mascot-eyes flex gap-3 drop-shadow-md absolute inset-x-0 mx-auto w-fit top-6">
                <div class="w-2.5 h-2.5 rounded-full bg-black eye-dot" style="animation-delay: 1s;"></div>
                <div class="w-2.5 h-2.5 rounded-full bg-black eye-dot" style="animation-delay: 1s;"></div>
            </div>
            <div id="prop-shield" class="mascot-prop text-gray-800 left-12 top-4">
                <i class="ph-fill ph-shield-check text-[5rem] drop-shadow-2xl"></i>
            </div>
        </div>

        <div id="mascot-rookie" class="mascot-body shape-rookie">
            <div class="mascot-eyes flex gap-2 drop-shadow-sm absolute inset-x-0 mx-auto w-fit top-6">
                <div class="w-2 h-2 rounded-full bg-black eye-dot" style="animation-delay: 2s;"></div>
                <div class="w-2 h-2 rounded-full bg-black eye-dot" style="animation-delay: 2s;"></div>
            </div>
        </div>

        <div id="mascot-yellow-girl" class="mascot-body shape-yellow-girl">
            <div class="mascot-eyes glasses-wrapper drop-shadow-sm absolute inset-x-0 mx-auto w-fit top-4">
                <div class="glasses-bridge"></div>
                <div class="glasses-frame glasses-sm relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 1.5s;"></div></div>
                <div class="glasses-frame glasses-sm relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 1.5s;"></div></div>
            </div>
        </div>

    </div>
</div>

<div class="w-full lg:w-1/2 flex items-center justify-center p-8 sm:p-16 relative z-50 bg-white shadow-[-20px_0_50px_rgba(0,0,0,0.05)]">

    <div class="absolute top-8 left-8 sm:top-12 sm:left-12">
        <a href="index.jsp" class="transition-link flex items-center gap-2 text-base font-bold text-gray-500 hover:text-black transition-colors">
            <i class="ph-bold ph-arrow-left text-2xl"></i> Back
        </a>
    </div>

    <div class="w-full max-w-[520px]">
        <div class="text-center mb-12 mt-12 sm:mt-8">
            <div class="flex justify-center mb-4">
                <div class="bg-gray-100 p-4 rounded-2xl">
                    <i class="ph-fill ph-users-three text-4xl text-[#0D6EFD]"></i>
                </div>
            </div>
            <h2 class="text-[2.5rem] font-extrabold mb-2 tracking-tight leading-tight">DriveAway</h2>
            <p class="text-gray-500 font-medium text-lg">Secure Employee & Customer Portal</p>
        </div>

        <% if ("invalid".equals(request.getParameter("error"))) { %>
        <div id="error-alert" class="bg-red-50 p-5 rounded-xl border border-red-200 text-base text-red-700 font-bold flex items-center gap-3 mb-8 shadow-sm">
            <i class="ph-fill ph-warning-circle text-2xl text-red-500"></i>
            Access Denied. Check credentials.
        </div>
        <% } else if ("registered".equals(request.getParameter("success"))) { %>
        <div class="bg-green-50 p-5 rounded-xl border border-green-200 text-base text-green-700 font-bold flex items-center gap-3 mb-8 shadow-sm">
            <i class="ph-fill ph-check-circle text-2xl text-green-500"></i>
            Account created! You can now sign in.
        </div>
        <% } %>

        <form action="auth" method="post" class="space-y-8">
            <input type="hidden" name="action" value="customer_login">

            <div>
                <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Username</label>
                <input type="text" name="username" id="usernameInput" required placeholder="Enter your ID"
                       class="w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-xl font-bold bg-transparent">
            </div>

            <div>
                <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Password</label>
                <div class="relative" id="passwordWrapper">
                    <input type="password" name="password" id="passwordInput" required placeholder="••••••••"
                           class="w-full border-b-2 border-gray-300 py-3 pr-12 focus:border-[#0D6EFD] outline-none transition-colors text-xl font-bold bg-transparent">
                    <i id="togglePasswordBtn" class="ph-bold ph-eye absolute right-2 top-3 text-gray-400 cursor-pointer hover:text-black text-2xl transition-all hover:scale-110"></i>
                </div>
            </div>

            <div class="flex justify-between items-center mt-6">
                <label class="flex items-center gap-3 cursor-pointer group">
                    <input type="checkbox" class="w-5 h-5 text-[#0D6EFD] border-gray-300 rounded focus:ring-[#0D6EFD] accent-[#0D6EFD]">
                    <span class="text-base text-gray-500 font-medium group-hover:text-gray-800 transition-colors">Remember me</span>
                </label>
                <a href="#" class="text-base text-[#0D6EFD] hover:text-blue-800 font-bold transition-colors">Forgot password?</a>
            </div>

            <button type="submit" class="w-full bg-[#0D6EFD] text-white font-bold py-5 rounded-2xl hover:bg-blue-700 transition-transform transform hover:-translate-y-1 shadow-lg hover:shadow-[#0D6EFD]/30 mt-8 text-xl tracking-wide flex justify-center items-center gap-3">
                Authenticate <i class="ph-bold ph-arrow-right"></i>
            </button>
        </form>

        <div class="mt-12 pt-8 border-t border-gray-100 flex justify-between items-center">
            <p class="text-sm font-medium text-gray-500">
                New user? <a href="register.jsp" class="transition-link text-black font-bold hover:underline">Register</a>
            </p>
            <a href="admin-login.jsp" class="text-sm text-red-500 font-bold hover:underline bg-red-50 px-4 py-2 rounded-lg flex items-center gap-2">
                <i class="ph-bold ph-shield-check"></i> Staff Login
            </a>
        </div>
    </div>
</div>

<!-- Success Animation Overlay -->
<div id="success-overlay">
    <div class="success-checkmark">
        <svg class="checkmark-icon" viewBox="0 0 52 52">
            <path d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
        </svg>
    </div>
    <div class="success-text">Login Successful!</div>
    <div class="success-subtext">Redirecting to dashboard...</div>
    <div class="loading-dots">
        <span></span>
        <span></span>
        <span></span>
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

    const usernameInput = document.getElementById('usernameInput');
    const passwordInput = document.getElementById('passwordInput');
    const passwordWrapper = document.getElementById('passwordWrapper');
    const toggleBtn = document.getElementById('togglePasswordBtn');
    const propShield = document.getElementById('prop-shield');
    const artContainer = document.getElementById('art-container');

    let globalMouseX = 0.5;
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

        if (document.activeElement === usernameInput) {
            activeState = 'inspecting';
            updateTypingTrack();

        } else if (document.activeElement === passwordInput || isHoveringPassword) {
            if (passwordInput.type === 'password') {
                activeState = 'shielding';
                propShield.classList.add('show-prop');
                guardCyanGuy.scale = 1.1;

                tallBlackGuy.lookAt(-1, -1);
                bossGuy.lookAt(-0.8, -0.8);
                hijabGirl.lookAt(-0.5, 0);
                guardCyanGuy.lookAt(0.6, 0);
                rookieGuy.lookAt(0.8, 0.5);
                yellowGirl.lookAt(0, 0.9);
            } else {
                activeState = 'peeking';
                artContainer.classList.add('alert-mode');

                mascots.forEach(m => {
                    m.scale = 1.05;
                    m.lookAt(0.7, 0.2);
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
        const typeProgress = Math.min(usernameInput.value.length / 15, 1);
        const lookX = 0.3 + (typeProgress * 0.4);
        mascots.forEach(m => m.lookAt(lookX, 0.1));
    }

    usernameInput.addEventListener('focus', evaluateState);
    usernameInput.addEventListener('blur', evaluateState);
    usernameInput.addEventListener('input', updateTypingTrack);

    passwordInput.addEventListener('focus', evaluateState);
    passwordInput.addEventListener('blur', evaluateState);

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

    usernameInput.addEventListener('input', () => { if(activeState === 'sad') { activeState = 'idle'; evaluateState(); } }, {once: true});
    passwordInput.addEventListener('input', () => { if(activeState === 'sad') { activeState = 'idle'; evaluateState(); } }, {once: true});

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

    // --- SUCCESS ANIMATION LOGIC ---
    const form = document.querySelector('form');
    const successOverlay = document.getElementById('success-overlay');

    function triggerSuccessAnimation() {
        successOverlay.classList.add('active');
    }

    form.addEventListener('submit', function(e) {
        // Check if form is valid
        if (form.checkValidity()) {
            e.preventDefault();
            triggerSuccessAnimation();
            
            // Submit form after animation
            setTimeout(() => {
                form.submit();
            }, 2000);
        }
    });
</script>
</body>
</html>