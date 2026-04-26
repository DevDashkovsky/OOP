package ru.nsu.dashkovskii.checker;

import java.time.LocalDate;
import ru.nsu.dashkovskii.model.LabTask;
import ru.nsu.dashkovskii.model.TaskResult;

/**
 * Считает балл за задачу: четыре равные четверти (build/docs/style/tests),
 * затем штраф за дедлайн (soft → ×0.5, hard → 0), затем бонус.
 */
public final class ScoreCalculator {

    private static final double STAGE_WEIGHT = 0.25;

    /** Записывает в {@code result} итоговый балл с учётом дедлайна и бонуса. */
    public void apply(LabTask task, double bonus, TaskResult result) {
        result.setBonus(bonus);
        double maxPoints = task != null ? task.getMaxPoints() : 1.0;
        double raw = stagesShare(result) * maxPoints;
        double earned = applyDeadline(raw, task, result.getSubmissionDate());
        result.setTotal(earned + bonus);
    }

    private double stagesShare(TaskResult result) {
        double share = 0;
        if (result.isBuilt()) {
            share += STAGE_WEIGHT;
        }
        if (result.isDocs()) {
            share += STAGE_WEIGHT;
        }
        if (result.isStyleOk()) {
            share += STAGE_WEIGHT;
        }
        int totalTests = result.getTestsPassed() + result.getTestsFailed();
        if (totalTests > 0) {
            share += STAGE_WEIGHT * ((double) result.getTestsPassed() / totalTests);
        }
        return share;
    }

    private static double applyDeadline(double score, LabTask task, LocalDate submission) {
        if (task == null || submission == null) {
            return score;
        }
        LocalDate hard = task.getHardDeadline();
        LocalDate soft = task.getSoftDeadline();
        if (hard != null && submission.isAfter(hard)) {
            return 0;
        }
        if (soft != null && submission.isAfter(soft)) {
            return score * 0.5;
        }
        return score;
    }
}
