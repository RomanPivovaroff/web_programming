let R = 1;

function saveR(x) {
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

    saveTable()
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

    const prams = new URLSearchParams({ "x": x, "y": y, "r": R});

    try {
        const response = await fetch(`/fcgi-bin/app-freebsd?${prams}`, {method: "GET"});

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

function saveTable() {
    const table = document.querySelector(".table-check");
    const rows = [];

    for (let i = 1; i < table.rows.length; i++) {
        const row = table.rows[i];
        const rowData = {
            x: row.cells[0].textContent,
            y: row.cells[1].textContent,
            r: row.cells[2].textContent,
            hit: row.cells[3].textContent === "Попал",
            date: row.cells[4].textContent,
            execTime: row.cells[5].textContent.replace(" ns", "")
        };
        rows.push(rowData);
    }

    localStorage.setItem('savedTableRows', JSON.stringify(rows));
}

async function loadTable() {
    let rows = [];
    try {
        const response = await fetch('/points/get');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const points = await response.json();
        rows = points;
    } catch (err) {
        console.error('Ошибка при загрузке точек:', err);
    }
    if (rows) {

        const table = document.querySelector(".table-check");
        while (table.rows.length > 1) {
            table.deleteRow(1);
        }
        rows.forEach(rowData => {
            addTableRow(
                rowData.X,
                rowData.Y,
                rowData.R,
                rowData.Hit,
                rowData.Date,
                rowData.ProgramWorkTime
            );
        });
    }
}

async function clearTable() {
    try {
        const response = await fetch('points/clear');

        if (!response.ok) {
          console.log(`Ошибка сервера! Статус: ${response.status}`);
        }

        const data = await response.json();

        if (!data.Result) {
          console.log("Ошибка при очистке: " + data.Error);
        }
        localStorage.removeItem('savedTableRows');
        const table = document.querySelector(".table-check");
        while (table.rows.length > 1) {
            table.deleteRow(1);
        }
        alert("Таблица очищена");
      } catch (err) {
        console.error('Ошибка при очистке точек:', err);
        alert("Ошибка при очистке точек");
      }
}

document.addEventListener('DOMContentLoaded', loadTable);