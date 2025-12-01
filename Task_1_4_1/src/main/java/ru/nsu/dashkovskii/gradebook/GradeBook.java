package ru.nsu.dashkovskii.gradebook;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ru.nsu.dashkovskii.enums.ControlType;
import ru.nsu.dashkovskii.enums.Grade;
import ru.nsu.dashkovskii.model.Semester;
import ru.nsu.dashkovskii.model.Session;
import ru.nsu.dashkovskii.model.Student;

/**
 * Класс электронной зачетной книжки студента ФИТ.
 * Содержит всю информацию об оценках, семестрах и выполняет все расчёты.
 * Зачётная книжка не может существовать без студента (композиция).
 */
public class GradeBook {
    private final Student student;
    private final Map<Integer, Semester> semesters;
    private Grade thesisGrade;

    /**
     * Конструктор зачетной книжки.
     *
     * @param student студент, владелец зачётной книжки
     */
    public GradeBook(Student student) {
        this.student = student;
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
     */
    public void addGrade(int semesterNumber, String subjectName,
                        ControlType controlType, Grade grade) {
        if (controlType == ControlType.THESIS) {
            thesisGrade = grade;
            return;
        }

        Semester semester = semesters.get(semesterNumber);
        if (semester == null) {
            semester = new Semester(semesterNumber);
            semesters.put(semesterNumber, semester);
        }
        semester.addGrade(subjectName, controlType, grade);
    }

    /**
     * Установить оценку за ВКР.
     *
     * @param grade оценка
     */
    public void setThesisGrade(Grade grade) {
        this.thesisGrade = grade;
    }

    /**
     * Вычислить средний балл за все время обучения.
     * Учитываются только последние положительные оценки по предметам.
     *
     * @return средний балл
     */
    public double getAverageGrade() {
        List<Grade> allGrades = semesters.values().stream()
                .flatMap(semester -> semester.getLastPassingGrades().values().stream())
                .filter(g -> g != Grade.PASS)
                .toList();

        if (allGrades.isEmpty()) {
            return 0.0;
        }

        return allGrades.stream()
                .mapToInt(Grade::getValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Проверить возможность перевода с платной на бюджетную форму обучения.
     * Требование: отсутствие оценок "удовлетворительно" и "неуд" за ЭКЗАМЕНЫ
     * в последние две экзаменационные сессии.
     *
     * @return true если возможен перевод
     */
    public boolean canTransferToBudget() {
        if (!student.isPaid()) {
            return false;
        }

        List<Session> sessions = getLastTwoSessionsWithExams();

        if (sessions.isEmpty()) {
            return false;
        }

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
        // Проверяем отсутствие удовлетворительных оценок используя методы семестров
        boolean hasNoSatisfactory = semesters.values().stream()
                .noneMatch(Semester::hasSatisfactoryInDiplomaSubjects);

        if (!hasNoSatisfactory) {
            return false;
        }

        // Подсчитываем процент отличных оценок агрегируя результаты семестров
        long totalExcellent = semesters.values().stream()
                .mapToLong(Semester::countExcellentInDiplomaSubjects)
                .sum();

        long totalDiplomaSubjects = semesters.values().stream()
                .mapToLong(Semester::countDiplomaSubjects)
                .sum();

        if (totalDiplomaSubjects == 0) {
            return false;
        }

        double excellentPercentage = (double) totalExcellent / totalDiplomaSubjects * 100;

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
        if (student.isPaid()) {
            return false;
        }

        Optional<Semester> lastSemester = getLastSemester();

        return lastSemester.map(Semester::allExamsAndDiffCreditsExcellent)
                .orElse(false);
    }

    /**
     * Получить студента.
     *
     * @return студент
     */
    public Student getStudent() {
        return student;
    }

    /**
     * Получить все семестры.
     *
     * @return карта семестров
     */
    public Map<Integer, Semester> getSemesters() {
        return new HashMap<>(semesters);
    }

    /**
     * Получить оценку за ВКР.
     *
     * @return оценка за ВКР или null если не защищена
     */
    public Grade getThesisGrade() {
        return thesisGrade;
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
}
