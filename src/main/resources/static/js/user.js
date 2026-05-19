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
        var type = document.querySelector('input[name="userType"]:checked');
        var t = type ? type.value : 'CUSTOMER';
        var c = document.getElementById('customerFields');
        var s = document.getElementById('staffFields');
        c.style.display = t === 'CUSTOMER' ? 'block' : 'none';
        s.style.display = t === 'STAFF' ? 'block' : 'none';
        setDisabled(c, t !== 'CUSTOMER');
        setDisabled(s, t !== 'STAFF');
    }

    document.querySelectorAll('input[name="userType"]').forEach(function (el) {
        el.addEventListener('change', sync);
    });
    sync();
})();
