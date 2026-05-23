document.addEventListener('DOMContentLoaded', function () {
  var reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  if (reducedMotion) return;

  var depthLayers = Array.prototype.slice.call(document.querySelectorAll('[data-depth]'));
  var tiltCards = Array.prototype.slice.call(document.querySelectorAll('[data-tilt-card]'));
  var ticking = false;

  function updateDepth() {
    var scrollY = window.scrollY || 0;
    var viewportH = window.innerHeight || 1;
    var progress = Math.min(1.2, scrollY / viewportH);

    depthLayers.forEach(function (layer) {
      var depth = parseFloat(layer.getAttribute('data-depth')) || 0;
      layer.style.setProperty('--depth-y', (progress * depth * -180).toFixed(2) + 'px');
      layer.style.setProperty('--depth-rx', (progress * depth * 10).toFixed(2) + 'deg');
    });

    tiltCards.forEach(function (card, index) {
      var rect = card.getBoundingClientRect();
      var center = rect.top + rect.height / 2;
      var distance = (center - viewportH / 2) / viewportH;
      card.style.setProperty('--scroll-lift', (distance * -22 + (index % 2) * 6).toFixed(2) + 'px');
    });

    ticking = false;
  }

  function requestDepthUpdate() {
    if (ticking) return;
    ticking = true;
    window.requestAnimationFrame(updateDepth);
  }

  tiltCards.forEach(function (card) {
    card.addEventListener('mousemove', function (event) {
      var rect = card.getBoundingClientRect();
      var x = (event.clientX - rect.left) / rect.width - 0.5;
      var y = (event.clientY - rect.top) / rect.height - 0.5;
      card.style.setProperty('--tilt-y', (x * 7).toFixed(2) + 'deg');
      card.style.setProperty('--tilt-x', (y * -7).toFixed(2) + 'deg');
    });

    card.addEventListener('mouseleave', function () {
      card.style.setProperty('--tilt-y', '0deg');
      card.style.setProperty('--tilt-x', '0deg');
    });
  });

  updateDepth();
  window.addEventListener('scroll', requestDepthUpdate, { passive: true });
  window.addEventListener('resize', requestDepthUpdate);
});
