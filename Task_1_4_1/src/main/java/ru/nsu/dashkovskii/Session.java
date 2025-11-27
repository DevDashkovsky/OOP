package ru.nsu.dashkovskii;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Добавить предмет в сессию.
     *
     * @param subject предмет
     */
    public void addSubject(Subject subject) {
        subjects.put(subject.getName(), subject);
    }

    /**
     * Добавить оценку по предмету (или создать предмет, если его нет).
     *
     * @param subjectName название предмета
     * @param controlType тип контроля
     * @param grade оценка
     * @param date дата попытки
     */
    public void addGrade(String subjectName, ControlType controlType,
                        Grade grade, LocalDate date) {
        Subject subject = subjects.get(subjectName);
        if (subject == null) {
            subject = new Subject(subjectName, controlType);
            subjects.put(subjectName, subject);
        }
        subject.addAttempt(grade, date);
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
     * Получить все предметы сессии.
     *
     * @return список предметов
     */
    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects.values());
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

    /**
     * Проверяет, все ли экзамены сданы на отлично.
     *
     * @return true если все экзамены на 5
     */
    public boolean allExamsExcellent() {
        List<Subject> exams = getExams();
        if (exams.isEmpty()) {
            return false;
        }
        return exams.stream()
                .allMatch(subject -> {
                    Optional<Grade> grade = subject.getLastPassingGrade();
                    return grade.isPresent() && grade.get().isExcellent();
                });
    }
}

