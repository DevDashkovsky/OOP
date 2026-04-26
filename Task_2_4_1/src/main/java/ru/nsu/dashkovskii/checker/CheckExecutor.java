package ru.nsu.dashkovskii.checker;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.nsu.dashkovskii.model.CheckerConfig;
import ru.nsu.dashkovskii.model.GroupReport;
import ru.nsu.dashkovskii.model.LabTask;
import ru.nsu.dashkovskii.model.Student;
import ru.nsu.dashkovskii.model.StudentGroup;
import ru.nsu.dashkovskii.model.StudentReport;
import ru.nsu.dashkovskii.model.TaskResult;

/**
 * Оркестрация проверок: клонирование, прогон стадий, расчёт балла, активность.
 * Зависимости приходят через конструктор — статики только в локальных хелперах.
 */
public final class CheckExecutor {

    private final CheckerConfig config;
    private final File workspace;
    private final LocalDate from;
    private final LocalDate to;

    private final GitClient git;
    private final GradleRunner gradle;
    private final TaskLocator taskLocator;
    private final TestResultsParser testResults;
    private final ActivityAnalyzer activity;
    private final ScoreCalculator scoreCalculator;

    /** Создаёт исполнитель со всеми сервисами, внедрёнными снаружи. */
    public CheckExecutor(
            CheckerConfig config,
            File workspace,
            LocalDate from,
            LocalDate to,
            GitClient git,
            GradleRunner gradle,
            TaskLocator taskLocator,
            TestResultsParser testResults,
            ActivityAnalyzer activity,
            ScoreCalculator scoreCalculator) {
        this.config = config;
        this.workspace = workspace;
        this.from = from;
        this.to = to;
        this.git = git;
        this.gradle = gradle;
        this.taskLocator = taskLocator;
        this.testResults = testResults;
        this.activity = activity;
        this.scoreCalculator = scoreCalculator;
    }

    /** Запускает все проверки, возвращает отчёты по группам. */
    public List<GroupReport> run() {
        List<GroupReport> reports = new ArrayList<>();
        Map<String, Map<String, List<String>>> checks = config.getAssignment().getChecks();
        workspace.mkdirs();

        for (Map.Entry<String, Map<String, List<String>>> groupEntry : checks.entrySet()) {
            StudentGroup group = config.getGroups().get(groupEntry.getKey());
            if (group == null) {
                continue;
            }
            GroupReport groupReport = new GroupReport(group);
            for (Map.Entry<String, List<String>> stEntry : groupEntry.getValue().entrySet()) {
                Student student = group.findByNick(stEntry.getKey());
                if (student == null) {
                    continue;
                }
                groupReport.addStudentReport(runForStudent(student, stEntry.getValue()));
            }
            reports.add(groupReport);
        }
        return reports;
    }

    private StudentReport runForStudent(Student student, List<String> taskIds) {
        StudentReport sr = new StudentReport(student);
        File repoDir = new File(workspace, student.getGithubNick());

        boolean cloned = student.getRepoUrl() != null
                && git.cloneOrFetch(student.getRepoUrl(), repoDir)
                && git.checkoutDefault(repoDir);

        long timeout = config.getSettings().getTestTimeoutMs();
        for (String taskId : taskIds) {
            sr.addResult(runForTask(repoDir, cloned, taskId, timeout, student.getGithubNick()));
        }

        if (cloned && from != null && to != null) {
            sr.setActivityRatio(activity.activityRatio(repoDir, from, to));
        }
        return sr;
    }

    private TaskResult runForTask(
            File repoDir, boolean cloned, String taskId, long timeoutMs, String nick) {
        TaskResult result = new TaskResult(taskId);
        if (!cloned) {
            result.setErrorMessage("Не удалось клонировать/обновить репозиторий");
            return result;
        }
        File projectDir = taskLocator.locate(repoDir, taskId);
        if (projectDir == null) {
            result.setErrorMessage("Не найден подпроект Task_" + taskId);
            return result;
        }
        String subPath = repoDir.toPath().relativize(projectDir.toPath()).toString();
        result.setSubmissionDate(git.lastCommitDate(repoDir, subPath));

        runStages(projectDir, timeoutMs, result);
        LabTask task = config.getTasks().get(taskId);
        scoreCalculator.apply(task, config.getSettings().bonusFor(nick, taskId), result);
        return result;
    }

    private void runStages(File projectDir, long timeoutMs, TaskResult result) {
        if (!gradle.build(projectDir, timeoutMs).success()) {
            result.setBuilt(false);
            return;
        }
        result.setBuilt(true);

        result.setDocs(gradle.javadoc(projectDir, timeoutMs).success());
        result.setStyleOk(gradle.checkstyle(projectDir, timeoutMs).success());
        if (!result.isDocs() || !result.isStyleOk()) {
            return;
        }
        gradle.test(projectDir, timeoutMs);
        TestResultsParser.Summary s = testResults.parse(projectDir);
        result.setTestsPassed(s.passed());
        result.setTestsFailed(s.failed());
        result.setTestsSkipped(s.skipped());
    }
}
