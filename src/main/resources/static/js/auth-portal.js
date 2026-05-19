(function () {
  'use strict';

  function prefersReducedMotion() {
    return window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  }

  function artPanelActive() {
    return window.matchMedia && window.matchMedia('(min-width: 1024px)').matches;
  }

  function Mascot(id, weight, maxRotation, baseZ) {
    this.body = document.getElementById(id);
    if (!this.body) {
      this.eyes = null;
      this.eyeDots = [];
      return;
    }
    this.eyes = this.body.querySelector('.mascot-eyes');
    this.eyeDots = this.body.querySelectorAll('.eye-dot');
    this.weight = weight;
    this.maxRotation = maxRotation;
    this.baseZ = baseZ;
    this.currentX = 0;
    this.currentY = 0;
    this.targetX = 0;
    this.targetY = 0;
    this.scale = 1;
  }

  Mascot.prototype.lookAt = function (x, y) {
    this.targetX = x;
    this.targetY = y;
  };

  Mascot.prototype.setSquint = function (squint) {
    if (!this.eyeDots || !this.eyeDots.length) return;
    for (var i = 0; i < this.eyeDots.length; i++) {
      var dot = this.eyeDots[i];
      if (squint) {
        dot.style.transform = 'scaleY(0.15) translateY(2px)';
        dot.style.animation = 'none';
      } else {
        dot.style.transform = '';
        dot.style.animation = '';
      }
    }
  };

  Mascot.prototype.update = function () {
    if (!this.body || !this.eyes) return;
    this.currentX += (this.targetX - this.currentX) * this.weight;
    this.currentY += (this.targetY - this.currentY) * this.weight;
    var moveX = this.currentX * 25;
    var moveY = this.currentY * 25;
    var rotX = this.currentY * -(this.maxRotation * 1.5);
    var rotY = this.currentX * (this.maxRotation * 1.5);
    this.body.style.transform =
      'translate3d(' + moveX + 'px, ' + moveY + 'px, ' + this.baseZ + 'px) ' +
      'rotateX(' + rotX + 'deg) rotateY(' + rotY + 'deg) ' +
      'scale(' + this.scale + ')';
    this.eyes.style.transform =
      'translateZ(45px) translate(' + this.currentX * 30 + 'px, ' + this.currentY * 30 + 'px)';
  };

  function bindTransitionLinks() {
    document.querySelectorAll('a.transition-link').forEach(function (link) {
      link.addEventListener('click', function (e) {
        var href = link.getAttribute('href') || '';
        if (href.indexOf('auth/login') >= 0 || href.indexOf('auth/register') >= 0 || href.indexOf('auth/staff-login') >= 0 || href === '/' || href.endsWith('/') || href.indexOf('admin-dashboard') >= 0 || href.indexOf('admin-home') >= 0) {
          e.preventDefault();
          document.body.classList.remove('page-loaded');
          setTimeout(function () {
            window.location.href = href;
          }, 150);
        }
      });
    });
  }

  function initPageFade() {
    window.addEventListener('load', function () {
      document.body.classList.add('page-loaded');
    });
    try {
      var ref = document.referrer || '';
      if (ref.indexOf('auth/login') >= 0 || ref.indexOf('auth/register') >= 0 || ref.indexOf('auth/staff-login') >= 0) {
        document.body.classList.remove('page-loaded');
        requestAnimationFrame(function () {
          document.body.classList.add('page-loaded');
        });
      }
    } catch (ignored) {}
  }

  function initMascots(mode) {
    var reduced = prefersReducedMotion();
    var artContainer = document.getElementById('art-container');
    if (!artContainer) return;

    var tallBlackGuy = new Mascot('mascot-tall-black', 0.015, 10, -20);
    var bossGuy = new Mascot('mascot-boss', 0.03, 15, 0);
    var hijabGirl = new Mascot('mascot-hijab', 0.08, 22, 5);
    var guardCyanGuy = new Mascot('mascot-guard-cyan', 0.05, 20, 10);
    var rookieGuy = new Mascot('mascot-rookie', 0.12, 30, 20);
    var yellowGirl = new Mascot('mascot-yellow-girl', 0.18, 40, 25);
    var mascots = [tallBlackGuy, bossGuy, hijabGirl, guardCyanGuy, rookieGuy, yellowGirl];

    var passwordInput = document.getElementById('passwordInput');
    var passwordWrapper = document.getElementById('passwordWrapper');
    var toggleBtn = document.getElementById('togglePasswordBtn');
    var propShield = document.getElementById('prop-shield');

    if (!passwordInput || !passwordWrapper || !toggleBtn || !propShield) return;

    var usernameInput = document.getElementById('usernameInput');
    var inspectableInputs = document.querySelectorAll('.inspectable-input');

    var globalMouseX = mode === 'register' ? -0.5 : 0.5;
    var globalMouseY = 0;
    var activeState = 'idle';
    var isHoveringPassword = false;

    if (document.querySelector('.auth-error-alert')) {
      activeState = 'sad';
      mascots.forEach(function (m) {
        m.lookAt(0, 0.8);
        m.setSquint(true);
      });
    }

    passwordWrapper.addEventListener('mouseenter', function () {
      isHoveringPassword = true;
      evaluateState();
    });
    passwordWrapper.addEventListener('mouseleave', function () {
      isHoveringPassword = false;
      evaluateState();
    });

    toggleBtn.addEventListener('click', function () {
      var isHidden = passwordInput.type === 'password';
      passwordInput.type = isHidden ? 'text' : 'password';
      toggleBtn.classList.toggle('ph-eye');
      toggleBtn.classList.toggle('ph-eye-closed');
      evaluateState();
    });

    function evaluateState() {
      if (activeState === 'sad') return;

      propShield.classList.remove('show-prop');
      artContainer.classList.remove('alert-mode');
      mascots.forEach(function (m) {
        m.scale = 1;
        m.setSquint(false);
      });

      if (mode === 'login') {
        if (document.activeElement === usernameInput) {
          activeState = 'inspecting';
          updateTypingTrackLogin();
        } else if (document.activeElement === passwordInput || isHoveringPassword) {
          if (passwordInput.type === 'password') {
            activeState = 'shielding';
            propShield.classList.add('show-prop');
            guardCyanGuy.scale = 1.1;
            tallBlackGuy.lookAt(-1, -1);
            bossGuy.lookAt(-0.8, -0.8);
            hijabGirl.lookAt(-0.5, 0);
            guardCyanGuy.lookAt(0.6, 0);
            rookieGuy.lookAt(0.8, 0.5);
            yellowGirl.lookAt(0, 0.9);
          } else {
            activeState = 'peeking';
            artContainer.classList.add('alert-mode');
            mascots.forEach(function (m) {
              m.scale = 1.05;
              m.lookAt(0.7, 0.2);
              m.setSquint(true);
            });
          }
        } else {
          activeState = 'idle';
          mascots.forEach(function (m) {
            m.lookAt(globalMouseX, globalMouseY);
          });
        }
      } else {
        var isInspecting = false;
        inspectableInputs.forEach(function (el) {
          if (document.activeElement === el) isInspecting = true;
        });
        if (isInspecting) {
          activeState = 'inspecting';
          updateTypingTrackRegister();
        } else if (document.activeElement === passwordInput || isHoveringPassword) {
          if (passwordInput.type === 'password') {
            activeState = 'shielding';
            propShield.classList.add('show-prop');
            guardCyanGuy.scale = 1.1;
            tallBlackGuy.lookAt(1, -1);
            bossGuy.lookAt(0.8, -0.8);
            hijabGirl.lookAt(0.5, 0);
            guardCyanGuy.lookAt(-0.6, 0);
            rookieGuy.lookAt(0.8, 0.5);
            yellowGirl.lookAt(0.9, 0.9);
          } else {
            activeState = 'peeking';
            artContainer.classList.add('alert-mode');
            mascots.forEach(function (m) {
              m.scale = 1.05;
              m.lookAt(-0.7, 0.2);
              m.setSquint(true);
            });
          }
        } else {
          activeState = 'idle';
          mascots.forEach(function (m) {
            m.lookAt(globalMouseX, globalMouseY);
          });
        }
      }
    }

    function updateTypingTrackLogin() {
      if (activeState !== 'inspecting' || !usernameInput) return;
      var typeProgress = Math.min(usernameInput.value.length / 15, 1);
      var lookX = 0.3 + typeProgress * 0.4;
      mascots.forEach(function (m) {
        m.lookAt(lookX, 0.1);
      });
    }

    function updateTypingTrackRegister() {
      if (activeState !== 'inspecting') return;
      var activeEl = document.activeElement;
      var len = (activeEl && activeEl.value) ? activeEl.value.length : 0;
      var typeProgress = Math.min(len / 20, 1);
      var lookX = -0.6 + typeProgress * 0.4;
      mascots.forEach(function (m) {
        m.lookAt(lookX, 0.1);
      });
    }

    if (mode === 'login' && usernameInput) {
      usernameInput.addEventListener('focus', evaluateState);
      usernameInput.addEventListener('blur', evaluateState);
      usernameInput.addEventListener('input', updateTypingTrackLogin);
      usernameInput.addEventListener(
        'input',
        function () {
          if (activeState === 'sad') {
            activeState = 'idle';
            evaluateState();
          }
        },
        { once: true }
      );
    }

    if (mode === 'register') {
      inspectableInputs.forEach(function (input) {
        input.addEventListener('focus', evaluateState);
        input.addEventListener('blur', evaluateState);
        input.addEventListener('input', updateTypingTrackRegister);
        input.addEventListener(
          'input',
          function () {
            if (activeState === 'sad') {
              activeState = 'idle';
              evaluateState();
            }
          },
          { once: true }
        );
      });
    }

    passwordInput.addEventListener('focus', evaluateState);
    passwordInput.addEventListener('blur', evaluateState);
    passwordInput.addEventListener(
      'input',
      function () {
        if (activeState === 'sad') {
          activeState = 'idle';
          evaluateState();
        }
      },
      { once: true }
    );

    document.addEventListener('mousemove', function (e) {
      globalMouseX = (e.clientX / window.innerWidth) * 2 - 1;
      globalMouseY = (e.clientY / window.innerHeight) * 2 - 1;
      if (activeState === 'idle') {
        mascots.forEach(function (m) {
          m.lookAt(globalMouseX, globalMouseY);
        });
      }
    });

    function renderLoop() {
      var wide = artPanelActive();
      if (!document.hidden && wide) {
        mascots.forEach(function (m) {
          m.update();
        });
      } else if (!wide) {
        mascots.forEach(function (m) {
          if (m.body) m.body.style.transform = '';
          if (m.eyes) m.eyes.style.transform = '';
        });
      }
      requestAnimationFrame(renderLoop);
    }

    if (!reduced) {
      requestAnimationFrame(renderLoop);
    }
  }

  function initLoginForm() {
    var form = document.querySelector('form#loginForm');
    var successOverlay = document.getElementById('success-overlay');
    if (!form || !successOverlay) return;

    form.addEventListener('submit', function (e) {
      if (!form.checkValidity()) return;
      if (prefersReducedMotion()) return;
      e.preventDefault();

      var submitBtn = form.querySelector('button[type="submit"]');
      if (submitBtn) submitBtn.disabled = true;

      var successNavScheduled = false;

      fetch(form.action, {
        method: 'POST',
        body: new FormData(form),
        credentials: 'same-origin',
        redirect: 'follow',
      })
        .then(function (res) {
          var finalUrl = res.url || '';
          var nextUrl;
          try {
            nextUrl = new URL(finalUrl, window.location.origin);
          } catch (err) {
            window.location.assign('/auth/login?error=1');
            return;
          }
          var path = nextUrl.pathname || '';
          var dest = nextUrl.pathname + nextUrl.search + nextUrl.hash;
          if (path.indexOf('/auth/login') === 0 && nextUrl.search.indexOf('error=1') >= 0) {
            window.location.assign(dest);
            return;
          }
          if (path.indexOf('/auth/login') === 0) {
            window.location.assign(dest);
            return;
          }
          if (path === '/dashboard' || path.indexOf('/dashboard/') === 0) {
            successNavScheduled = true;
            successOverlay.classList.add('active');
            setTimeout(function () {
              window.location.assign(dest);
            }, 750);
            return;
          }
          window.location.assign(dest);
        })
        .catch(function () {
          window.location.assign('/auth/login?error=1');
        })
        .finally(function () {
          if (!successNavScheduled && submitBtn) submitBtn.disabled = false;
        });
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    var body = document.body;
    var mode = body.getAttribute('data-auth-mode') || 'login';
    body.classList.add('auth-portal-body');

    bindTransitionLinks();
    initPageFade();

    if (prefersReducedMotion()) {
      body.classList.add('page-loaded');
    }

    initMascots(mode === 'register' ? 'register' : 'login');

    if (mode === 'login') {
      initLoginForm();
    }
  });
})();
