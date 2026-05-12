(function () {
  let hasRun = false;
  function qs(sel) { return document.querySelector(sel); }
  function finishLoader(force) {
    document.body.classList.add('page-loaded');
    var loader = qs('#page-loader');
    if (!loader) return;
    if (loader.dataset.closed === '1' && !force) return;
    loader.dataset.closed = '1';
    loader.classList.add('hide');
    window.setTimeout(function () {
      if (loader.parentNode) loader.parentNode.removeChild(loader);
    }, 1300);
  }
  function showLoaderSequence() {
    if (hasRun) return;
    hasRun = true;
    var prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    window.setTimeout(function () { finishLoader(false); }, prefersReducedMotion ? 0 : 800);
    window.setTimeout(function () { finishLoader(true); }, 1500);
  }
  if (document.readyState === 'complete') {
    requestAnimationFrame(showLoaderSequence);
  } else {
    window.addEventListener('load', function onLoad() {
      window.removeEventListener('load', onLoad);
      requestAnimationFrame(showLoaderSequence);
    });
    window.setTimeout(function () { if (!hasRun) showLoaderSequence(); }, 1800);
  }
})();

document.addEventListener('DOMContentLoaded', function () {
  var prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  document.body.classList.add('page-loaded');

  var navBar = document.getElementById('navbar');
  var progressBar = document.getElementById('scroll-progress');
  var backToTopBtn = document.getElementById('back-to-top');
  var cinematicHero = document.getElementById('cinematic-hero');
  var heroScrollVideo = document.getElementById('hero-scroll-video');
  var heroScrubDuration = 22;

  if (heroScrollVideo && !prefersReducedMotion) {
    heroScrollVideo.addEventListener('loadedmetadata', function () {
      if (!Number.isNaN(heroScrollVideo.duration) && Number.isFinite(heroScrollVideo.duration)) {
        heroScrubDuration = heroScrollVideo.duration * 1.7;
      }
      heroScrollVideo.currentTime = 0;
      heroScrollVideo.pause();
    });
  }

  function syncCinematicHero() {
    if (!cinematicHero || !heroScrollVideo || prefersReducedMotion) return;
    var rect = cinematicHero.getBoundingClientRect();
    var viewportHeight = window.innerHeight;
    var fullTravel = rect.height + viewportHeight;
    var progress = Math.min(Math.max((viewportHeight - rect.top) / fullTravel, 0), 1);
    if (heroScrollVideo.readyState >= 2) {
      var targetTime = heroScrubDuration * progress;
      if (Math.abs(heroScrollVideo.currentTime - targetTime) > 0.25) {
        heroScrollVideo.currentTime = targetTime;
      }
    }
    var scale = 1 + (progress * 0.06);
    heroScrollVideo.style.transform = 'scale(' + scale + ')';
    heroScrollVideo.style.opacity = String(1.0 - (progress * 0.05));
  }

  var ticking = false;
  function updateScrollChrome() {
    var scrollTop = window.scrollY;
    var docHeight = document.documentElement.scrollHeight - window.innerHeight;
    var progress = docHeight > 0 ? (scrollTop / docHeight) * 100 : 0;
    if (progressBar) progressBar.style.width = progress + '%';
    if (navBar) navBar.classList.toggle('nav-scrolled', scrollTop > 24);
    if (backToTopBtn) backToTopBtn.classList.toggle('visible', scrollTop > 500);
    syncCinematicHero();
    ticking = false;
  }
  function scheduleScrollFrame() {
    if (!ticking) {
      requestAnimationFrame(updateScrollChrome);
      ticking = true;
    }
  }
  updateScrollChrome();
  window.addEventListener('scroll', scheduleScrollFrame, { passive: true });
  window.addEventListener('resize', scheduleScrollFrame);
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
          setTimeout(function () { item.style.display = 'block'; }, 50);
        } else {
          item.classList.add('hidden-item');
          setTimeout(function () { item.style.display = 'none'; }, 400);
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
  function showSlide(index) {
    slides.forEach(function (s) { s.classList.remove('active'); });
    if (slides[index]) slides[index].classList.add('active');
  }
  function nextSlide() { currentSlide = (currentSlide + 1) % slides.length; showSlide(currentSlide); }
  function prevSlide() { currentSlide = (currentSlide - 1 + slides.length) % slides.length; showSlide(currentSlide); }
  var nextBtn = document.getElementById('next-test');
  var prevBtn = document.getElementById('prev-test');
  if (nextBtn && prevBtn && slides.length) {
    nextBtn.addEventListener('click', nextSlide);
    prevBtn.addEventListener('click', prevSlide);
    setInterval(nextSlide, 6000);
  }

  var counters = document.querySelectorAll('.counter');
  var hasCounted = false;
  var counterObserver = new IntersectionObserver(function (entries) {
    if (entries[0].isIntersecting && !hasCounted) {
      hasCounted = true;
      counters.forEach(function (counter) {
        var target = +counter.getAttribute('data-target');
        var duration = 2000;
        var increment = target / (duration / 16);
        var current = 0;
        function updateCounter() {
          current += increment;
          if (current < target) {
            counter.innerText = target % 1 !== 0 ? current.toFixed(1) : Math.ceil(current);
            requestAnimationFrame(updateCounter);
          } else {
            counter.innerText = target;
          }
        }
        updateCounter();
      });
    }
  }, { threshold: 0.5 });
  if (counters.length > 0) counterObserver.observe(counters[0].parentElement);
});
