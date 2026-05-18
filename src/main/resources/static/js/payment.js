(function () {
    function setDisabled(container, disabled) {
        if (!container) {
            return;
        }
        container.querySelectorAll('input,select,textarea').forEach(function (el) {
            el.disabled = disabled;
        });
    }

    function sync() {
        var type = document.querySelector('input[name="paymentType"]:checked');
        var t = type ? type.value : 'CARD';
        var card = document.getElementById('cardBlock');
        var cash = document.getElementById('cashBlock');
        card.style.display = t === 'CARD' ? 'block' : 'none';
        cash.style.display = t === 'CASH' ? 'block' : 'none';
        setDisabled(card, t !== 'CARD');
        setDisabled(cash, t !== 'CASH');
    }

    document.querySelectorAll('input[name="paymentType"]').forEach(function (el) {
        el.addEventListener('change', sync);
    });
    sync();
})();
