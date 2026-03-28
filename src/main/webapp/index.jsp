<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DriveAway | Premium Vehicle Rentals</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap');
        body { font-family: 'Inter', sans-serif; overflow-x: hidden; background-color: #ffffff; }

        .reveal { opacity: 0; transform: translateY(30px); transition: all 0.6s cubic-bezier(0.2, 0.8, 0.2, 1); }
        .reveal.active { opacity: 1; transform: translateY(0); }

        .brand-shape { position: absolute; transform-style: preserve-3d; animation: float 6s ease-in-out infinite; }

        .shape-purple {
            background: linear-gradient(135deg, #7A52FA, #4A12DA);
            box-shadow: inset 4px 4px 15px rgba(255,255,255,0.3), inset -4px -4px 15px rgba(0,0,0,0.3), 10px 20px 30px rgba(90, 50, 250, 0.3);
            border-radius: 4rem 4rem 1rem 1rem;
        }
        .shape-yellow {
            background: linear-gradient(135deg, #FFE044, #D6A200);
            box-shadow: inset 4px 4px 15px rgba(255,255,255,0.6), inset -4px -4px 15px rgba(0,0,0,0.2), 10px 20px 30px rgba(251, 207, 20, 0.3);
            border-radius: 3rem 3rem 1rem 3rem;
        }
        .shape-cyan {
            background: linear-gradient(135deg, #00F2FE, #0088FF);
            box-shadow: inset 4px 4px 15px rgba(255,255,255,0.4), inset -4px -4px 15px rgba(0,0,0,0.3), 10px 20px 30px rgba(0, 136, 255, 0.3);
            border-radius: 2rem 2rem 1rem 1rem;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0) rotate(0deg); }
            50% { transform: translateY(-20px) rotate(2deg); }
        }

        input[type="date"]::-webkit-calendar-picker-indicator { cursor: pointer; opacity: 0.6; transition: 0.2s; }
        input[type="date"]::-webkit-calendar-picker-indicator:hover { opacity: 1; }
    </style>
</head>
<body class="text-gray-900 antialiased">

<nav id="navbar" class="fixed w-full z-50 transition-all duration-300 py-6 bg-white/90 backdrop-blur-md border-b border-gray-100">
    <div class="max-w-[1400px] mx-auto px-8 flex justify-between items-center">
        <a href="index.jsp" class="flex items-center gap-3 group">
            <div class="bg-gray-100 p-2.5 rounded-xl group-hover:bg-[#0D6EFD] transition-colors duration-300">
                <i class="ph-fill ph-car-profile text-2xl text-[#0D6EFD] group-hover:text-white transition-colors duration-300"></i>
            </div>
            <span class="text-2xl font-extrabold tracking-tight text-gray-900">DriveAway</span>
        </a>
        <div class="hidden md:flex items-center gap-10">
            <a href="#fleet" class="text-base font-bold text-gray-500 hover:text-black transition-colors">Our Fleet</a>
            <a href="#how-it-works" class="text-base font-bold text-gray-500 hover:text-black transition-colors">How it Works</a>
        </div>
        <div class="hidden md:flex items-center gap-6">
            <a href="login.jsp" class="text-base font-bold text-gray-500 hover:text-black transition-colors">Sign In</a>
            <a href="register.jsp" class="bg-[#0D6EFD] text-white font-bold py-3 px-8 rounded-2xl hover:bg-blue-700 transition-transform transform hover:-translate-y-1 shadow-lg hover:shadow-[#0D6EFD]/30 tracking-wide">
                Register
            </a>
        </div>
    </div>
</nav>

<header class="pt-32 pb-20 bg-[#F0F2F5] relative overflow-hidden min-h-[85vh] flex items-center border-b border-gray-200">
    <div class="absolute right-[-5%] top-[20%] w-[600px] h-[600px] opacity-80 pointer-events-none hidden lg:block">
        <div class="brand-shape shape-purple w-48 h-64 top-10 left-32 transform -rotate-12" style="animation-delay: 0s;"></div>
        <div class="brand-shape shape-yellow w-40 h-40 bottom-32 left-10 transform rotate-6" style="animation-delay: 1.5s;"></div>
        <div class="brand-shape shape-cyan w-32 h-80 top-0 right-32 transform rotate-12" style="animation-delay: 0.7s;"></div>
    </div>

    <div class="max-w-[1400px] mx-auto px-8 relative z-10 w-full flex flex-col lg:flex-row items-center gap-16">
        <div class="w-full lg:w-1/2">
            <div class="inline-flex items-center gap-2 bg-white px-4 py-2 rounded-full border border-gray-200 shadow-sm mb-8">
                <div class="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
                <span class="text-sm font-bold text-gray-600 tracking-wide uppercase">Vehicles Available Now</span>
            </div>
            <h1 class="text-[3.5rem] md:text-[4.5rem] font-extrabold mb-6 tracking-tight leading-[1.1] text-gray-900">
                Your journey,<br>
                <span class="text-[#0D6EFD]">perfectly driven.</span>
            </h1>
            <p class="text-xl text-gray-500 font-medium mb-12 max-w-lg leading-relaxed">
                Skip the paperwork. Browse verified vehicles, book your dates, and pick up the keys. It really is that simple.
            </p>

            <div class="bg-white p-8 rounded-3xl shadow-[0_20px_50px_rgba(0,0,0,0.05)] border border-gray-100 reveal max-w-xl">
                <form action="login.jsp" method="GET" class="space-y-6">
                    <div class="grid grid-cols-2 gap-6">
                        <div>
                            <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Pick-up</label>
                            <input type="date" required class="w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-lg font-bold bg-transparent text-gray-800">
                        </div>
                        <div>
                            <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Drop-off</label>
                            <input type="date" required class="w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-lg font-bold bg-transparent text-gray-800">
                        </div>
                    </div>
                    <button type="submit" class="w-full bg-[#1A1C23] text-white font-bold py-5 rounded-2xl hover:bg-black transition-transform transform hover:-translate-y-1 shadow-lg mt-4 text-xl tracking-wide flex justify-center items-center gap-3">
                        <i class="ph-bold ph-magnifying-glass text-2xl"></i> Find Vehicles
                    </button>
                </form>
            </div>
        </div>

        <div class="w-full lg:w-1/2 flex justify-center lg:justify-end reveal">
            <img src="https://images.unsplash.com/photo-1605559424843-9e4c228bf1c2?q=80&w=1000&auto=format&fit=crop" alt="Luxury Car" class="rounded-3xl shadow-2xl object-cover h-[400px] w-[80%] lg:w-full border-4 border-white transform lg:rotate-3 transition-transform hover:rotate-0 duration-500">
        </div>
    </div>
</header>

<section id="how-it-works" class="py-24 bg-white border-b border-gray-100">
    <div class="max-w-[1400px] mx-auto px-8">
        <div class="text-center mb-20 reveal">
            <h2 class="text-[2.5rem] font-extrabold mb-3 tracking-tight leading-tight text-gray-900">How it works</h2>
            <p class="text-gray-500 font-medium text-lg">Three steps to get behind the wheel.</p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-12">
            <div class="text-center reveal">
                <div class="flex justify-center mb-6"><div class="bg-gray-100 p-6 rounded-3xl inline-block shadow-sm"><i class="ph-fill ph-cursor-click text-5xl text-[#0D6EFD]"></i></div></div>
                <h3 class="text-2xl font-bold mb-3 text-gray-900">1. Select</h3>
                <p class="text-gray-500 font-medium text-lg px-4">Browse our premium fleet and find the perfect car for your journey.</p>
            </div>
            <div class="text-center reveal" style="transition-delay: 0.1s;">
                <div class="flex justify-center mb-6"><div class="bg-gray-100 p-6 rounded-3xl inline-block shadow-sm"><i class="ph-fill ph-calendar-check text-5xl text-black"></i></div></div>
                <h3 class="text-2xl font-bold mb-3 text-gray-900">2. Book</h3>
                <p class="text-gray-500 font-medium text-lg px-4">Choose your dates. Our system instantly checks availability and pricing.</p>
            </div>
            <div class="text-center reveal" style="transition-delay: 0.2s;">
                <div class="flex justify-center mb-6"><div class="bg-gray-100 p-6 rounded-3xl inline-block shadow-sm"><i class="ph-fill ph-car text-5xl text-[#0D6EFD]"></i></div></div>
                <h3 class="text-2xl font-bold mb-3 text-gray-900">3. Drive</h3>
                <p class="text-gray-500 font-medium text-lg px-4">Pick up the keys from our lot. Your vehicle is cleaned, fueled, and ready.</p>
            </div>
        </div>
    </div>
</section>

<section id="fleet" class="py-24 bg-[#F0F2F5]">
    <div class="max-w-[1400px] mx-auto px-8">
        <div class="flex flex-col md:flex-row justify-between items-end mb-16 reveal">
            <div>
                <h2 class="text-[2.5rem] font-extrabold mb-3 tracking-tight leading-tight text-gray-900">Featured Fleet</h2>
                <p class="text-gray-500 font-medium text-lg">A sneak peek at our top-rated rentals.</p>
            </div>
            <a href="login.jsp" class="hidden md:flex items-center gap-2 text-base font-bold text-[#0D6EFD] hover:text-blue-800 transition-colors">
                View All Vehicles <i class="ph-bold ph-arrow-right"></i>
            </a>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
            <%
                String[][] featuredCars = {
                        {"Honda Civic 2022", "Sedan", "Rs. 9,500", "Auto", "5", "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?q=80&w=800&auto=format&fit=crop"},
                        {"Toyota Axio 2020", "Hybrid", "Rs. 8,000", "Auto", "5", "https://images.unsplash.com/photo-1590362891991-f776e747a588?q=80&w=800&auto=format&fit=crop"},
                        {"Mitsubishi Every", "Van", "Rs. 6,500", "Manual", "7", "https://images.unsplash.com/photo-1520031441872-265e4ff70366?q=80&w=800&auto=format&fit=crop"}
                };
                for(int i = 0; i < featuredCars.length; i++) {
            %>
            <div class="bg-white rounded-3xl border border-gray-100 shadow-[0_20px_50px_rgba(0,0,0,0.05)] overflow-hidden group reveal transition-transform duration-300 hover:-translate-y-2">
                <div class="h-64 overflow-hidden relative bg-gray-100">
                    <img src="<%= featuredCars[i][5] %>" alt="Car" class="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-700">
                    <div class="absolute top-4 right-4 bg-white/90 backdrop-blur-sm px-4 py-2 rounded-xl text-sm font-bold text-gray-900 shadow-sm"><%= featuredCars[i][1] %></div>
                </div>
                <div class="p-8">
                    <div class="flex justify-between items-start mb-6">
                        <h3 class="text-2xl font-extrabold text-gray-900 tracking-tight"><%= featuredCars[i][0] %></h3>
                        <div class="text-right">
                            <span class="block text-xl font-black text-[#0D6EFD]"><%= featuredCars[i][2] %></span>
                            <span class="text-xs font-bold text-gray-400 uppercase tracking-wider">Per Day</span>
                        </div>
                    </div>
                    <div class="flex gap-6 mb-8">
                        <div class="flex items-center gap-2 text-gray-600 font-bold"><i class="ph-fill ph-steering-wheel text-[#0D6EFD] text-xl"></i> <%= featuredCars[i][3] %></div>
                        <div class="flex items-center gap-2 text-gray-600 font-bold"><i class="ph-fill ph-users text-[#0D6EFD] text-xl"></i> <%= featuredCars[i][4] %> Seats</div>
                    </div>
                    <a href="login.jsp" class="block w-full bg-[#1A1C23] text-white font-bold py-4 rounded-2xl hover:bg-black transition-colors shadow-md text-center text-lg tracking-wide">Reserve Now</a>
                </div>
            </div>
            <% } %>
        </div>
        <div class="mt-12 text-center md:hidden reveal">
            <a href="login.jsp" class="inline-flex items-center gap-2 text-base font-bold text-[#0D6EFD] hover:text-blue-800 transition-colors">View All Vehicles <i class="ph-bold ph-arrow-right"></i></a>
        </div>
    </div>
</section>

<section class="py-24 bg-white border-t border-gray-100">
    <div class="max-w-[1400px] mx-auto px-8 text-center reveal">
        <div class="flex justify-center mb-6"><div class="bg-gray-100 p-4 rounded-2xl inline-block"><i class="ph-fill ph-keys text-5xl text-[#0D6EFD]"></i></div></div>
        <h2 class="text-[2.5rem] font-extrabold mb-4 tracking-tight text-gray-900">Ready to drive?</h2>
        <p class="text-xl text-gray-500 font-medium mb-10 max-w-2xl mx-auto">Create your account in under a minute and get access to Sri Lanka's best vehicle rental network.</p>
        <a href="register.jsp" class="inline-flex justify-center items-center gap-3 bg-[#0D6EFD] text-white font-bold py-5 px-12 rounded-2xl hover:bg-blue-700 transition-transform transform hover:-translate-y-1 shadow-lg hover:shadow-[#0D6EFD]/30 text-xl tracking-wide">
            Create Account <i class="ph-bold ph-arrow-right"></i>
        </a>
    </div>
</section>

<footer class="bg-[#1A1C23] py-12 border-t border-[#2A2D35]">
    <div class="max-w-[1400px] mx-auto px-8 flex flex-col md:flex-row justify-between items-center gap-6">
        <div class="flex items-center gap-3">
            <div class="bg-[#0D6EFD] p-2 rounded-lg"><i class="ph-fill ph-car-profile text-xl text-white"></i></div>
            <span class="text-xl font-extrabold tracking-tight text-white">DriveAway</span>
        </div>
        <div class="text-gray-400 font-medium text-sm">&copy; 2026 DriveAway Rentals. All rights reserved.</div>
    </div>
</footer>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => { if (entry.isIntersecting) { entry.target.classList.add('active'); } });
        }, { threshold: 0.1, rootMargin: "0px 0px -50px 0px" });
        document.querySelectorAll('.reveal').forEach((el) => observer.observe(el));
    });
</script>
</body>
</html>