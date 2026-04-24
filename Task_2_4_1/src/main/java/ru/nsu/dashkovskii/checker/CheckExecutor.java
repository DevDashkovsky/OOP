package ru.nsu.dashkovskii.checker;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import ru.nsu.dashkovskii.model.CheckerConfig;
import ru.nsu.dashkovskii.model.CheckerSettings;
import ru.nsu.dashkovskii.model.GroupReport;
import ru.nsu.dashkovskii.model.LabTask;
import ru.nsu.dashkovskii.model.Student;
import ru.nsu.dashkovskii.model.StudentGroup;
import ru.nsu.dashkovskii.model.StudentReport;
import ru.nsu.dashkovskii.model.TaskResult;

/**
 * Оркестрация проверок: клонирование, сборка, тесты, стиль, javadoc, активность.
 */
public final class CheckExecutor {

    private final CheckerConfig config;
    private final File workspace;
    private final LocalDate from;
    private final LocalDate to;

    /** Создаёт исполнитель с заданной конфигурацией и рабочей директорией. */
    public CheckExecutor(CheckerConfig config, File workspace, LocalDate from, LocalDate to) {
        this.config = config;
        this.workspace = workspace;
        this.from = from;
        this.to = to;
    }

    /** Запускает все проверки, возвращает отчёты по группам. */
    public java.util.List<GroupReport> run() {
        java.util.List<GroupReport> reports = new java.util.ArrayList<>();
        Map<String, Map<String, List<String>>> checks = config.getAssignment().getChecks();
        workspace.mkdirs();

        for (Map.Entry<String, Map<String, List<String>>> groupEntry : checks.entrySet()) {
            String groupName = groupEntry.getKey();
            StudentGroup group = config.getGroups().get(groupName);
            if (group == null) {
                continue;
            }
            GroupReport groupReport = new GroupReport(group);

            for (Map.Entry<String, List<String>> stEntry : groupEntry.getValue().entrySet()) {
                Student student = group.findByNick(stEntry.getKey());
                if (student == null) {
                    continue;
                }
                StudentReport sr = runForStudent(student, stEntry.getValue());
                groupReport.addStudentReport(sr);
            }
            reports.add(groupReport);
        }
        return reports;
    }

    private StudentReport runForStudent(Student student, List<String> taskIds) {
        StudentReport sr = new StudentReport(student);
        File repoDir = new File(workspace, student.getGithubNick());

        boolean cloned = student.getRepoUrl() != null
                && GitClient.cloneOrFetch(student.getRepoUrl(), repoDir)
                && GitClient.checkoutDefault(repoDir);

        for (String taskId : taskIds) {
            LabTask task = config.getTasks().get(taskId);
            TaskResult result = new TaskResult(taskId);
            if (!cloned) {
                result.setErrorMessage("Не удалось клонировать/обновить репозиторий");
                sr.addResult(result);
                continue;
            }
            File projectDir = TaskLocator.locate(repoDir, taskId);
            if (projectDir == null) {
                result.setErrorMessage("Не найден подпроект Task_" + taskId);
                sr.addResult(result);
                continue;
            }
            String subPath = repoDir.toPath().relativize(projectDir.toPath()).toString();
            result.setSubmissionDate(GitClient.lastCommitDate(repoDir, subPath));
            executeChecks(projectDir, task, result);
            computeScore(task, student.getGithubNick(), result);
            sr.addResult(result);
        }

        if (cloned && from != null && to != null) {
            sr.setActivityRatio(ActivityAnalyzer.activityRatio(repoDir, from, to));
        }
        return sr;
    }

    private void executeChecks(File projectDir, LabTask task, TaskResult result) {
        CheckerSettings s = config.getSettings();
        long timeout = s.getTestTimeoutMs();

        ProcessRunner.Result build = GradleRunner.build(projectDir, timeout);
        result.setBuilt(build.success());
        if (!build.success()) {
            return;
        }

        ProcessRunner.Result docs = GradleRunner.javadoc(projectDir, timeout);
        result.setDocs(docs.success());

        ProcessRunner.Result style = GradleRunner.checkstyle(projectDir, timeout);
        result.setStyleOk(style.success());

        if (!result.isDocs() || !result.isStyleOk()) {
            return;
        }
        GradleRunner.test(projectDir, timeout);
        TestResultsParser.Summary summary = TestResultsParser.parse(projectDir);
        result.setTestsPassed(summary.passed());
        result.setTestsFailed(summary.failed());
        result.setTestsSkipped(summary.skipped());
    }

    private void computeScore(LabTask task, String nick, TaskResult result) {
        double bonus = config.getSettings().bonusFor(nick, result.getTaskId());
        result.setBonus(bonus);
        double total = 0;
        if (result.isBuilt()) {
            total += 0.25;
        }
        if (result.isDocs()) {
            total += 0.25;
        }
        if (result.isStyleOk()) {
            total += 0.25;
        }
        int totalTests = result.getTestsPassed() + result.getTestsFailed();
        if (totalTests > 0) {
            total += 0.25 * ((double) result.getTestsPassed() / totalTests);
        }
        double maxPoints = task != null ? task.getMaxPoints() : 1.0;
        double earned = applyDeadlinePenalty(total * maxPoints, task, result.getSubmissionDate());
        result.setTotal(earned + bonus);
    }

    private static double applyDeadlinePenalty(double score, LabTask task, LocalDate submissionDate) {
        if (task == null || submissionDate == null) {
            return score;
        }
        LocalDate hard = task.getHardDeadline();
        LocalDate soft = task.getSoftDeadline();
        if (hard != null && submissionDate.isAfter(hard)) {
            return 0;
        }
        if (soft != null && submissionDate.isAfter(soft)) {
            return score * 0.5;
        }
        return score;
    }
}
