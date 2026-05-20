(function () {
  var qs = function (sel) { return document.querySelector(sel); };
  var closed = false;
  var prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  var tDom = 0;
  var minShowMs = prefersReducedMotion ? 0 : 700;
  var maxWaitMs = 1800;
  var pollHandle = null;
  var ultimateHandle = null;
  var revealTimer = null;

  function removeLoaderEl(loader) {
    if (loader && loader.parentNode) loader.parentNode.removeChild(loader);
  }

  function triggerReveal() {
    if (document.body.classList.contains('is-revealed')) return;
    document.body.classList.add('page-loaded', 'is-revealed');
  }

  /**
   * Stagger child index for elements inside data-reveal-step="4"
   * so the signal cards rise sequentially. Pure index assignment;
   * actual animation lives in CSS.
   */
  function setupStaggerIndices() {
    var groups = document.querySelectorAll('[data-reveal-step="4"]');
    Array.prototype.forEach.call(groups, function (group) {
      var children = group.children;
      for (var i = 0; i < children.length; i++) {
        children[i].style.setProperty('--idx', String(i));
      }
    });
  }

  function finishLoader() {
    if (closed) return;
    closed = true;
    window.Landing = window.Landing || {};
    window.Landing.loaderClosed = true;
    window.dispatchEvent(new CustomEvent('landing:loader-closed'));
    if (pollHandle) { window.clearInterval(pollHandle); pollHandle = null; }
    if (ultimateHandle) { window.clearTimeout(ultimateHandle); ultimateHandle = null; }

    setupStaggerIndices();

    var loader = qs('#page-loader');

    if (!loader) {
      triggerReveal();
      return;
    }

    if (prefersReducedMotion) {
      loader.classList.add('hide');
      triggerReveal();
      removeLoaderEl(loader);
      return;
    }

    // Phase 1: open the curtain (760ms) while loader inner fades out.
    loader.classList.add('is-opening');

    // Phase 2: at ~520ms — once the curtain is mostly retracted — release the hero reveal.
    revealTimer = window.setTimeout(triggerReveal, 520);

    // Phase 3: at ~820ms — fade the loader chrome itself out and remove it from the DOM.
    window.setTimeout(function () {
      loader.classList.add('hide');
    }, 760);

    var fallbackMs = 2200;
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

    setupStaggerIndices();

    var loader = qs('#page-loader');
    if (loader) {
      function enableSkip() {
        loader.style.cursor = 'pointer';
        loader.addEventListener('click', forceFinish);
      }
      function forceFinish(e) {
        if (e) e.preventDefault();
        var elapsed = performance.now() - tDom;
        if (elapsed >= minShowMs) finishLoader();
      }
      window.setTimeout(enableSkip, minShowMs);
    }

    pollHandle = window.setInterval(maybeFinish, 48);
    window.addEventListener('load', maybeFinish);
    ultimateHandle = window.setTimeout(finishLoader, 2200);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', boot);
  } else {
    boot();
  }
})();
