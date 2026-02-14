function getX() {
    const xButtons = document.querySelectorAll('.x-button.selected');
    if (xButtons.length > 0) {
        return parseFloat(xButtons[0].value);
    }
    return null;
}

function getY() {
    const yInput = document.querySelector('[id$="y"]');
    if (!yInput) return null;

    const yVal = yInput.value.trim();
    if (!/^[-+]?\d*(\.\d+)?$/.test(yVal) || yVal === "" || yVal === "." || yVal === "-" || yVal === "+") {
        return null;
    }
    const yNum = parseFloat(yVal);
    return isNaN(yNum) ? null : yNum;
}

function getR() {
    const spinner = document.querySelector('.ui-spinner-input');
    if (!spinner) {
        console.error("Спиннер не найден! Проверьте селектор");
        return null;
    }
    const val = parseFloat(spinner.value);
    return isNaN(val) ? null : val;
}

function validateUserInput() {
    const x = getX();
    const y = getY();
    const r = getR();

    if (x == null) {
        alert("Выберите значение X");
        return false;
    }
    if (y == null || y <= -5 || y >= 5) {
        alert("Y должен быть числом от -5 до 5");
        return false;
    }
    if (r == null || r < 1 || r > 3) {
        alert("R должен быть числом от 1 до 3");
        return false;
    }
    return true;
}

function validateInput(x, y, r) {
    if (x == null || x < -5 || x > 3) {
        alert("X должен быть от -5 до 3");
        return false;
    }
    if (y == null || y < -5 || y > 5) {
        alert("Y должен быть от -5 до 5");
        return false;
    }
    if (r == null || r < 1 || r > 3) {
        alert("R должен быть от 1 до 3");
        return false;
    }
    return true;
}

function handleCanvasClick(event) {
    const r = getR();
    if (!r) {
        alert("Сначала выберите радиус R");
        return;
    }

    const canvas = document.getElementById("canvas");
    const rect = canvas.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;

    const centerX = canvas.width.baseVal.value / 2;
    const centerY = canvas.height.baseVal.value / 2;
    const scale = 250 / r;

    const realX = (x - centerX) / scale;
    const realY = -((y - centerY) / scale);

    if (validateInput(realX, realY, r)) {
        if (typeof PrimeFaces !== 'undefined') {
            PrimeFaces.ab({
                source: 'inputForm:canvasButton',
                process: '@this',
                params: [
                    {name: 'canvasX', value: realX},
                    {name: 'canvasY', value: realY},
                    {name: 'canvasR', value: r}
                ],
                update: 'tableCheck historyMessage paginationInfo'
            });
        }
    }
}

function updateChartFromSpinner(data) {
    if (data.status === 'success') {
        const r = getR();
        if (r) {
            updateChartLabels(r);
            redrawAllPoints();
        }
    }
}

function updateChartLabels(R) {
    const half = (R/2).toFixed(1);
    document.getElementById('r-half-x').textContent = half;
    document.getElementById('r-full-x').textContent = R.toString();
    document.getElementById('r-full-neg-x').textContent = (-R).toString();
    document.getElementById('r-half-neg-x').textContent = (-R/2).toFixed(1);

    document.getElementById('r-half-y').textContent = half;
    document.getElementById('r-full-y').textContent = R.toString();
    document.getElementById('r-half-neg-y').textContent = (-R/2).toFixed(1);
    document.getElementById('r-full-neg-y').textContent = (-R).toString();
}

function redrawAllPoints() {
    const svg = document.getElementById("canvas");
    const oldPoints = svg.querySelectorAll('.dynamic-point');
    oldPoints.forEach(p => p.remove());
    const r = getR();
    if (!r) return;
    const table = document.getElementById('tableCheck');
    if (!table) return;

    const rows = table.querySelectorAll('tr:not(.table-header)');
    rows.forEach(row => {
        const cells = row.querySelectorAll('td');
        if (cells.length >= 4) {
            const x = parseFloat(cells[0].textContent);
            const y = parseFloat(cells[1].textContent);
            const hit = cells[3].textContent.includes('Попадание');
            createPoint(x, y, r, hit ? 'green' : 'red');
        }
    });
}

function createPoint(x, y, r, color) {
    const svg = document.getElementById("canvas");
    const scale = 250 / r;
    const cx = 300 + x * scale;
    const cy = 300 - y * scale;

    const point = document.createElementNS("http://www.w3.org/2000/svg", "circle");
    point.setAttribute("cx", cx);
    point.setAttribute("cy", cy);
    point.setAttribute("r", "6");
    point.setAttribute("fill", color);
    point.setAttribute("stroke", "white");
    point.setAttribute("stroke-width", "2");
    point.setAttribute("class", "dynamic-point");

    svg.appendChild(point);
}

function afterCheck(data) {
    if (data.status === 'complete') {
        redrawAllPoints();
    }
}


document.addEventListener('DOMContentLoaded', function() {
    setTimeout(() => {
        const r = getR();
        if (r) {
            updateChartLabels(r);
            redrawAllPoints();
        }
    }, 500);
});