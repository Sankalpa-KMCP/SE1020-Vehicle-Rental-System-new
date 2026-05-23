(function () {
  'use strict';

  function initDigitCounters() {
    document.querySelectorAll('input[data-field-counter]').forEach(function (input) {
      if (input.dataset.counterBound === '1') {
        return;
      }
      input.dataset.counterBound = '1';

      var max = parseInt(input.getAttribute('data-field-counter'), 10);
      if (!max || max < 1) {
        max = 10;
      }

      var counter = document.createElement('p');
      counter.className = 'field-digit-counter';
      counter.setAttribute('aria-live', 'polite');
      input.insertAdjacentElement('afterend', counter);

      function sanitize() {
        var digits = (input.value || '').replace(/\D/g, '');
        if (digits.length > max) {
          digits = digits.slice(0, max);
        }
        if (input.value !== digits) {
          input.value = digits;
        }
        return digits.length;
      }

      function render() {
        var len = sanitize();
        counter.textContent = len + ' / ' + max;
        counter.classList.remove('field-digit-counter--complete', 'field-digit-counter--partial', 'field-digit-counter--empty');
        if (len === max) {
          counter.classList.add('field-digit-counter--complete');
        } else if (len > 0) {
          counter.classList.add('field-digit-counter--partial');
        } else {
          counter.classList.add('field-digit-counter--empty');
        }
      }

      input.addEventListener('input', render);
      input.addEventListener('blur', render);
      render();
    });
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initDigitCounters);
  } else {
    initDigitCounters();
  }
})();
