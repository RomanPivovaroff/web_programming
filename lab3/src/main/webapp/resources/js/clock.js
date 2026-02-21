
function updateTime() {
    var date = new Date(),
        h = date.getHours(),
        m = date.getMinutes(),
        s = date.getSeconds();

    h = (h < 10) ? '0' + h : h;
    m = (m < 10) ? '0' + m : m;
    s = (s < 10) ? '0' + s : s;

    var hours = document.querySelector('[id$="hourComponent"]');
        var minutes = document.querySelector('[id$="minuteComponent"]');
        var seconds = document.querySelector('[id$="secondComponent"]');

    if (hours) hours.innerHTML = h;
    if (minutes) minutes.innerHTML = m;
    if (seconds) seconds.innerHTML = s;
}

document.addEventListener('DOMContentLoaded', function() {
    updateTime();
    setInterval(updateTime, 5000);
    if (typeof jsf !== 'undefined') {
        jsf.ajax.addOnEvent(function(data) {
            if (data.status === 'success') {
                setTimeout(updateTime, 50);
            }
        });
    }
});