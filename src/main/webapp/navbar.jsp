<nav id="navbar" class="fixed w-full z-50 transition-all duration-300 py-5 bg-white/90 backdrop-blur-md border-b border-gray-100 shadow-sm">
    <div class="max-w-[1400px] mx-auto px-6 lg:px-8 flex justify-between items-center">
        <a href="index.jsp" class="flex items-center gap-3 group">
            <div class="bg-gray-100 p-2.5 rounded-xl group-hover:bg-[#0D6EFD] transition-colors duration-300">
                <i class="ph-fill ph-car-profile text-2xl text-[#0D6EFD] group-hover:text-white transition-colors duration-300"></i>
            </div>
            <span class="text-2xl font-extrabold tracking-tight text-gray-900">DriveAway</span>
        </a>
        <div class="hidden md:flex items-center gap-8">
            <a href="index.jsp#fleet" class="text-base font-semibold text-gray-600 hover:text-black transition-colors">Our Fleet</a>
            <a href="index.jsp#how-it-works" class="text-base font-semibold text-gray-600 hover:text-black transition-colors">How it Works</a>
            <a href="index.jsp#testimonials" class="text-base font-semibold text-gray-600 hover:text-black transition-colors">Reviews</a>
            <a href="about.jsp" class="text-base font-semibold text-gray-600 hover:text-black transition-colors">About</a>
            <a href="contact.jsp" class="text-base font-semibold text-gray-600 hover:text-black transition-colors">Contact</a>
        </div>
        <div class="hidden md:flex items-center gap-5">
            <a href="login.jsp" class="text-base font-semibold text-gray-600 hover:text-black transition-colors">Sign In</a>
            <a href="register.jsp" class="bg-[#0D6EFD] text-white font-bold py-3 px-8 rounded-2xl hover:bg-blue-700 transition-all transform hover:-translate-y-1 shadow-lg hover:shadow-[#0D6EFD]/30 tracking-wide">
                Register
            </a>
        </div>
        <button id="mobile-menu-btn" class="md:hidden text-gray-600 focus:outline-none">
            <i class="ph-bold ph-list text-2xl"></i>
        </button>
    </div>
    <div id="mobile-menu" class="md:hidden hidden bg-white/95 backdrop-blur-md absolute top-full left-0 w-full border-b border-gray-100 shadow-lg">
        <div class="flex flex-col p-6 space-y-4">
            <a href="index.jsp#fleet" class="text-base font-semibold text-gray-600 hover:text-black py-2">Our Fleet</a>
            <a href="index.jsp#how-it-works" class="text-base font-semibold text-gray-600 hover:text-black py-2">How it Works</a>
            <a href="index.jsp#testimonials" class="text-base font-semibold text-gray-600 hover:text-black py-2">Reviews</a>
            <a href="about.jsp" class="text-base font-semibold text-gray-600 hover:text-black py-2">About</a>
            <a href="contact.jsp" class="text-base font-semibold text-gray-600 hover:text-black py-2">Contact</a>
            <hr class="my-2 border-gray-200">
            <a href="login.jsp" class="text-base font-semibold text-gray-600 py-2">Sign In</a>
            <a href="register.jsp" class="bg-[#0D6EFD] text-white font-bold py-3 rounded-2xl text-center">Register</a>
        </div>
    </div>
</nav>
<script>
    document.getElementById('mobile-menu-btn')?.addEventListener('click', () => {
        document.getElementById('mobile-menu')?.classList.toggle('hidden');
    });
</script>