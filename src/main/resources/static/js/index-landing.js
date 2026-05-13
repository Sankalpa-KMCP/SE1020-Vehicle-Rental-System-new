(function () {
  var qs = function (sel) { return document.querySelector(sel); };
  var closed = false;
  var prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  var tDom = 0;
  var minShowMs = 4000;
  var maxWaitMs = 5600;
  var shellRevealDelayMs = 420;
  var pollHandle = null;
  var ultimateHandle = null;

  function removeLoaderEl(loader) {
    if (loader && loader.parentNode) loader.parentNode.removeChild(loader);
  }

  function finishLoader() {
    if (closed) return;
    closed = true;
    if (pollHandle) {
      window.clearInterval(pollHandle);
      pollHandle = null;
    }
    if (ultimateHandle) {
      window.clearTimeout(ultimateHandle);
      ultimateHandle = null;
    }
    var loader = qs('#page-loader');
    if (!loader) {
      document.body.classList.add('page-loaded');
      return;
    }
    if (prefersReducedMotion) {
      loader.classList.add('hide');
      document.body.classList.add('page-loaded');
      removeLoaderEl(loader);
      return;
    }
    loader.classList.add('hide');
    window.setTimeout(function () {
      document.body.classList.add('page-loaded');
    }, shellRevealDelayMs);
    var fallbackMs = 2600;
    var didCleanup = false;
    function cleanup() {
      if (didCleanup) return;
      didCleanup = true;
      removeLoaderEl(loader);
    }
    function onEnd(e) {
      if (e.target !== loader || e.propertyName !== 'opacity') return;
      loader.removeEventListener('transitionend', onEnd);
      cleanup();
    }
    loader.addEventListener('transitionend', onEnd);
    window.setTimeout(function () {
      loader.removeEventListener('transitionend', onEnd);
      cleanup();
    }, fallbackMs);
  }

  function maybeFinish() {
    if (closed) return;
    var elapsed = performance.now() - tDom;
    var loadComplete = document.readyState === 'complete';
    if ((loadComplete && elapsed >= minShowMs) || elapsed >= maxWaitMs) {
      finishLoader();
    }
  }

  function boot() {
    if (closed) return;
    if (prefersReducedMotion) {
      finishLoader();
      return;
    }
    tDom = performance.now();
    pollHandle = window.setInterval(function () {
      maybeFinish();
    }, 48);
    window.addEventListener('load', function () { maybeFinish(); });
    ultimateHandle = window.setTimeout(function () {
      finishLoader();
    }, 6000);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', boot);
  } else {
    boot();
  }
})();

document.addEventListener('DOMContentLoaded', function () {
  var prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  document.addEventListener('visibilitychange', function () {
    document.body.classList.toggle('index-tab-paused', document.hidden);
  });

  var parallaxBg = document.getElementById('parallax-bg');
  function updateParallax(scrollY, docEl) {
    var maxScroll = docEl.scrollHeight - window.innerHeight;
    var scrollPercent = maxScroll > 0 ? scrollY / maxScroll : 0;
    if (parallaxBg) {
      if (prefersReducedMotion) {
        parallaxBg.style.transform = 'translate3d(0, 0, 0)';
        parallaxBg.style.opacity = '0.06';
      } else {
        var parallaxDistance = Math.min(scrollY * 0.18, 140);
        var bgOpacity = 0.12 - (scrollPercent * 0.06);
        parallaxBg.style.transform = 'translate3d(0, ' + parallaxDistance + 'px, 0)';
        parallaxBg.style.opacity = String(Math.max(0.04, bgOpacity));
      }
    }
    var glowOpacity = prefersReducedMotion ? 0.04 : 0.04 + (Math.sin(scrollPercent * Math.PI) * 0.08);
    document.body.style.setProperty('--ambient-glow-opacity', String(Math.max(0.03, glowOpacity)));
  }

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

  var menuBtn = document.getElementById('mobile-menu-btn');
  var mobileMenu = document.getElementById('mobile-menu');
  if (menuBtn && mobileMenu) {
    menuBtn.addEventListener('click', function () {
      mobileMenu.classList.toggle('hidden');
    });
    mobileMenu.querySelectorAll('a').forEach(function (anchor) {
      anchor.addEventListener('click', function () { mobileMenu.classList.add('hidden'); });
    });
  }

  var navBar = document.getElementById('navbar');
  var progressBar = document.getElementById('scroll-progress');
  var progressFill = progressBar ? progressBar.querySelector('.scroll-progress-fill') : null;
  var backToTopBtn = document.getElementById('back-to-top');

  var ticking = false;
  function updateScrollChrome() {
    var docEl = document.documentElement;
    var scrollTop = window.scrollY;
    var docHeight = docEl.scrollHeight - window.innerHeight;
    var progress = docHeight > 0 ? scrollTop / docHeight : 0;

    updateParallax(scrollTop, docEl);

    if (progressFill) {
      progressFill.style.transform = 'translate3d(0, 0, 0) scaleX(' + progress + ')';
    }
    if (navBar) navBar.classList.toggle('nav-scrolled', scrollTop > 24);
    if (backToTopBtn) backToTopBtn.classList.toggle('visible', scrollTop > 500);

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
        entry.target.classList.add('active');
        revealObserver.unobserve(entry.target);
      }
    });
  }, { threshold: 0.1, rootMargin: '0px 0px -50px 0px' });
  document.querySelectorAll('.reveal, .reveal-left, .reveal-right').forEach(function (el) {
    revealObserver.observe(el);
  });

  var sectionLinks = document.querySelectorAll('[data-section-link]');
  var sections = Array.from(sectionLinks)
    .map(function (link) { return document.getElementById(link.getAttribute('data-section-link')); })
    .filter(Boolean);
  var sectionObserver = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      if (!entry.isIntersecting) return;
      sectionLinks.forEach(function (link) { link.classList.remove('active'); });
      var activeLink = document.querySelector('[data-section-link="' + entry.target.id + '"]');
      if (activeLink) activeLink.classList.add('active');
    });
  }, { threshold: 0.35 });
  sections.forEach(function (section) { sectionObserver.observe(section); });

  var filterBtns = document.querySelectorAll('.filter-btn');
  var carItems = document.querySelectorAll('.car-item');
  filterBtns.forEach(function (btn) {
    btn.addEventListener('click', function () {
      filterBtns.forEach(function (b) {
        b.classList.remove('active', 'bg-[#0D6EFD]', 'text-white');
        b.classList.add('text-gray-700', 'border-gray-200', 'bg-white');
        b.setAttribute('aria-pressed', 'false');
      });
      btn.classList.add('active', 'bg-[#0D6EFD]', 'text-white');
      btn.classList.remove('text-gray-700', 'border-gray-200', 'bg-white');
      btn.setAttribute('aria-pressed', 'true');
      var filterValue = btn.getAttribute('data-filter');
      carItems.forEach(function (item) {
        if (filterValue === 'all' || item.getAttribute('data-category') === filterValue) {
          item.classList.remove('hidden-item');
          window.setTimeout(function () { item.style.display = 'block'; }, 50);
        } else {
          item.classList.add('hidden-item');
          window.setTimeout(function () { item.style.display = 'none'; }, 400);
        }
      });
    });
  });

  document.querySelectorAll('.faq-item').forEach(function (item) {
    item.addEventListener('click', function () {
      var isActive = item.classList.contains('faq-open');
      document.querySelectorAll('.faq-item').forEach(function (i) { i.classList.remove('faq-open'); });
      if (!isActive) item.classList.add('faq-open');
    });
  });

  var slides = document.querySelectorAll('.testimonial-slide');
  var currentSlide = 0;
  var slideInterval;
  function showSlide(index) {
    slides.forEach(function (s) { s.classList.remove('active'); });
    if (slides[index]) slides[index].classList.add('active');
  }
  function nextSlide() { currentSlide = (currentSlide + 1) % slides.length; showSlide(currentSlide); }
  function prevSlide() { currentSlide = (currentSlide - 1 + slides.length) % slides.length; showSlide(currentSlide); }
  var nextBtn = document.getElementById('next-test');
  var prevBtn = document.getElementById('prev-test');
  function resetInterval() {
    if (slideInterval) clearInterval(slideInterval);
    slideInterval = window.setInterval(nextSlide, 6000);
  }
  if (nextBtn && prevBtn && slides.length) {
    nextBtn.addEventListener('click', function () { nextSlide(); resetInterval(); });
    prevBtn.addEventListener('click', function () { prevSlide(); resetInterval(); });
    resetInterval();
  }

  var counters = document.querySelectorAll('.counter');
  var hasCounted = false;
  var counterObserver = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      if (!entry.isIntersecting || hasCounted) return;
      hasCounted = true;
      var counterList = Array.from(counters).map(function (c) {
        return { el: c, target: +c.getAttribute('data-target'), decimals: (+c.getAttribute('data-target')) % 1 !== 0 };
      });
      var start = performance.now();
      var duration = 2000;
      function tickCounter(now) {
        var elapsed = now - start;
        var t = Math.min(1, elapsed / duration);
        var eased = 1 - Math.pow(1 - t, 3);
        counterList.forEach(function (item) {
          var val = item.target * eased;
          item.el.innerText = item.decimals ? val.toFixed(1) : String(Math.ceil(val));
        });
        if (t < 1) {
          requestAnimationFrame(tickCounter);
        } else {
          counterList.forEach(function (item) { item.el.innerText = item.target; });
        }
      }
      requestAnimationFrame(tickCounter);
    });
  }, { threshold: 0.5 });
  if (counters.length > 0) counterObserver.observe(counters[0].parentElement);

  function Mascot(id, weight, maxRotation) {
    this.avatarContainer = document.getElementById(id);
    if (!this.avatarContainer) return;
    this.body = this.avatarContainer.querySelector('.mascot-body');
    this.eyes = this.avatarContainer.querySelector('.mascot-eyes');
    this.weight = weight;
    this.maxRotation = maxRotation;
    this.currentX = 0;
    this.currentY = 0;
    this.targetX = 0;
    this.targetY = 0;
  }
  Mascot.prototype.lookAt = function (x, y) {
    this.targetX = x;
    this.targetY = y;
  };
  Mascot.prototype.isSettled = function () {
    if (!this.body || !this.eyes) return true;
    return Math.abs(this.targetX - this.currentX) < 0.004 && Math.abs(this.targetY - this.currentY) < 0.004;
  };
  Mascot.prototype.update = function () {
    if (!this.body || !this.eyes) return;
    this.currentX += (this.targetX - this.currentX) * this.weight;
    this.currentY += (this.targetY - this.currentY) * this.weight;
    var moveX = this.currentX * 15;
    var moveY = this.currentY * 15;
    var rotX = this.currentY * -this.maxRotation;
    var rotY = this.currentX * this.maxRotation;
    this.body.style.transform = 'translate3d(' + moveX + 'px, ' + moveY + 'px, 0) rotateX(' + rotX + 'deg) rotateY(' + rotY + 'deg)';
    this.eyes.style.transform = 'translate3d(' + (this.currentX * 25) + 'px, ' + (this.currentY * 25) + 'px, 40px)';
  };

  var mascots = [
    new Mascot('mascot-1', 0.05, 20),
    new Mascot('mascot-2', 0.06, 15),
    new Mascot('mascot-3', 0.04, 25),
    new Mascot('mascot-4', 0.07, 30),
    new Mascot('mascot-5', 0.05, 15),
    new Mascot('mascot-6', 0.06, 20)
  ].filter(function (m) { return m.body != null; });

  var mascotRafId = null;
  var mascotsActive = false;
  var pendingMouseX = 0;
  var pendingMouseY = 0;
  var mouseDirty = false;
  var mouseFlushRaf = null;
  var pendingClientX = 0;
  var pendingClientY = 0;

  function mascotsAllSettled() {
    for (var i = 0; i < mascots.length; i++) {
      if (!mascots[i].isSettled()) return false;
    }
    return true;
  }

  function ensureMascotLoop() {
    if (!mascotsActive || mascots.length === 0) return;
    if (mascotRafId == null) {
      mascotRafId = window.requestAnimationFrame(mascotFrame);
    }
  }

  function mascotFrame() {
    mascotRafId = null;
    if (!mascotsActive || mascots.length === 0) return;

    if (mouseDirty) {
      mascots.forEach(function (m) { m.lookAt(pendingMouseX, pendingMouseY); });
      mouseDirty = false;
    }
    mascots.forEach(function (m) { m.update(); });

    if (!mascotsAllSettled()) {
      mascotRafId = window.requestAnimationFrame(mascotFrame);
    }
  }

  function startMascots() {
    if (mascots.length === 0 || mascotsActive) return;
    mascotsActive = true;
    ensureMascotLoop();
  }

  function stopMascots() {
    mascotsActive = false;
    if (mascotRafId != null) {
      window.cancelAnimationFrame(mascotRafId);
      mascotRafId = null;
    }
    if (mouseFlushRaf != null) {
      window.cancelAnimationFrame(mouseFlushRaf);
      mouseFlushRaf = null;
    }
  }

  var teamSection = document.getElementById('team');
  if (teamSection && mascots.length) {
    var teamIo = new IntersectionObserver(function (entries) {
      entries.forEach(function (e) {
        if (e.isIntersecting) startMascots();
        else stopMascots();
      });
    }, { rootMargin: '100px 0px', threshold: 0 });
    teamIo.observe(teamSection);
  }

  document.addEventListener('mousemove', function (e) {
    if (!mascotsActive || mascots.length === 0) return;
    pendingClientX = e.clientX;
    pendingClientY = e.clientY;
    if (mouseFlushRaf != null) return;
    mouseFlushRaf = window.requestAnimationFrame(function () {
      mouseFlushRaf = null;
      pendingMouseX = (pendingClientX / window.innerWidth) * 2 - 1;
      pendingMouseY = (pendingClientY / window.innerHeight) * 2 - 1;
      mouseDirty = true;
      ensureMascotLoop();
    });
  }, { passive: true });
});
