package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Сводка по группе.
 */
public final class GroupReport {
    private final StudentGroup group;
    private final List<StudentReport> studentReports = new ArrayList<>();

    public GroupReport(StudentGroup group) {
        this.group = group;
    }

    public StudentGroup getGroup() {
        return group;
    }

    public List<StudentReport> getStudentReports() {
        return Collections.unmodifiableList(studentReports);
    }

    public void addStudentReport(StudentReport report) {
        studentReports.add(report);
    }
}
