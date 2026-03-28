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

        /* custom gradient text utility */
        .gradient-text {
            background: linear-gradient(135deg, #0D6EFD 0%, #4A12DA 100%);
            -webkit-background-clip: text;
            background-clip: text;
            color: transparent;
        }

        /* reveal animations */
        .reveal {
            opacity: 0;
            transform: translateY(30px);
            transition: all 0.8s cubic-bezier(0.2, 0.8, 0.2, 1);
        }
        .reveal.active {
            opacity: 1;
            transform: translateY(0);
        }

        /* floating shapes */
        .brand-shape {
            position: absolute;
            transform-style: preserve-3d;
            animation: float 8s ease-in-out infinite;
            filter: blur(60px);
            opacity: 0.5;
        }
        @keyframes float {
            0%, 100% { transform: translateY(0) rotate(0deg) scale(1); }
            50% { transform: translateY(-20px) rotate(2deg) scale(1.05); }
        }

        /* custom card hover effect */
        .car-card {
            transition: all 0.3s ease;
        }
        .car-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 25px 40px -12px rgba(0,0,0,0.2);
        }

        /* testimonial carousel */
        .testimonial-item {
            transition: opacity 0.5s ease;
        }
        .testimonial-item.active {
            opacity: 1;
            display: block;
        }
        .testimonial-item:not(.active) {
            opacity: 0;
            display: none;
        }
    </style>
</head>
<body class="text-gray-900 antialiased">

<!-- Navbar -->
<nav id="navbar" class="fixed w-full z-50 transition-all duration-300 py-5 bg-white/90 backdrop-blur-md border-b border-gray-100 shadow-sm">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 flex justify-between items-center">
        <a href="index.jsp" class="flex items-center gap-3 group">
            <div class="bg-gray-100 p-2.5 rounded-xl group-hover:bg-[#0D6EFD] transition-colors duration-300">
                <i class="ph-fill ph-car-profile text-2xl text-[#0D6EFD] group-hover:text-white transition-colors duration-300"></i>
            </div>
            <span class="text-2xl font-extrabold tracking-tight text-gray-900">DriveAway</span>
        </a>
        <div class="hidden md:flex items-center gap-8">
            <a href="#fleet" class="text-base font-semibold text-gray-600 hover:text-black transition-colors">Our Fleet</a>
            <a href="#how-it-works" class="text-base font-semibold text-gray-600 hover:text-black transition-colors">How it Works</a>
            <a href="#testimonials" class="text-base font-semibold text-gray-600 hover:text-black transition-colors">Reviews</a>
        </div>
        <div class="hidden md:flex items-center gap-5">
            <a href="login.jsp" class="text-base font-semibold text-gray-600 hover:text-black transition-colors">Sign In</a>
            <a href="register.jsp" class="bg-[#0D6EFD] text-white font-bold py-3 px-8 rounded-2xl hover:bg-blue-700 transition-all transform hover:-translate-y-1 shadow-lg hover:shadow-[#0D6EFD]/30 tracking-wide">
                Register
            </a>
        </div>
        <!-- mobile menu button (simplified) -->
        <button id="mobile-menu-btn" class="md:hidden text-gray-600 focus:outline-none">
            <i class="ph-bold ph-list text-2xl"></i>
        </button>
    </div>
    <!-- mobile menu (hidden by default) -->
    <div id="mobile-menu" class="md:hidden hidden bg-white/95 backdrop-blur-md absolute top-full left-0 w-full border-b border-gray-100 shadow-lg">
        <div class="flex flex-col p-6 space-y-4">
            <a href="#fleet" class="text-base font-semibold text-gray-600 hover:text-black py-2">Our Fleet</a>
            <a href="#how-it-works" class="text-base font-semibold text-gray-600 hover:text-black py-2">How it Works</a>
            <a href="#testimonials" class="text-base font-semibold text-gray-600 hover:text-black py-2">Reviews</a>
            <hr class="my-2 border-gray-200">
            <a href="login.jsp" class="text-base font-semibold text-gray-600 py-2">Sign In</a>
            <a href="register.jsp" class="bg-[#0D6EFD] text-white font-bold py-3 rounded-2xl text-center">Register</a>
        </div>
    </div>
</nav>

<!-- Hero Section -->
<header class="pt-32 pb-20 bg-gradient-to-br from-[#F8FAFF] to-white relative overflow-hidden min-h-[85vh] flex items-center">
    <!-- background shapes (more refined) -->
    <div class="absolute inset-0 pointer-events-none overflow-hidden">
        <div class="brand-shape shape-purple w-64 h-64 -top-20 -left-20 bg-gradient-to-br from-[#7A52FA] to-[#4A12DA] rounded-full" style="animation-delay: 0s;"></div>
        <div class="brand-shape shape-yellow w-80 h-80 bottom-0 right-0 bg-gradient-to-br from-[#FFE044] to-[#D6A200] rounded-full" style="animation-delay: 1.5s;"></div>
        <div class="brand-shape shape-cyan w-72 h-72 top-1/3 right-1/4 bg-gradient-to-br from-[#00F2FE] to-[#0088FF] rounded-full" style="animation-delay: 0.7s;"></div>
    </div>

    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 relative z-10 w-full flex flex-col lg:flex-row items-center gap-12">
        <div class="w-full lg:w-1/2 text-center lg:text-left reveal">
            <div class="inline-flex items-center gap-2 bg-white/70 backdrop-blur-sm px-4 py-2 rounded-full border border-gray-200 shadow-sm mb-6">
                <div class="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
                <span class="text-sm font-bold text-gray-600 tracking-wide uppercase">25+ vehicles ready</span>
            </div>
            <h1 class="text-[3rem] md:text-[4rem] font-extrabold mb-6 tracking-tight leading-[1.1]">
                Your journey,<br>
                <span class="gradient-text">perfectly driven.</span>
            </h1>
            <p class="text-xl text-gray-500 font-medium mb-10 max-w-lg mx-auto lg:mx-0 leading-relaxed">
                Skip the paperwork. Browse verified vehicles, book your dates, and pick up the keys. Simple, fast, and secure.
            </p>

            <div class="bg-white p-8 rounded-3xl shadow-xl border border-gray-100 max-w-xl mx-auto lg:mx-0 reveal">
                <form action="login.jsp" method="GET" class="space-y-5">
                    <div class="grid grid-cols-2 gap-5">
                        <div>
                            <label class="block text-sm font-bold text-gray-700 mb-1 uppercase tracking-wide">Pick-up</label>
                            <input type="date" required class="w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-lg font-semibold bg-transparent text-gray-800">
                        </div>
                        <div>
                            <label class="block text-sm font-bold text-gray-700 mb-1 uppercase tracking-wide">Drop-off</label>
                            <input type="date" required class="w-full border-b-2 border-gray-300 py-3 focus:border-[#0D6EFD] outline-none transition-colors text-lg font-semibold bg-transparent text-gray-800">
                        </div>
                    </div>
                    <button type="submit" class="w-full bg-[#1A1C23] text-white font-bold py-4 rounded-2xl hover:bg-black transition-all transform hover:-translate-y-1 shadow-lg mt-2 text-lg tracking-wide flex justify-center items-center gap-2">
                        <i class="ph-bold ph-magnifying-glass text-xl"></i> Find Vehicles
                    </button>
                </form>
            </div>
        </div>

        <div class="w-full lg:w-1/2 flex justify-center lg:justify-end reveal">
            <div class="relative w-full max-w-md lg:max-w-full">
                <img src="https://images.unsplash.com/photo-1605559424843-9e4c228bf1c2?q=80&w=1000&auto=format&fit=crop" alt="Luxury Car" class="rounded-3xl shadow-2xl object-cover h-[380px] w-full border-4 border-white transform lg:rotate-2 transition-transform hover:rotate-0 duration-500">
                <div class="absolute -bottom-6 -right-6 bg-white p-4 rounded-2xl shadow-lg hidden lg:flex items-center gap-2">
                    <i class="ph-fill ph-star text-yellow-400 text-2xl"></i>
                    <div>
                        <div class="font-extrabold text-gray-900">4.98</div>
                        <div class="text-xs text-gray-500">(1.2k reviews)</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>

<!-- How it works -->
<section id="how-it-works" class="py-24 bg-white border-b border-gray-100">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">
        <div class="text-center mb-16 reveal">
            <h2 class="text-[2.5rem] font-extrabold mb-3 tracking-tight leading-tight">How it works</h2>
            <p class="text-gray-500 font-medium text-lg">Three steps to get behind the wheel.</p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-12">
            <div class="text-center reveal group">
                <div class="flex justify-center mb-6 relative">
                    <div class="bg-gray-100 p-6 rounded-3xl inline-block shadow-sm group-hover:shadow-md transition-all">
                        <i class="ph-fill ph-cursor-click text-5xl text-[#0D6EFD]"></i>
                    </div>
                    <div class="absolute -top-3 -right-3 bg-[#0D6EFD] text-white w-8 h-8 rounded-full flex items-center justify-center font-bold">1</div>
                </div>
                <h3 class="text-2xl font-bold mb-3">Select</h3>
                <p class="text-gray-500 font-medium text-lg px-4">Browse our premium fleet and find the perfect car for your journey.</p>
            </div>
            <div class="text-center reveal group" style="transition-delay: 0.1s;">
                <div class="flex justify-center mb-6 relative">
                    <div class="bg-gray-100 p-6 rounded-3xl inline-block shadow-sm group-hover:shadow-md transition-all">
                        <i class="ph-fill ph-calendar-check text-5xl text-black"></i>
                    </div>
                    <div class="absolute -top-3 -right-3 bg-[#0D6EFD] text-white w-8 h-8 rounded-full flex items-center justify-center font-bold">2</div>
                </div>
                <h3 class="text-2xl font-bold mb-3">Book</h3>
                <p class="text-gray-500 font-medium text-lg px-4">Choose your dates. Our system instantly checks availability and pricing.</p>
            </div>
            <div class="text-center reveal group" style="transition-delay: 0.2s;">
                <div class="flex justify-center mb-6 relative">
                    <div class="bg-gray-100 p-6 rounded-3xl inline-block shadow-sm group-hover:shadow-md transition-all">
                        <i class="ph-fill ph-car text-5xl text-[#0D6EFD]"></i>
                    </div>
                    <div class="absolute -top-3 -right-3 bg-[#0D6EFD] text-white w-8 h-8 rounded-full flex items-center justify-center font-bold">3</div>
                </div>
                <h3 class="text-2xl font-bold mb-3">Drive</h3>
                <p class="text-gray-500 font-medium text-lg px-4">Pick up the keys from our lot. Your vehicle is cleaned, fueled, and ready.</p>
            </div>
        </div>
    </div>
</section>

<!-- Featured Fleet -->
<section id="fleet" class="py-24 bg-[#F8FAFF]">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">
        <div class="flex flex-col md:flex-row justify-between items-end mb-16 reveal">
            <div>
                <h2 class="text-[2.5rem] font-extrabold mb-3 tracking-tight leading-tight">Featured Fleet</h2>
                <p class="text-gray-500 font-medium text-lg">A sneak peek at our top-rated rentals.</p>
            </div>
            <a href="login.jsp" class="hidden md:flex items-center gap-2 text-base font-bold text-[#0D6EFD] hover:text-blue-800 transition-colors">
                View All Vehicles <i class="ph-bold ph-arrow-right"></i>
            </a>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            <%
                String[][] featuredCars = {
                        {"Honda Civic 2022", "Sedan", "Rs. 9,500", "Auto", "5", "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?q=80&w=800&auto=format&fit=crop", "Petrol", "4.9"},
                        {"Toyota Axio 2020", "Hybrid", "Rs. 8,000", "Auto", "5", "https://images.unsplash.com/photo-1590362891991-f776e747a588?q=80&w=800&auto=format&fit=crop", "Hybrid", "4.8"},
                        {"Mitsubishi Every", "Van", "Rs. 6,500", "Manual", "7", "https://images.unsplash.com/photo-1520031441872-265e4ff70366?q=80&w=800&auto=format&fit=crop", "Diesel", "4.7"}
                };
                for(int i = 0; i < featuredCars.length; i++) {
            %>
            <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden group car-card transition-all duration-300 hover:shadow-2xl">
                <div class="h-64 overflow-hidden relative bg-gray-100">
                    <img src="<%= featuredCars[i][5] %>" alt="Car" class="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-700">
                    <div class="absolute top-4 left-4 bg-white/90 backdrop-blur-sm px-3 py-1 rounded-xl text-xs font-bold text-gray-900 shadow-sm"><%= featuredCars[i][1] %></div>
                    <div class="absolute top-4 right-4 bg-[#0D6EFD]/90 text-white px-3 py-1 rounded-xl text-xs font-bold shadow-sm"><i class="ph-fill ph-star text-yellow-300"></i> <%= featuredCars[i][7] %></div>
                </div>
                <div class="p-6">
                    <div class="flex justify-between items-start mb-4">
                        <h3 class="text-2xl font-extrabold tracking-tight"><%= featuredCars[i][0] %></h3>
                        <div class="text-right">
                            <span class="block text-xl font-black text-[#0D6EFD]"><%= featuredCars[i][2] %></span>
                            <span class="text-xs font-bold text-gray-400 uppercase">/ day</span>
                        </div>
                    </div>
                    <div class="flex flex-wrap gap-4 mb-5">
                        <div class="flex items-center gap-1.5 text-gray-600 font-medium"><i class="ph-fill ph-steering-wheel text-[#0D6EFD] text-lg"></i> <%= featuredCars[i][3] %></div>
                        <div class="flex items-center gap-1.5 text-gray-600 font-medium"><i class="ph-fill ph-users text-[#0D6EFD] text-lg"></i> <%= featuredCars[i][4] %> seats</div>
                        <div class="flex items-center gap-1.5 text-gray-600 font-medium"><i class="ph-fill ph-gas-pump text-[#0D6EFD] text-lg"></i> <%= featuredCars[i][6] %></div>
                    </div>
                    <a href="login.jsp" class="block w-full bg-[#1A1C23] text-white font-semibold py-3.5 rounded-2xl hover:bg-black transition-colors shadow-md text-center text-lg tracking-wide">Book Now</a>
                </div>
            </div>
            <% } %>
        </div>
        <div class="mt-12 text-center md:hidden reveal">
            <a href="login.jsp" class="inline-flex items-center gap-2 text-base font-bold text-[#0D6EFD] hover:text-blue-800 transition-colors">View All Vehicles <i class="ph-bold ph-arrow-right"></i></a>
        </div>
    </div>
</section>

<!-- Customer Favourites (dynamic demo) -->
<section class="py-24 bg-white border-b border-gray-100">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">
        <div class="text-center mb-16 reveal">
            <h2 class="text-[2.5rem] font-extrabold mb-3">Customer Favourites</h2>
            <p class="text-gray-500 text-lg">Our most loved vehicles this month</p>
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            <!-- static favorites for demo (can be dynamic) -->
            <div class="bg-gray-50 rounded-2xl p-6 text-center hover:shadow-lg transition-all cursor-pointer group">
                <i class="ph-fill ph-car-simple text-5xl text-[#0D6EFD] mb-3 group-hover:scale-110 transition-transform"></i>
                <h3 class="font-bold text-xl">Suzuki Wagon R</h3>
                <p class="text-gray-500 text-sm">Auto · 5 seats</p>
                <div class="mt-3 text-[#0D6EFD] font-bold">Rs. 5,500/day</div>
                <button class="mt-4 text-sm bg-white border border-gray-200 rounded-full px-5 py-2 hover:bg-[#0D6EFD] hover:text-white transition-colors">View</button>
            </div>
            <div class="bg-gray-50 rounded-2xl p-6 text-center hover:shadow-lg transition-all cursor-pointer group">
                <i class="ph-fill ph-car-profile text-5xl text-[#0D6EFD] mb-3 group-hover:scale-110 transition-transform"></i>
                <h3 class="font-bold text-xl">Nissan Leaf</h3>
                <p class="text-gray-500 text-sm">Auto · Electric</p>
                <div class="mt-3 text-[#0D6EFD] font-bold">Rs. 9,000/day</div>
                <button class="mt-4 text-sm bg-white border border-gray-200 rounded-full px-5 py-2 hover:bg-[#0D6EFD] hover:text-white transition-colors">View</button>
            </div>
            <div class="bg-gray-50 rounded-2xl p-6 text-center hover:shadow-lg transition-all cursor-pointer group">
                <i class="ph-fill ph-suv text-5xl text-[#0D6EFD] mb-3 group-hover:scale-110 transition-transform"></i>
                <h3 class="font-bold text-xl">Toyota Land Cruiser</h3>
                <p class="text-gray-500 text-sm">Auto · 7 seats</p>
                <div class="mt-3 text-[#0D6EFD] font-bold">Rs. 15,000/day</div>
                <button class="mt-4 text-sm bg-white border border-gray-200 rounded-full px-5 py-2 hover:bg-[#0D6EFD] hover:text-white transition-colors">View</button>
            </div>
            <div class="bg-gray-50 rounded-2xl p-6 text-center hover:shadow-lg transition-all cursor-pointer group">
                <i class="ph-fill ph-van text-5xl text-[#0D6EFD] mb-3 group-hover:scale-110 transition-transform"></i>
                <h3 class="font-bold text-xl">Kia Carnival</h3>
                <p class="text-gray-500 text-sm">Auto · 8 seats</p>
                <div class="mt-3 text-[#0D6EFD] font-bold">Rs. 12,000/day</div>
                <button class="mt-4 text-sm bg-white border border-gray-200 rounded-full px-5 py-2 hover:bg-[#0D6EFD] hover:text-white transition-colors">View</button>
            </div>
        </div>
    </div>
</section>

<!-- Testimonials Carousel -->
<section id="testimonials" class="py-24 bg-[#F8FAFF] border-b border-gray-100">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">
        <div class="text-center mb-16 reveal">
            <h2 class="text-[2.5rem] font-extrabold mb-3">What our drivers say</h2>
            <p class="text-gray-500 text-lg">Trusted by hundreds of happy customers</p>
        </div>
        <div class="relative max-w-3xl mx-auto">
            <div class="overflow-hidden">
                <div class="testimonial-item active" data-index="0">
                    <div class="bg-white p-8 rounded-3xl shadow-md text-center">
                        <i class="ph-fill ph-star text-yellow-400 text-3xl"></i>
                        <p class="text-xl font-medium text-gray-700 my-6">"Absolutely seamless experience. The car was spotless and the pickup was a breeze. Will definitely use DriveAway again!"</p>
                        <div class="font-bold text-gray-900">– Saman Perera</div>
                        <div class="text-gray-500 text-sm">Colombo</div>
                    </div>
                </div>
                <div class="testimonial-item" data-index="1">
                    <div class="bg-white p-8 rounded-3xl shadow-md text-center">
                        <i class="ph-fill ph-star text-yellow-400 text-3xl"></i>
                        <p class="text-xl font-medium text-gray-700 my-6">"Great selection of cars and very competitive pricing. The online booking system is super easy to use. Highly recommended."</p>
                        <div class="font-bold text-gray-900">– Nimali Jayawardena</div>
                        <div class="text-gray-500 text-sm">Kandy</div>
                    </div>
                </div>
                <div class="testimonial-item" data-index="2">
                    <div class="bg-white p-8 rounded-3xl shadow-md text-center">
                        <i class="ph-fill ph-star text-yellow-400 text-3xl"></i>
                        <p class="text-xl font-medium text-gray-700 my-6">"Outstanding customer service. They even helped me with an early drop-off. The vehicle was in perfect condition."</p>
                        <div class="font-bold text-gray-900">– Ravi Fernando</div>
                        <div class="text-gray-500 text-sm">Galle</div>
                    </div>
                </div>
            </div>
            <div class="flex justify-center gap-3 mt-8">
                <button id="prev-testimonial" class="w-10 h-10 rounded-full bg-white shadow-md flex items-center justify-center hover:bg-gray-100 transition-colors"><i class="ph-bold ph-arrow-left text-gray-700"></i></button>
                <button id="next-testimonial" class="w-10 h-10 rounded-full bg-white shadow-md flex items-center justify-center hover:bg-gray-100 transition-colors"><i class="ph-bold ph-arrow-right text-gray-700"></i></button>
            </div>
        </div>
    </div>
</section>

<!-- Final CTA -->
<section class="py-24 bg-gradient-to-r from-[#0D6EFD] to-[#4A12DA] relative overflow-hidden">
    <div class="absolute inset-0 bg-black/20"></div>
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 text-center relative z-10 reveal">
        <div class="inline-flex justify-center mb-6 bg-white/20 backdrop-blur-sm p-3 rounded-2xl">
            <i class="ph-fill ph-keys text-5xl text-white"></i>
        </div>
        <h2 class="text-3xl md:text-4xl font-extrabold text-white mb-4 tracking-tight">Ready to drive?</h2>
        <p class="text-xl text-white/90 font-medium mb-10 max-w-2xl mx-auto">Create your account in under a minute and get access to Sri Lanka's best vehicle rental network.</p>
        <a href="register.jsp" class="inline-flex justify-center items-center gap-3 bg-white text-[#0D6EFD] font-bold py-5 px-12 rounded-2xl hover:bg-gray-100 transition-all transform hover:-translate-y-1 shadow-xl text-xl tracking-wide">
            Create Account <i class="ph-bold ph-arrow-right"></i>
        </a>
    </div>
</section>

<!-- Footer -->
<footer class="bg-[#1A1C23] py-12 border-t border-[#2A2D35]">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8">
        <div class="flex flex-col md:flex-row justify-between items-center gap-6">
            <div class="flex items-center gap-3">
                <div class="bg-[#0D6EFD] p-2 rounded-lg"><i class="ph-fill ph-car-profile text-xl text-white"></i></div>
                <span class="text-xl font-extrabold tracking-tight text-white">DriveAway</span>
            </div>
            <div class="flex gap-8 text-gray-400 text-sm">
                <a href="#" class="hover:text-white transition">About</a>
                <a href="#" class="hover:text-white transition">Contact</a>
                <a href="#" class="hover:text-white transition">Terms</a>
                <a href="#" class="hover:text-white transition">Privacy</a>
            </div>
            <div class="text-gray-400 font-medium text-sm">&copy; 2026 DriveAway Rentals. All rights reserved.</div>
        </div>
    </div>
</footer>

<script>
    // Reveal animations
    document.addEventListener('DOMContentLoaded', () => {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('active');
                }
            });
        }, { threshold: 0.1, rootMargin: "0px 0px -50px 0px" });
        document.querySelectorAll('.reveal').forEach(el => observer.observe(el));

        // Mobile menu toggle
        const menuBtn = document.getElementById('mobile-menu-btn');
        const mobileMenu = document.getElementById('mobile-menu');
        if (menuBtn && mobileMenu) {
            menuBtn.addEventListener('click', () => {
                mobileMenu.classList.toggle('hidden');
            });
        }

        // Testimonial carousel
        const items = document.querySelectorAll('.testimonial-item');
        let current = 0;
        const nextBtn = document.getElementById('next-testimonial');
        const prevBtn = document.getElementById('prev-testimonial');
        function showTestimonial(index) {
            items.forEach((item, i) => {
                if (i === index) item.classList.add('active');
                else item.classList.remove('active');
            });
        }
        if (nextBtn && prevBtn && items.length) {
            nextBtn.addEventListener('click', () => {
                current = (current + 1) % items.length;
                showTestimonial(current);
            });
            prevBtn.addEventListener('click', () => {
                current = (current - 1 + items.length) % items.length;
                showTestimonial(current);
            });
        }
    });
</script>
</body>
</html>