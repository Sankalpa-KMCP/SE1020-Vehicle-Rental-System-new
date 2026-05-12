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
        var type = document.querySelector('input[name="reviewType"]:checked');
        var t = type ? type.value : 'CUSTOMER';
        var c = document.getElementById('customerReviewBlock');
        var s = document.getElementById('staffReplyBlock');
        c.style.display = t === 'CUSTOMER' ? 'block' : 'none';
        s.style.display = t === 'STAFF_REPLY' ? 'block' : 'none';
        setDisabled(c, t !== 'CUSTOMER');
        setDisabled(s, t !== 'STAFF_REPLY');
    }

    document.querySelectorAll('input[name="reviewType"]').forEach(function (el) {
        el.addEventListener('change', sync);
    });
    sync();
})();
