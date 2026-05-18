package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Что именно надо проверить: для каждой группы — какие задачи у каких студентов.
 */
public final class Assignment {
    private final Map<String, Map<String, List<String>>> checks = new LinkedHashMap<>();

    /** Добавляет задачи к списку проверок конкретного студента. */
    public void addCheck(String groupName, String studentNick, List<String> taskIds) {
        checks.computeIfAbsent(groupName, g -> new LinkedHashMap<>())
                .computeIfAbsent(studentNick, s -> new ArrayList<>())
                .addAll(taskIds);
    }

    public Map<String, Map<String, List<String>>> getChecks() {
        return Collections.unmodifiableMap(checks);
    }

    public List<String> getTasksFor(String groupName, String studentNick) {
        return checks.getOrDefault(groupName, Collections.emptyMap())
                .getOrDefault(studentNick, Collections.emptyList());
    }
}
