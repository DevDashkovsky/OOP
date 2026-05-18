package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Сводка по одному студенту.
 */
public final class StudentReport {
    private final Student student;
    private final List<TaskResult> results = new ArrayList<>();
    private double activityRatio;

    public StudentReport(Student student) {
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

    public List<TaskResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    public void addResult(TaskResult result) {
        results.add(result);
    }

    public double getActivityRatio() {
        return activityRatio;
    }

    public void setActivityRatio(double activityRatio) {
        this.activityRatio = activityRatio;
    }

    public double totalPoints() {
        return results.stream().mapToDouble(TaskResult::getTotal).sum();
    }
}
