function getX() {
    const xInput = document.getElementById("x");
    return xInput ? parseFloat(xInput.value) : null;
}


function getY() {
    const yVal = document.getElementById("y").value.trim();
    if (!/^[-+]?\d*(\.\d+)?$/.test(yVal) || yVal === "" || yVal === "." || yVal === "-" || yVal === "+") {
        return null;
    }
    const yNum = parseFloat(yVal);
    return isNaN(yNum) ? null : yNum;
}

function getR() {
    const rVal = document.getElementById("r").value.trim();
    if (!/^[-+]?\d*(\.\d+)?$/.test(rVal) || rVal === "" || rVal === "." || rVal === "-" || rVal === "+") {
        return null;
    }
    const rNum = parseFloat(rVal);
    return isNaN(rNum) ? null : rNum;
}

function validateUserInput() {
    const x = getX();
    const y = getY();
    const r = getR();
    return validateInput(x, y, r);
}

function validateInput(x, y, r) {
    if  (x == null) {
        alert("X должен быть целым числом от -4 до 4");
        return false;
    }
    if (y == null || y <= -3 || y >= 3) {
            alert("Y должен быть целым числом от -3 до 3");
            return false;
        }
    if (r == null || r < 1 || r > 4) {
        alert("R должен быть числом от 1 до 4");
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
    if (!validateInput(0, 0, r)) {return;};
    updateChartLabels(r);
    const canvas = document.getElementById("canvas");
    const rect = canvas.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    console.log(x, y)

    const centerX = canvas.width.baseVal.value / 2;
    const centerY = canvas.height.baseVal.value / 2;
    const scale = 250 / r;
    console.log(centerX, centerY)

    const realX = (x - centerX) / scale;
    const realY = -((y - centerY) / scale);
     if (validateInput(realX, realY, r)) {
        const url = new URL('/lab-2/web2', window.location.origin);
        url.searchParams.set('x', realX);
        url.searchParams.set('y', realY);
        url.searchParams.set('r', r);

        window.location.href = url.toString();
      }
}

function updateChartLabels(R) {
    document.getElementById('r-half-x').textContent = (R/2).toFixed(3);
    document.getElementById('r-full-x').textContent = R.toString();
    document.getElementById('r-full-neg-x').textContent = (-R).toString();
    document.getElementById('r-half-neg-x').textContent = (-R/2).toFixed(3);

    document.getElementById('r-half-y').textContent = (R/2).toFixed(3);
    document.getElementById('r-full-y').textContent = R.toString();
    document.getElementById('r-half-neg-y').textContent = (-R/2).toFixed(3);
    document.getElementById('r-full-neg-y').textContent = (-R).toString();
}

function createPoint(x, y, r, color) {
    if (r != getR()) {
        return;
    };
    const svg = document.getElementById("canvas");
    const scale = 250 / r;
    const cx = 300 + x * scale;
    const cy = 300 - y * scale;

    const point = document.createElementNS("http://www.w3.org/2000/svg", "circle");
    point.setAttribute("cx", cx);
    point.setAttribute("cy", cy);
    point.setAttribute("r", "8");
    point.setAttribute("fill", color);
    point.setAttribute("stroke", "white");

    svg.appendChild(point);
}
