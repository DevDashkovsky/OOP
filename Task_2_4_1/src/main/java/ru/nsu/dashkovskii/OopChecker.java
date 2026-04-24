package ru.nsu.dashkovskii;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import ru.nsu.dashkovskii.checker.CheckExecutor;
import ru.nsu.dashkovskii.checker.GitClient;
import ru.nsu.dashkovskii.dsl.ConfigLoader;
import ru.nsu.dashkovskii.model.CheckerConfig;
import ru.nsu.dashkovskii.model.Checkpoint;
import ru.nsu.dashkovskii.model.GroupReport;
import ru.nsu.dashkovskii.report.HtmlReportGenerator;

/**
 * Входная точка: ищет checker.groovy в рабочей директории и исполняет команду.
 */
public final class OopChecker {

    private OopChecker() {
    }

    /** Точка входа. */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Использование: oop-checker <команда>");
            System.err.println("Команды: test");
            System.exit(1);
        }
        String command = args[0];
        File workDir = new File(System.getProperty("user.dir"));

        switch (command) {
            case "test" -> runTest(workDir);
            default -> {
                System.err.println("Неизвестная команда: " + command);
                System.exit(1);
            }
        }
    }

    private static void runTest(File workDir) throws Exception {
        if (!GitClient.isNonInteractive()) {
            System.err.println("Предупреждение: GIT_TERMINAL_PROMPT!=0 — git может "
                    + "запрашивать ввод. Запустите с GIT_TERMINAL_PROMPT=0.");
        }

        CheckerConfig config = ConfigLoader.loadFromDir(workDir);

        LocalDate from = config.getCheckpoints().stream()
                .map(Checkpoint::date).min(LocalDate::compareTo)
                .orElse(LocalDate.now().minusMonths(4));
        LocalDate to = config.getCheckpoints().stream()
                .map(Checkpoint::date).max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        File workspace = new File(workDir, ".oop-checker/workspace");
        CheckExecutor executor = new CheckExecutor(config, workspace, from, to);
        List<GroupReport> reports = executor.run();

        HtmlReportGenerator gen = new HtmlReportGenerator(config);
        System.out.println(gen.render(reports));
    }
}
