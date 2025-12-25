package app.backend;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/clear"})
public class PointsHistoryClearServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(PointsHistoryClearServlet.class.getName());

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        PointsHistory.clearPointsHistory(req.getSession());
        try {
            logger.info("HttpServletRequest: " + "история сброшена");
            resp.sendRedirect(req.getContextPath() + "/result.jsp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
