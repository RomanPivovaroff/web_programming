package app.backend.service;

import app.backend.dto.AreaCheckResponse;
import app.backend.exception.InvalidPointDataException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PointLogicService {

    private BigDecimal xMin = new BigDecimal("-5");
    private BigDecimal xMax = new BigDecimal("3");
    private BigDecimal yMin = new BigDecimal("-3");
    private BigDecimal yMax = new BigDecimal("3");
    private BigDecimal rMin = new BigDecimal("1");
    private BigDecimal rMax = new BigDecimal("3");

    public boolean checkHit(BigDecimal x, BigDecimal y, BigDecimal r) {
        // четверть круга
        if (x.compareTo(BigDecimal.ZERO) <= 0 && y.compareTo(BigDecimal.ZERO) >= 0) {
            BigDecimal sumSquares = x.multiply(x).add(y.multiply(y));
            BigDecimal left = sumSquares.multiply(new BigDecimal("4"));
            BigDecimal right = r.multiply(r);

            return left.compareTo(right) <= 0;
        }

        // прямоугольник
        if (x.compareTo(BigDecimal.ZERO) <= 0 && y.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal negativeR = r.negate();
            return x.compareTo(negativeR) >= 0 && y.compareTo(negativeR) >= 0;
        }

        // треугольник
        if (x.compareTo(BigDecimal.ZERO) >= 0 && y.compareTo(BigDecimal.ZERO) >= 0) {
            BigDecimal halfR = r.divide(new BigDecimal(2), RoundingMode.HALF_UP);
            BigDecimal rightSideOfHypotenuse = halfR.subtract(x.multiply(halfR).divide(r, RoundingMode.HALF_UP));
            return x.compareTo(r) <= 0 && y.compareTo(rightSideOfHypotenuse) <= 0;
        }

        return false;
    }

    // Валидация для формы
    private boolean validateForm(BigDecimal x, BigDecimal y, BigDecimal r) {
        return x.compareTo(xMin) >= 0 && x.compareTo(xMax) <= 0 &&
                x.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0 &&
                r.compareTo(rMin) >= 0 && r.compareTo(rMax) <= 0 &&
                y.compareTo(yMin) > 0 && y.compareTo(yMax) < 0;
    }

    // Валидация для canvas (клик по графику)
    private boolean validateCanvas(BigDecimal x, BigDecimal y, BigDecimal r) {
        return x.compareTo(rMax.negate()) >= 0 && x.compareTo(rMax) <= 0 &&
                r.compareTo(rMin) >= 0 && r.compareTo(rMax) <= 0 &&
                y.compareTo(rMax.negate()) > 0 && y.compareTo(rMax) < 0;
    }

    public AreaCheckResponse createResponse(BigDecimal x, BigDecimal y, BigDecimal r,
                                            boolean isCanvas) throws InvalidPointDataException {
        long startTime = System.nanoTime();

        if (isCanvas) {
            if (!validateCanvas(x, y, r)) {
                throw new InvalidPointDataException("Некорректные данные с canvas");
            }
        } else {
            if (!validateForm(x, y, r)) {
                throw new InvalidPointDataException("Некорректные данные из формы");
            }
        }

        boolean hit = checkHit(x, y, r);
        long duration = System.nanoTime() - startTime;

        return new AreaCheckResponse(x, y, r, hit, duration);
    }

    public BigDecimal getxMin() { return xMin; }
    public void setxMin(BigDecimal xMin) { this.xMin = xMin; }

    public BigDecimal getxMax() { return xMax; }
    public void setxMax(BigDecimal xMax) { this.xMax = xMax; }

    public BigDecimal getyMin() { return yMin; }
    public void setyMin(BigDecimal yMin) { this.yMin = yMin; }

    public BigDecimal getyMax() { return yMax; }
    public void setyMax(BigDecimal yMax) { this.yMax = yMax; }

    public BigDecimal getrMin() { return rMin; }
    public void setrMin(BigDecimal rMin) { this.rMin = rMin; }

    public BigDecimal getrMax() { return rMax; }
    public void setrMax(BigDecimal rMax) { this.rMax = rMax; }
}