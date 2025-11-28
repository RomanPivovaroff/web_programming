package app.backend;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

public class PointsHistory {
    public static List<AreaCheckResponse> getPointsHistory(HttpSession session) {
        List<AreaCheckResponse> history = (List<AreaCheckResponse>) session.getAttribute("history");
        if (history == null) {
            history = new ArrayList<>();
            session.setAttribute("history", history);
        }
        return history;
    }
    public static void clearPointsHistory(HttpSession session) {
        session.removeAttribute("history");
    }
}
