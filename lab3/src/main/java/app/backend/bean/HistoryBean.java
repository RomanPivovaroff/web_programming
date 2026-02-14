package app.backend.bean;

import app.backend.dto.AreaCheckResponse;
import app.backend.service.HistoryService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Named
public class HistoryBean implements Serializable {

    private static final Logger logger = Logger.getLogger(HistoryBean.class.getName());

    private int maxHistorySize = 100;
    private boolean autoLoadHistory = true;
    private boolean enablePagination = true;
    private int pageSize = 20;

    @Inject
    private HistoryService historyService;

    private List<AreaCheckResponse> history;
    private int currentPage = 1;
    private int totalPages = 1;

    public HistoryBean() {
        history = new ArrayList<>();
    }

    @PostConstruct
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

            calculatePagination();

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

        calculatePagination();

        logger.info("Добавлен новый результат: " + response);
    }

    public String clearHistory() {
        try {
            historyService.clearHistory();
            history.clear();
            currentPage = 1;
            totalPages = 1;

            addSuccessMessage("История очищена");

            return "index";

        } catch (Exception e) {
            logger.severe("Ошибка при очистке истории: " + e.getMessage());
            addErrorMessage("Ошибка при очистке истории");
            return null;
        }
    }

    public List<AreaCheckResponse> getPagedHistory() {
        if (!enablePagination || pageSize <= 0) {
            return history;
        }

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, history.size());

        if (start >= history.size()) {
            return new ArrayList<>();
        }

        return history.subList(start, end);
    }

    public void nextPage() {
        if (currentPage < totalPages) {
            this.currentPage++;
        }
    }

    public void previousPage() {
        if (currentPage > 1) {
            this.currentPage--;
        }
    }

    public void goToPage(int page) {
        if (page >= 1 && page <= totalPages) {
            this.currentPage = page;
        }
    }

    private void calculatePagination() {
        if (!enablePagination || pageSize <= 0) {
            totalPages = 1;
            return;
        }

        totalPages = (int) Math.ceil((double) history.size() / pageSize);
        if (totalPages < 1) {
            totalPages = 1;
        }

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }
    }

    private void addSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    public int getMaxHistorySize() {
        return maxHistorySize;
    }

    public void setMaxHistorySize(int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
    }

    public boolean isAutoLoadHistory() {
        return autoLoadHistory;
    }

    public void setAutoLoadHistory(boolean autoLoadHistory) {
        this.autoLoadHistory = autoLoadHistory;
    }

    public boolean isEnablePagination() {
        return enablePagination;
    }

    public void setEnablePagination(boolean enablePagination) {
        this.enablePagination = enablePagination;
        calculatePagination();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        calculatePagination();
    }


    public HistoryService getHistoryService() {
        return historyService;
    }

    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public List<AreaCheckResponse> getHistory() {
        if (enablePagination) {
            return getPagedHistory();
        }
        return history;
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }

    public int getTotalItems() {
        return history.size();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public String getPaginationInfo() {
        if (!enablePagination || pageSize <= 0) {
            return "Всего записей: " + history.size();
        }

        int start = (currentPage - 1) * pageSize + 1;
        int end = Math.min(currentPage * pageSize, history.size());

        return "Показано " + start + "-" + end + " из " + history.size() + " записей";
    }
    public List<Integer> getPages() {
        List<Integer> pages = new ArrayList<>();

        if (totalPages <= 1) {
            return pages;
        }

        int start = Math.max(1, currentPage - 2);
        int end = Math.min(totalPages, currentPage + 2);

        for (int i = start; i <= end; i++) {
            pages.add(i);
        }

        return pages;
    }
}