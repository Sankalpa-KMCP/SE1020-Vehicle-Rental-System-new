/* ----------------------------------------------------------------------------
 * mascots.js — SVG mascot animator
 *
 *  Responsibilities:
 *    1. Stroke-draw reveal on first IntersectionObserver hit
 *    2. Cursor-tracking eyes (lerp-smoothed, in-view only, fine pointer only)
 *    3. Gentle body tilt for in-view mascots (rotate3d ≤ 4deg)
 *
 *  All effects are gated on prefers-reduced-motion. Tracking only runs on
 *  pointers with hover capability; touch users get static mascots.
 * -------------------------------------------------------------------------- */
(function () {
    'use strict';

    var prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    var hasFinePointer = window.matchMedia('(hover: hover) and (pointer: fine)').matches;
    var LERP = 0.10;

    function lerp(a, b, t) { return a + (b - a) * t; }
    function clamp(v, mn, mx) { return v < mn ? mn : (v > mx ? mx : v); }

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

    if (prefersReducedMotion || !hasFinePointer) return;

    // ─── 2 & 3. Eye tracking + body tilt ──────────────────────────────────
    var states = mascots.map(function (root) {
        var eyes = root.querySelectorAll('.da-mascot__eye');
        if (!eyes.length) return null;
        return {
            root: root,
            eyes: eyes,
            inView: false,
            // Pixel-space target / current for eye translation
            tx: 0, ty: 0, cx: 0, cy: 0,
            // Tilt target / current (degrees)
            trx: 0, try_: 0, crx: 0, cry: 0
        };
    }).filter(Boolean);

    if (!states.length) return;

    var viewIO = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
            for (var i = 0; i < states.length; i++) {
                if (states[i].root === entry.target) {
                    states[i].inView = entry.isIntersecting;
                    break;
                }
            }
        });
    }, { threshold: 0, rootMargin: '120px 0px' });
    states.forEach(function (s) { viewIO.observe(s.root); });

    var mouseX = 0, mouseY = 0;
    var dirty = false;
    var rafId = null;

    function frame() {
        rafId = null;
        var anyMoving = false;
        for (var i = 0; i < states.length; i++) {
            var s = states[i];
            if (!s.inView) continue;
            var rect = s.root.getBoundingClientRect();
            var cx = rect.left + rect.width / 2;
            var cy = rect.top + rect.height / 2;
            // Distance from center, normalized by half-viewport for stable feel
            var dx = clamp((mouseX - cx) / Math.max(120, window.innerWidth * 0.25), -1, 1);
            var dy = clamp((mouseY - cy) / Math.max(120, window.innerHeight * 0.25), -1, 1);
            s.tx = dx * 1.8;            // eye translate px (in SVG units this is small but visible)
            s.ty = dy * 1.4;
            s.trx = -dy * 4;            // tilt body around X
            s.try_ = dx * 4;            // tilt body around Y

            s.cx = lerp(s.cx, s.tx, LERP);
            s.cy = lerp(s.cy, s.ty, LERP);
            s.crx = lerp(s.crx, s.trx, LERP);
            s.cry = lerp(s.cry, s.try_, LERP);

            var moving =
                Math.abs(s.cx - s.tx) > 0.02 ||
                Math.abs(s.cy - s.ty) > 0.02 ||
                Math.abs(s.crx - s.trx) > 0.05 ||
                Math.abs(s.cry - s.try_) > 0.05;

            // Apply eye translation
            for (var e = 0; e < s.eyes.length; e++) {
                s.eyes[e].style.transform = 'translate(' + s.cx.toFixed(2) + 'px,' + s.cy.toFixed(2) + 'px)';
            }
            // Apply body tilt to the root (subtle; SVG handles it via CSS transform)
            s.root.style.transform =
                'perspective(400px) rotateX(' + s.crx.toFixed(2) + 'deg) rotateY(' + s.cry.toFixed(2) + 'deg)';

            if (moving) anyMoving = true;
        }
        if ((dirty || anyMoving) && hasInViewMascots()) {
            dirty = false;
            rafId = requestAnimationFrame(frame);
        }
    }

    function hasInViewMascots() {
        for (var i = 0; i < states.length; i++) if (states[i].inView) return true;
        return false;
    }

    function wake() {
        if (rafId != null) return;
        if (!hasInViewMascots()) return;
        rafId = requestAnimationFrame(frame);
    }

    document.addEventListener('mousemove', function (e) {
        mouseX = e.clientX;
        mouseY = e.clientY;
        dirty = true;
        wake();
    }, { passive: true });

    document.addEventListener('visibilitychange', function () {
        if (!document.hidden) wake();
    });
})();
