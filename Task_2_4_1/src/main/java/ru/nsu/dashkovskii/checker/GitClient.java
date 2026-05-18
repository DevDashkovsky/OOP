package ru.nsu.dashkovskii.checker;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Работа с git через консольный клиент. Курс запрещает GitHub API,
 * любая новая операция должна идти через {@link ProcessRunner}.
 */
public final class GitClient {

    private final ProcessRunner processes;

    /** Создаёт клиент поверх заданного {@link ProcessRunner}. */
    public GitClient(ProcessRunner processes) {
        this.processes = processes;
    }

    /** Клонирует репозиторий, либо делает fetch, если он уже есть. */
    public boolean cloneOrFetch(String repoUrl, File targetDir) {
        if (targetDir.exists() && new File(targetDir, ".git").exists()) {
            ProcessRunner.Result r = processes.run(targetDir,
                    List.of("git", "fetch", "--all", "--prune"), 60_000);
            return r.success();
        }
        targetDir.getParentFile().mkdirs();
        ProcessRunner.Result r = processes.run(targetDir.getParentFile(),
                List.of("git", "clone", "--quiet", repoUrl, targetDir.getName()), 300_000);
        return r.success();
    }

    /** Переключается на main или master и подтягивает изменения. */
    public boolean checkoutDefault(File repoDir) {
        for (String branch : List.of("main", "master")) {
            ProcessRunner.Result r = processes.run(repoDir,
                    List.of("git", "checkout", branch), 30_000);
            if (r.success()) {
                processes.run(repoDir, List.of("git", "pull", "--ff-only"), 60_000);
                return true;
            }
        }
        return false;
    }

    /** Список unix-таймстампов всех коммитов (без merge — это не активная работа). */
    public List<Long> listCommitTimestamps(File repoDir) {
        ProcessRunner.Result r = processes.run(repoDir,
                List.of("git", "log", "--no-merges", "--pretty=format:%ct"), 30_000);
        List<Long> result = new ArrayList<>();
        if (!r.success()) {
            return result;
        }
        for (String line : r.output().split("\n")) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                result.add(Long.parseLong(line));
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    /** Дата последнего коммита, затрагивающего {@code subPath}, или {@code null}. */
    public LocalDate lastCommitDate(File repoDir, String subPath) {
        ProcessRunner.Result r = processes.run(repoDir,
                List.of("git", "log", "-1", "--pretty=format:%ct", "--", subPath), 10_000);
        if (!r.success() || r.output().isBlank()) {
            return null;
        }
        try {
            long ts = Long.parseLong(r.output().trim());
            return Instant.ofEpochSecond(ts).atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /** Проверяет, что git настроен без интерактивных prompt'ов. */
    public boolean isNonInteractive() {
        return "0".equals(System.getenv("GIT_TERMINAL_PROMPT"));
    }
}
