document.addEventListener('DOMContentLoaded', function () {
  var filterBtns = document.querySelectorAll('.filter-btn, .da-filter-pill');
  var carItems = document.querySelectorAll('.car-item');
  filterBtns.forEach(function (btn) {
    btn.addEventListener('click', function () {
      filterBtns.forEach(function (b) {
        b.classList.remove('is-active', 'active');
        b.setAttribute('aria-pressed', 'false');
      });
      btn.classList.add('is-active', 'active');
      btn.setAttribute('aria-pressed', 'true');
      var filterValue = btn.getAttribute('data-filter');
      carItems.forEach(function (item) {
        if (filterValue === 'all' || item.getAttribute('data-category') === filterValue) {
          item.classList.remove('hidden-item');
          item.style.display = '';
        } else {
          item.classList.add('hidden-item');
          item.style.display = 'none';
        }
      });
    });
  });

  document.querySelectorAll('.da-faq, .faq-item').forEach(function (item) {
    var trigger = item.querySelector('.da-faq__trigger, .faq-header');
    var handler = function () {
      var isActive = item.classList.contains('is-open') || item.classList.contains('faq-open');
      document.querySelectorAll('.da-faq, .faq-item').forEach(function (i) {
        i.classList.remove('is-open', 'faq-open');
      });
      if (!isActive) {
        item.classList.add('is-open', 'faq-open');
      }
    };
    if (trigger) {
      trigger.addEventListener('click', handler);
    } else {
      item.addEventListener('click', handler);
    }
  });

  var slides = document.querySelectorAll('.da-testimonial-slide, .testimonial-slide');
  var currentSlide = 0;
  var slideInterval;
  function showSlide(index) {
    slides.forEach(function (s) { s.classList.remove('is-active', 'active'); });
    if (slides[index]) slides[index].classList.add('is-active', 'active');
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

  // Per-counter intersection — each counter ticks when it scrolls into view.
  // Counters can be authored anywhere on the page (hero signal cards, stats band, etc.)
  var counters = document.querySelectorAll('.counter');
  function runCounter(el) {
    if (el.dataset.counted === '1') return;
    el.dataset.counted = '1';
    var target = parseFloat(el.getAttribute('data-target')) || 0;
    var decimals = target % 1 !== 0;
    var duration = 1600;
    var start = performance.now();
    function tick(now) {
      var t = Math.min(1, (now - start) / duration);
      var eased = 1 - Math.pow(1 - t, 3);
      var val = target * eased;
      el.innerText = decimals ? val.toFixed(1) : String(Math.ceil(val));
      if (t < 1) requestAnimationFrame(tick);
      else el.innerText = decimals ? target.toFixed(1) : target;
    }
    requestAnimationFrame(tick);
  }
  if (counters.length > 0) {
    var counterObserver = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          runCounter(entry.target);
          counterObserver.unobserve(entry.target);
        }
      });
    }, { threshold: 0.4 });
    counters.forEach(function (c) { counterObserver.observe(c); });
  }

  // Horizontal Carousel Glass Arrow Navigation
  var prevArrow = document.getElementById('fleet-prev');
  var nextArrow = document.getElementById('fleet-next');
  var track = document.getElementById('fleet-grid');

  function updateArrowState() {
    if (!track || !prevArrow || !nextArrow) return;
    var atStart = track.scrollLeft <= 8;
    var atEnd = track.scrollLeft + track.clientWidth >= track.scrollWidth - 8;
    prevArrow.classList.toggle('is-hidden', atStart);
    nextArrow.classList.toggle('is-hidden', atEnd);
  }

  if (prevArrow && nextArrow && track) {
    // Initial state
    updateArrowState();

    track.addEventListener('scroll', updateArrowState, { passive: true });

    nextArrow.addEventListener('click', function () {
      var firstCard = track.querySelector('.da-card:not([style*="display: none"])');
      var scrollAmount = firstCard ? firstCard.clientWidth + 20 : 360;
      track.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    });
    prevArrow.addEventListener('click', function () {
      var firstCard = track.querySelector('.da-card:not([style*="display: none"])');
      var scrollAmount = firstCard ? firstCard.clientWidth + 20 : 360;
      track.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    });
  }
});
