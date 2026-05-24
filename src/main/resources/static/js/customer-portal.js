(function () {
    'use strict';

    var root = document.documentElement;
    var body = document.body;
    var reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    var coarsePointer = window.matchMedia('(pointer: coarse)').matches;
    var enableTilt = !reducedMotion && !coarsePointer;

    function setViewportVars() {
        var w = Math.max(window.innerWidth || 0, root.clientWidth || 0);
        root.style.setProperty('--stage-width', Math.min(Math.round(w * 1.05), 1860) + 'px');
    }

    function setupTopbarScroll() {
        var topbar = document.getElementById('topbar');
        if (!topbar) return;
        function onScroll() {
            topbar.classList.toggle('topbar--scrolled', window.scrollY > 8);
        }
        window.addEventListener('scroll', onScroll, { passive: true });
        onScroll();
    }

    function setupReveal() {
        var revealEls = document.querySelectorAll('.reveal');
        if (!revealEls.length) return;
        if (reducedMotion) {
            revealEls.forEach(function (el) { el.classList.add('reveal--visible'); });
            return;
        }
        revealEls.forEach(function (el) {
            if (!el.classList.contains('reveal--visible')) {
                el.classList.add('reveal--pending');
            }
        });
        var observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add('reveal--visible');
                    entry.target.classList.remove('reveal--pending');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.1, rootMargin: '0px 0px -32px 0px' });
        revealEls.forEach(function (el) { observer.observe(el); });
    }

    function setupAlertDismiss() {
        var alertEl = document.getElementById('profile-alert');
        if (!alertEl) return;

        function cleanUrl() {
            if (!window.history.replaceState) return;
            var url = new URL(window.location.href);
            url.searchParams.delete('msg');
            var qs = url.searchParams.toString();
            window.history.replaceState({}, '', url.pathname + (qs ? '?' + qs : '') + url.hash);
        }

        function dismissAlert() {
            if (alertEl.classList.contains('alert--hide')) return;
            alertEl.classList.add('alert--hide');
            cleanUrl();
            window.setTimeout(function () { alertEl.remove(); }, 320);
        }

        var closeBtn = alertEl.querySelector('.alert__close');
        if (closeBtn) closeBtn.addEventListener('click', dismissAlert);
        window.setTimeout(dismissAlert, 5000);
    }

    function bindTilt(el, amount) {
        var ticking = false;
        el.addEventListener('mousemove', function (e) {
            if (e.target.closest('a, button, input, select, textarea')) return;
            if (!ticking) {
                window.requestAnimationFrame(function () {
                    var rect = el.getBoundingClientRect();
                    var px = (e.clientX - rect.left) / rect.width;
                    var py = (e.clientY - rect.top) / rect.height;
                    var rx = Math.max(-amount, Math.min(amount, (0.5 - py) * amount));
                    var ry = Math.max(-amount, Math.min(amount, (px - 0.5) * amount));
                    el.style.transform = 'rotateX(' + rx.toFixed(2) + 'deg) rotateY(' + ry.toFixed(2) + 'deg)';
                    ticking = false;
                });
                ticking = true;
            }
        });
        el.addEventListener('mouseleave', function () { el.style.transform = ''; });
    }

    setViewportVars();
    setupTopbarScroll();
    setupReveal();
    setupAlertDismiss();

    if (body.classList.contains('customer-premium-theme')) {
        requestAnimationFrame(function () { body.classList.add('ready'); });
    }

    if (enableTilt) {
        document.querySelectorAll('.flow-panel[data-tilt]').forEach(function (panel) {
            bindTilt(panel, 5);
        });
    }

    window.addEventListener('resize', setViewportVars);
})();
