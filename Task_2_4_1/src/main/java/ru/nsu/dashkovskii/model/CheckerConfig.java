package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Корневой объект конфигурации.
 */
public final class CheckerConfig {
    private final Map<String, LabTask> tasks = new LinkedHashMap<>();
    private final Map<String, StudentGroup> groups = new LinkedHashMap<>();
    private final List<Checkpoint> checkpoints = new ArrayList<>();
    private final Assignment assignment = new Assignment();
    private final CheckerSettings settings = new CheckerSettings();

    public Map<String, LabTask> getTasks() {
        return tasks;
    }

    public Map<String, StudentGroup> getGroups() {
        return groups;
    }

    public List<Checkpoint> getCheckpoints() {
        return Collections.unmodifiableList(checkpoints);
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public CheckerSettings getSettings() {
        return settings;
    }

    public void addTask(LabTask task) {
        tasks.put(task.getId(), task);
    }

    public void addGroup(StudentGroup group) {
        groups.put(group.getName(), group);
    }

    public void addCheckpoint(Checkpoint checkpoint) {
        checkpoints.add(checkpoint);
    }
}
