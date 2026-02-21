package app.backend.bean;

import app.backend.dto.AreaCheckResponse;
import app.backend.service.HistoryService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HistoryBean implements Serializable {

    private static final Logger logger = Logger.getLogger(HistoryBean.class.getName());

    private int maxHistorySize = 100;
    private boolean autoLoadHistory = true;

    private HistoryService historyService;

    private List<AreaCheckResponse> history;

    public HistoryBean() {
        history = new ArrayList<>();
    }

    public void init() {
        if (autoLoadHistory) {
            loadHistory();
        }
    }

    public void loadHistory() {
        try {
            List<AreaCheckResponse> allAttempts = historyService.getAllAttempts();

            if (maxHistorySize > 0 && allAttempts.size() > maxHistorySize) {
                history = new ArrayList<>(allAttempts.subList(0, maxHistorySize));
            } else {
                history = new ArrayList<>(allAttempts);
            }

            logger.info("Загружено " + history.size() + " записей истории");

        } catch (Exception e) {
            logger.severe("Ошибка при загрузке истории: " + e.getMessage());
            addErrorMessage("Ошибка при загрузке истории");
            history = new ArrayList<>();
        }
    }

    public void addResponse(AreaCheckResponse response) {
        if (response == null) {
            return;
        }

        history.add(0, response);

        if (maxHistorySize > 0 && history.size() > maxHistorySize) {
            history = new ArrayList<>(history.subList(0, maxHistorySize));
        }

        logger.info("Добавлен новый результат: " + response);
    }

    public void clearHistory() {
        try {
            historyService.clearHistory();
            history.clear();
            addSuccessMessage("История очищена");

        } catch (Exception e) {
            logger.severe("Ошибка при очистке истории: " + e.getMessage());
            addErrorMessage("Ошибка при очистке истории");
        }
    }

    public List<AreaCheckResponse> getHistory() {
        return history;  // Просто возвращаем весь список
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }

    public int getTotalItems() {
        return history.size();
    }

    // Геттеры и сеттеры
    public int getMaxHistorySize() { return maxHistorySize; }
    public void setMaxHistorySize(int maxHistorySize) { this.maxHistorySize = maxHistorySize; }

    public boolean isAutoLoadHistory() { return autoLoadHistory; }
    public void setAutoLoadHistory(boolean autoLoadHistory) { this.autoLoadHistory = autoLoadHistory; }

    public HistoryService getHistoryService() { return historyService; }
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    private void addSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
}