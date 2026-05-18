package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Дополнительные настройки проверки.
 */
public final class CheckerSettings {

    /** Шкала оценок: баллы порога -> оценка (убывающий поиск). */
    private final Map<Double, Integer> gradeScale = new TreeMap<>(Collections.reverseOrder());

    /** Таймаут на один прогон тестов, мс. */
    private long testTimeoutMs = 120_000;

    /** Бонусные баллы студентам. */
    private final List<BonusPoint> bonusPoints = new ArrayList<>();

    /** Множитель оценки по активности: порог -> множитель (убывающий поиск). */
    private final Map<Double, Double> activityScale = new TreeMap<>(Collections.reverseOrder());

    public Map<Double, Integer> getGradeScale() {
        return gradeScale;
    }

    public long getTestTimeoutMs() {
        return testTimeoutMs;
    }

    public void setTestTimeoutMs(long testTimeoutMs) {
        this.testTimeoutMs = testTimeoutMs;
    }

    public List<BonusPoint> getBonusPoints() {
        return Collections.unmodifiableList(bonusPoints);
    }

    public void addBonus(BonusPoint bonus) {
        bonusPoints.add(bonus);
    }

    public Map<Double, Double> getActivityScale() {
        return activityScale;
    }

    /** Суммирует бонусы конкретному студенту по конкретной задаче. */
    public double bonusFor(String studentNick, String taskId) {
        return bonusPoints.stream()
                .filter(b -> b.student().equals(studentNick) && b.taskId().equals(taskId))
                .mapToDouble(BonusPoint::points).sum();
    }

    /**
     * Возвращает числовую оценку по сумме баллов.
     * Если шкала пуста — -1.
     */
    public int grade(double totalPoints) {
        for (Map.Entry<Double, Integer> e : gradeScale.entrySet()) {
            if (totalPoints >= e.getKey()) {
                return e.getValue();
            }
        }
        return -1;
    }

    /**
     * Возвращает множитель по доле активных недель.
     */
    public double activityMultiplier(double activityRatio) {
        if (activityScale.isEmpty()) {
            return 1.0;
        }
        for (Map.Entry<Double, Double> e : activityScale.entrySet()) {
            if (activityRatio >= e.getKey()) {
                return e.getValue();
            }
        }
        return activityScale.values().stream().min(Double::compare).orElse(1.0);
    }

    /** Запись о бонусе. */
    public record BonusPoint(String student, String taskId, double points) {
    }
}
