document.addEventListener('DOMContentLoaded', function () {
  var prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  document.addEventListener('visibilitychange', function () {
    document.body.classList.toggle('index-tab-paused', document.hidden);
  });

  var welcomeOverlay = document.getElementById('welcome-overlay');
  document.querySelectorAll('.transition-link').forEach(function (link) {
    link.addEventListener('click', function (e) {
      var hrefAttr = this.getAttribute('href') || '';
      if (hrefAttr.indexOf('#') >= 0 || (this.href && this.href.indexOf('#') >= 0)) return;
      if (this.origin === window.location.origin && this.pathname === window.location.pathname) return;
      e.preventDefault();
      var targetUrl = this.href;
      var isLoginNav = targetUrl.indexOf('/auth/login') >= 0 || targetUrl.indexOf('login.jsp') >= 0;
      if (isLoginNav) {
        document.body.classList.add('page-exit');
        if (welcomeOverlay) welcomeOverlay.classList.add('active');
        window.setTimeout(function () { window.location.href = targetUrl; }, prefersReducedMotion ? 0 : 600);
      } else {
        document.body.classList.remove('page-loaded');
        window.setTimeout(function () { window.location.href = targetUrl; }, prefersReducedMotion ? 0 : 150);
      }
    });
  });

  // Hero action form — smooth transition to /auth/login on submit so the
  // welcome overlay plays even when the form GETs instead of a link click.
  document.querySelectorAll('form.da-act__form').forEach(function (form) {
    form.addEventListener('submit', function () {
      var action = form.getAttribute('action') || '';
      if (action.indexOf('/auth/login') >= 0) {
        document.body.classList.add('page-exit');
        if (welcomeOverlay) welcomeOverlay.classList.add('active');
      }
    });
  });

  var menuBtn = document.getElementById('mobile-menu-btn');
  var mobileMenu = document.getElementById('mobile-menu');
  if (menuBtn && mobileMenu) {
    menuBtn.addEventListener('click', function () {
      var open = !mobileMenu.classList.contains('is-open');
      mobileMenu.classList.toggle('is-open', open);
      if (open) {
        mobileMenu.removeAttribute('hidden');
        menuBtn.setAttribute('aria-expanded', 'true');
      } else {
        mobileMenu.setAttribute('hidden', '');
        menuBtn.setAttribute('aria-expanded', 'false');
      }
    });
    mobileMenu.querySelectorAll('a').forEach(function (anchor) {
      anchor.addEventListener('click', function () {
        mobileMenu.classList.remove('is-open');
        mobileMenu.setAttribute('hidden', '');
        menuBtn.setAttribute('aria-expanded', 'false');
      });
    });
  }

  var navBar = document.getElementById('navbar');
  var progressBar = document.getElementById('scroll-progress');
  var progressFill = progressBar ? progressBar.querySelector('.scroll-progress-fill, .da-scroll-progress__fill') : null;
  var backToTopBtn = document.getElementById('back-to-top');

  var ticking = false;
  function updateScrollChrome() {
    if (document.hidden) {
      ticking = false;
      return;
    }
    var docEl = document.documentElement;
    var scrollTop = window.scrollY;
    var docHeight = docEl.scrollHeight - window.innerHeight;
    var progress = docHeight > 0 ? scrollTop / docHeight : 0;

    if (progressFill) {
      progressFill.style.transform = 'translate3d(0, 0, 0) scaleX(' + progress + ')';
    }
    if (navBar) navBar.classList.toggle('is-scrolled', scrollTop > 24);
    if (backToTopBtn) backToTopBtn.classList.toggle('is-visible', scrollTop > 500);

    ticking = false;
  }
  function scheduleScrollFrame() {
    if (!ticking) {
      ticking = true;
      requestAnimationFrame(updateScrollChrome);
    }
  }
  updateScrollChrome();
  window.addEventListener('scroll', scheduleScrollFrame, { passive: true });
  var resizeRafPending = false;
  window.addEventListener('resize', function () {
    if (resizeRafPending) return;
    resizeRafPending = true;
    requestAnimationFrame(function () {
      resizeRafPending = false;
      scheduleScrollFrame();
    });
  });
  if (backToTopBtn) {
    backToTopBtn.addEventListener('click', function () {
      window.scrollTo({ top: 0, behavior: prefersReducedMotion ? 'auto' : 'smooth' });
    });
  }

  var revealObserver = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible');
        revealObserver.unobserve(entry.target);
      }
    });
  }, { threshold: 0.1, rootMargin: '0px 0px -50px 0px' });
  document.querySelectorAll('.da-reveal, .reveal, .reveal-left, .reveal-right').forEach(function (el) {
    revealObserver.observe(el);
  });

  var sectionLinks = document.querySelectorAll('[data-section-link]');
  var sections = Array.from(sectionLinks)
    .map(function (link) { return document.getElementById(link.getAttribute('data-section-link')); })
    .filter(Boolean);
  var sectionObserver = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      if (!entry.isIntersecting) return;
      sectionLinks.forEach(function (link) { link.classList.remove('is-active', 'active'); });
      var activeLink = document.querySelector('[data-section-link="' + entry.target.id + '"]');
      if (activeLink) activeLink.classList.add('is-active');
    });
  }, { threshold: 0.35 });
  sections.forEach(function (section) { sectionObserver.observe(section); });
});
