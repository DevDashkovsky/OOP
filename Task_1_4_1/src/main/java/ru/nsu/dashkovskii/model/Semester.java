package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ru.nsu.dashkovskii.enums.ControlType;
import ru.nsu.dashkovskii.enums.Grade;

/**
 * Семестр обучения студента.
 * Содержит информацию о сессии и всех предметах семестра.
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
     * Добавить оценку по предмету.
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

        // Добавляем в сессию, если это экзамен или диф. зачет
        if (controlType == ControlType.EXAM || controlType == ControlType.DIFF_CREDIT) {
            session.addGrade(subjectName, controlType, grade);
        }
    }

    /**
     * Получить предмет по названию.
     *
     * @param subjectName название предмета
     * @return предмет или empty если не найден
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
     * Проверяет, все ли экзамены и диф. зачеты сданы на отлично.
     *
     * @return true если все на отлично
     */
    public boolean allExamsAndDiffCreditsExcellent() {
        return subjects.values().stream()
                .filter(s -> s.getControlType() == ControlType.EXAM
                        || s.getControlType() == ControlType.DIFF_CREDIT)
                .allMatch(subject -> {
                    Optional<Grade> lastGrade = subject.getLastPassingGrade();
                    return lastGrade.isPresent() && lastGrade.get().isExcellent();
                });
    }
}
