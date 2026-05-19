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
        var type = document.querySelector('input[name="bookingType"]:checked');
        var t = type ? type.value : 'DAILY';
        var d = document.getElementById('dailyBlock');
        var l = document.getElementById('longBlock');
        d.style.display = t === 'DAILY' ? 'block' : 'none';
        l.style.display = t === 'LONGTERM' ? 'block' : 'none';
        setDisabled(d, t !== 'DAILY');
        setDisabled(l, t !== 'LONGTERM');
    }

    document.querySelectorAll('input[name="bookingType"]').forEach(function (el) {
        el.addEventListener('change', sync);
    });
    sync();
})();
