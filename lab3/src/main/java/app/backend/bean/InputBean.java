package app.backend.bean;

import app.backend.dto.AreaCheckResponse;
import app.backend.service.HistoryService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Named
public class InputBean implements Serializable {

    private static final Logger logger = Logger.getLogger(InputBean.class.getName());

    private BigDecimal x = new BigDecimal("0");
    private BigDecimal y = new BigDecimal("0");
    private BigDecimal r = new BigDecimal("3");
    private boolean fromCanvas = false;

    @Inject
    private HistoryService historyService;

    @Inject
    private HistoryBean historyBean;

    public InputBean() {}

    public String check() {
        logger.info("Обработка запроса: x=" + x + ", y=" + y + ", r=" + r);

        try {
            AreaCheckResponse response = historyService.saveAttempt(x, y, r, fromCanvas);
            historyBean.addResponse(response);

            logger.info("Результат: " + (response.hit() ? "Попадание" : "Промах"));

            fromCanvas = false;

            addSuccessMessage("Проверка выполнена успешно");
            return "success";

        } catch (IllegalArgumentException e) {
            logger.warning("Ошибка валидации: " + e.getMessage());
            addErrorMessage(e.getMessage());
            return "error";

        } catch (Exception e) {
            logger.severe("Системная ошибка: " + e.getMessage());
            addErrorMessage("Системная ошибка");
            return "error";
        }
    }

    public void handleCanvasClick(double canvasX, double canvasY, BigDecimal rValue) {
        logger.info("Canvas click: x=" + canvasX + ", y=" + canvasY + ", r=" + rValue);

        this.x = new BigDecimal(String.valueOf(canvasX));
        this.y = new BigDecimal(String.valueOf(canvasY));
        this.r = rValue;
        this.fromCanvas = true;

        check();
    }

    public void reset() {
        x = new BigDecimal("0");
        y = new BigDecimal("0");;
        r = new BigDecimal("3");
        fromCanvas = false;
        logger.info("Форма сброшена");
    }

    private void addSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    public BigDecimal getX() { return x; }
    public void setX(BigDecimal x) { this.x = x; }

    public BigDecimal getY() { return y; }
    public void setY(BigDecimal y) { this.y = y; }

    public BigDecimal getR() { return r; }
    public void setR(BigDecimal r) { this.r = r; }

    public boolean isFromCanvas() { return fromCanvas; }
    public void setFromCanvas(boolean fromCanvas) { this.fromCanvas = fromCanvas; }

    public HistoryService getHistoryService() { return historyService; }
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public String getXAsString() {
        return x != null ? x.toString() : "";
    }

    public void setXAsString(String xStr) {
        try {
            this.x = new BigDecimal(xStr);
        } catch (NumberFormatException e) {
            this.x = new BigDecimal("0");
        }
    }

    public List<String> getxOptions() {
        return Arrays.asList("-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3");
    }

    public void canvasCheck() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        try {
            BigDecimal canvasX = new BigDecimal(params.get("canvasX"));
            BigDecimal canvasY = new BigDecimal(params.get("canvasY"));
            BigDecimal canvasR = new BigDecimal(params.get("canvasR"));

            logger.info("Canvas click: x=" + canvasX + ", y=" + canvasY + ", r=" + canvasR);

            AreaCheckResponse response = historyService.saveAttempt(
                    canvasX, canvasY, canvasR, true
            );

            historyBean.addResponse(response);

        } catch (Exception e) {
            logger.severe("Ошибка: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при обработке клика", null));
        }
    }

    public void setXValue(String xValue) {
        this.x = new BigDecimal(xValue);
        logger.info("X установлен: " + xValue);
    }
}