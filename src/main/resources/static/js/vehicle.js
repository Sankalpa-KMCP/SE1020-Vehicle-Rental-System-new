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
        var type = document.querySelector('input[name="vehicleType"]:checked');
        var t = type ? type.value : 'CAR';
        var car = document.getElementById('carFields');
        var moto = document.getElementById('motoFields');
        car.style.display = t === 'CAR' ? 'block' : 'none';
        moto.style.display = t === 'MOTORCYCLE' ? 'block' : 'none';
        setDisabled(car, t !== 'CAR');
        setDisabled(moto, t !== 'MOTORCYCLE');
    }

    document.querySelectorAll('input[name="vehicleType"]').forEach(function (el) {
        el.addEventListener('change', sync);
    });
    sync();
})();
