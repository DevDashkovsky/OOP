package ru.nsu.dashkovskii.checker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Тонкая обёртка над {@link ProcessBuilder}. Создаётся один раз и
 * передаётся как зависимость в {@link GitClient} и {@link GradleRunner}.
 */
public final class ProcessRunner {

    /** Результат запуска. */
    public record Result(int exitCode, String output, boolean timedOut) {
        public boolean success() {
            return !timedOut && exitCode == 0;
        }
    }

    /** Запускает процесс в {@code dir} и возвращает результат с захваченным выводом. */
    public Result run(File dir, List<String> cmd, long timeoutMs) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        if (dir != null) {
            pb.directory(dir);
        }
        pb.redirectErrorStream(true);
        Process p;
        try {
            p = pb.start();
        } catch (IOException e) {
            return new Result(-1, "Не удалось запустить: " + e.getMessage(), false);
        }
        StringBuilder out = new StringBuilder();
        Thread reader = new Thread(() -> {
            try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = r.readLine()) != null) {
                    out.append(line).append('\n');
                }
            } catch (IOException ignored) {
                // поток закрыт — выходим тихо
            }
        });
        reader.setDaemon(true);
        reader.start();
        try {
            boolean finished;
            if (timeoutMs > 0) {
                finished = p.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
            } else {
                p.waitFor();
                finished = true;
            }
            if (!finished) {
                p.destroyForcibly();
            }
            reader.join(5_000);
            if (!finished) {
                return new Result(-1, out.toString(), true);
            }
            return new Result(p.exitValue(), out.toString(), false);
        } catch (InterruptedException e) {
            p.destroyForcibly();
            Thread.currentThread().interrupt();
            return new Result(-1, out + "\n" + e.getMessage(), false);
        }
    }
}
