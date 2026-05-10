package ru.nsu.dashkovskii;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import ru.nsu.dashkovskii.checker.ActivityAnalyzer;
import ru.nsu.dashkovskii.checker.CheckExecutor;
import ru.nsu.dashkovskii.checker.GitClient;
import ru.nsu.dashkovskii.checker.GradleRunner;
import ru.nsu.dashkovskii.checker.ProcessRunner;
import ru.nsu.dashkovskii.checker.ScoreCalculator;
import ru.nsu.dashkovskii.checker.TaskLocator;
import ru.nsu.dashkovskii.checker.TestResultsParser;
import ru.nsu.dashkovskii.dsl.ConfigLoader;
import ru.nsu.dashkovskii.model.CheckerConfig;
import ru.nsu.dashkovskii.model.Checkpoint;
import ru.nsu.dashkovskii.model.GroupReport;
import ru.nsu.dashkovskii.report.HtmlReportGenerator;

/**
 * Входная точка: парсит args, собирает граф зависимостей, запускает команду.
 */
public final class OopChecker {

    private final ProcessRunner processes = new ProcessRunner();
    private final GitClient git = new GitClient(processes);
    private final GradleRunner gradle = new GradleRunner(processes);
    private final TaskLocator taskLocator = new TaskLocator();
    private final TestResultsParser testResults = new TestResultsParser();
    private final ActivityAnalyzer activity = new ActivityAnalyzer(git);
    private final ScoreCalculator scoreCalculator = new ScoreCalculator();
    private final ConfigLoader configLoader = new ConfigLoader();

    /** Точка входа JVM. */
    public static void main(String[] args) {
        try {
            System.exit(new OopChecker().run(args, new File(System.getProperty("user.dir"))));
        } catch (Exception e) {
            String message = e.getMessage();
            if (message == null) {
                message = e.getClass().getSimpleName();
            }
            System.err.println("Ошибка: " + message);
            System.exit(1);
        }
    }

    /** Запускает выбранную команду; возвращает код возврата. */
    public int run(String[] args, File workDir) throws Exception {
        if (args.length == 0) {
            System.err.println("Использование: oop-checker <команда>");
            System.err.println("Команды: test");
            return 1;
        }
        return switch (args[0]) {
            case "test" -> {
                runTest(workDir);
                yield 0;
            }
            default -> {
                System.err.println("Неизвестная команда: " + args[0]);
                yield 1;
            }
        };
    }

    private void runTest(File workDir) throws Exception {
        if (!git.isNonInteractive()) {
            System.err.println("Предупреждение: GIT_TERMINAL_PROMPT!=0 — git может "
                    + "запрашивать ввод. Запустите с GIT_TERMINAL_PROMPT=0.");
        }
        CheckerConfig config = configLoader.loadFromDir(workDir);
        LocalDate from = config.getCheckpoints().stream()
                .map(Checkpoint::date).min(LocalDate::compareTo)
                .orElse(LocalDate.now().minusMonths(4));
        LocalDate to = config.getCheckpoints().stream()
                .map(Checkpoint::date).max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        File workspace = new File(workDir, ".oop-checker/workspace");
        CheckExecutor executor = new CheckExecutor(
                config, workspace, from, to,
                git, gradle, taskLocator, testResults, activity, scoreCalculator);
        List<GroupReport> reports = executor.run();
        System.out.println(new HtmlReportGenerator(config).render(reports));
    }
}
