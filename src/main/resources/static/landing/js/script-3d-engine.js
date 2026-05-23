/**
 * KINETIC CURSOR TRACKING & WEBGL FLUID MATRIX
 * Advanced 3D interactions inspired by mont-fort.com
 */

document.addEventListener('DOMContentLoaded', () => {
    /* ========================================================================
     * KINETIC CURSOR TRACKING (CSS 3D Transformation Drivers)
     * ======================================================================== */
    const hoverTargets = document.querySelectorAll('[data-3d-hover="true"]');
    const kineticTargets = document.querySelectorAll('[data-da-kinetic="true"]');
    const MAX_TILT_ANGLE = 12; // Degrees

    // Existing hover system (glare mappings)
    hoverTargets.forEach((target) => {
        target.addEventListener('mousemove', (e) => {
            const rect = target.getBoundingClientRect();
            const x = (e.clientX - rect.left) / rect.width - 0.5;
            const y = (e.clientY - rect.top) / rect.height - 0.5;

            const rotateX = -y * MAX_TILT_ANGLE * 2; 
            const rotateY = x * MAX_TILT_ANGLE * 2;

            target.style.transform = `rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale3d(1.02, 1.02, 1.02)`;
            target.style.setProperty('--mouse-x', (x + 0.5).toFixed(2));
            target.style.setProperty('--mouse-y', (y + 0.5).toFixed(2));
        });

        target.addEventListener('mouseleave', () => {
            target.style.transform = `rotateX(0deg) rotateY(0deg) translateZ(0px) scale3d(1, 1, 1)`;
            target.style.setProperty('--mouse-x', 0.5);
            target.style.setProperty('--mouse-y', 0.5);
        });
    });

    // New Kinetic System (Smooth easing based on page coordinates)
    let mouseX = 0, mouseY = 0;
    let currentX = 0, currentY = 0;

    window.addEventListener('mousemove', (e) => {
        mouseX = (e.clientX / window.innerWidth) - 0.5;
        mouseY = (e.clientY / window.innerHeight) - 0.5;
    });

    function renderKinetic() {
        // Easing function
        currentX += (mouseX - currentX) * 0.1;
        currentY += (mouseY - currentY) * 0.1;

        kineticTargets.forEach((target) => {
            const depth = parseFloat(target.getAttribute('data-da-parallax-depth') || "20");
            const rotateX = currentY * 10;
            const rotateY = -currentX * 10;
            const translateX = currentX * depth;
            const translateY = currentY * depth;

            // Apply matrix
            target.style.transform = `translate3d(${translateX}px, ${translateY}px, ${depth}px) rotateX(${rotateX}deg) rotateY(${rotateY}deg)`;
        });

        requestAnimationFrame(renderKinetic);
    }
    renderKinetic();

    /* ========================================================================
     * SCROLL-DRIVEN ATMOSPHERE
     * ======================================================================== */
    const body = document.body;
    window.addEventListener('scroll', () => {
        const scrollY = window.scrollY;
        const maxScroll = document.body.scrollHeight - window.innerHeight;
        const scrollPercent = Math.min(scrollY / maxScroll, 1);
        
        // Darken the radial gradient deeper as we scroll
        const alphaViolet = Math.max(0.12 - (scrollPercent * 0.1), 0);
        const alphaIndigo = Math.max(0.08 - (scrollPercent * 0.08), 0);
        
        body.style.backgroundImage = `radial-gradient(circle at 100% 0%, rgba(58, 58, 255, ${alphaViolet}) 0%, rgba(76, 29, 149, ${alphaIndigo}) 40%, #050508 80%)`;
    }, { passive: true });

});
