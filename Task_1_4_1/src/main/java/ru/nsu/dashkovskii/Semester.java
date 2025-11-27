package ru.nsu.dashkovskii;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Семестр обучения студента.
 */
public class Semester {
    private final int number;
    private final Map<String, Subject> subjects;
    private final Session session;

    /**
     * Конструктор семестра.
     *
     * @param number номер семестра
     */
    public Semester(int number) {
        this.number = number;
        this.subjects = new HashMap<>();
        this.session = new Session(number);
    }

    /**
     * Добавить результат по предмету.
     *
     * @param subjectName название предмета
     * @param controlType тип контроля
     * @param grade оценка
     * @param date дата
     */
    public void addGrade(String subjectName, ControlType controlType,
                        Grade grade, LocalDate date) {
        Subject subject = subjects.get(subjectName);
        if (subject == null) {
            subject = new Subject(subjectName, controlType);
            subjects.put(subjectName, subject);
        }
        subject.addAttempt(grade, date);

        // Если это экзамен или диф. зачет, добавляем в сессию
        if (controlType == ControlType.EXAM || controlType == ControlType.DIFF_CREDIT) {
            session.addGrade(subjectName, controlType, grade, date);
        }
    }

    /**
     * Получить предмет по названию.
     *
     * @param subjectName название предмета
     * @return предмет или empty
     */
    public Optional<Subject> getSubject(String subjectName) {
        return Optional.ofNullable(subjects.get(subjectName));
    }

    /**
     * Получить все предметы семестра.
     *
     * @return список предметов
     */
    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects.values());
    }

    /**
     * Получить сессию семестра.
     *
     * @return сессия
     */
    public Session getSession() {
        return session;
    }

    /**
     * Получить номер семестра.
     *
     * @return номер
     */
    public int getNumber() {
        return number;
    }

    /**
     * Проверяет, все ли экзамены и диф. зачеты сданы на отлично.
     *
     * @return true если все на 5
     */
    public boolean allExamsAndDiffCreditsExcellent() {
        return subjects.values().stream()
                .filter(s -> s.getControlType() == ControlType.EXAM
                          || s.getControlType() == ControlType.DIFF_CREDIT)
                .allMatch(subject -> {
                    Optional<Grade> grade = subject.getLastPassingGrade();
                    return grade.isPresent() && grade.get().isExcellent();
                });
    }

    /**
     * Вычислить средний балл за семестр (только положительные оценки).
     *
     * @return средний балл
     */
    public double getAverageGrade() {
        List<Grade> grades = subjects.values().stream()
                .map(Subject::getLastPassingGrade)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(g -> g != Grade.PASS)
                .toList();

        if (grades.isEmpty()) {
            return 0.0;
        }

        return grades.stream()
                .mapToInt(Grade::getValue)
                .average()
                .orElse(0.0);
    }
}

