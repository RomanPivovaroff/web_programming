package app.backend.service;

import app.backend.dto.AreaCheckResponse;
import app.backend.exception.InvalidPointDataException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Named
@ApplicationScoped
public class PointLogicService {

    private BigDecimal xMin = new BigDecimal("-5");
    private BigDecimal xMax = new BigDecimal("3");
    private BigDecimal yMin = new BigDecimal("-3");
    private BigDecimal yMax = new BigDecimal("3");
    private BigDecimal rMin = new BigDecimal("1");
    private BigDecimal rMax = new BigDecimal("3");

    public boolean checkHit(BigDecimal x, BigDecimal y, BigDecimal r) {
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
            return (x.compareTo(halfR) <= 0 && y.compareTo(r.negate()) >= 0)
                    && (y.compareTo(rightSideOfHypotenuse) >= 0);
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

    public BigDecimal getXMin() { return xMin; }
    public void setXMin(BigDecimal xMin) { this.xMin = xMin; }

    public BigDecimal getXMax() { return xMax; }
    public void setXMax(BigDecimal xMax) { this.xMax = xMax; }

    public BigDecimal getYMin() { return yMin; }
    public void setYMin(BigDecimal yMin) { this.yMin = yMin; }

    public BigDecimal getYMax() { return yMax; }
    public void setYMax(BigDecimal yMax) { this.yMax = yMax; }

    public BigDecimal getRMin() { return rMin; }
    public void setRMin(BigDecimal rMin) { this.rMin = rMin; }

    public BigDecimal getRMax() { return rMax; }
    public void setRMax(BigDecimal rMax) { this.rMax = rMax; }
}