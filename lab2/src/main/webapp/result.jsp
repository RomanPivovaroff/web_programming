<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>

    <link href="https://fonts.googleapis.com/css2?family=Russo+One&display=swap&subset=cyrillic" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@1,300..800&display=swap" rel="stylesheet">
    <link href="style.css" rel="stylesheet">
     <script>
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
        </script>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<header>
    <h2 id="header-left"> Лабораторная работа №1</h2>
    <h2 id="header-center">Веб-программирование</h2>
    <h2 id="header-right">Пивоваров Роман Николаевич P3231</h2>
</header>
<main>
    <div class="backgroundResult">
        <%
        app.backend.AreaCheckResponse result = (app.backend.AreaCheckResponse) request.getAttribute("result");
        if (result != null) {
        %>
        <h1 color="red">ТЫ - <%= (!result.getHit()) ? "НЕ " : "" %>ПОПАЛ!<h1>
        <div class="chart">
            <svg id="canvas" height="600" width="600" xmlns="http://www.w3.org/2000/svg" onclick="handleCanvasClick(event)">

                <line stroke="black" x1="0" x2="600" y1="300" y2="300"></line>
                <line stroke="black" x1="300" x2="300" y1="0" y2="600"></line>
                <polygon fill="black" points="300,0 288,30 312,30"></polygon>
                <polygon fill="black" points="600,300 570,312 570,288"></polygon>

                <line stroke="black" x1="425" x2="425" y1="310" y2="290"></line>
                <line stroke="black" x1="550" x2="550" y1="310" y2="290"></line>

                <line stroke="black" x1="50" x2="50" y1="310" y2="290"></line>
                <line stroke="black" x1="175" x2="175" y1="310" y2="290"></line>

                <line stroke="black" x1="290" x2="310" y1="175" y2="175"></line>
                <line stroke="black" x1="290" x2="310" y1="50" y2="50"></line>

                <line stroke="black" x1="290" x2="310" y1="425" y2="425"></line>
                <line stroke="black" x1="290" x2="310" y1="550" y2="550"></line>

                <text id = "r-half-x" x="420" y="280">R/2</text>
                <text id = "r-full-x" x="545" y="280">R</text>

                <text id = "r-half-neg-x" x="164" y="280">-R/2</text>
                <text id = "r-full-neg-x" x="60" y="280">-R</text>

                <text id = "r-half-y" x="320" y="180">R/2</text>
                <text id = "r-full-y" x="320" y="55">R</text>

                <text id = "r-half-neg-y" x="320" y="430">-R/2</text>
                <text id = "r-full-neg-y" x="320" y="555">-R</text>

                <text x="570" y="280">X</text>
                <text x="320" y="30">Y</text>


                <polygon points="300,300 425,300 300,550" fill-opacity="0.4" stroke="black" fill="blue"></polygon>

                <rect x="175" y="300" width="125" height="250" fill-opacity="0.4" stroke="black" fill="blue"></rect>

                <path d="M 300 300 L 300 175 A120,120 0 0,1 425,300 Z" fill-opacity="0.4" stroke="black" fill="blue"></path>

            </svg>
        </div>
    <script>
        updateChartLabels(<%=result.getR()%>)
        createPoint(<%=result.getX()%>, <%=result.getY()%>, <%=result.getR()%>, "blue")
    </script>
    <table class="table-check">
        <tr class="table-header">
            <th scope="col">X</th>
            <th scope="col">Y</th>
            <th scope="col">R</th>
            <th scope="col">Попадание</th>
            <th scope="col">Время работы</th>
            <th scope="col">Дата</th>
        </tr>
        <tr>
            <td><%= result.getX() %></td>
            <td><%= result.getY() %></td>
            <td><%= result.getR() %></td>
            <td><%= result.getHit() ? "попадание" : "промах" %></td>
            <td><%= result.getDuration() + " ns" %></td>
            <td><%= result.getDate()%></td>
        </tr>
        </table>
        <% } else { %>
        <p>История пуста...</p>
        <% } %>
        <a id="back" href="index.jsp">Назад</a>
    </div>
</main>
<footer>
    <div id="contacts">
        <div id="links">
            <a href="https://t.me/rnpivovarov">
                <img src="https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white">
            </a>
            <a href="https://vk.com/rnpivovarov">
                <img src="https://img.shields.io/badge/вконтакте-%232E87FB.svg?&style=for-the-badge&logo=vk&logoColor=white">
            </a>
            <a href="mailto:romain.pivovarov@yandex.ru">
                <img src="https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white">
            </a>
            <a href="mailto:roman.pivovarov@niuitmo.ru">
                <img src="https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white">
            </a>
        </div>
    </div>
</footer>
</body>
</html>