/* ----------------------------------------------------------------------------
 * motion.js — unified motion layer
 *
 *  All effects share a single rAF scheduler so we never run more than one
 *  loop per frame. Each effect is registered as a "task" and stays in the
 *  pool while it has work; the loop sleeps when the pool is empty.
 *
 *  Effects:
 *    1. Parallax           [data-parallax="<rate>"]  lerp-smoothed
 *    2. Magnetic           .da-magnetic              lerp-smoothed, soft
 *    3. Tabs slider        [data-tabs]               (event-driven, no rAF)
 *    4. Chapter numerator  [data-chapter]            scroll-driven, pulse
 *    5. Stagger reveal     .da-reveal--stagger       (sets --idx)
 *    6. Cinema depth       .da-cinema__track .da-card lerp-smoothed
 *    7. Section seams      .da-seam                  IntersectionObserver
 *
 *  All effects gate on prefers-reduced-motion and pointer type where relevant.
 * -------------------------------------------------------------------------- */
(function () {
    'use strict';

    var prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    var hasFinePointer = window.matchMedia('(hover: hover) and (pointer: fine)').matches;

    var LERP_STRONG = 0.16;
    var LERP_SOFT = 0.08;

    function lerp(current, target, t) { return current + (target - current) * t; }
    function clamp(v, min, max) { return v < min ? min : (v > max ? max : v); }

    // ─── Shared rAF scheduler ─────────────────────────────────────────────
    // Tasks are functions returning `true` while they want to keep running,
    // or `false` to stop being scheduled. The loop sleeps when empty.
    var scheduler = (function () {
        var tasks = [];
        var rafId = null;
        var lastTime = 0;

        function frame(now) {
            rafId = null;
            var dt = lastTime ? (now - lastTime) : 16;
            lastTime = now;
            // Cap dt so backgrounded tabs don't produce huge jumps
            if (dt > 64) dt = 64;
            for (var i = tasks.length - 1; i >= 0; i--) {
                var keep;
                try { keep = tasks[i](dt); }
                catch (err) { keep = false; }
                if (keep === false) tasks.splice(i, 1);
            }
            if (tasks.length) rafId = requestAnimationFrame(frame);
        }

        function wake() {
            if (rafId == null && tasks.length) {
                lastTime = 0;
                rafId = requestAnimationFrame(frame);
            }
        }

        return {
            add: function (task) {
                if (tasks.indexOf(task) === -1) tasks.push(task);
                wake();
            },
            wake: wake
        };
    })();

    // ─── 1. Parallax (lerp-smoothed) ──────────────────────────────────────
    function initParallax() {
        if (prefersReducedMotion) return;

        var nodes = Array.prototype.slice.call(document.querySelectorAll('[data-parallax]'));
        if (!nodes.length) return;

        var items = nodes.map(function (el) {
            var rate = parseFloat(el.getAttribute('data-parallax')) || 0;
            return { el: el, rate: rate, anchor: 0, current: 0, target: 0 };
        });

        function measure() {
            var viewportH = window.innerHeight || 0;
            var y = window.pageYOffset || 0;
            items.forEach(function (it) {
                var rect = it.el.getBoundingClientRect();
                it.anchor = y + rect.top + rect.height / 2 - viewportH / 2;
            });
        }

        function computeTargets() {
            var y = window.pageYOffset || 0;
            items.forEach(function (it) {
                var t = (y - it.anchor) * it.rate * -1;
                it.target = clamp(t, -80, 80);
            });
        }

        // Animation task — runs only while values haven't converged.
        function task() {
            var moving = false;
            for (var i = 0; i < items.length; i++) {
                var it = items[i];
                var prev = it.current;
                it.current = lerp(it.current, it.target, LERP_STRONG);
                if (Math.abs(it.target - it.current) > 0.05) {
                    moving = true;
                } else {
                    it.current = it.target;
                }
                if (it.current !== prev) {
                    it.el.style.setProperty('--parallax-y', it.current.toFixed(2) + 'px');
                }
            }
            return moving;
        }

        function onScroll() {
            computeTargets();
            scheduler.add(task);
        }

        measure();
        computeTargets();
        scheduler.add(task);
        window.addEventListener('scroll', onScroll, { passive: true });
        window.addEventListener('resize', function () {
            measure();
            computeTargets();
            scheduler.add(task);
        }, { passive: true });
    }

    // ─── 2. Magnetic buttons (soft lerp) ──────────────────────────────────
    function initMagnetic() {
        if (prefersReducedMotion || !hasFinePointer) return;

        var targets = document.querySelectorAll('.da-magnetic');
        if (!targets.length) return;

        var STRENGTH = 0.14;
        var MAX = 8;

        Array.prototype.forEach.call(targets, function (el) {
            var state = { tx: 0, ty: 0, cx: 0, cy: 0, active: false };

            function settle() {
                if (Math.abs(state.cx - state.tx) < 0.04 && Math.abs(state.cy - state.ty) < 0.04) {
                    state.cx = state.tx;
                    state.cy = state.ty;
                    el.style.setProperty('--mag-x', state.cx.toFixed(2) + 'px');
                    el.style.setProperty('--mag-y', state.cy.toFixed(2) + 'px');
                    return false;
                }
                state.cx = lerp(state.cx, state.tx, LERP_SOFT);
                state.cy = lerp(state.cy, state.ty, LERP_SOFT);
                el.style.setProperty('--mag-x', state.cx.toFixed(2) + 'px');
                el.style.setProperty('--mag-y', state.cy.toFixed(2) + 'px');
                return true;
            }

            el.addEventListener('mousemove', function (e) {
                var rect = el.getBoundingClientRect();
                var x = e.clientX - (rect.left + rect.width / 2);
                var y = e.clientY - (rect.top + rect.height / 2);
                state.tx = clamp(x * STRENGTH, -MAX, MAX);
                state.ty = clamp(y * STRENGTH, -MAX, MAX);
                scheduler.add(settle);
            });
            el.addEventListener('mouseleave', function () {
                state.tx = 0;
                state.ty = 0;
                scheduler.add(settle);
            });
            el.addEventListener('blur', function () {
                state.tx = 0;
                state.ty = 0;
                scheduler.add(settle);
            });
        });
    }

    // ─── 3. Sliding tab indicator ─────────────────────────────────────────
    function initTabs() {
        var groups = document.querySelectorAll('[data-tabs]');
        if (!groups.length) return;

        Array.prototype.forEach.call(groups, function (group) {
            var indicator = group.querySelector('[data-tabs-indicator]');
            if (!indicator) return;

            function update(active) {
                if (!active) return;
                var groupRect = group.getBoundingClientRect();
                var rect = active.getBoundingClientRect();
                var x = rect.left - groupRect.left;
                indicator.style.transform = 'translateX(' + x + 'px)';
                indicator.style.width = rect.width + 'px';
            }

            var tabs = group.querySelectorAll('.da-tab, .filter-btn');

            Array.prototype.forEach.call(tabs, function (tab) {
                tab.addEventListener('click', function () {
                    Array.prototype.forEach.call(tabs, function (t) {
                        t.classList.remove('is-active');
                        t.setAttribute('aria-pressed', 'false');
                    });
                    tab.classList.add('is-active');
                    tab.setAttribute('aria-pressed', 'true');
                    update(tab);
                });
                tab.addEventListener('focus', function () { update(tab); });
            });

            var initial = group.querySelector('.da-tab.is-active, .filter-btn.is-active') || tabs[0];
            indicator.style.transition = 'none';
            update(initial);
            requestAnimationFrame(function () { indicator.style.transition = ''; });

            window.addEventListener('resize', function () {
                var current = group.querySelector('.da-tab.is-active, .filter-btn.is-active') || tabs[0];
                update(current);
            }, { passive: true });
        });
    }

    // ─── 4. Chapter numerator with pulse on change ───────────────────────
    function initChapter() {
        var marker = document.getElementById('chapter-marker');
        if (!marker) return;

        var sections = Array.prototype.slice.call(document.querySelectorAll('[data-chapter]'));
        if (!sections.length) {
            marker.style.display = 'none';
            return;
        }

        var numEl = marker.querySelector('[data-chapter-num]');
        var labelEl = marker.querySelector('[data-chapter-label]');
        var current = '';
        var pulseTimer = null;

        function pickActive() {
            var anchor = (window.innerHeight || 0) * 0.35;
            var best = sections[0];
            for (var i = 0; i < sections.length; i++) {
                var rect = sections[i].getBoundingClientRect();
                if (rect.top <= anchor) best = sections[i];
            }
            return best;
        }

        var ticking = false;
        function onScroll() {
            if (ticking) return;
            ticking = true;
            requestAnimationFrame(function () {
                var active = pickActive();
                var num = active.getAttribute('data-chapter') || '01';
                var name = active.getAttribute('data-chapter-name') || '';
                if (num !== current) {
                    current = num;
                    marker.classList.add('is-changing');
                    if (pulseTimer) clearTimeout(pulseTimer);
                    setTimeout(function () {
                        if (numEl) numEl.textContent = num;
                        if (labelEl) labelEl.textContent = name;
                        marker.classList.remove('is-changing');
                        marker.classList.add('is-pulsing');
                        pulseTimer = setTimeout(function () {
                            marker.classList.remove('is-pulsing');
                        }, 760);
                    }, 200);
                }
                ticking = false;
            });
        }

        onScroll();
        window.addEventListener('scroll', onScroll, { passive: true });
    }

    // ─── 5. Stagger reveal — sets --idx per child ─────────────────────────
    function initStagger() {
        var groups = document.querySelectorAll('.da-reveal--stagger, .da-stagger');
        if (!groups.length) return;

        Array.prototype.forEach.call(groups, function (group) {
            var children = group.children;
            for (var i = 0; i < children.length; i++) {
                children[i].style.setProperty('--idx', String(i));
                children[i].style.setProperty('--reveal-delay', (i * 70) + 'ms');
                if (!children[i].classList.contains('da-reveal')) {
                    children[i].classList.add('da-reveal');
                }
            }
        });
    }

    // ─── 6. Cinema cards lerp-smoothed depth ─────────────────────────────
    function initCinema() {
        if (prefersReducedMotion) return;
        var tracks = document.querySelectorAll('.da-cinema__track');
        if (!tracks.length) return;

        Array.prototype.forEach.call(tracks, function (track) {
            var cardStates = [];

            function measure() {
                var cards = track.querySelectorAll('.da-card');
                cardStates = [];
                Array.prototype.forEach.call(cards, function (card) {
                    cardStates.push({ el: card, scale: 1, opacity: 1, targetScale: 1, targetOpacity: 1 });
                });
            }

            function computeTargets() {
                var trackRect = track.getBoundingClientRect();
                var center = trackRect.left + trackRect.width / 2;
                cardStates.forEach(function (s) {
                    var rect = s.el.getBoundingClientRect();
                    var mid = rect.left + rect.width / 2;
                    var dist = Math.abs(center - mid) / Math.max(1, trackRect.width / 2);
                    s.targetScale = 1 - Math.min(0.04, dist * 0.04);
                    s.targetOpacity = 1 - Math.min(0.22, dist * 0.22);
                });
            }

            function task() {
                var moving = false;
                for (var i = 0; i < cardStates.length; i++) {
                    var s = cardStates[i];
                    var sPrev = s.scale, oPrev = s.opacity;
                    s.scale = lerp(s.scale, s.targetScale, LERP_STRONG);
                    s.opacity = lerp(s.opacity, s.targetOpacity, LERP_STRONG);
                    if (Math.abs(s.scale - s.targetScale) > 0.001 || Math.abs(s.opacity - s.targetOpacity) > 0.005) {
                        moving = true;
                    } else {
                        s.scale = s.targetScale;
                        s.opacity = s.targetOpacity;
                    }
                    if (s.scale !== sPrev || s.opacity !== oPrev) {
                        s.el.style.transform = 'scale3d(' + s.scale.toFixed(3) + ',' + s.scale.toFixed(3) + ',1)';
                        s.el.style.opacity = s.opacity.toFixed(3);
                    }
                }
                return moving;
            }

            function refresh() {
                computeTargets();
                scheduler.add(task);
            }

            measure();
            refresh();
            track.addEventListener('scroll', refresh, { passive: true });
            window.addEventListener('resize', function () { measure(); refresh(); }, { passive: true });
        });
    }

    // ─── 7. Animated section seams ────────────────────────────────────────
    //  Every .da-scene that follows another .da-scene gets a hairline that
    //  scales in via the ::after pseudo. We observe each candidate and tag
    //  it with .is-seamed when it enters the viewport.
    function initSeams() {
        var allScenes = Array.prototype.slice.call(document.querySelectorAll('.da-scene'));
        var seamTargets = allScenes.filter(function (el, idx) {
            return idx > 0 && el.previousElementSibling && el.previousElementSibling.classList &&
                   el.previousElementSibling.classList.contains('da-scene');
        });

        // Also support explicit .da-seam elements if any author uses them.
        var explicit = Array.prototype.slice.call(document.querySelectorAll('.da-seam'));

        var all = seamTargets.concat(explicit);
        if (!all.length) return;

        if (!('IntersectionObserver' in window)) {
            seamTargets.forEach(function (s) { s.classList.add('is-seamed'); });
            explicit.forEach(function (s) { s.classList.add('is-visible'); });
            return;
        }
        var io = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (!entry.isIntersecting) return;
                if (entry.target.classList.contains('da-seam')) {
                    entry.target.classList.add('is-visible');
                } else {
                    entry.target.classList.add('is-seamed');
                }
                io.unobserve(entry.target);
            });
        }, { threshold: 0.02, rootMargin: '0px 0px -8% 0px' });

        all.forEach(function (s) { io.observe(s); });
    }

    function boot() {
        initStagger();
        initParallax();
        initMagnetic();
        initTabs();
        initChapter();
        initCinema();
        initSeams();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', boot);
    } else {
        boot();
    }
})();
