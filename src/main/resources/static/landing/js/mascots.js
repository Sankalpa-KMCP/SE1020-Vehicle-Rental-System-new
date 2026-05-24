/* ----------------------------------------------------------------------------
 * mascots.js — SVG mascot animator
 *
 *  Responsibilities:
 *    1. Stroke-draw reveal on first IntersectionObserver hit
 *    2. Gentle parallax/float effect for vehicle mascots
 *
 *  All effects are gated on prefers-reduced-motion.
 * -------------------------------------------------------------------------- */
(function () {
    'use strict';

    var prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    var mascots = Array.prototype.slice.call(document.querySelectorAll('.da-mascot'));
    if (!mascots.length) return;

    // ─── 1. Stroke-draw reveal ────────────────────────────────────────────
    if ('IntersectionObserver' in window && !prefersReducedMotion) {
        var drawIO = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (!entry.isIntersecting) return;
                entry.target.classList.add('is-drawn');
                drawIO.unobserve(entry.target);
            });
        }, { threshold: 0.15, rootMargin: '40px 0px' });
        mascots.forEach(function (m) { drawIO.observe(m); });
    } else {
        mascots.forEach(function (m) { m.classList.add('is-drawn'); });
    }

})();
