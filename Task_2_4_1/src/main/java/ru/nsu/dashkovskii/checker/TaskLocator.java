package ru.nsu.dashkovskii.checker;

import java.io.File;

/**
 * Ищет подпроект {@code Task_<id>} внутри клонированного репозитория.
 */
public final class TaskLocator {

    /** Возвращает директорию подпроекта или {@code null}, если не найдена. */
    public File locate(File repoDir, String taskId) {
        String expected = "Task_" + taskId;
        File direct = new File(repoDir, expected);
        if (direct.isDirectory()) {
            return direct;
        }
        File[] children = repoDir.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory() && child.getName().equalsIgnoreCase(expected)) {
                    return child;
                }
            }
        }
        return null;
    }
}
