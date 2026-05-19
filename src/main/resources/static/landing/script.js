/**

 * DriveAway — Cinematic micro-interactions (vanilla JS)

 */

(function () {

  "use strict";



  document.documentElement.classList.add("js");



  const prefersReduced = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

  const finePointer = window.matchMedia("(pointer: fine)").matches;



  function initSmoothScroll() {

    document.querySelectorAll('a[href^="#"]').forEach(function (link) {

      link.addEventListener("click", function (e) {

        const id = link.getAttribute("href");

        if (!id || id === "#") return;

        const target = document.querySelector(id);

        if (!target) return;

        e.preventDefault();

        target.scrollIntoView({ behavior: prefersReduced ? "auto" : "smooth", block: "start" });

        closeMobileNav();

      });

    });

  }



  function markVisible(el, io) {

    const order = parseInt(el.getAttribute("data-reveal-order") || el.getAttribute("data-reveal-delay") || "0", 10);

    const delay = prefersReduced ? 0 : order * 80;

    setTimeout(function () {

      el.classList.add("is-visible");

    }, delay);

    if (io) io.unobserve(el);

  }



  function initReveal() {

    const els = document.querySelectorAll("[data-reveal]");

    if (!els.length) return;



    els.forEach(function (el) {

      el.classList.add("da-reveal");

    });



    const io = new IntersectionObserver(

      function (entries) {

        entries.forEach(function (entry) {

          if (!entry.isIntersecting) return;

          markVisible(entry.target, io);

        });

      },

      { threshold: 0.1, rootMargin: "0px 0px -8% 0px" }

    );



    els.forEach(function (el) {

      io.observe(el);

      const rect = el.getBoundingClientRect();

      if (rect.top < window.innerHeight * 0.92 && rect.bottom > 0) {

        markVisible(el, io);

      }

    });

  }



  function initTimelineProgress() {

    const line = document.querySelector(".timeline__progress");

    if (!line || prefersReduced) return;

    const section = document.querySelector(".timeline");

    if (!section) return;

    const io = new IntersectionObserver(

      function (entries) {

        entries.forEach(function (entry) {

          if (entry.isIntersecting) line.classList.add("is-visible");

        });

      },

      { threshold: 0.25 }

    );

    io.observe(section);

  }



  function initCounters() {

    const section = document.getElementById("stats");

    if (!section || prefersReduced) return;

    const values = section.querySelectorAll("[data-target]");

    let started = false;

    function animateValue(el) {

      const target = parseInt(el.getAttribute("data-target"), 10) || 0;

      const suffix = el.getAttribute("data-suffix") || "";

      const duration = 2200;

      const start = performance.now();

      function tick(now) {

        const t = Math.min((now - start) / duration, 1);

        const eased = 1 - Math.pow(1 - t, 3);

        el.textContent = Math.floor(eased * target).toLocaleString() + suffix;

        if (t < 1) requestAnimationFrame(tick);

        else el.textContent = target.toLocaleString() + suffix;

      }

      requestAnimationFrame(tick);

    }

    const io = new IntersectionObserver(

      function (entries) {

        if (started) return;

        entries.forEach(function (entry) {

          if (entry.isIntersecting) {

            started = true;

            values.forEach(animateValue);

            io.disconnect();

          }

        });

      },

      { threshold: 0.35 }

    );

    io.observe(section);

  }



  function initNav() {

    const nav = document.getElementById("nav");

    if (!nav) return;

    const progress = document.getElementById("nav-progress");

    let scrollTicking = false;



    function onScroll() {

      nav.classList.toggle("nav--scrolled", window.scrollY > 24);

      if (progress) {

        const h = document.documentElement.scrollHeight - window.innerHeight;

        const p = h > 0 ? Math.min(window.scrollY / h, 1) : 0;

        progress.style.transform = "scaleX(" + p + ")";

      }

      scrollTicking = false;

    }



    window.addEventListener("scroll", function () {

      if (!scrollTicking) {

        requestAnimationFrame(onScroll);

        scrollTicking = true;

      }

    }, { passive: true });

    onScroll();



    const toggle = document.getElementById("nav-toggle");

    const mobile = document.getElementById("nav-mobile");

    if (toggle && mobile) {

      toggle.addEventListener("click", function () {

        const open = toggle.classList.toggle("is-open");

        mobile.classList.toggle("is-open", open);

        toggle.setAttribute("aria-expanded", open ? "true" : "false");

      });

    }

  }



  function closeMobileNav() {

    const toggle = document.getElementById("nav-toggle");

    const mobile = document.getElementById("nav-mobile");

    if (toggle) {

      toggle.classList.remove("is-open");

      toggle.setAttribute("aria-expanded", "false");

    }

    if (mobile) mobile.classList.remove("is-open");

  }



  function parallaxOffset(el, speed) {

    const rect = el.getBoundingClientRect();

    const viewCenter = window.innerHeight * 0.5;

    const elCenter = rect.top + rect.height * 0.5;

    return (viewCenter - elCenter) * speed;

  }



  function initParallax() {

    if (prefersReduced) return;



    const dataLayers = document.querySelectorAll("[data-parallax]");

    const fleetImgs = document.querySelectorAll(".fleet-card__img img");

    if (!dataLayers.length && !fleetImgs.length) return;



    let ticking = false;



    function applyFleetTransform(img) {

      const card = img.closest(".fleet-card");

      const speed = 0.05;

      const y = parallaxOffset(img, speed);

      const scale = card && card.matches(":hover") ? 1.02 : 1;

      img.style.transform = "translate3d(0, " + y + "px, 0) scale(" + scale + ")";

    }



    function update() {

      dataLayers.forEach(function (el) {

        const speed = parseFloat(el.getAttribute("data-parallax")) || 0.05;

        const y = parallaxOffset(el, speed);

        el.style.transform = "translate3d(0, " + y + "px, 0)";

      });

      fleetImgs.forEach(applyFleetTransform);

      ticking = false;

    }



    window.addEventListener("scroll", function () {

      if (!ticking) {

        requestAnimationFrame(update);

        ticking = true;

      }

    }, { passive: true });



    document.querySelectorAll(".fleet-card").forEach(function (card) {

      card.addEventListener("mouseenter", function () {

        const img = card.querySelector(".fleet-card__img img");

        if (img) requestAnimationFrame(function () { applyFleetTransform(img); });

      });

      card.addEventListener("mouseleave", function () {

        const img = card.querySelector(".fleet-card__img img");

        if (img) requestAnimationFrame(function () { applyFleetTransform(img); });

      });

    });



    update();

  }



  function initHero3D() {

    const stage = document.querySelector(".hero__model-stage");

    if (!stage) return;



    const viewer = stage.querySelector(".hero__model-viewer");

    const cssCar = stage.querySelector(".hero__car-3d");

    const tiltWrap = stage.querySelector(".hero__car-3d__tilt");



    if (prefersReduced && viewer) {

      viewer.removeAttribute("auto-rotate");

    }



    function markLoaded() {

      stage.classList.add("is-loaded");

      if (cssCar) cssCar.setAttribute("aria-hidden", "true");

    }



    function setupViewer() {

      if (!viewer) return;

      viewer.addEventListener("load", markLoaded);

      viewer.addEventListener("error", function () {

        stage.classList.remove("is-loaded");

        if (cssCar) cssCar.removeAttribute("aria-hidden");

      });

      if (viewer.loaded) markLoaded();

    }



    if (window.customElements && customElements.whenDefined) {

      customElements.whenDefined("model-viewer").then(setupViewer).catch(setupViewer);

    } else {

      setupViewer();

    }



    if (prefersReduced || !finePointer || !tiltWrap) return;



    let ticking = false;

    let tx = 0;

    let ty = 0;



    function applyTilt() {

      tiltWrap.style.transform = "rotateX(" + (-ty * 10) + "deg) rotateY(" + (tx * 14) + "deg)";

      ticking = false;

    }



    stage.addEventListener("mousemove", function (e) {

      const rect = stage.getBoundingClientRect();

      tx = (e.clientX - rect.left) / rect.width - 0.5;

      ty = (e.clientY - rect.top) / rect.height - 0.5;

      if (!ticking) {

        requestAnimationFrame(applyTilt);

        ticking = true;

      }

    });



    stage.addEventListener("mouseleave", function () {

      tiltWrap.style.transform = "";

    });

  }



  function initHeroScene() {

    const scene = document.querySelector(".hero__showcase .hero__cinema--photo");

    if (!scene || prefersReduced || !finePointer) return;

    const img = scene.querySelector(".hero__cinema-img");

    if (!img) return;



    let ticking = false;

    let x = 0;

    let y = 0;



    function apply() {

      img.style.transform = "translate3d(" + (x * 8) + "px, " + (y * 6) + "px, 0) scale(1.02)";

      ticking = false;

    }



    scene.addEventListener("mousemove", function (e) {

      const rect = scene.getBoundingClientRect();

      x = (e.clientX - rect.left) / rect.width - 0.5;

      y = (e.clientY - rect.top) / rect.height - 0.5;

      if (!ticking) {

        requestAnimationFrame(apply);

        ticking = true;

      }

    });



    scene.addEventListener("mouseleave", function () {

      img.style.transform = "";

    });

  }



  function initMagneticHover() {

    if (prefersReduced || !finePointer) return;



    const targets = document.querySelectorAll(".btn--primary, .hero__model-stage, .hero__badge");

    if (!targets.length) return;



    function strengthFor(el) {

      if (el.classList.contains("hero__badge")) return 0.08;

      if (el.classList.contains("hero__model-stage")) return 0.06;

      return 0.15;

    }



    targets.forEach(function (el) {

      let rafId = 0;

      let px = 0;

      let py = 0;



      function apply() {

        rafId = 0;

        const rect = el.getBoundingClientRect();

        const cx = rect.left + rect.width * 0.5;

        const cy = rect.top + rect.height * 0.5;

        const s = strengthFor(el);

        const dx = (px - cx) * s;

        const dy = (py - cy) * s;

        el.style.transform = "translate3d(" + dx + "px, " + dy + "px, 0)";

      }



      function schedule() {

        if (!rafId) rafId = requestAnimationFrame(apply);

      }



      el.addEventListener("pointerenter", function () {

        el.classList.add("is-magnetic");

      });



      el.addEventListener("pointermove", function (e) {

        px = e.clientX;

        py = e.clientY;

        schedule();

      });



      el.addEventListener("pointerleave", function () {

        if (rafId) cancelAnimationFrame(rafId);

        rafId = 0;

        el.classList.remove("is-magnetic");

        el.style.transform = "";

      });

    });

  }



  function initFleetSpotlight() {

    if (prefersReduced || !finePointer) return;

    document.querySelectorAll(".fleet-card").forEach(function (card) {

      card.addEventListener("mousemove", function (e) {

        const rect = card.getBoundingClientRect();

        const x = ((e.clientX - rect.left) / rect.width) * 100;

        const y = ((e.clientY - rect.top) / rect.height) * 100;

        card.style.setProperty("--spot-x", x + "%");

        card.style.setProperty("--spot-y", y + "%");

      });

    });

  }



  function initCursorLight() {

    const overlay = document.getElementById("canvas-light");

    if (!overlay || prefersReduced || !finePointer) return;

    let visible = false;

    let rafId = 0;

    let x = 0;

    let y = 0;



    function apply() {

      overlay.style.setProperty("--cursor-x", x + "px");

      overlay.style.setProperty("--cursor-y", y + "px");

      rafId = 0;

    }



    document.addEventListener("mousemove", function (e) {

      if (!visible) {

        overlay.style.opacity = "1";

        visible = true;

      }

      x = e.clientX;

      y = e.clientY;

      if (!rafId) rafId = requestAnimationFrame(apply);

    });



    document.addEventListener("mouseleave", function () {

      overlay.style.opacity = "0";

      visible = false;

    });

  }



  function initTestimonials() {
    const ring = document.querySelector(".testimonials__ring");
    if (!ring) return;

    const cards = Array.from(ring.children);
    if (cards.length < 2) return;

    cards.forEach(function (card) {
      const clone = card.cloneNode(true);
      clone.setAttribute("aria-hidden", "true");
      ring.appendChild(clone);
    });

    if (!prefersReduced) {
      ring.classList.add("is-marquee");
    }
  }



  function initParticles() {

    const canvas = document.getElementById("particles");

    if (!canvas || prefersReduced) return;

    const ctx = canvas.getContext("2d");

    let w, h;

    const dots = [];

    const COUNT = 28;

    const LINK = 120;



    function resize() {

      w = canvas.width = window.innerWidth;

      h = canvas.height = window.innerHeight;

    }



    function createDots() {

      dots.length = 0;

      for (let i = 0; i < COUNT; i++) {

        dots.push({

          x: Math.random() * w,

          y: Math.random() * h,

          r: Math.random() * 1.2 + 0.4,

          vx: (Math.random() - 0.5) * 0.2,

          vy: (Math.random() - 0.5) * 0.2,

        });

      }

    }



    function draw() {

      ctx.clearRect(0, 0, w, h);

      for (let i = 0; i < dots.length; i++) {

        for (let j = i + 1; j < dots.length; j++) {

          const dx = dots[i].x - dots[j].x;

          const dy = dots[i].y - dots[j].y;

          const dist = Math.sqrt(dx * dx + dy * dy);

          if (dist < LINK) {

            ctx.beginPath();

            ctx.strokeStyle = "rgba(29, 29, 31, " + (0.08 * (1 - dist / LINK)) + ")";

            ctx.lineWidth = 0.5;

            ctx.moveTo(dots[i].x, dots[i].y);

            ctx.lineTo(dots[j].x, dots[j].y);

            ctx.stroke();

          }

        }

      }

      dots.forEach(function (d) {

        d.x += d.vx;

        d.y += d.vy;

        if (d.x < 0) d.x = w;

        if (d.x > w) d.x = 0;

        if (d.y < 0) d.y = h;

        if (d.y > h) d.y = 0;

        ctx.beginPath();

        ctx.arc(d.x, d.y, d.r, 0, Math.PI * 2);

        ctx.fillStyle = "rgba(29, 29, 31, 0.08)";

        ctx.fill();

      });

      requestAnimationFrame(draw);

    }



    resize();

    createDots();

    draw();

    window.addEventListener("resize", function () {

      resize();

      createDots();

    });

  }



  function initPageTransition() {

    if (prefersReduced) return;

    document.querySelectorAll('a[href^="/"]').forEach(function (link) {

      const href = link.getAttribute("href");

      if (!href || href.startsWith("/auth") || href.startsWith("/customer") || href.startsWith("/public")) return;

      if (["/features", "/how-it-works", "/fleet", "/testimonials", "/"].indexOf(href) === -1 && href !== "/index") return;

      link.addEventListener("click", function () {

        document.body.classList.add("is-transitioning");

      });

    });

  }



  function init() {

    initSmoothScroll();

    initReveal();

    initTimelineProgress();

    initCounters();

    initNav();

    initParallax();

    initHeroScene();

    initHero3D();

    initMagneticHover();

    initFleetSpotlight();

    initCursorLight();

    initTestimonials();

    initParticles();

    initPageTransition();

  }



  if (document.readyState === "loading") {

    document.addEventListener("DOMContentLoaded", init);

  } else {

    init();

  }

})();


