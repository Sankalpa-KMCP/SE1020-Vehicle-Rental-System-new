<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.example.vehiclerentalsystem01.dao.VehicleDAO" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Vehicle" %>
<%
    // Check if user is logged in
    Boolean customerLoggedIn = (Boolean) session.getAttribute("customerLoggedIn");
    Boolean staffLoggedIn = (Boolean) session.getAttribute("staffLoggedIn");
    String displayName = "";
    String userRole = "";

    if (customerLoggedIn != null && customerLoggedIn) {
        displayName = (String) session.getAttribute("name");
        if (displayName == null) displayName = (String) session.getAttribute("username");
        if (displayName == null) displayName = "Customer";
        userRole = "customer";
    } else if (staffLoggedIn != null && staffLoggedIn) {
        displayName = (String) session.getAttribute("name");
        if (displayName == null) displayName = "Staff";
        userRole = "staff";
    }

    // Fetch vehicles from DAO
    VehicleDAO vehicleDAO = new VehicleDAO();
    List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DriveAway | Premium Vehicle Rentals</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap');

        /* ==========================================
           1. CORE STYLES & GLOBALS
           ========================================== */
        :root {
            --brand-primary: #0D6EFD;
            --brand-secondary: #4A12DA;
            --surface-muted: #F8FAFF;
            --text-main: #0f172a;
            --ambient-glow-opacity: 0.05;
        }

        body {
            font-family: 'Inter', sans-serif;
            overflow-x: hidden;
            background-color: #ffffff;
            opacity: 0;
            transition: opacity 0.2s ease-out, transform 0.3s ease-out;
            scroll-behavior: smooth;
            -webkit-font-smoothing: antialiased;
            -moz-osx-font-smoothing: grayscale;
        }
        body.page-loaded { opacity: 1; }
        body.page-exit { opacity: 0; transform: scale(0.98); }

        body::before {
            content: "";
            position: fixed;
            inset: 0;
            pointer-events: none;
            background-image: radial-gradient(rgba(13, 110, 253, 0.06) 1px, transparent 1px);
            background-size: 24px 24px;
            opacity: 0.25;
            z-index: -1;
        }

        body::after {
            content: "";
            position: fixed;
            inset: 0;
            pointer-events: none;
            background: linear-gradient(180deg, rgba(248, 250, 255, 0.0) 0%, rgba(13, 110, 253, 0.02) 50%, rgba(248, 250, 255, 0.0) 100%);
            z-index: -1;
            opacity: var(--ambient-glow-opacity);
            transition: opacity 0.6s ease;
            will-change: opacity;
        }

        .parallax-bg {
            position: fixed;
            inset: 0;
            pointer-events: none;
            z-index: -2;
            opacity: 0.08;
            will-change: opacity, transform;
            transition: opacity 0.4s ease;
            background: linear-gradient(135deg,
            rgba(13, 110, 253, 0.06) 0%,
            rgba(74, 18, 218, 0.03) 25%,
            rgba(0, 242, 254, 0.04) 50%,
            rgba(13, 110, 253, 0.03) 75%,
            rgba(248, 250, 255, 0.02) 100%);
            background-size: 400% 400%;
            animation: gradientShift 24s ease-in-out infinite;
            transform: translate3d(0, 0, 0);
        }

        @keyframes gradientShift {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }

        ::selection {
            background-color: #0D6EFD;
            color: #ffffff;
        }

        .gradient-text {
            background: linear-gradient(135deg, #0D6EFD 0%, #4A12DA 100%);
            -webkit-background-clip: text;
            background-clip: text;
            color: transparent;
        }

        /* --- Custom Scrollbar --- */
        ::-webkit-scrollbar { width: 10px; }
        ::-webkit-scrollbar-track { background: #F0F2F5; }
        ::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
        ::-webkit-scrollbar-thumb:hover { background: #94a3b8; }

        /* ==========================================
           2. ANIMATIONS & REVEALS
           ========================================== */
        .reveal {
            opacity: 0;
            transform: translateY(40px);
            transition: opacity 0.8s cubic-bezier(0.2, 0.8, 0.2, 1), transform 0.8s cubic-bezier(0.2, 0.8, 0.2, 1);
            will-change: transform, opacity;
        }
        .reveal.active {
            opacity: 1;
            transform: translateY(0);
            will-change: auto;
        }

        .reveal-left { opacity: 0; transform: translateX(-40px); transition: all 0.8s ease; will-change: transform, opacity; }
        .reveal-left.active { opacity: 1; transform: translateX(0); will-change: auto; }

        .reveal-right { opacity: 0; transform: translateX(40px); transition: all 0.8s ease; will-change: transform, opacity; }
        .reveal-right.active { opacity: 1; transform: translateX(0); will-change: auto; }

        .brand-shape {
            position: absolute;
            transform-style: preserve-3d;
            animation: float 8s ease-in-out infinite;
            filter: blur(60px);
            opacity: 0.4;
            pointer-events: none;
            z-index: 0;
            will-change: transform;
            transform: translateZ(0);
        }
        @keyframes float {
            0%, 100% { transform: translateY(0) rotate(0deg) scale(1) translateZ(0); }
            50% { transform: translateY(-20px) rotate(2deg) scale(1.05) translateZ(0); }
        }

        /* ==========================================
           3. CARD HOVER EFFECTS
           ========================================== */
        .premium-card {
            transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
            will-change: transform, box-shadow;
        }

        .premium-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 30px 60px -15px rgba(13, 110, 253, 0.15);
        }

        .feature-card {
            transition: all 0.3s ease;
            border: 1px solid transparent;
        }
        .feature-card:hover {
            border-color: #0D6EFD;
            box-shadow: 0 10px 30px rgba(13, 110, 253, 0.08);
            transform: translateY(-5px);
        }

        .category-card {
            transition: all 0.3s ease;
        }
        .category-card:hover {
            transform: scale(1.03);
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
        }

        /* ==========================================
           4. UNIFIED TEAM MASCOT CSS
           ========================================== */
        .mascot-avatar {
            position: relative;
            margin: 0 auto;
            z-index: 10;
            width: 7rem;
            height: 9rem;
        }

        .mascot-body {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            margin: 0 auto;
            transform-style: preserve-3d;
            will-change: transform;
            width: 7rem;
            height: 9rem;
            border-radius: 3.5rem 3.5rem 0 0;
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

        /* Colors for the 6 Shapes */
        .shape-tall-black { background: linear-gradient(135deg, #2A2D35, #0D0F16); box-shadow: inset 4px 4px 15px rgba(255,255,255,0.1), inset -4px -4px 15px rgba(0,0,0,0.6), 10px 20px 30px rgba(0, 0, 0, 0.4); }
        .shape-guard-cyan { background: linear-gradient(135deg, #00F2FE, #0088FF); box-shadow: inset 4px 4px 15px rgba(255,255,255,0.4), inset -4px -4px 15px rgba(0,0,0,0.3), 10px 20px 30px rgba(0, 136, 255, 0.3); }
        .shape-hijab { background: linear-gradient(135deg, #7A52FA, #4A12DA); box-shadow: inset 4px 4px 15px rgba(255,255,255,0.4), inset -4px -4px 15px rgba(0,0,0,0.5), 10px 20px 30px rgba(90, 50, 250, 0.4); }
        .shape-yellow-girl { background: linear-gradient(135deg, #FFE044, #D6A200); box-shadow: inset 3px 3px 10px rgba(255,255,255,0.6), inset -3px -3px 10px rgba(0,0,0,0.2), 10px 20px 30px rgba(251, 207, 20, 0.3); }
        .shape-boss { background: linear-gradient(135deg, #EF4444, #B91C1C); box-shadow: inset 4px 4px 15px rgba(255,255,255,0.3), inset -4px -4px 15px rgba(0,0,0,0.3), 10px 20px 30px rgba(239, 68, 68, 0.3); }
        .shape-rookie { background: linear-gradient(135deg, #FF9A44, #D65A00); box-shadow: inset 4px 4px 15px rgba(255,255,255,0.6), inset -4px -4px 15px rgba(0,0,0,0.2), 10px 20px 30px rgba(255, 154, 68, 0.3); }

        .hijab-face {
            background: linear-gradient(135deg, #FFEDD5, #FDBA74);
            box-shadow: inset 0px 5px 10px rgba(0,0,0,0.2), 0px 4px 6px rgba(0,0,0,0.1);
            width: 4.5rem; height: 6rem; border-radius: 2rem 2rem 1.5rem 1.5rem;
            margin-top: 1rem;
        }
        .glasses-wrapper { display: flex; gap: 0.1rem; position: relative; align-items: center; justify-content: center; }
        .glasses-frame { border: 3px solid #1F2937; border-radius: 50%; display: flex; justify-content: center; align-items: center; background: rgba(255, 255, 255, 0.2); box-shadow: 1px 2px 4px rgba(0,0,0,0.15); }
        .glasses-lg { width: 1.6rem; height: 1.6rem; }
        .glasses-sm { width: 1.2rem; height: 1.2rem; border-width: 2px; }
        .glasses-bridge { position: absolute; width: 0.4rem; height: 3px; background-color: #1F2937; left: 50%; transform: translateX(-50%); top: 50%; z-index: 10; }

        /* ==========================================
           5. UI COMPONENTS
           ========================================== */
        .filter-btn { transition: all 0.3s ease; }
        .filter-btn.active { background-color: #0D6EFD; color: white; border-color: #0D6EFD; box-shadow: 0 4px 15px rgba(13, 110, 253, 0.3); }

        .car-item { transition: opacity 0.4s ease, transform 0.4s ease; display: block; }
        .car-item.hidden-item { opacity: 0; transform: scale(0.9); display: none; }

        /* FAQ Accordion */
        .faq-answer { display: grid; grid-template-rows: 0fr; transition: grid-template-rows 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
        .faq-answer > div { overflow: hidden; }
        .faq-item.faq-open .faq-answer { grid-template-rows: 1fr; }
        .faq-item.faq-open .faq-icon { transform: rotate(180deg); color: #0D6EFD; }
        .faq-item.faq-open { border-color: #0D6EFD; background-color: white; box-shadow: 0 10px 20px rgba(0,0,0,0.03); }

        .testimonial-slide { display: none; opacity: 0; transition: opacity 0.5s ease; }
        .testimonial-slide.active { display: block; opacity: 1; }

        input[type="date"]::-webkit-calendar-picker-indicator { cursor: pointer; opacity: 0.6; transition: 0.2s; }
        input[type="date"]::-webkit-calendar-picker-indicator:hover { opacity: 1; }

        .scroll-progress {
            position: fixed;
            top: 0;
            left: 0;
            height: 3px;
            width: 0;
            background: linear-gradient(90deg, var(--brand-primary), var(--brand-secondary));
            z-index: 80;
            box-shadow: 0 0 12px rgba(13, 110, 253, 0.45);
            will-change: width;
            transform: translateZ(0);
        }

        #navbar.nav-scrolled {
            padding-top: 0.75rem;
            padding-bottom: 0.75rem;
            background: rgba(255, 255, 255, 0.95);
            transform: translateZ(0);
            will-change: transform;
            box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
        }

        .nav-link {
            position: relative;
        }
        .nav-link::after {
            content: "";
            position: absolute;
            left: 0;
            bottom: -6px;
            width: 0;
            height: 2px;
            background: linear-gradient(90deg, var(--brand-primary), var(--brand-secondary));
            transition: width 0.25s ease;
        }
        .nav-link:hover::after,
        .nav-link.active::after {
            width: 100%;
        }

        .glass-panel {
            background: linear-gradient(135deg, rgba(255,255,255,0.98), rgba(255,255,255,0.96));
            border: 1px solid rgba(255, 255, 255, 0.9);
            backdrop-filter: blur(10px);
        }

        .hero-metric {
            border: 1px solid #e2e8f0;
            background: #ffffff;
            border-radius: 14px;
            padding: 0.75rem 1rem;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            font-weight: 700;
            color: #334155;
            box-shadow: 0 6px 14px rgba(2, 6, 23, 0.04);
        }

        .section-divider {
            height: 70px;
            background: linear-gradient(180deg, #ffffff 0%, #f8faff 100%);
            clip-path: polygon(0 0, 100% 18%, 100% 100%, 0 100%);
        }

        .cinematic-hero {
            isolation: isolate;
        }

        .hero-video-shell {
            position: absolute;
            inset: 0;
            overflow: hidden;
            z-index: 0;
        }

        .hero-video {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transform: scale(1) translateZ(0);
            opacity: 1;
            filter: saturate(1.1) contrast(1.05);
            transition: transform 0.2s linear, opacity 0.2s linear;
            will-change: transform, opacity;
            backface-visibility: hidden;
        }

        .hero-video-overlay {
            position: absolute;
            inset: 0;
            background: linear-gradient(95deg, rgba(248, 250, 255, 0.2) 0%, rgba(248, 250, 255, 0.1) 38%, rgba(15, 23, 42, 0.05) 100%);
        }

        .hero-heading {
            text-shadow: 0 2px 8px rgba(0, 0, 0, 0.14), 0 10px 28px rgba(0, 0, 0, 0.12);
        }

        .hero-text {
            text-shadow: 0 1px 4px rgba(0, 0, 0, 0.16);
        }

        .hero-paragraph {
            color: #111827;
            text-shadow: 0 1px 4px rgba(255, 255, 255, 0.55);
        }

        .hero-copy {
            background: linear-gradient(180deg, rgba(255, 255, 255, 0.58), rgba(255, 255, 255, 0.22));
            border: 1px solid rgba(255, 255, 255, 0.5);
            border-radius: 1.75rem;
            padding: 1.25rem 1.25rem 0.5rem;
            backdrop-filter: blur(4px);
        }

        .floating-actions {
            position: fixed;
            right: 1rem;
            bottom: 1rem;
            z-index: 60;
            display: flex;
            flex-direction: column;
            gap: 0.75rem;
        }

        .fab {
            width: 52px;
            height: 52px;
            border-radius: 999px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            box-shadow: 0 10px 24px rgba(15, 23, 42, 0.25);
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .fab:hover { transform: translateY(-3px); }
        .fab-main { background: linear-gradient(135deg, #0D6EFD, #4A12DA); }
        .fab-muted { background: #111827; opacity: 0; pointer-events: none; }
        .fab-muted.visible { opacity: 1; pointer-events: auto; }

        /* Intro reveal helpers */
        .intro-fade { opacity: 0; transform: translateY(14px); transition: opacity 0.9s ease, transform 0.9s cubic-bezier(.16,.84,.24,1); }
        .intro-fade.visible { opacity: 1; transform: translateY(0); }
        .intro-staggered { opacity: 0; transform: translateY(18px) scale(.995); }
        .intro-staggered.visible { opacity: 1; transform: translateY(0) scale(1); transition: opacity 0.8s ease, transform 0.9s cubic-bezier(.16,.84,.24,1); }

        /* ==========================================
           LOAD ANIMATION & PAGE INTRO (UPDATED)
           ========================================== */
        #page-loader {
            position: fixed;
            inset: 0;
            z-index: 9999;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            background: #0f172a; /* Deep slate background */
            transition: opacity 0.6s cubic-bezier(0.4, 0, 0.2, 1), visibility 0.6s, transform 0.6s ease-out;
            overflow: hidden;
        }

        /* Aurora background with brand colors */
        #page-loader::before {
            content: '';
            position: absolute;
            inset: 0;
            background: 
                linear-gradient(45deg, transparent 30%, rgba(13, 110, 253, 0.08) 50%, transparent 70%),
                linear-gradient(-45deg, transparent 30%, rgba(74, 18, 218, 0.06) 50%, transparent 70%),
                linear-gradient(135deg, transparent 30%, rgba(0, 242, 254, 0.05) 50%, transparent 70%);
            background-size: 200% 200%;
            animation: auroraShift 8s ease-in-out infinite;
            mix-blend-mode: screen;
            pointer-events: none;
        }

        @keyframes auroraShift {
            0%, 100% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
        }

        #page-loader.hide {
            opacity: 0;
            visibility: hidden;
            transform: scale(1.02);
        }

        .loader-ring {
            position: relative;
            width: 90px;
            height: 90px;
            margin-bottom: 2.5rem;
        }

        /* Static outer ring */
        .loader-ring::before {
            content: '';
            position: absolute;
            inset: 0;
            border-radius: 50%;
            border: 3px solid rgba(13, 110, 253, 0.15);
        }

        /* Spinning gradient ring */
        .loader-ring::after {
            content: '';
            position: absolute;
            inset: 0;
            border-radius: 50%;
            border: 3px solid transparent;
            border-top-color: #0D6EFD;
            border-right-color: #4A12DA;
            animation: spinRing 0.8s linear infinite;
        }

        @keyframes spinRing {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        /* Center icon with rotation */
        .loader-icon {
            position: absolute;
            inset: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2.5rem;
            color: white;
            animation: pulseGlow 2s ease-in-out infinite, steerWheel 3s ease-in-out infinite;
            filter: drop-shadow(0 0 12px rgba(13, 110, 253, 0.5));
        }

        @keyframes pulseGlow {
            0%, 100% { transform: scale(0.95); opacity: 0.8; }
            50% { transform: scale(1.05); opacity: 1; filter: drop-shadow(0 0 20px rgba(13, 110, 253, 0.9)); }
        }

        @keyframes steerWheel {
            0%, 100% { transform: rotate(-15deg); }
            50% { transform: rotate(15deg); }
        }

        /* Progress track */
        .loader-track {
            width: 180px;
            height: 3px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 4px;
            overflow: hidden;
            position: relative;
        }

        /* Progress bar sliding effect */
        .loader-bar {
            position: absolute;
            top: 0;
            left: 0;
            height: 100%;
            width: 40%;
            background: linear-gradient(90deg, #0D6EFD, #4A12DA, #00F2FE);
            border-radius: 4px;
            animation: slideBar 1.2s ease-in-out infinite;
        }

        @keyframes slideBar {
            0% { transform: translateX(-100%); }
            50% { transform: translateX(150%); }
            100% { transform: translateX(-100%); }
        }

        /* Animated Text */
        .loader-text {
            margin-top: 1.5rem;
            font-size: 0.75rem;
            font-weight: 800;
            letter-spacing: 0.35em;
            text-transform: uppercase;
            background: linear-gradient(90deg, #ffffff 0%, #0D6EFD 50%, #ffffff 100%);
            background-size: 200% auto;
            -webkit-background-clip: text;
            background-clip: text;
            color: transparent;
            animation: textShine 2s linear infinite;
        }

        @keyframes textShine {
            to { background-position: 200% center; }
        }

        /* DriveAway logo above loader */
        .loader-logo {
            font-size: 1.5rem;
            font-weight: 900;
            letter-spacing: 0.3em;
            text-transform: uppercase;
            background: linear-gradient(90deg, #ffffff 0%, #0D6EFD 50%, #ffffff 100%);
            background-size: 200% auto;
            -webkit-background-clip: text;
            background-clip: text;
            color: transparent;
            animation: logoFadeIn 0.5s ease-out forwards, textShine 2s linear infinite 0.5s;
            margin-bottom: 2rem;
            opacity: 0;
        }

        @keyframes logoFadeIn {
            0% { opacity: 0; transform: translateY(-10px); }
            100% { opacity: 1; transform: translateY(0); }
        }

        /* Car silhouette animation */
        .loader-car {
            position: absolute;
            bottom: 15%;
            left: -100px;
            width: 60px;
            height: 30px;
            opacity: 0.6;
            animation: carDrive 2.5s ease-in-out infinite;
        }

        .loader-car svg {
            width: 100%;
            height: 100%;
            fill: rgba(13, 110, 253, 0.4);
        }

        @keyframes carDrive {
            0% { left: -100px; transform: scale(0.8); }
            50% { left: calc(100% + 100px); transform: scale(1); }
            50.01% { left: -100px; transform: scale(0.8); opacity: 0; }
            50.02% { opacity: 0.6; }
            100% { left: calc(100% + 100px); transform: scale(1); }
        }

        /* Particle effect around ring */
        .loader-particles {
            position: absolute;
            inset: -20px;
            pointer-events: none;
        }

        .loader-particle {
            position: absolute;
            width: 4px;
            height: 4px;
            background: radial-gradient(circle, rgba(13, 110, 253, 0.8), transparent);
            border-radius: 50%;
            animation: particleFloat 2s ease-in-out infinite;
        }

        .loader-particle:nth-child(1) { top: 0; left: 50%; animation-delay: 0s; }
        .loader-particle:nth-child(2) { top: 50%; right: 0; animation-delay: 0.3s; }
        .loader-particle:nth-child(3) { bottom: 0; left: 50%; animation-delay: 0.6s; }
        .loader-particle:nth-child(4) { top: 50%; left: 0; animation-delay: 0.9s; }
        .loader-particle:nth-child(5) { top: 20%; left: 20%; animation-delay: 0.15s; }
        .loader-particle:nth-child(6) { top: 20%; right: 20%; animation-delay: 0.45s; }
        .loader-particle:nth-child(7) { bottom: 20%; left: 20%; animation-delay: 0.75s; }
        .loader-particle:nth-child(8) { bottom: 20%; right: 20%; animation-delay: 1.05s; }

        @keyframes particleFloat {
            0%, 100% { transform: translate(0, 0) scale(0.5); opacity: 0; }
            50% { transform: translate(0, -15px) scale(1); opacity: 1; }
        }

        /* Accessibility: Reduce motion */
        @media (prefers-reduced-motion: reduce) {
            .loader-ring::after, .loader-bar, .loader-text, .loader-icon {
                animation: none !important;
            }
            *, *::before, *::after {
                animation-duration: 0.01ms !important;
                animation-iteration-count: 1 !important;
                transition-duration: 0.01ms !important;
                scroll-behavior: auto !important;
            }
        }
    </style>
</head>
<body class="text-gray-900 antialiased selection:bg-[#0D6EFD] selection:text-white">

<!-- Welcome Transition Overlay -->
<div id="welcome-overlay">
    <div class="welcome-bg-circle"></div>
    <div class="welcome-bg-circle"></div>
    <div class="welcome-bg-circle"></div>
    <div class="welcome-particles">
        <div class="welcome-particle"></div>
        <div class="welcome-particle"></div>
        <div class="welcome-particle"></div>
        <div class="welcome-particle"></div>
        <div class="welcome-particle"></div>
        <div class="welcome-particle"></div>
    </div>
    <div class="welcome-text">Welcome</div>
</div>

<div id="page-loader" aria-hidden="true">
    <div class="loader-logo">DriveAway</div>
    <div class="loader-ring">
        <div class="loader-particles">
            <div class="loader-particle"></div>
            <div class="loader-particle"></div>
            <div class="loader-particle"></div>
            <div class="loader-particle"></div>
            <div class="loader-particle"></div>
            <div class="loader-particle"></div>
            <div class="loader-particle"></div>
            <div class="loader-particle"></div>
        </div>
        <div class="loader-icon">
            <i class="ph-fill ph-steering-wheel"></i>
        </div>
    </div>
    <div class="loader-track">
        <div class="loader-bar"></div>
    </div>
    <div class="loader-text">Igniting Engine</div>
    <div class="loader-car">
        <svg viewBox="0 0 60 30" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M5 20 L5 15 L10 10 L20 8 L40 8 L50 10 L55 15 L55 20 L50 25 L10 25 L5 20 Z" fill="currentColor"/>
            <circle cx="12" cy="25" r="4" fill="#0f172a"/>
            <circle cx="48" cy="25" r="4" fill="#0f172a"/>
        </svg>
    </div>
</div>

<nav id="navbar" class="fixed w-full z-50 transition-all duration-300 py-5 bg-white/90 backdrop-blur-md border-b border-gray-100 shadow-sm">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 flex justify-between items-center">

        <a href="index.jsp" class="flex items-center gap-3 group transition-link">
            <div class="bg-gradient-to-br from-blue-500 to-indigo-600 p-2.5 rounded-xl shadow-lg group-hover:shadow-blue-500/30 transition-all">
                <i class="ph-fill ph-car-profile text-2xl text-white"></i>
            </div>
            <span class="text-2xl font-black tracking-tight bg-gradient-to-r from-gray-900 to-gray-700 bg-clip-text text-transparent">DriveAway</span>
        </a>

        <div class="hidden lg:flex items-center gap-8">
            <a href="#fleet" data-section-link="fleet" class="nav-link text-sm font-bold text-gray-600 hover:text-[#0D6EFD] transition-colors uppercase tracking-wide">Our Fleet</a>
            <a href="#how-it-works" data-section-link="how-it-works" class="nav-link text-sm font-bold text-gray-600 hover:text-[#0D6EFD] transition-colors uppercase tracking-wide">Process</a>
            <a href="#team" data-section-link="team" class="nav-link text-sm font-bold text-gray-600 hover:text-[#0D6EFD] transition-colors uppercase tracking-wide">Team</a>
            <a href="about.jsp" class="text-sm font-bold text-gray-600 hover:text-[#0D6EFD] transition-colors uppercase tracking-wide transition-link">About</a>
            <a href="contact.jsp" class="text-sm font-bold text-gray-600 hover:text-[#0D6EFD] transition-colors uppercase tracking-wide transition-link">Contact</a>
        </div>

        <% if (userRole.isEmpty()) { %>
        <div class="hidden md:flex items-center gap-4">
            <a href="login.jsp" class="text-base font-extrabold text-gray-600 hover:text-black transition-colors px-4 py-2 transition-link">Sign In</a>
            <a href="register.jsp" class="bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-extrabold py-3 px-8 rounded-full hover:from-blue-700 hover:to-indigo-700 transition-all transform hover:-translate-y-0.5 shadow-lg shadow-blue-500/30 tracking-wide transition-link">
                Join Network
            </a>
        </div>
        <% } else { %>
        <div class="hidden md:flex items-center gap-4">
            <div class="flex items-center gap-3 bg-gradient-to-r from-blue-50 to-indigo-50 px-4 py-2 rounded-xl border border-blue-100">
                <div class="w-9 h-9 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-full flex items-center justify-center">
                    <i class="ph-bold ph-user text-lg text-white"></i>
                </div>
                <span class="font-bold text-gray-900 text-sm"><%= displayName %></span>
            </div>
            <% if ("customer".equals(userRole)) { %>
            <a href="customer-home.jsp" class="bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-extrabold py-3 px-6 rounded-xl hover:from-blue-700 hover:to-indigo-700 transition-all shadow-lg shadow-blue-500/30 transition-link">
                Dashboard
            </a>
            <% } else { %>
            <a href="staff-home.jsp" class="bg-gradient-to-r from-rose-500 to-pink-600 text-white font-extrabold py-3 px-6 rounded-xl hover:from-rose-600 hover:to-pink-700 transition-all shadow-lg shadow-rose-500/30 transition-link">
                Staff Portal
            </a>
            <% } %>
        </div>
        <% } %>

        <button id="mobile-menu-btn" class="lg:hidden text-gray-600 focus:outline-none bg-gray-50 p-2 rounded-lg">
            <i class="ph-bold ph-list text-2xl"></i>
        </button>
    </div>

    <div id="mobile-menu" class="hidden lg:hidden bg-white absolute top-full left-0 w-full border-b border-gray-100 shadow-2xl">
        <div class="flex flex-col p-6 space-y-3">
            <a href="#fleet" class="text-lg font-bold text-gray-800 hover:text-[#0D6EFD] py-2 border-b border-gray-50">Our Fleet</a>
            <a href="#how-it-works" class="text-lg font-bold text-gray-800 hover:text-[#0D6EFD] py-2 border-b border-gray-50">How it Works</a>
            <a href="about.jsp" class="text-lg font-bold text-gray-800 hover:text-[#0D6EFD] py-2 border-b border-gray-50 transition-link">About Us</a>
            <a href="contact.jsp" class="text-lg font-bold text-gray-800 hover:text-[#0D6EFD] py-2 border-b border-gray-50 transition-link">Contact Support</a>
            <% if (userRole.isEmpty()) { %>
            <div class="pt-4 flex flex-col gap-3">
                <a href="login.jsp" class="bg-gray-100 text-gray-900 font-bold py-3 rounded-xl text-center transition-link">Sign In</a>
                <a href="register.jsp" class="bg-[#0D6EFD] text-white font-bold py-3 rounded-xl text-center transition-link">Join Network</a>
            </div>
            <% } else { %>
            <div class="pt-4 flex flex-col gap-3">
                <div class="bg-blue-50 text-gray-900 font-bold py-3 rounded-xl text-center px-4 flex items-center justify-center gap-2">
                    <i class="ph-bold ph-user"></i> <%= displayName %>
                </div>
                <% if ("customer".equals(userRole)) { %>
                <a href="customer-home.jsp" class="bg-[#0D6EFD] text-white font-bold py-3 rounded-xl text-center transition-link">Go to Dashboard</a>
                <% } else { %>
                <a href="staff-home.jsp" class="bg-rose-500 text-white font-bold py-3 rounded-xl text-center transition-link">Staff Portal</a>
                <% } %>
            </div>
            <% } %>
        </div>
    </div>
</nav>

<header id="cinematic-hero" class="cinematic-hero pt-32 pb-20 bg-[#F8FAFF] relative overflow-hidden min-h-[90vh] flex items-center border-b border-gray-200">
    <div class="hero-video-shell" aria-hidden="true">
        <video id="hero-scroll-video" class="hero-video" muted loop playsinline preload="metadata" poster="https://placehold.co/1920x1080/0f172a/ffffff?text=DriveAway+Cinematic+Hero">
            <source src="<%= request.getContextPath() %>/assets/videos/hero-car.mp4" type="video/mp4">
        </video>
        <div class="hero-video-overlay"></div>
    </div>

    <div class="absolute inset-0 pointer-events-none overflow-hidden">
        <div class="brand-shape w-[500px] h-[500px] -top-32 -left-32 bg-gradient-to-br from-[#7A52FA] to-[#4A12DA] rounded-full" style="animation-delay: 0s;"></div>
        <div class="brand-shape w-[600px] h-[600px] bottom-[-10%] right-[-5%] bg-gradient-to-br from-[#00F2FE] to-[#0088FF] rounded-full" style="animation-delay: 1.5s;"></div>
        <div class="brand-shape w-[400px] h-[400px] top-[20%] left-[40%] bg-gradient-to-br from-[#FFE044] to-[#D6A200] rounded-full" style="animation-delay: 0.7s;"></div>
    </div>

    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 relative z-10 w-full flex flex-col lg:flex-row items-center gap-16">

        <div class="w-full lg:w-1/2 text-center lg:text-left">
            <div class="reveal-left hero-copy">
                <div class="inline-flex items-center gap-2 bg-white px-5 py-2.5 rounded-full border border-gray-200 shadow-sm mb-8">
                    <div class="w-2.5 h-2.5 rounded-full bg-green-500 animate-pulse"></div>
                    <span class="text-sm font-black text-gray-700 tracking-widest uppercase hero-text">Sri Lanka's #1 Fleet</span>
                </div>
                <h1 class="text-[4rem] md:text-[5rem] font-black mb-6 tracking-tight leading-[1.05] text-gray-900 hero-heading">
                    Your journey,<br>
                    <span class="gradient-text">perfectly driven.</span>
                </h1>
                <p class="text-xl font-medium mb-10 max-w-lg mx-auto lg:mx-0 leading-relaxed hero-paragraph">
                    Skip the counter. Browse verified vehicles, book your exact dates, and unlock the island's most reliable rental network.
                </p>
                <div class="flex flex-wrap gap-3 mb-10 justify-center lg:justify-start">
                    <div class="hero-metric"><i class="ph-fill ph-shield-check text-[#0D6EFD]"></i> Fully insured</div>
                    <div class="hero-metric"><i class="ph-fill ph-lightning text-[#0D6EFD]"></i> Instant booking</div>
                    <div class="hero-metric"><i class="ph-fill ph-headset text-[#0D6EFD]"></i> 24/7 support</div>
                </div>
            </div>

            <div class="glass-panel p-8 md:p-10 rounded-[2.5rem] shadow-[0_20px_60px_rgba(0,0,0,0.06)] border border-gray-100 max-w-2xl mx-auto lg:mx-0 reveal" style="transition-delay: 0.1s;">
                <% if (userRole.isEmpty()) { %>
                <form action="login.jsp" method="GET" class="space-y-6">
                        <% } else { %>
                    <form action="browse-cars.jsp" method="GET" class="space-y-6">
                        <% } %>
                        <div class="border-b border-gray-200 pb-6 mb-6">
                            <label for="pickup-location" class="block text-xs font-black text-gray-400 mb-3 uppercase tracking-widest">Pick-up Location</label>
                            <div class="relative">
                                <i class="ph-fill ph-map-pin absolute left-4 top-4 text-[#0D6EFD] text-xl"></i>
                                <select id="pickup-location" name="location" class="w-full bg-gray-50 border border-gray-200 py-4 pl-12 pr-4 rounded-xl focus:border-[#0D6EFD] outline-none transition-colors text-lg font-bold text-gray-900 appearance-none">
                                    <option value="colombo">Colombo 03 - Main Hub</option>
                                    <option value="airport">BIA Airport - Katunayake</option>
                                    <option value="kandy">Kandy - City Center</option>
                                    <option value="galle">Galle - Fort Entrance</option>
                                </select>
                                <i class="ph-bold ph-caret-down absolute right-4 top-4 text-gray-400"></i>
                            </div>
                        </div>

                        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div class="bg-gray-50 p-4 rounded-xl border border-gray-200 hover:border-[#0D6EFD] transition-colors">
                                <label for="pickup-date" class="block text-xs font-black text-gray-400 mb-2 uppercase tracking-widest">Pick-up</label>
                                <div class="flex items-center gap-2">
                                    <input id="pickup-date" type="date" name="pickupDate" required class="w-full bg-transparent outline-none text-lg font-bold text-gray-900">
                                </div>
                            </div>
                            <div class="bg-gray-50 p-4 rounded-xl border border-gray-200 hover:border-[#0D6EFD] transition-colors">
                                <label for="dropoff-date" class="block text-xs font-black text-gray-400 mb-2 uppercase tracking-widest">Drop-off</label>
                                <div class="flex items-center gap-2">
                                    <input id="dropoff-date" type="date" name="dropoffDate" required class="w-full bg-transparent outline-none text-lg font-bold text-gray-900">
                                </div>
                            </div>
                        </div>

                        <button type="submit" class="w-full bg-gradient-to-r from-gray-900 to-black text-white font-black py-5 rounded-2xl hover:from-black hover:to-gray-900 transition-all transform hover:-translate-y-1 shadow-lg mt-4 text-xl tracking-wide flex justify-center items-center gap-3">
                            Find Available Vehicles <i class="ph-bold ph-magnifying-glass text-2xl"></i>
                        </button>
                    </form>
            </div>
        </div>

        <div class="w-full lg:w-1/2 flex justify-center lg:justify-end reveal-right" style="transition-delay: 0.2s;">
            <div class="relative w-full max-w-lg lg:max-w-full perspective-[1000px]">
                <img src="https://placehold.co/800x600/0D6EFD/ffffff?text=DriveAway+Premium+Fleet" alt="Luxury Car" class="rounded-[3rem] shadow-2xl object-cover h-[600px] w-full border-[8px] border-white transform lg:rotate-3 transition-transform hover:rotate-0 duration-700 ease-out">

                <div class="absolute -bottom-8 -left-8 bg-white p-6 rounded-3xl shadow-[0_20px_40px_rgba(0,0,0,0.1)] hidden lg:flex items-center gap-4 animate-bounce" style="animation-duration: 3s;">
                    <div class="bg-yellow-50 p-3 rounded-2xl"><i class="ph-fill ph-star text-yellow-400 text-3xl"></i></div>
                    <div>
                        <div class="font-black text-2xl text-gray-900">4.98 / 5.0</div>
                        <div class="text-sm font-bold text-gray-500 uppercase tracking-widest">Customer Rating</div>
                    </div>
                </div>

                <div class="absolute top-8 -right-6 bg-gray-900 text-white p-5 rounded-2xl shadow-xl hidden xl:block">
                    <div class="text-[10px] font-bold text-blue-200 uppercase tracking-widest mb-1">Concierge</div>
                    <div class="font-black text-lg">Roadside in 25 min</div>
                    <div class="text-xs text-gray-300 mt-1">Island-wide emergency support</div>
                </div>
            </div>
        </div>
    </div>
</header>

<section class="py-16 bg-white border-b border-gray-100 relative z-20 -mt-12 mx-6 lg:mx-auto max-w-[1200px] rounded-[2.5rem] shadow-[0_20px_50px_rgba(0,0,0,0.06)]">
    <div class="grid grid-cols-2 md:grid-cols-4 gap-8 text-center divide-x divide-gray-100">
        <div class="reveal">
            <div class="flex justify-center mb-3"><i class="ph-fill ph-car text-3xl text-[#0D6EFD]"></i></div>
            <div class="text-4xl font-black text-gray-900 mb-1"><span class="counter" data-target="250">0</span>+</div>
            <div class="text-xs font-black text-gray-400 uppercase tracking-widest">Premium Vehicles</div>
        </div>
        <div class="reveal" style="transition-delay: 0.1s;">
            <div class="flex justify-center mb-3"><i class="ph-fill ph-users text-3xl text-[#0D6EFD]"></i></div>
            <div class="text-4xl font-black text-gray-900 mb-1"><span class="counter" data-target="1200">0</span>+</div>
            <div class="text-xs font-black text-gray-400 uppercase tracking-widest">Active Drivers</div>
        </div>
        <div class="reveal" style="transition-delay: 0.2s;">
            <div class="flex justify-center mb-3"><i class="ph-fill ph-map-pin text-3xl text-[#0D6EFD]"></i></div>
            <div class="text-4xl font-black text-gray-900 mb-1"><span class="counter" data-target="15">0</span></div>
            <div class="text-xs font-black text-gray-400 uppercase tracking-widest">City Hubs</div>
        </div>
        <div class="reveal" style="transition-delay: 0.3s;">
            <div class="flex justify-center mb-3"><i class="ph-fill ph-star text-3xl text-[#0D6EFD]"></i></div>
            <div class="text-4xl font-black text-gray-900 mb-1"><span class="counter" data-target="4.9">0</span></div>
            <div class="text-xs font-black text-gray-400 uppercase tracking-widest">Average Rating</div>
        </div>
    </div>
</section>

<div class="section-divider" aria-hidden="true"></div>

<section class="py-12 bg-white border-b border-gray-100 overflow-hidden">
    <div class="max-w-[1400px] mx-auto px-6 text-center reveal">
        <p class="text-sm font-bold text-gray-400 uppercase tracking-widest mb-8">Trusted by corporate teams across Sri Lanka</p>
        <div class="flex flex-wrap justify-center items-center gap-12 md:gap-20 opacity-50 grayscale hover:grayscale-0 transition-all duration-500">
            <div class="flex items-center gap-2 font-black text-2xl text-gray-800"><i class="ph-fill ph-buildings text-4xl"></i> TechCorp</div>
            <div class="flex items-center gap-2 font-black text-2xl text-gray-800"><i class="ph-fill ph-airplane-tilt text-4xl"></i> AeroLanka</div>
            <div class="flex items-center gap-2 font-black text-2xl text-gray-800"><i class="ph-fill ph-bank text-4xl"></i> FinTrust</div>
            <div class="flex items-center gap-2 font-black text-2xl text-gray-800"><i class="ph-fill ph-code text-4xl"></i> DevStudios</div>
        </div>
    </div>
</section>

<section id="categories" class="py-24 bg-[#F8FAFF]">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">
        <div class="text-center mb-16 reveal">
            <h2 class="text-[2.5rem] font-black mb-4 tracking-tight text-gray-900">Find Your Perfect Drive</h2>
            <p class="text-gray-500 font-medium text-xl">From compact city cars to rugged off-roaders.</p>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6">
            <div class="category-card bg-white p-6 rounded-3xl text-center border border-gray-100 shadow-sm cursor-pointer reveal">
                <img src="https://cdn-icons-png.flaticon.com/512/3204/3204934.png" alt="Economy" class="w-20 mx-auto mb-4 opacity-80">
                <h3 class="font-black text-lg text-gray-900">Economy</h3>
                <p class="text-xs font-bold text-gray-400 uppercase tracking-widest mt-1">From Rs.4,000</p>
            </div>
            <div class="category-card bg-white p-6 rounded-3xl text-center border border-gray-100 shadow-sm cursor-pointer reveal" style="transition-delay: 0.1s;">
                <img src="https://cdn-icons-png.flaticon.com/512/3097/3097180.png" alt="Sedan" class="w-20 mx-auto mb-4 opacity-80">
                <h3 class="font-black text-lg text-gray-900">Sedan</h3>
                <p class="text-xs font-bold text-gray-400 uppercase tracking-widest mt-1">From Rs.8,000</p>
            </div>
            <div class="category-card bg-[#0D6EFD] text-white p-6 rounded-3xl text-center shadow-lg cursor-pointer transform scale-105 reveal" style="transition-delay: 0.2s;">
                <img src="https://cdn-icons-png.flaticon.com/512/3757/3757342.png" alt="SUV" class="w-20 mx-auto mb-4 filter invert">
                <h3 class="font-black text-lg">Premium SUV</h3>
                <p class="text-xs font-bold text-blue-200 uppercase tracking-widest mt-1">Most Popular</p>
            </div>
            <div class="category-card bg-white p-6 rounded-3xl text-center border border-gray-100 shadow-sm cursor-pointer reveal" style="transition-delay: 0.3s;">
                <img src="https://cdn-icons-png.flaticon.com/512/2554/2554982.png" alt="Van" class="w-20 mx-auto mb-4 opacity-80">
                <h3 class="font-black text-lg text-gray-900">Family Van</h3>
                <p class="text-xs font-bold text-gray-400 uppercase tracking-widest mt-1">From Rs.6,000</p>
            </div>
            <div class="category-card bg-white p-6 rounded-3xl text-center border border-gray-100 shadow-sm cursor-pointer reveal" style="transition-delay: 0.4s;">
                <img src="https://cdn-icons-png.flaticon.com/512/2962/2962303.png" alt="4x4" class="w-20 mx-auto mb-4 opacity-80">
                <h3 class="font-black text-lg text-gray-900">4x4 Off-Road</h3>
                <p class="text-xs font-bold text-gray-400 uppercase tracking-widest mt-1">From Rs.15,000</p>
            </div>
            <div class="category-card bg-white p-6 rounded-3xl text-center border border-gray-100 shadow-sm cursor-pointer reveal" style="transition-delay: 0.5s;">
                <img src="https://cdn-icons-png.flaticon.com/512/2830/2830305.png" alt="EV" class="w-20 mx-auto mb-4 opacity-80">
                <h3 class="font-black text-lg text-gray-900">Electric</h3>
                <p class="text-xs font-bold text-gray-400 uppercase tracking-widest mt-1">Eco Friendly</p>
            </div>
        </div>
    </div>
</section>

<section id="how-it-works" class="py-24 bg-white">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">
        <div class="text-center mb-20 reveal">
            <h2 class="text-[2.5rem] font-black mb-4 tracking-tight text-gray-900">The DriveAway Process</h2>
            <p class="text-gray-500 font-medium text-xl">Get behind the wheel in three effortless steps.</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-12 relative">
            <div class="hidden md:block absolute top-12 left-[15%] right-[15%] h-1 bg-gray-100 z-0"></div>

            <div class="text-center reveal relative z-10">
                <div class="w-24 h-24 mx-auto bg-blue-50 rounded-3xl flex items-center justify-center mb-8 border-4 border-white shadow-lg">
                    <i class="ph-fill ph-magnifying-glass text-4xl text-[#0D6EFD]"></i>
                </div>
                <h3 class="text-2xl font-extrabold mb-3 text-gray-900">1. Browse</h3>
                <p class="text-gray-500 font-medium text-lg leading-relaxed">Search our live inventory. Filter by type, transmission, and seating capacity to find your perfect match.</p>
            </div>

            <div class="text-center reveal relative z-10" style="transition-delay: 0.1s;">
                <div class="w-24 h-24 mx-auto bg-[#0D6EFD] rounded-3xl flex items-center justify-center mb-8 border-4 border-white shadow-xl shadow-blue-500/30 transform scale-110">
                    <i class="ph-fill ph-calendar-check text-4xl text-white"></i>
                </div>
                <h3 class="text-2xl font-extrabold mb-3 text-gray-900">2. Reserve</h3>
                <p class="text-gray-500 font-medium text-lg leading-relaxed">Select your dates. Our smart backend instantly calculates transparent pricing with absolutely zero hidden fees.</p>
            </div>

            <div class="text-center reveal relative z-10" style="transition-delay: 0.2s;">
                <div class="w-24 h-24 mx-auto bg-gray-900 rounded-3xl flex items-center justify-center mb-8 border-4 border-white shadow-lg">
                    <i class="ph-fill ph-steering-wheel text-4xl text-white"></i>
                </div>
                <h3 class="text-2xl font-extrabold mb-3 text-gray-900">3. Drive</h3>
                <p class="text-gray-500 font-medium text-lg leading-relaxed">Show your secure digital booking confirmation at the desk, grab the keys, and start your adventure.</p>
            </div>
        </div>
    </div>
</section>

<section id="fleet" class="py-24 bg-[#F0F2F5] border-y border-gray-200">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">

        <div class="flex flex-col md:flex-row justify-between items-end mb-12 reveal">
            <div>
                <h2 class="text-[2.5rem] font-black mb-4 tracking-tight text-gray-900">Live Inventory</h2>
                <p class="text-gray-500 font-medium text-xl">The cleanest, most reliable fleet in the country.</p>
            </div>
            <% if (userRole.isEmpty()) { %>
            <a href="login.jsp" class="transition-link hidden md:flex items-center gap-2 text-lg font-bold text-white bg-gradient-to-r from-gray-900 to-black hover:from-black hover:to-gray-900 px-8 py-4 rounded-2xl transition-all shadow-lg transform hover:-translate-y-1">
                View All Vehicles <i class="ph-bold ph-arrow-right"></i>
            </a>
            <% } else { %>
            <a href="browse-cars.jsp" class="transition-link hidden md:flex items-center gap-2 text-lg font-bold text-white bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 px-8 py-4 rounded-2xl transition-all shadow-lg shadow-blue-500/30 transform hover:-translate-y-1">
                Browse Full Fleet <i class="ph-bold ph-arrow-right"></i>
            </a>
            <% } %>
        </div>

        <div class="flex flex-wrap gap-3 mb-10 reveal" style="transition-delay: 0.05s;">
            <button type="button" class="filter-btn active border border-blue-600 bg-[#0D6EFD] text-white px-5 py-2.5 rounded-full text-sm font-bold tracking-wide" data-filter="all" aria-pressed="true">All Vehicles</button>
            <button type="button" class="filter-btn border border-gray-200 bg-white text-gray-700 px-5 py-2.5 rounded-full text-sm font-bold tracking-wide" data-filter="sedan" aria-pressed="false">Sedan</button>
            <button type="button" class="filter-btn border border-gray-200 bg-white text-gray-700 px-5 py-2.5 rounded-full text-sm font-bold tracking-wide" data-filter="suv" aria-pressed="false">SUV / 4x4</button>
            <button type="button" class="filter-btn border border-gray-200 bg-white text-gray-700 px-5 py-2.5 rounded-full text-sm font-bold tracking-wide" data-filter="luxury" aria-pressed="false">Luxury</button>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10" id="fleet-grid">
            <%
                int displayCount = 0;
                int maxDisplay = 6;
                if (vehicles != null && !vehicles.isEmpty()) {
                    for (Vehicle v : vehicles) {
                        if (displayCount >= maxDisplay) break;
                        displayCount++;
                        String vehicleType = v.getVehicleType().toLowerCase();
                        String category = "sedan";
                        if (vehicleType.contains("suv") || vehicleType.contains("4x4")) category = "suv";
                        else if (vehicleType.contains("luxury")) category = "luxury";
                        String imageUrl = "https://placehold.co/600x400/0D6EFD/ffffff?text=Sedan+" + v.getBrand();
                        if (vehicleType.contains("suv")) imageUrl = "https://placehold.co/600x400/10B981/ffffff?text=SUV+" + v.getBrand();
                        else if (vehicleType.contains("luxury")) imageUrl = "https://placehold.co/600x400/7C3AED/ffffff?text=Luxury+" + v.getBrand();
                        else if (vehicleType.contains("hatchback")) imageUrl = "https://placehold.co/600x400/F59E0B/ffffff?text=Hatchback+" + v.getBrand();
            %>
            <div class="car-item premium-card bg-white rounded-[2.5rem] border border-gray-100 overflow-hidden reveal shadow-[0_10px_30px_rgba(0,0,0,0.03)]" data-category="<%= category %>">
                <div class="h-64 overflow-hidden relative bg-gray-100 group">
                    <img src="<%= imageUrl %>" alt="Car" class="w-full h-full object-cover transform group-hover:scale-110 transition-transform duration-700 ease-out">
                    <div class="absolute top-5 left-5 bg-white/95 backdrop-blur-md px-4 py-1.5 rounded-xl text-xs font-black text-gray-900 shadow-sm uppercase tracking-widest"><%= v.getBrand() %></div>
                    <div class="absolute top-5 right-5 bg-black/80 backdrop-blur-md text-white px-3 py-1.5 rounded-xl text-xs font-bold shadow-sm flex items-center gap-1">
                        <i class="ph-fill ph-star text-yellow-400"></i> 4.8
                    </div>
                </div>

                <div class="p-8">
                    <div class="flex justify-between items-start mb-6 border-b border-gray-100 pb-6">
                        <h3 class="text-2xl font-black text-gray-900 tracking-tight leading-tight w-2/3"><%= v.getBrand() %> <%= v.getModel() %></h3>
                        <div class="text-right w-1/3">
                            <span class="block text-2xl font-black text-[#0D6EFD]">$<%= String.format("%.0f", v.calculateRentalRate()) %></span>
                            <span class="text-[10px] font-bold text-gray-400 uppercase tracking-widest">Per Day</span>
                        </div>
                    </div>

                    <div class="grid grid-cols-2 gap-4 mb-8">
                        <div class="flex items-center gap-3 text-gray-700 font-bold bg-[#F8FAFF] border border-blue-50 p-3 rounded-2xl"><div class="bg-white p-1.5 rounded-lg shadow-sm"><i class="ph-fill ph-car text-[#0D6EFD] text-lg"></i></div> <%= v.getVehicleType() %></div>
                        <div class="flex items-center gap-3 text-gray-700 font-bold bg-[#F8FAFF] border border-blue-50 p-3 rounded-2xl"><div class="bg-white p-1.5 rounded-lg shadow-sm"><i class="ph-fill ph-info text-[#0D6EFD] text-lg"></i></div> <%= v.getSpecificDetail() %></div>
                    </div>

                    <% if (userRole.isEmpty()) { %>
                    <a href="login.jsp" class="transition-link block w-full bg-gray-50 border border-gray-200 text-gray-900 font-extrabold py-4 rounded-2xl hover:bg-[#0D6EFD] hover:text-white hover:border-[#0D6EFD] transition-colors shadow-sm text-center text-lg tracking-wide">
                        Check Dates
                    </a>
                    <% } else { %>
                    <a href="add-booking.jsp?vehicleId=<%= v.getId() %>&rate=<%= v.calculateRentalRate() %>" class="transition-link block w-full bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-extrabold py-4 rounded-2xl hover:from-blue-700 hover:to-indigo-700 transition-colors shadow-lg shadow-blue-500/30 text-center text-lg tracking-wide">
                        Book Now
                    </a>
                    <% } %>
                </div>
            </div>
            <% } } else { %>
            <div class="col-span-full text-center py-16 bg-white border border-dashed border-gray-300 rounded-3xl">
                <i class="ph-fill ph-car text-5xl text-gray-300"></i>
                <p class="text-gray-500 font-medium text-lg mt-4">No vehicles available at the moment.</p>
            </div>
            <% } %>
        </div>
    </div>
</section>

<section class="py-24 bg-white">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">
        <div class="text-center mb-16 reveal">
            <h2 class="text-[2.5rem] font-black mb-4 tracking-tight text-gray-900">The DriveAway Advantage</h2>
            <p class="text-gray-500 font-medium text-xl">We don't just rent cars. We deliver peace of mind.</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            <div class="feature-card bg-gray-50 p-10 rounded-[2.5rem] reveal shadow-sm">
                <div class="w-16 h-16 bg-blue-50 rounded-2xl flex items-center justify-center mb-8"><i class="ph-fill ph-shield-check text-4xl text-[#0D6EFD]"></i></div>
                <h3 class="text-2xl font-black mb-3">Fully Insured</h3>
                <p class="text-gray-500 font-medium text-lg leading-relaxed">Every vehicle comes with comprehensive insurance coverage for your total protection on the road.</p>
            </div>
            <div class="feature-card bg-gray-50 p-10 rounded-[2.5rem] reveal shadow-sm" style="transition-delay: 0.1s;">
                <div class="w-16 h-16 bg-blue-50 rounded-2xl flex items-center justify-center mb-8"><i class="ph-fill ph-headset text-4xl text-[#0D6EFD]"></i></div>
                <h3 class="text-2xl font-black mb-3">24/7 Support</h3>
                <p class="text-gray-500 font-medium text-lg leading-relaxed">Our dedicated response team is available around the clock for roadside assistance and booking queries.</p>
            </div>
            <div class="feature-card bg-gray-50 p-10 rounded-[2.5rem] reveal shadow-sm" style="transition-delay: 0.2s;">
                <div class="w-16 h-16 bg-blue-50 rounded-2xl flex items-center justify-center mb-8"><i class="ph-fill ph-sparkle text-4xl text-[#0D6EFD]"></i></div>
                <h3 class="text-2xl font-black mb-3">Clean Promise</h3>
                <p class="text-gray-500 font-medium text-lg leading-relaxed">Every car undergoes a rigorous 15-point mechanical and interior cleaning inspection before handover.</p>
            </div>
            <div class="feature-card bg-gray-50 p-10 rounded-[2.5rem] reveal shadow-sm">
                <div class="w-16 h-16 bg-blue-50 rounded-2xl flex items-center justify-center mb-8"><i class="ph-fill ph-money text-4xl text-[#0D6EFD]"></i></div>
                <h3 class="text-2xl font-black mb-3">Transparent Pricing</h3>
                <p class="text-gray-500 font-medium text-lg leading-relaxed">What you see is what you pay. Zero hidden fees, clear deposit rules, and instant refunds.</p>
            </div>
            <div class="feature-card bg-gray-50 p-10 rounded-[2.5rem] reveal shadow-sm" style="transition-delay: 0.1s;">
                <div class="w-16 h-16 bg-blue-50 rounded-2xl flex items-center justify-center mb-8"><i class="ph-fill ph-clock-clockwise text-4xl text-[#0D6EFD]"></i></div>
                <h3 class="text-2xl font-black mb-3">Instant Booking</h3>
                <p class="text-gray-500 font-medium text-lg leading-relaxed">Bypass the rental counter. Book completely online and your car will be waiting for you.</p>
            </div>
            <div class="feature-card bg-gray-50 p-10 rounded-[2.5rem] reveal shadow-sm" style="transition-delay: 0.2s;">
                <div class="w-16 h-16 bg-blue-50 rounded-2xl flex items-center justify-center mb-8"><i class="ph-fill ph-map-trifold text-4xl text-[#0D6EFD]"></i></div>
                <h3 class="text-2xl font-black mb-3">Unlimited Mileage</h3>
                <p class="text-gray-500 font-medium text-lg leading-relaxed">Select premium packages include unlimited kilometers so you can explore Sri Lanka without limits.</p>
            </div>
        </div>
    </div>
</section>

<section id="locations" class="py-24 bg-white border-b border-gray-100">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">
        <div class="flex flex-col lg:flex-row gap-16 items-center">
            <div class="w-full lg:w-1/3 reveal">
                <div class="inline-flex items-center gap-2 bg-blue-50 px-4 py-2 rounded-full mb-6">
                    <i class="ph-fill ph-map-pin text-[#0D6EFD]"></i>
                    <span class="text-sm font-bold text-[#0D6EFD] tracking-wide uppercase">Island Wide</span>
                </div>
                <h2 class="text-[2.5rem] font-black mb-6 tracking-tight text-gray-900">Find us across Sri Lanka</h2>
                <p class="text-gray-500 font-medium text-xl leading-relaxed mb-8">With hubs in major cities and airports, picking up and dropping off your vehicle is incredibly convenient.</p>
                <a href="contact.jsp" class="text-[#0D6EFD] font-bold text-lg flex items-center gap-2 hover:underline transition-link">View all 15 locations <i class="ph-bold ph-arrow-right"></i></a>
            </div>

            <div class="w-full lg:w-2/3 grid grid-cols-1 sm:grid-cols-2 gap-6">
                <div class="bg-gray-50 p-8 rounded-[2rem] border border-gray-200 reveal">
                    <h3 class="text-2xl font-black text-gray-900 mb-2">Colombo Main Hub</h3>
                    <p class="text-gray-500 font-medium mb-6">123 Galle Road, Colombo 03</p>
                    <div class="flex gap-2">
                        <span class="bg-white px-3 py-1 rounded-lg text-xs font-bold text-gray-600 shadow-sm border border-gray-200">24/7 Drop-off</span>
                        <span class="bg-white px-3 py-1 rounded-lg text-xs font-bold text-gray-600 shadow-sm border border-gray-200">Full Fleet</span>
                    </div>
                </div>
                <div class="bg-gray-50 p-8 rounded-[2rem] border border-gray-200 reveal" style="transition-delay: 0.1s;">
                    <h3 class="text-2xl font-black text-gray-900 mb-2">BIA Airport</h3>
                    <p class="text-gray-500 font-medium mb-6">Terminal 1 Arrivals, Katunayake</p>
                    <div class="flex gap-2">
                        <span class="bg-white px-3 py-1 rounded-lg text-xs font-bold text-gray-600 shadow-sm border border-gray-200">Meet & Greet</span>
                    </div>
                </div>
                <div class="bg-gray-50 p-8 rounded-[2rem] border border-gray-200 reveal">
                    <h3 class="text-2xl font-black text-gray-900 mb-2">Kandy Center</h3>
                    <p class="text-gray-500 font-medium mb-6">45 Dalada Vidiya, Kandy</p>
                    <div class="flex gap-2">
                        <span class="bg-white px-3 py-1 rounded-lg text-xs font-bold text-gray-600 shadow-sm border border-gray-200">SUV Specialist</span>
                    </div>
                </div>
                <div class="bg-gray-50 p-8 rounded-[2rem] border border-gray-200 reveal" style="transition-delay: 0.1s;">
                    <h3 class="text-2xl font-black text-gray-900 mb-2">Galle Fort</h3>
                    <p class="text-gray-500 font-medium mb-6">Main Gate Entrance, Galle</p>
                    <div class="flex gap-2">
                        <span class="bg-white px-3 py-1 rounded-lg text-xs font-bold text-gray-600 shadow-sm border border-gray-200">Compact Cars</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<section class="py-24 bg-white overflow-hidden">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">
        <div class="bg-[#1A1C23] rounded-[3rem] p-12 lg:p-20 relative flex flex-col lg:flex-row items-center justify-between overflow-hidden shadow-2xl reveal">
            <div class="absolute top-0 right-0 w-[600px] h-[600px] bg-[#0D6EFD]/20 rounded-full blur-[80px] -translate-y-1/2 translate-x-1/3"></div>

            <div class="w-full lg:w-1/2 relative z-10 text-center lg:text-left mb-12 lg:mb-0">
                <h2 class="text-[3rem] font-black text-white mb-6 leading-tight">Your keys, <br><span class="text-[#0D6EFD]">in your pocket.</span></h2>
                <p class="text-xl text-gray-400 font-medium mb-10 max-w-lg mx-auto lg:mx-0">Manage bookings, unlock cars digitally, and contact 24/7 support instantly with the new DriveAway mobile app.</p>
                <div class="flex flex-wrap justify-center lg:justify-start gap-4">
                    <button class="bg-white text-gray-900 font-black py-4 px-8 rounded-2xl hover:bg-gray-100 transition-colors flex items-center gap-3">
                        <i class="ph-fill ph-apple-logo text-2xl"></i> App Store
                    </button>
                    <button class="bg-transparent border-2 border-white/20 text-white font-black py-4 px-8 rounded-2xl hover:bg-white/10 transition-colors flex items-center gap-3">
                        <i class="ph-fill ph-google-play-logo text-2xl"></i> Google Play
                    </button>
                </div>
            </div>

            <div class="w-full lg:w-1/2 flex justify-center relative z-10 perspective-[1000px]">
                <div class="w-[300px] h-[600px] bg-white rounded-[3rem] border-8 border-gray-800 shadow-2xl relative overflow-hidden transform rotate-6 hover:rotate-0 transition-transform duration-500">
                    <div class="absolute top-0 inset-x-0 h-6 bg-gray-800 rounded-b-3xl w-40 mx-auto"></div>
                    <div class="p-6 pt-12 h-full flex flex-col">
                        <div class="flex justify-between items-center mb-8">
                            <div class="w-10 h-10 bg-gray-100 rounded-full"></div>
                            <div class="font-black text-lg">DriveAway</div>
                            <i class="ph-bold ph-list text-2xl"></i>
                        </div>
                        <div class="bg-blue-50 rounded-2xl p-6 mb-6">
                            <div class="text-xs font-bold text-[#0D6EFD] uppercase tracking-widest mb-1">Active Booking</div>
                            <div class="font-black text-xl mb-4">Toyota Axio</div>
                            <img src="https://images.unsplash.com/photo-1590362891991-f776e747a588?q=80&w=400&auto=format&fit=crop" alt="DriveAway mobile app showing active booking" class="rounded-xl w-full h-24 object-cover">
                        </div>
                        <div class="bg-gray-900 text-white text-center py-4 rounded-xl font-bold mt-auto flex justify-center items-center gap-2">
                            <i class="ph-bold ph-lock-key-open"></i> Tap to Unlock
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<section id="team" class="py-24 bg-[#F8FAFF] overflow-hidden relative border-y border-gray-200">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 relative z-10">
        <div class="text-center mb-20 reveal">
            <div class="inline-flex items-center gap-2 bg-white px-4 py-2 rounded-full border border-gray-200 shadow-sm mb-6">
                <i class="ph-fill ph-code text-[#0D6EFD]"></i>
                <span class="text-sm font-bold text-gray-600 tracking-widest uppercase">Built by Students</span>
            </div>
            <h2 class="text-[3rem] font-black mb-4 tracking-tight text-gray-900">Meet the Creators</h2>
            <p class="text-gray-500 font-medium text-xl max-w-2xl mx-auto">The engineering and design minds behind the DriveAway architecture.</p>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-3 gap-6 lg:gap-10">

            <div class="bg-white rounded-[2.5rem] p-8 border border-gray-100 shadow-[0_10px_30px_rgba(0,0,0,0.03)] flex flex-col items-center text-center hover:shadow-[0_20px_50px_rgba(13,110,253,0.1)] transition-all duration-300 reveal">
                <div class="h-40 w-full bg-[#F0F2F5] rounded-3xl relative flex justify-center items-end overflow-hidden mb-6">
                    <div id="mascot-1" class="mascot-avatar">
                        <div class="mascot-body shape-tall-black"></div>
                        <div class="mascot-eyes flex gap-3 drop-shadow-md absolute inset-x-0 mx-auto w-fit top-8"><div class="w-2 h-2 rounded-full bg-white eye-dot" style="animation-delay: 1s;"></div><div class="w-2 h-2 rounded-full bg-white eye-dot" style="animation-delay: 1s;"></div></div>
                    </div>
                </div>
                <h3 class="text-xl font-black text-gray-900 mb-1">Sankalpa K.M.C.P</h3>
                <p class="text-gray-400 text-xs font-bold tracking-widest mb-4">IT25100142</p>
                <span class="bg-blue-50 text-[#0D6EFD] text-xs font-bold px-4 py-1.5 rounded-lg uppercase tracking-wider">Founder & CEO</span>
            </div>

            <div class="bg-white rounded-[2.5rem] p-8 border border-gray-100 shadow-[0_10px_30px_rgba(0,0,0,0.03)] flex flex-col items-center text-center hover:shadow-[0_20px_50px_rgba(13,110,253,0.1)] transition-all duration-300 reveal" style="transition-delay: 0.1s;">
                <div class="h-40 w-full bg-[#F0F2F5] rounded-3xl relative flex justify-center items-end overflow-hidden mb-6">
                    <div id="mascot-2" class="mascot-avatar">
                        <div class="mascot-body shape-guard-cyan"></div>
                        <div class="mascot-eyes flex gap-3 drop-shadow-md absolute inset-x-0 mx-auto w-fit top-8"><div class="w-2.5 h-2.5 rounded-full bg-black eye-dot" style="animation-delay: 2s;"></div><div class="w-2.5 h-2.5 rounded-full bg-black eye-dot" style="animation-delay: 2s;"></div></div>
                    </div>
                </div>
                <h3 class="text-xl font-black text-gray-900 mb-1">Weerakoon W.M.Y.B</h3>
                <p class="text-gray-400 text-xs font-bold tracking-widest mb-4">IT25100565</p>
                <span class="bg-blue-50 text-[#0D6EFD] text-xs font-bold px-4 py-1.5 rounded-lg uppercase tracking-wider">Senior Developer</span>
            </div>

            <div class="bg-white rounded-[2.5rem] p-8 border border-gray-100 shadow-[0_10px_30px_rgba(0,0,0,0.03)] flex flex-col items-center text-center hover:shadow-[0_20px_50px_rgba(13,110,253,0.1)] transition-all duration-300 reveal" style="transition-delay: 0.2s;">
                <div class="h-40 w-full bg-[#F0F2F5] rounded-3xl relative flex justify-center items-end overflow-hidden mb-6">
                    <div id="mascot-3" class="mascot-avatar">
                        <div class="mascot-body shape-hijab">
                            <div class="hijab-face relative mx-auto">
                                <div class="mascot-eyes glasses-wrapper drop-shadow-sm absolute inset-x-0 mx-auto inset-y-0 my-auto w-fit h-fit"><div class="glasses-bridge"></div><div class="glasses-frame glasses-lg relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 1.5s;"></div></div><div class="glasses-frame glasses-lg relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 1.5s;"></div></div></div>
                            </div>
                        </div>
                    </div>
                </div>
                <h3 class="text-xl font-black text-gray-900 mb-1">Miftha N.S</h3>
                <p class="text-gray-400 text-xs font-bold tracking-widest mb-4">IT24100879</p>
                <span class="bg-blue-50 text-[#0D6EFD] text-xs font-bold px-4 py-1.5 rounded-lg uppercase tracking-wider">Operations</span>
            </div>

            <div class="bg-white rounded-[2.5rem] p-8 border border-gray-100 shadow-[0_10px_30px_rgba(0,0,0,0.03)] flex flex-col items-center text-center hover:shadow-[0_20px_50px_rgba(13,110,253,0.1)] transition-all duration-300 reveal">
                <div class="h-40 w-full bg-[#F0F2F5] rounded-3xl relative flex justify-center items-end overflow-hidden mb-6">
                    <div id="mascot-4" class="mascot-avatar">
                        <div class="mascot-body shape-yellow-girl"></div>
                        <div class="mascot-eyes glasses-wrapper drop-shadow-sm absolute inset-x-0 mx-auto w-fit top-8"><div class="glasses-bridge"></div><div class="glasses-frame glasses-sm relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 3s;"></div></div><div class="glasses-frame glasses-sm relative z-20"><div class="w-1.5 h-1.5 rounded-full bg-black eye-dot" style="animation-delay: 3s;"></div></div></div>
                    </div>
                </div>
                <h3 class="text-xl font-black text-gray-900 mb-1">Rajapakse J.A.C.D.</h3>
                <p class="text-gray-400 text-xs font-bold tracking-widest mb-4">IT25100385</p>
                <span class="bg-blue-50 text-[#0D6EFD] text-xs font-bold px-4 py-1.5 rounded-lg uppercase tracking-wider">Customer Exp.</span>
            </div>

            <div class="bg-white rounded-[2.5rem] p-8 border border-gray-100 shadow-[0_10px_30px_rgba(0,0,0,0.03)] flex flex-col items-center text-center hover:shadow-[0_20px_50px_rgba(13,110,253,0.1)] transition-all duration-300 reveal" style="transition-delay: 0.1s;">
                <div class="h-40 w-full bg-[#F0F2F5] rounded-3xl relative flex justify-center items-end overflow-hidden mb-6">
                    <div id="mascot-5" class="mascot-avatar">
                        <div class="mascot-body shape-boss"></div>
                        <div class="mascot-eyes flex gap-4 drop-shadow-md absolute inset-x-0 mx-auto w-fit top-8"><div class="w-2.5 h-2.5 rounded-full bg-white eye-dot" style="animation-delay: 0.5s;"></div><div class="w-2.5 h-2.5 rounded-full bg-white eye-dot" style="animation-delay: 0.5s;"></div></div>
                    </div>
                </div>
                <h3 class="text-xl font-black text-gray-900 mb-1">Liyanage H.K</h3>
                <p class="text-gray-400 text-xs font-bold tracking-widest mb-4">IT25100429</p>
                <span class="bg-blue-50 text-[#0D6EFD] text-xs font-bold px-4 py-1.5 rounded-lg uppercase tracking-wider">Tech Director</span>
            </div>

            <div class="bg-white rounded-[2.5rem] p-8 border border-gray-100 shadow-[0_10px_30px_rgba(0,0,0,0.03)] flex flex-col items-center text-center hover:shadow-[0_20px_50px_rgba(13,110,253,0.1)] transition-all duration-300 reveal" style="transition-delay: 0.2s;">
                <div class="h-40 w-full bg-[#F0F2F5] rounded-3xl relative flex justify-center items-end overflow-hidden mb-6">
                    <div id="mascot-6" class="mascot-avatar">
                        <div class="mascot-body shape-rookie"></div>
                        <div class="mascot-eyes flex gap-2 drop-shadow-sm absolute inset-x-0 mx-auto w-fit top-8"><div class="w-2 h-2 rounded-full bg-black eye-dot" style="animation-delay: 2.5s;"></div><div class="w-2 h-2 rounded-full bg-black eye-dot" style="animation-delay: 2.5s;"></div></div>
                    </div>
                </div>
                <h3 class="text-xl font-black text-gray-900 mb-1">Arusha H M D K</h3>
                <p class="text-gray-400 text-xs font-bold tracking-widest mb-4">IT25100445</p>
                <span class="bg-blue-50 text-[#0D6EFD] text-xs font-bold px-4 py-1.5 rounded-lg uppercase tracking-wider">Marketing Lead</span>
            </div>

        </div>
    </div>
</section>

<section id="testimonials" class="py-24 bg-white">
    <div class="max-w-[1000px] mx-auto px-6 lg:px-8 text-center reveal">
        <h2 class="text-[2.5rem] font-black mb-12 tracking-tight text-gray-900">What our drivers say</h2>

        <div class="relative bg-gray-50 p-12 md:p-16 rounded-[3rem] border border-gray-100 shadow-sm">
            <i class="ph-fill ph-quotes text-[#0D6EFD]/10 text-8xl absolute top-8 left-8"></i>

            <div id="carousel-track" class="relative z-10 min-h-[250px] flex items-center justify-center">
                <div class="testimonial-slide active">
                    <div class="flex justify-center gap-1 text-yellow-400 mb-8"><i class="ph-fill ph-star text-3xl"></i><i class="ph-fill ph-star text-3xl"></i><i class="ph-fill ph-star text-3xl"></i><i class="ph-fill ph-star text-3xl"></i><i class="ph-fill ph-star text-3xl"></i></div>
                    <p class="text-2xl md:text-3xl font-bold text-gray-800 mb-10 leading-relaxed tracking-tight">"The digital booking system is flawless. I bypassed the rental counter completely and just picked up the keys. DriveAway is the future of rentals."</p>
                    <div class="font-black text-gray-900 text-xl uppercase tracking-widest">Saman Perera</div>
                    <div class="text-gray-500 font-medium text-sm mt-1">Verified Driver, Colombo</div>
                </div>
                <div class="testimonial-slide">
                    <div class="flex justify-center gap-1 text-yellow-400 mb-8"><i class="ph-fill ph-star text-3xl"></i><i class="ph-fill ph-star text-3xl"></i><i class="ph-fill ph-star text-3xl"></i><i class="ph-fill ph-star text-3xl"></i><i class="ph-fill ph-star-half text-3xl"></i></div>
                    <p class="text-2xl md:text-3xl font-bold text-gray-800 mb-10 leading-relaxed tracking-tight">"I rented the Toyota Axio Hybrid for a down-south trip. The car was immaculate, fully fueled, and incredibly efficient."</p>
                    <div class="font-black text-gray-900 text-xl uppercase tracking-widest">Nimali Jayawardena</div>
                    <div class="text-gray-500 font-medium text-sm mt-1">Verified Driver, Kandy</div>
                </div>
                <div class="testimonial-slide">
                    <div class="flex justify-center gap-1 text-yellow-400 mb-8"><i class="ph-fill ph-star text-3xl"></i><i class="ph-fill ph-star text-3xl"></i><i class="ph-fill ph-star text-3xl"></i><i class="ph-fill ph-star text-3xl"></i><i class="ph-fill ph-star text-3xl"></i></div>
                    <p class="text-2xl md:text-3xl font-bold text-gray-800 mb-10 leading-relaxed tracking-tight">"Their customer support team is phenomenal. I needed to extend my rental by two days and it was handled in minutes via the portal."</p>
                    <div class="font-black text-gray-900 text-xl uppercase tracking-widest">Ravi Fernando</div>
                    <div class="text-gray-500 font-medium text-sm mt-1">Verified Driver, Galle</div>
                </div>
            </div>

            <div class="flex justify-center gap-4 mt-4 relative z-10">
                <button id="prev-test" class="w-14 h-14 rounded-full bg-white shadow-md border border-gray-100 flex items-center justify-center hover:bg-[#0D6EFD] hover:text-white hover:border-[#0D6EFD] transition-all"><i class="ph-bold ph-arrow-left text-xl"></i></button>
                <button id="next-test" class="w-14 h-14 rounded-full bg-white shadow-md border border-gray-100 flex items-center justify-center hover:bg-[#0D6EFD] hover:text-white hover:border-[#0D6EFD] transition-all"><i class="ph-bold ph-arrow-right text-xl"></i></button>
            </div>
        </div>
    </div>
</section>

<section id="faq" class="py-24 bg-[#F8FAFF] border-y border-gray-200">
    <div class="max-w-[900px] mx-auto px-6 lg:px-8">
        <div class="text-center mb-16 reveal">
            <h2 class="text-[2.5rem] font-black mb-4 tracking-tight text-gray-900">Frequently Asked Questions</h2>
            <p class="text-gray-500 font-medium text-xl">Everything you need to know about the DriveAway experience.</p>
        </div>

        <div class="space-y-4">
            <div class="faq-item bg-white rounded-[1.5rem] overflow-hidden reveal cursor-pointer border border-gray-100 transition-all shadow-sm">
                <div class="faq-header flex justify-between items-center p-6 md:p-8">
                    <h4 class="font-bold text-lg text-gray-900">What documents do I need to rent a vehicle?</h4>
                    <i class="ph-bold ph-caret-down text-xl text-gray-400 transition-transform duration-300 faq-icon"></i>
                </div>
                <div class="faq-answer">
                    <div class="px-6 md:px-8 pb-8 text-gray-500 font-medium text-lg leading-relaxed border-t border-gray-50 pt-4">You must provide a valid Sri Lankan National Identity Card (NIC) or Passport, along with a valid Driver's License that matches the vehicle class you are renting.</div>
                </div>
            </div>

            <div class="faq-item bg-white rounded-[1.5rem] overflow-hidden reveal cursor-pointer border border-gray-100 transition-all shadow-sm">
                <div class="faq-header flex justify-between items-center p-6 md:p-8">
                    <h4 class="font-bold text-lg text-gray-900">Do you require a security deposit?</h4>
                    <i class="ph-bold ph-caret-down text-xl text-gray-400 transition-transform duration-300 faq-icon"></i>
                </div>
                <div class="faq-answer">
                    <div class="px-6 md:px-8 pb-8 text-gray-500 font-medium text-lg leading-relaxed border-t border-gray-50 pt-4">Yes. A refundable security deposit is held on your credit or debit card at the time of pickup. The amount varies based on the vehicle class (e.g., Luxury vs Economy).</div>
                </div>
            </div>

            <div class="faq-item bg-white rounded-[1.5rem] overflow-hidden reveal cursor-pointer border border-gray-100 transition-all shadow-sm">
                <div class="faq-header flex justify-between items-center p-6 md:p-8">
                    <h4 class="font-bold text-lg text-gray-900">What is the cancellation policy?</h4>
                    <i class="ph-bold ph-caret-down text-xl text-gray-400 transition-transform duration-300 faq-icon"></i>
                </div>
                <div class="faq-answer">
                    <div class="px-6 md:px-8 pb-8 text-gray-500 font-medium text-lg leading-relaxed border-t border-gray-50 pt-4">You can cancel your reservation for free up to 24 hours before your scheduled pickup time. Cancellations made within 24 hours of pickup will incur a fee equivalent to one rental day.</div>
                </div>
            </div>

            <div class="faq-item bg-white rounded-[1.5rem] overflow-hidden reveal cursor-pointer border border-gray-100 transition-all shadow-sm">
                <div class="faq-header flex justify-between items-center p-6 md:p-8">
                    <h4 class="font-bold text-lg text-gray-900">What happens in the event of an accident?</h4>
                    <i class="ph-bold ph-caret-down text-xl text-gray-400 transition-transform duration-300 faq-icon"></i>
                </div>
                <div class="faq-answer">
                    <div class="px-6 md:px-8 pb-8 text-gray-500 font-medium text-lg leading-relaxed border-t border-gray-50 pt-4">All DriveAway vehicles are fully insured. In the event of an accident, immediately ensure your safety, notify local authorities, and then call our 24/7 hotline. We will dispatch a replacement vehicle and handle the insurance claims.</div>
                </div>
            </div>

            <div class="faq-item bg-white rounded-[1.5rem] overflow-hidden reveal cursor-pointer border border-gray-100 transition-all shadow-sm">
                <div class="faq-header flex justify-between items-center p-6 md:p-8">
                    <h4 class="font-bold text-lg text-gray-900">Is there a mileage limit?</h4>
                    <i class="ph-bold ph-caret-down text-xl text-gray-400 transition-transform duration-300 faq-icon"></i>
                </div>
                <div class="faq-answer">
                    <div class="px-6 md:px-8 pb-8 text-gray-500 font-medium text-lg leading-relaxed border-t border-gray-50 pt-4">Standard rentals include a generous daily mileage limit of 150km. Excess mileage is billed per kilometer. Unlimited mileage packages are available for select long-term rentals and luxury vehicles.</div>
                </div>
            </div>

        </div>
    </div>
</section>

<section class="py-32 bg-[#0D6EFD] relative overflow-hidden">
    <div class="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10"></div>
    <div class="absolute top-0 left-1/4 w-[500px] h-[500px] bg-white/20 rounded-full blur-[100px] pointer-events-none"></div>

    <div class="max-w-[1000px] mx-auto px-6 lg:px-8 text-center relative z-10 reveal">
        <div class="inline-flex justify-center mb-10 bg-white p-5 rounded-[2rem] shadow-2xl transform rotate-3 hover:rotate-0 transition-transform duration-500">
            <i class="ph-fill ph-keys text-6xl text-[#0D6EFD]"></i>
        </div>
        <h2 class="text-[3.5rem] md:text-[5rem] font-black text-white mb-8 tracking-tight leading-tight">Ready to start your engine?</h2>
        <p class="text-2xl text-blue-100 font-medium mb-12 max-w-2xl mx-auto">Create your account in under 60 seconds and unlock Sri Lanka's premium vehicle network.</p>
        <a href="register.jsp" class="transition-link inline-flex justify-center items-center gap-3 bg-[#1A1C23] text-white font-black py-6 px-16 rounded-full hover:bg-black hover:scale-105 transition-all shadow-[0_20px_50px_rgba(0,0,0,0.4)] text-2xl tracking-wide">
            Create Free Account <i class="ph-bold ph-arrow-right"></i>
        </a>
    </div>
</section>

<div class="floating-actions" aria-label="Quick actions">
    <a href="customer-support.jsp" class="fab fab-main transition-link" title="Customer support" aria-label="Open support">
        <i class="ph-fill ph-chat-centered-dots text-2xl"></i>
    </a>
    <button id="back-to-top" class="fab fab-muted" type="button" title="Back to top" aria-label="Back to top">
        <i class="ph-bold ph-arrow-up text-2xl"></i>
    </button>
</div>

<footer class="bg-[#1A1C23] py-20 relative z-20">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">

        <div class="grid grid-cols-1 md:grid-cols-12 gap-12 mb-16">

            <div class="md:col-span-4">
                <div class="flex items-center gap-3 mb-8">
                    <div class="bg-[#0D6EFD] p-3 rounded-xl"><i class="ph-fill ph-car-profile text-3xl text-white"></i></div>
                    <span class="text-4xl font-black tracking-tight text-white">DriveAway</span>
                </div>
                <p class="text-gray-400 font-medium text-lg leading-relaxed max-w-sm mb-8">Redefining vehicle rentals through digital innovation. Premium cars, transparent pricing, and instant bookings in Sri Lanka.</p>
                <div class="flex gap-4">
                    <a href="#" class="w-12 h-12 rounded-full bg-white/5 flex items-center justify-center hover:bg-[#0D6EFD] hover:text-white transition-colors text-gray-400"><i class="ph-fill ph-facebook-logo text-2xl"></i></a>
                    <a href="#" class="w-12 h-12 rounded-full bg-white/5 flex items-center justify-center hover:bg-[#0D6EFD] hover:text-white transition-colors text-gray-400"><i class="ph-fill ph-instagram-logo text-2xl"></i></a>
                    <a href="#" class="w-12 h-12 rounded-full bg-white/5 flex items-center justify-center hover:bg-[#0D6EFD] hover:text-white transition-colors text-gray-400"><i class="ph-fill ph-twitter-logo text-2xl"></i></a>
                </div>
            </div>

            <div class="md:col-span-2 md:col-start-7">
                <h4 class="text-white font-black uppercase tracking-widest mb-8 text-sm">Company</h4>
                <ul class="space-y-4 text-gray-400 font-medium text-lg">
                    <li><a href="about.jsp" class="transition-link hover:text-white transition-colors">About Us</a></li>
                    <li><a href="#fleet" class="hover:text-white transition-colors">Our Fleet</a></li>
                    <li><a href="contact.jsp" class="transition-link hover:text-white transition-colors">Contact Support</a></li>
                    <li><a href="#locations" class="hover:text-white transition-colors">Hub Locations</a></li>
                </ul>
            </div>

            <div class="md:col-span-3">
                <h4 class="text-white font-black uppercase tracking-widest mb-8 text-sm">Legal & Security</h4>
                <ul class="space-y-4 text-gray-400 font-medium text-lg">
                    <li><a href="terms.jsp" class="transition-link hover:text-white transition-colors">Terms of Service</a></li>
                    <li><a href="privacy.jsp" class="transition-link hover:text-white transition-colors">Privacy Policy</a></li>
                    <li><a href="#" class="hover:text-white transition-colors">Cookie Preferences</a></li>
                    <li class="pt-4 mt-4 border-t border-white/10">
                        <a href="admin-login.jsp" class="transition-link text-red-500 font-bold hover:text-red-400 transition-colors flex items-center gap-2">
                            <i class="ph-bold ph-shield-check text-xl"></i> Admin Portal
                        </a>
                    </li>
                </ul>
            </div>
        </div>

        <div class="pt-8 border-t border-[#2A2D35] flex flex-col md:flex-row justify-between items-center text-sm font-bold text-gray-500 uppercase tracking-widest">
            <p>&copy; 2026 DriveAway Vehicle Rentals. Built by IT25100142 & Team.</p>
            <p class="mt-4 md:mt-0 flex items-center gap-2"><i class="ph-fill ph-map-pin text-gray-400 text-lg"></i> Colombo, Sri Lanka</p>
        </div>
    </div>
</footer>

<script>
    (function () {
        let hasRun = false;

        function qs(sel) { return document.querySelector(sel); }

        function finishLoader(force) {
            const loader = qs('#page-loader');

            // Always reveal page content, even if loader markup is missing.
            document.body.classList.add('page-loaded');

            if (!loader) return;
            if (loader.dataset.closed === '1' && !force) return;
            loader.dataset.closed = '1';
            loader.classList.add('hide');

            // Hard fallback removal in case CSS transition never fires.
            window.setTimeout(function () {
                if (loader.parentNode) loader.parentNode.removeChild(loader);
            }, 1300);
        }

        function showLoaderSequence() {
            if (hasRun) return;
            hasRun = true;

            const loader = qs('#page-loader');
            const loaderText = qs('.loader-text');
            const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

            if (!loader) {
                finishLoader(true);
                return;
            }

            // Dynamic text rotation
            const textMessages = ['Igniting Engine', 'Loading Fleet', 'Ready to Drive'];
            let textIndex = 0;

            if (loaderText && !prefersReducedMotion) {
                const textInterval = setInterval(function () {
                    textIndex = (textIndex + 1) % textMessages.length;
                    loaderText.textContent = textMessages[textIndex];
                }, 400);
                
                // Clear interval when loader finishes
                window.setTimeout(function () {
                    clearInterval(textInterval);
                }, 1200);
            }

            // With the new fast loader, we can get the user into the page much quicker.
            // 800ms gives just enough time for the intro animation to register cleanly.
            window.setTimeout(function () {
                finishLoader(false);
            }, prefersReducedMotion ? 0 : 800);

            // Safety valve
            window.setTimeout(function () { finishLoader(true); }, 1500);
        }

        if (document.readyState === 'complete') {
            requestAnimationFrame(showLoaderSequence);
        } else {
            window.addEventListener('load', function onLoad() {
                window.removeEventListener('load', onLoad);
                requestAnimationFrame(showLoaderSequence);
            });

            // If load is delayed too much, still let users in.
            window.setTimeout(function () {
                if (!hasRun) showLoaderSequence();
            }, 1800);
        }
    })();
</script>
<script>
    document.addEventListener('DOMContentLoaded', () => {

        const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

        // 0. PREMIUM PARALLAX & SCROLL EFFECTS
        const parallaxBg = document.getElementById('parallax-bg');

        function updateParallax() {
            const scrollY = window.scrollY;
            const maxScroll = document.documentElement.scrollHeight - window.innerHeight;
            const scrollPercent = maxScroll > 0 ? scrollY / maxScroll : 0;

            if (parallaxBg) {
                if (prefersReducedMotion) {
                    parallaxBg.style.transform = 'translate3d(0, 0, 0)';
                    parallaxBg.style.opacity = '0.06';
                } else {
                    const parallaxDistance = Math.min(scrollY * 0.18, 140);
                    const bgOpacity = 0.12 - (scrollPercent * 0.06);
                    parallaxBg.style.transform = 'translate3d(0, ' + parallaxDistance + 'px, 0)';
                    parallaxBg.style.opacity = String(Math.max(0.04, bgOpacity));
                }
            }

            const glowOpacity = prefersReducedMotion ? 0.04 : 0.04 + (Math.sin(scrollPercent * Math.PI) * 0.08);
            document.body.style.setProperty('--ambient-glow-opacity', String(Math.max(0.03, glowOpacity)));
        }

        // 1. PAGE TRANSITION LOGIC
        document.body.classList.add('page-loaded');
        const welcomeOverlay = document.getElementById('welcome-overlay');
        
        document.querySelectorAll('.transition-link').forEach(link => {
            link.addEventListener('click', function(e) {
                if (this.href.includes('#')) return;
                if (this.origin === window.location.origin && this.pathname === window.location.pathname) return;
                e.preventDefault();
                const targetUrl = this.href;
                
                // Special animation for login navigation
                if (targetUrl.includes('login.jsp')) {
                    document.body.classList.add('page-exit');
                    welcomeOverlay.classList.add('active');
                    setTimeout(() => { window.location.href = targetUrl; }, prefersReducedMotion ? 0 : 600);
                } else {
                    document.body.classList.remove('page-loaded');
                    setTimeout(() => { window.location.href = targetUrl; }, prefersReducedMotion ? 0 : 150);
                }
            });
        });

        // 2. MOBILE MENU TOGGLE
        const menuBtn = document.getElementById('mobile-menu-btn');
        const mobileMenu = document.getElementById('mobile-menu');
        if (menuBtn && mobileMenu) {
            menuBtn.addEventListener('click', () => {
                mobileMenu.classList.toggle('hidden');
            });

            mobileMenu.querySelectorAll('a').forEach(anchor => {
                anchor.addEventListener('click', () => mobileMenu.classList.add('hidden'));
            });
        }

        // 3. SCROLL CHROME (progress bar, navbar state, back-to-top)
        const navBar = document.getElementById('navbar');
        const progressBar = document.getElementById('scroll-progress');
        const backToTopBtn = document.getElementById('back-to-top');
        const cinematicHero = document.getElementById('cinematic-hero');
        const heroScrollVideo = document.getElementById('hero-scroll-video');
        let heroScrubDuration = 22;

        if (heroScrollVideo && !prefersReducedMotion) {
            heroScrollVideo.addEventListener('loadedmetadata', () => {
                if (!Number.isNaN(heroScrollVideo.duration) && Number.isFinite(heroScrollVideo.duration)) {
                    heroScrubDuration = heroScrollVideo.duration * 1.7;
                }
                heroScrollVideo.currentTime = 0;
                heroScrollVideo.pause();
            });
        }

        function syncCinematicHero() {
            if (!cinematicHero || !heroScrollVideo || prefersReducedMotion) return;
            const rect = cinematicHero.getBoundingClientRect();
            const viewportHeight = window.innerHeight;
            const fullTravel = rect.height + viewportHeight;
            const progress = Math.min(Math.max((viewportHeight - rect.top) / fullTravel, 0), 1);

            if (heroScrollVideo.readyState >= 2) {
                const targetTime = heroScrubDuration * progress;
                if (Math.abs(heroScrollVideo.currentTime - targetTime) > 0.25) {
                    heroScrollVideo.currentTime = targetTime;
                }
            }

            const scale = 1 + (progress * 0.06);
            heroScrollVideo.style.transform = 'scale(' + scale + ')';
            heroScrollVideo.style.opacity = String(1.0 - (progress * 0.05));
        }
        let ticking = false;

        function updateScrollChrome() {
            const scrollTop = window.scrollY;
            const docHeight = document.documentElement.scrollHeight - window.innerHeight;
            const progress = docHeight > 0 ? (scrollTop / docHeight) * 100 : 0;

            if (progressBar) progressBar.style.width = progress + '%';
            if (navBar) navBar.classList.toggle('nav-scrolled', scrollTop > 24);
            if (backToTopBtn) backToTopBtn.classList.toggle('visible', scrollTop > 500);
            syncCinematicHero();
        }

        function runScrollFrame() {
            updateParallax();
            updateScrollChrome();
            ticking = false;
        }

        function scheduleScrollFrame() {
            if (!ticking) {
                requestAnimationFrame(runScrollFrame);
                ticking = true;
            }
        }

        runScrollFrame();
        window.addEventListener('scroll', scheduleScrollFrame, { passive: true });
        window.addEventListener('resize', scheduleScrollFrame);

        if (backToTopBtn) {
            backToTopBtn.addEventListener('click', () => {
                window.scrollTo({ top: 0, behavior: prefersReducedMotion ? 'auto' : 'smooth' });
            });
        }

        // 4. SCROLL REVEAL
        const revealObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('active');
                    revealObserver.unobserve(entry.target);
                }
            });
        }, { threshold: 0.1, rootMargin: "0px 0px -50px 0px" });
        document.querySelectorAll('.reveal, .reveal-left, .reveal-right').forEach(el => revealObserver.observe(el));

        // 5. NAVBAR SECTION HIGHLIGHT
        const sectionLinks = document.querySelectorAll('[data-section-link]');
        const sections = Array.from(sectionLinks)
            .map(link => document.getElementById(link.getAttribute('data-section-link')))
            .filter(Boolean);

        const sectionObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (!entry.isIntersecting) return;
                sectionLinks.forEach(link => link.classList.remove('active'));
                const activeLink = document.querySelector('[data-section-link="' + entry.target.id + '"]');
                if (activeLink) activeLink.classList.add('active');
            });
        }, { threshold: 0.35 });
        sections.forEach(section => sectionObserver.observe(section));

        // 6. STATS COUNTER ANIMATION
        const counters = document.querySelectorAll('.counter');
        let hasCounted = false;

        const counterObserver = new IntersectionObserver((entries) => {
            if(entries[0].isIntersecting && !hasCounted) {
                hasCounted = true;
                counters.forEach(counter => {
                    const target = +counter.getAttribute('data-target');
                    const duration = 2000;
                    const increment = target / (duration / 16);
                    let current = 0;

                    const updateCounter = () => {
                        current += increment;
                        if(current < target) {
                            counter.innerText = target % 1 !== 0 ? current.toFixed(1) : Math.ceil(current);
                            requestAnimationFrame(updateCounter);
                        } else {
                            counter.innerText = target;
                        }
                    };
                    updateCounter();
                });
            }
        }, { threshold: 0.5 });
        if(counters.length > 0) counterObserver.observe(counters[0].parentElement);

        // 7. FLEET FILTERING ENGINE
        const filterBtns = document.querySelectorAll('.filter-btn');
        const carItems = document.querySelectorAll('.car-item');

        filterBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                filterBtns.forEach(b => {
                    b.classList.remove('active', 'bg-[#0D6EFD]', 'text-white');
                    b.classList.add('text-gray-700', 'border-gray-200', 'bg-white');
                    b.setAttribute('aria-pressed', 'false');
                });
                btn.classList.add('active', 'bg-[#0D6EFD]', 'text-white');
                btn.classList.remove('text-gray-700', 'border-gray-200', 'bg-white');
                btn.setAttribute('aria-pressed', 'true');

                const filterValue = btn.getAttribute('data-filter');

                carItems.forEach(item => {
                    if (filterValue === 'all' || item.getAttribute('data-category') === filterValue) {
                        item.classList.remove('hidden-item');
                        setTimeout(() => { item.style.display = 'block'; }, 50);
                    } else {
                        item.classList.add('hidden-item');
                        setTimeout(() => { item.style.display = 'none'; }, 400);
                    }
                });
            });
        });

        // 8. FAQ ACCORDION LOGIC
        const faqItems = document.querySelectorAll('.faq-item');
        faqItems.forEach(item => {
            item.addEventListener('click', () => {
                const isActive = item.classList.contains('faq-open');
                faqItems.forEach(i => i.classList.remove('faq-open')); // close all
                if(!isActive) item.classList.add('faq-open'); // open clicked
            });
        });

        // 9. TESTIMONIAL CAROUSEL
        const slides = document.querySelectorAll('.testimonial-slide');
        let currentSlide = 0;
        let slideInterval;

        function showSlide(index) {
            slides.forEach(s => s.classList.remove('active'));
            slides[index].classList.add('active');
        }
        function nextSlide() {
            currentSlide = (currentSlide + 1) % slides.length;
            showSlide(currentSlide);
        }
        function prevSlide() {
            currentSlide = (currentSlide - 1 + slides.length) % slides.length;
            showSlide(currentSlide);
        }

        const nextBtn = document.getElementById('next-test');
        const prevBtn = document.getElementById('prev-test');
        if(nextBtn && prevBtn && slides.length) {
            nextBtn.addEventListener('click', () => { nextSlide(); resetInterval(); });
            prevBtn.addEventListener('click', () => { prevSlide(); resetInterval(); });

            function resetInterval() {
                clearInterval(slideInterval);
                slideInterval = setInterval(nextSlide, 6000);
            }
            resetInterval();
        }

        // 10. UNIFIED INTERACTIVE TEAM MASCOTS ENGINE
        class Mascot {
            constructor(id, weight, maxRotation) {
                this.avatarContainer = document.getElementById(id);
                if(!this.avatarContainer) return;

                this.body = this.avatarContainer.querySelector('.mascot-body');
                this.eyes = this.avatarContainer.querySelector('.mascot-eyes');

                this.weight = weight;
                this.maxRotation = maxRotation;
                this.currentX = 0; this.currentY = 0;
                this.targetX = 0;  this.targetY = 0;
            }
            lookAt(x, y) {
                this.targetX = x; this.targetY = y;
            }
            update() {
                if(!this.body || !this.eyes) return;

                this.currentX += (this.targetX - this.currentX) * this.weight;
                this.currentY += (this.targetY - this.currentY) * this.weight;

                const moveX = this.currentX * 15;
                const moveY = this.currentY * 15;
                const rotX = this.currentY * -this.maxRotation;
                const rotY = this.currentX * this.maxRotation;

                this.body.style.transform = 'translate3d(' + moveX + 'px, ' + moveY + 'px, 0) rotateX(' + rotX + 'deg) rotateY(' + rotY + 'deg)';
                this.eyes.style.transform = 'translateZ(40px) translate(' + (this.currentX * 25) + 'px, ' + (this.currentY * 25) + 'px)';
            }
        }

        const mascots = [
            new Mascot('mascot-1', 0.05, 20),
            new Mascot('mascot-2', 0.06, 15),
            new Mascot('mascot-3', 0.04, 25),
            new Mascot('mascot-4', 0.07, 30),
            new Mascot('mascot-5', 0.05, 15),
            new Mascot('mascot-6', 0.06, 20)
        ].filter(m => m.body !== null);

        document.addEventListener('mousemove', (e) => {
            if(mascots.length === 0) return;
            const mouseX = (e.clientX / window.innerWidth) * 2 - 1;
            const mouseY = (e.clientY / window.innerHeight) * 2 - 1;
            mascots.forEach(m => m.lookAt(mouseX, mouseY));
        });

        function renderMascots() {
            mascots.forEach(m => m.update());
            requestAnimationFrame(renderMascots);
        }
        if(mascots.length > 0) renderMascots();

    });
</script>
</body>
</html>