package app.backend;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/areaCheck"})
public class AreaCheckServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AreaCheckServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Boolean controllerAccess = (Boolean) req.getSession().getAttribute("controllerAccess");
        if (controllerAccess == null || !controllerAccess) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "сервлет принимает запросы только от контроллера");
            return;
        }

        try {
            AreaCheckResponse response = PointLogic.parseReq(req);
            logger.info("AreaCheckServlet: response: " + response);

            List<AreaCheckResponse> responses = PointsHistory.getPointsHistory(req.getSession());
            responses.add(response);
            req.setAttribute("result", response);
            req.setAttribute("history", responses);
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
        } catch (NumberFormatException | InvalidPointDataException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Один из элементов задан неккоректно");
        }
    }
}
