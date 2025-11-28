package app.backend;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/areaCheck"})
public class AreaCheckServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AreaCheckServlet.class.getName());

    private boolean checkHit(int x, double y, double r) {
        // четверть круга
        if (x >= 0 && y >= 0) {
            return x * x + y * y <= r * r;
        }
        // прямоугольник
        if (x <= 0 && y <= 0) {
            return (x >= -r / 2 && y >= -r);
        }
        // треугольник
        if (x >= 0 && y <= 0) {
            return (x * x + y * y <= ((r * r) / 4));
        }
        return false;
    }
    private boolean validate(int x, double y, double r) {
        return Arrays.asList(-4, -3, -2, -1, 0, 1, 2, 3, 4).contains(x) &&
                r >= 1 && r <= 4 &&
                y > -3 && y < 3;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            int x = Integer.parseInt(req.getParameter("x"));
            double y = Double.parseDouble(req.getParameter("y"));
            double r = Double.parseDouble(req.getParameter("r"));
            if (!validate(x, y, r)) {
                throw new InvalidPointDataException("Один из элементов задан неккоректно");
            }
            boolean hit = checkHit(x, y, r);

            AreaCheckResponse response = new AreaCheckResponse();
            response.setX(x);
            response.setY(y);
            response.setR(r);
            response.setHit(hit);
            logger.info("AreaCheckServlet: response: " + response.toString());
            List<AreaCheckResponse> responses = PointsHistory.getPointsHistory(req.getSession());
            responses.add(response);
            req.setAttribute("result", response);
            req.setAttribute("history", responses);
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
        } catch (NumberFormatException | InvalidPointDataException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Один из элементов задан неккоректно");
        } catch (NullPointerException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "один из элементов не обнаружен");
        }
    }
}
