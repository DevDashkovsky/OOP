package ru.nsu.dashkovskii;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Студент - центральная сущность системы.
 */
public class Student {
    private final String name;
    private boolean isPaid;
    private final Map<Integer, Semester> semesters;
    private Grade thesisGrade;

    /**
     * Конструктор студента.
     *
     * @param name имя студента
     * @param isPaid обучается ли на платной основе
     */
    public Student(String name, boolean isPaid) {
        this.name = name;
        this.isPaid = isPaid;
        this.semesters = new HashMap<>();
        this.thesisGrade = null;
    }

    /**
     * Добавить оценку по предмету.
     *
     * @param semesterNumber номер семестра
     * @param subjectName название предмета
     * @param controlType тип контроля
     * @param grade оценка
     * @param date дата
     */
    public void addGrade(int semesterNumber, String subjectName,
                        ControlType controlType, Grade grade, LocalDate date) {
        if (controlType == ControlType.THESIS) {
            thesisGrade = grade;
            return;
        }

        Semester semester = semesters.get(semesterNumber);
        if (semester == null) {
            semester = new Semester(semesterNumber);
            semesters.put(semesterNumber, semester);
        }
        semester.addGrade(subjectName, controlType, grade, date);
    }

    /**
     * Добавить оценку по предмету (без указания даты - используется текущая).
     *
     * @param semesterNumber номер семестра
     * @param subjectName название предмета
     * @param controlType тип контроля
     * @param grade оценка
     */
    public void addGrade(int semesterNumber, String subjectName,
                        ControlType controlType, Grade grade) {
        addGrade(semesterNumber, subjectName, controlType, grade, LocalDate.now());
    }

    /**
     * Вычислить средний балл за все время обучения.
     * Учитываются только последние положительные оценки по предметам.
     *
     * @return средний балл
     */
    public double getAverageGrade() {
        Map<String, Grade> lastGrades = getAllLastPassingGrades();

        List<Grade> grades = lastGrades.values().stream()
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

    /**
     * Проверить возможность перевода с платной на бюджетную форму.
     * Требование: отсутствие оценок "удовлетворительно" и "неуд" за ЭКЗАМЕНЫ
     * в последние две экзаменационные сессии.
     *
     * @return true если возможен перевод
     */
    public boolean canTransferToBudget() {
        if (!isPaid) {
            return false;
        }

        List<Session> sessions = getLastTwoSessionsWithExams();

        if (sessions.isEmpty()) {
            return false;
        }

        // Проверяем, что в последних двух сессиях нет удовлетворительных оценок по экзаменам
        return sessions.stream()
                .noneMatch(Session::hasExamsSatisfactoryOrFailed);
    }

    /**
     * Проверить возможность получения красного диплома.
     * Требования:
     * 1) 75% оценок в приложении к диплому – "отлично"
     * 2) Отсутствие итоговых оценок "удовлетворительно" по экзаменам и диф. зачетам
     * 3) ВКР на "отлично" (если защищена)
     *
     * @return true если возможен красный диплом
     */
    public boolean canGetRedDiploma() {
        Map<String, Grade> diplomaGrades = getDiplomaGrades();

        if (diplomaGrades.isEmpty()) {
            return false;
        }

        // Проверяем отсутствие удовлетворительных оценок
        boolean hasNoSatisfactory = diplomaGrades.values().stream()
                .noneMatch(g -> g.isSatisfactory() || g.isFailed());

        if (!hasNoSatisfactory) {
            return false;
        }

        // Подсчитываем процент отличных оценок
        long excellentCount = diplomaGrades.values().stream()
                .filter(Grade::isExcellent)
                .count();

        double excellentPercentage = (double) excellentCount / diplomaGrades.size() * 100;

        // Проверяем ВКР (если защищена)
        if (thesisGrade != null && !thesisGrade.isExcellent()) {
            return false;
        }

        return excellentPercentage >= 75.0;
    }

    /**
     * Проверить возможность получения повышенной стипендии.
     * Требование: только отличные оценки в последнем семестре
     * по экзаменам и диф. зачетам.
     *
     * @return true если возможна повышенная стипендия
     */
    public boolean canGetIncreasedScholarship() {
        if (isPaid) {
            return false;
        }

        Optional<Semester> lastSemester = getLastSemester();

        return lastSemester.map(Semester::allExamsAndDiffCreditsExcellent)
                .orElse(false);
    }

    /**
     * Получить последний семестр.
     *
     * @return последний семестр или empty
     */
    private Optional<Semester> getLastSemester() {
        return semesters.keySet().stream()
                .max(Integer::compareTo)
                .map(semesters::get);
    }

    /**
     * Получить последние две сессии с экзаменами.
     *
     * @return список сессий
     */
    private List<Session> getLastTwoSessionsWithExams() {
        return semesters.values().stream()
                .map(Semester::getSession)
                .filter(session -> !session.getExams().isEmpty())
                .sorted((s1, s2) -> Integer.compare(s2.getSemesterNumber(),
                                                   s1.getSemesterNumber()))
                .limit(2)
                .toList();
    }

    /**
     * Получить последние положительные оценки по всем предметам.
     *
     * @return карта предмет -> последняя положительная оценка
     */
    private Map<String, Grade> getAllLastPassingGrades() {
        Map<String, Grade> result = new HashMap<>();

        for (Semester semester : semesters.values()) {
            for (Subject subject : semester.getAllSubjects()) {
                Optional<Grade> grade = subject.getLastPassingGrade();
                grade.ifPresent(g -> result.put(subject.getName(), g));
            }
        }

        return result;
    }

    /**
     * Получить оценки для приложения к диплому.
     * Учитываются только экзамены и диф. зачеты (последние положительные оценки).
     *
     * @return карта предмет -> оценка
     */
    private Map<String, Grade> getDiplomaGrades() {
        Map<String, Grade> result = new HashMap<>();

        for (Semester semester : semesters.values()) {
            for (Subject subject : semester.getAllSubjects()) {
                if (subject.getControlType() == ControlType.EXAM
                        || subject.getControlType() == ControlType.DIFF_CREDIT) {
                    Optional<Grade> grade = subject.getLastPassingGrade();
                    grade.ifPresent(g -> result.put(subject.getName(), g));
                }
            }
        }

        return result;
    }

    /**
     * Перевести на бюджет.
     */
    public void transferToBudget() {
        if (canTransferToBudget()) {
            this.isPaid = false;
        }
    }

    /**
     * Получить имя студента.
     *
     * @return имя
     */
    public String getName() {
        return name;
    }

    /**
     * Проверить, обучается ли на платной основе.
     *
     * @return true если на платной основе
     */
    public boolean isPaid() {
        return isPaid;
    }

    /**
     * Получить все семестры.
     *
     * @return карта номер семестра -> семестр
     */
    public Map<Integer, Semester> getSemesters() {
        return new HashMap<>(semesters);
    }

    /**
     * Получить оценку за ВКР.
     *
     * @return оценка за ВКР или null
     */
    public Grade getThesisGrade() {
        return thesisGrade;
    }

    /**
     * Установить оценку за ВКР.
     *
     * @param thesisGrade оценка
     */
    public void setThesisGrade(Grade thesisGrade) {
        this.thesisGrade = thesisGrade;
    }
}

