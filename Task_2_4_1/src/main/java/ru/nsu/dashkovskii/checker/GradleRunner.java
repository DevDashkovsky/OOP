package ru.nsu.dashkovskii.checker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Запуск gradle-задач в (под)проекте студента.
 */
public final class GradleRunner {

    private GradleRunner() {
    }

    /** Сборка без тестов. */
    public static ProcessRunner.Result build(File projectDir, long timeoutMs) {
        return runGradle(projectDir, List.of("build", "-x", "test", "-x", "check"), timeoutMs);
    }

    /** Генерация javadoc. */
    public static ProcessRunner.Result javadoc(File projectDir, long timeoutMs) {
        return runGradle(projectDir, List.of("javadoc"), timeoutMs);
    }

    /** Проверка стиля через checkstyle (Google Java Style ожидается в config). */
    public static ProcessRunner.Result checkstyle(File projectDir, long timeoutMs) {
        return runGradle(projectDir, List.of("checkstyleMain"), timeoutMs);
    }

    /** Запуск тестов. */
    public static ProcessRunner.Result test(File projectDir, long timeoutMs) {
        return runGradle(projectDir, List.of("test"), timeoutMs);
    }

    private static ProcessRunner.Result runGradle(File dir, List<String> args, long timeoutMs) {
        List<String> cmd = new ArrayList<>();
        File wrapper = new File(dir, isWindows() ? "gradlew.bat" : "gradlew");
        if (wrapper.exists()) {
            if (!wrapper.canExecute()) {
                wrapper.setExecutable(true);
            }
            cmd.add(wrapper.getAbsolutePath());
        } else {
            cmd.add("gradle");
        }
        cmd.addAll(args);
        cmd.add("--no-daemon");
        cmd.add("--console=plain");
        return ProcessRunner.run(dir, cmd, timeoutMs);
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }
}
