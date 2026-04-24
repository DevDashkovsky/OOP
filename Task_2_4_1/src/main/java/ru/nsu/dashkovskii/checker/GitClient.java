package ru.nsu.dashkovskii.checker;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Работа с git через консольный клиент.
 */
public final class GitClient {

    private GitClient() {
    }

    /** Клонирует репозиторий в {@code targetDir}, если его ещё нет, иначе делает fetch. */
    public static boolean cloneOrFetch(String repoUrl, File targetDir) {
        if (targetDir.exists() && new File(targetDir, ".git").exists()) {
            ProcessRunner.Result r = ProcessRunner.run(targetDir,
                    List.of("git", "fetch", "--all", "--prune"), 60_000);
            return r.success();
        }
        targetDir.getParentFile().mkdirs();
        ProcessRunner.Result r = ProcessRunner.run(targetDir.getParentFile(),
                List.of("git", "clone", "--quiet", repoUrl, targetDir.getName()), 300_000);
        return r.success();
    }

    /**
     * Пытается переключиться на master или main.
     */
    public static boolean checkoutDefault(File repoDir) {
        for (String branch : List.of("main", "master")) {
            ProcessRunner.Result r = ProcessRunner.run(repoDir,
                    List.of("git", "checkout", branch), 30_000);
            if (r.success()) {
                ProcessRunner.run(repoDir, List.of("git", "pull", "--ff-only"), 60_000);
                return true;
            }
        }
        return false;
    }

    /** Список timestamp коммитов (unix seconds). */
    public static List<Long> listCommitTimestamps(File repoDir) {
        ProcessRunner.Result r = ProcessRunner.run(repoDir,
                List.of("git", "log", "--pretty=format:%ct"), 30_000);
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
                // пропускаем мусорные строки
            }
        }
        return result;
    }

    /**
     * Возвращает дату последнего коммита, затрагивающего указанный путь внутри репозитория.
     * Возвращает {@code null}, если коммитов нет или путь не существует.
     */
    public static LocalDate lastCommitDate(File repoDir, String subPath) {
        ProcessRunner.Result r = ProcessRunner.run(repoDir,
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

    /** Проверяет, что git настроен без интерактивных запросов аутентификации. */
    public static boolean isNonInteractive() {
        String askpass = System.getenv("GIT_TERMINAL_PROMPT");
        return "0".equals(askpass);
    }

    /** Возвращает {@code git config --global user.name}. */
    public static String globalUser() {
        ProcessRunner.Result r = ProcessRunner.run(null,
                List.of("git", "config", "--global", "user.name"), 5_000);
        return r.success() ? r.output().trim() : "";
    }

    /** Текущее время — для определения «свежести» репозитория. */
    public static Instant now() {
        return Instant.now();
    }
}
