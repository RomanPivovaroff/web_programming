package app.backend;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PointLogic {
    public static boolean checkHit(BigDecimal x, BigDecimal y, BigDecimal r) {
        // четверть круга
        if (x.compareTo(BigDecimal.ZERO) >= 0 && y.compareTo(BigDecimal.ZERO) >= 0) {
            BigDecimal xSquared = x.multiply(x);
            BigDecimal ySquared = y.multiply(y);
            BigDecimal rSquared = r.multiply(r).divide(new BigDecimal(4), RoundingMode.HALF_UP);
            return xSquared.add(ySquared).compareTo(rSquared) <= 0;
        }

        // прямоугольник
        if (x.compareTo(BigDecimal.ZERO) <= 0 && y.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal halfR = r.divide(new BigDecimal(2), RoundingMode.HALF_UP);
            BigDecimal negativeR = r.negate();
            return x.compareTo(halfR.negate()) >= 0 && y.compareTo(negativeR) >= 0;
        }

        // треугольник
        if (x.compareTo(BigDecimal.ZERO) >= 0 && y.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal halfR = r.divide(new BigDecimal(2), RoundingMode.HALF_UP);
            BigDecimal rightSideOfHypotenuse = x.multiply(new BigDecimal(2)).subtract(r);
            return (x.compareTo(halfR) <= 0 && y.compareTo(r.negate()) >= 0) && (y.compareTo(rightSideOfHypotenuse) >= 0);
        }

        return false;
    }

    private static boolean validate(BigDecimal x, BigDecimal y, BigDecimal r) {
        return x.compareTo(new BigDecimal("-4")) >= 0 && x.compareTo(new BigDecimal("4")) <= 0 &&
                r.compareTo(new BigDecimal("1")) >= 0 && r.compareTo(new BigDecimal("4")) <= 0 &&
                y.compareTo(new BigDecimal("-3")) > 0 && y.compareTo(new BigDecimal("3")) < 0;
    }

    private static boolean validateCanvas(BigDecimal x, BigDecimal y, BigDecimal r) {
        return x.compareTo(new BigDecimal("-4")) >= 0 && x.compareTo(new BigDecimal("4")) <= 0 &&
                r.compareTo(new BigDecimal("1")) >= 0 && r.compareTo(new BigDecimal("4")) <= 0 &&
                y.compareTo(new BigDecimal("-4")) > 0 && y.compareTo(new BigDecimal("4")) < 0;
    }

    public static AreaCheckResponse parseReq(HttpServletRequest req) throws InvalidPointDataException {
        long startTime = System.nanoTime();
        BigDecimal x = new BigDecimal(req.getParameter("x"));
        BigDecimal y = new BigDecimal(req.getParameter("y"));
        BigDecimal r = new BigDecimal(req.getParameter("r"));
        boolean isCanvas = Boolean.parseBoolean(req.getParameter("isCanvas"));
        if (isCanvas) {
            if (!validateCanvas(x, y, r)) {
                throw new InvalidPointDataException("Один из элементов задан неккоректно");
            }
        }
        else {
            if (!validate(x, y, r)) {
                throw new InvalidPointDataException("Один из элементов задан неккоректно");
            }
        }
        boolean hit = checkHit(x, y, r);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        return new AreaCheckResponse(x, y, r, hit, duration);
    }

}
