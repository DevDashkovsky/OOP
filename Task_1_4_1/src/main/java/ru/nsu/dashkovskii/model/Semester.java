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
     * Получить номер семестра.
     *
     * @return номер семестра
     */
    public int getNumber() {
        return number;
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

    /**
     * Получить последние положительные оценки по всем предметам семестра.
     *
     * @return карта предмет -> оценка
     */
    public Map<String, Grade> getLastPassingGrades() {
        Map<String, Grade> result = new HashMap<>();
        for (Subject subject : subjects.values()) {
            subject.getLastPassingGrade().ifPresent(grade ->
                    result.put(subject.getName(), grade)
            );
        }
        return result;
    }

    /**
     * Получить оценки для диплома (только экзамены и диф. зачёты).
     *
     * @return карта предмет -> оценка
     */
    public Map<String, Grade> getDiplomaGrades() {
        Map<String, Grade> result = new HashMap<>();
        for (Subject subject : subjects.values()) {
            ControlType type = subject.getControlType();
            if (type == ControlType.EXAM || type == ControlType.DIFF_CREDIT) {
                subject.getLastPassingGrade().ifPresent(grade ->
                        result.put(subject.getName(), grade)
                );
            }
        }
        return result;
    }

    /**
     * Проверяет, есть ли в семестре удовлетворительные оценки
     * по экзаменам или диф. зачётам.
     *
     * @return true если есть удовлетворительные или неуд оценки
     */
    public boolean hasSatisfactoryInDiplomaSubjects() {
        return subjects.values().stream()
                .filter(s -> s.getControlType() == ControlType.EXAM
                        || s.getControlType() == ControlType.DIFF_CREDIT)
                .anyMatch(subject -> {
                    Optional<Grade> lastGrade = subject.getLastPassingGrade();
                    return lastGrade.isEmpty()
                            || lastGrade.get().isSatisfactory()
                            || lastGrade.get().isFailed();
                });
    }

    /**
     * Подсчитать количество отличных оценок в дипломных предметах.
     *
     * @return количество отличных оценок
     */
    public long countExcellentInDiplomaSubjects() {
        return subjects.values().stream()
                .filter(s -> s.getControlType() == ControlType.EXAM
                        || s.getControlType() == ControlType.DIFF_CREDIT)
                .filter(subject -> {
                    Optional<Grade> lastGrade = subject.getLastPassingGrade();
                    return lastGrade.isPresent() && lastGrade.get().isExcellent();
                })
                .count();
    }

    /**
     * Подсчитать количество дипломных предметов (экзамены и диф. зачёты).
     *
     * @return количество предметов
     */
    public long countDiplomaSubjects() {
        return subjects.values().stream()
                .filter(s -> s.getControlType() == ControlType.EXAM
                        || s.getControlType() == ControlType.DIFF_CREDIT)
                .count();
    }
}
