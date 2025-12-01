package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ru.nsu.dashkovskii.enums.ControlType;
import ru.nsu.dashkovskii.enums.Grade;

/**
 * Сессия - экзаменационный период в конце семестра.
 */
public class Session {
    private final int semesterNumber;
    private final Map<String, Subject> subjects;

    /**
     * Конструктор сессии.
     *
     * @param semesterNumber номер семестра
     */
    public Session(int semesterNumber) {
        this.semesterNumber = semesterNumber;
        this.subjects = new HashMap<>();
    }

    /**
     * Добавить оценку по предмету (или создать предмет, если его нет).
     *
     * @param subjectName название предмета
     * @param controlType тип контроля
     * @param grade оценка
     */
    public void addGrade(String subjectName, ControlType controlType, Grade grade) {
        Subject subject = subjects.get(subjectName);
        if (subject == null) {
            subject = new Subject(subjectName, controlType);
            subjects.put(subjectName, subject);
        }
        subject.addAttempt(grade);
    }

    /**
     * Получить все экзамены сессии.
     *
     * @return список экзаменов
     */
    public List<Subject> getExams() {
        return subjects.values().stream()
                .filter(s -> s.getControlType() == ControlType.EXAM)
                .toList();
    }

    /**
     * Проверяет, есть ли в сессии неудовлетворительные оценки по экзаменам.
     *
     * @return true если есть хотя бы одна удовлетворительная или неуд оценка
     */
    public boolean hasExamsSatisfactoryOrFailed() {
        return getExams().stream()
                .anyMatch(subject -> {
                    Optional<Grade> lastGrade = subject.getLastPassingGrade();
                    return lastGrade.isEmpty()
                            || lastGrade.get().isSatisfactory();
                });
    }

    /**
     * Получить номер семестра.
     *
     * @return номер семестра
     */
    public int getSemesterNumber() {
        return semesterNumber;
    }
}
