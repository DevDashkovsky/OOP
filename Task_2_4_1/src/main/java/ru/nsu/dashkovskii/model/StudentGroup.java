package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Учебная группа.
 */
public final class StudentGroup {
    private final String name;
    private final List<Student> students = new ArrayList<>();

    public StudentGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Student> getStudents() {
        return Collections.unmodifiableList(students);
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    /** Ищет студента по github-нику; возвращает {@code null}, если не найден. */
    public Student findByNick(String nick) {
        return students.stream()
                .filter(s -> s.getGithubNick().equals(nick))
                .findFirst().orElse(null);
    }
}
