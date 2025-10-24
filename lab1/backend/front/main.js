"use strict";
let R = 1;

function SaveR(x) {
    R = x;
}

function getX() {
    const xInput = document.getElementById("x");
    return xInput ? parseFloat(xInput.value) : null;
}

function getRawY() {
    return document.getElementById("y").value.trim();
}

function getY() {
    const yVal = document.getElementById("y").value.trim();
    if (!/^[-+]?\d*(\.\d+)?$/.test(yVal) || yVal === "" || yVal === "." || yVal === "-" || yVal === "+") {
        return null;
    }
    const yNum = parseFloat(yVal);
    return isNaN(yNum) ? null : yNum;
}

function validateUserInput(x, y, r) {
    return (
        x !== null &&
        r !== null &&
        y !== null &&
        y > -3 &&
        y < 3
    );
}

function addTableRow(x, y, r, hit, date, execTime) {
    const table = document.querySelector(".table-check");

    const row = table.insertRow(-1);
    row.insertCell(0).textContent = x;
    row.insertCell(1).textContent = y;
    row.insertCell(2).textContent = r;
    row.insertCell(3).textContent = hit ? "Попал" : "Не попал";
    row.insertCell(4).textContent = date;
    row.insertCell(5).textContent = execTime + " ns";
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

function movePoint(x, y, r) {
    const point = document.getElementById("point");
    const scale = 250 / r;
    const cx = 300 + x * scale;
    const cy = 300 - y * scale;

    point.setAttribute("cx", cx);
    point.setAttribute("cy", cy);
    point.setAttribute("visibility", "visible");
}

async function submitToBackend() {
    const x = getX();
    const y = getY();
    const rawY = getRawY();

    if (!validateUserInput(x, y, R)) {
        alert("Введите корректные значения:\nY должно быть числом от -3 до 3.");
        return;
    }

    const data = { x, y, R};

    try {
        const response = await fetch("/fcgi-bin/app-freebsd", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        if (!response.ok) throw new Error(`Ошибка сервера: ${response.status}`);

        const raw = await response.text();
        console.log("Сырой ответ сервера:", raw);
        const parsed = JSON.parse(raw);
        movePoint(x, y, R)
        updateChartLabels(R)
        addTableRow(
            x,
            rawY,
            R,
            parsed.result,
            parsed.current_time,
            parsed.uptime
        );

    } catch (err) {
        console.error("Запрос не выполнен:", err);
        alert("Произошла ошибка при отправке данных на сервер.");
    }
}