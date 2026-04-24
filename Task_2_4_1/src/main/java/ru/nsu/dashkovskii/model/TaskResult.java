package ru.nsu.dashkovskii.model;

import java.time.LocalDate;

/**
 * Результат проверки одной задачи у студента.
 */
public final class TaskResult {
    private final String taskId;
    private boolean built;
    private boolean docs;
    private boolean styleOk;
    private int testsPassed;
    private int testsFailed;
    private int testsSkipped;
    private double bonus;
    private double total;
    private String errorMessage;
    private LocalDate submissionDate;

    public TaskResult(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public boolean isBuilt() {
        return built;
    }

    public void setBuilt(boolean built) {
        this.built = built;
    }

    public boolean isDocs() {
        return docs;
    }

    public void setDocs(boolean docs) {
        this.docs = docs;
    }

    public boolean isStyleOk() {
        return styleOk;
    }

    public void setStyleOk(boolean styleOk) {
        this.styleOk = styleOk;
    }

    public int getTestsPassed() {
        return testsPassed;
    }

    public void setTestsPassed(int testsPassed) {
        this.testsPassed = testsPassed;
    }

    public int getTestsFailed() {
        return testsFailed;
    }

    public void setTestsFailed(int testsFailed) {
        this.testsFailed = testsFailed;
    }

    public int getTestsSkipped() {
        return testsSkipped;
    }

    public void setTestsSkipped(int testsSkipped) {
        this.testsSkipped = testsSkipped;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }
}
