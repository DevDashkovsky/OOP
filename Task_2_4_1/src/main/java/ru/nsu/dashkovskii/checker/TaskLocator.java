package ru.nsu.dashkovskii.checker;

import java.io.File;

/**
 * Ищет подпроект лабораторной внутри клонированного репозитория.
 * Предполагается имя вида {@code Task_2_1_1}.
 */
public final class TaskLocator {

    private TaskLocator() {
    }

    /**
     * Возвращает директорию подпроекта по идентификатору задачи либо {@code null}.
     */
    public static File locate(File repoDir, String taskId) {
        String expected = "Task_" + taskId;
        File direct = new File(repoDir, expected);
        if (direct.isDirectory()) {
            return direct;
        }
        File[] children = repoDir.listFiles();
        if (children != null) {
            for (File child : children) {
                if (!child.isDirectory()) {
                    continue;
                }
                if (child.getName().equalsIgnoreCase(expected)) {
                    return child;
                }
            }
        }
        return null;
    }
}
