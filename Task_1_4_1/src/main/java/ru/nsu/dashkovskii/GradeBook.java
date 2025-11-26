package ru.nsu.dashkovskii;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс электронной зачетной книжки студента ФИТ.
 */
public class GradeBook {
    private final String studentName;
    private final boolean isPaid;
    private final List<Record> records;
    private Grade thesisGrade;

    /**
     * Конструктор зачетной книжки.
     *
     * @param studentName имя студента
     * @param isPaid обучается ли студент на платной основе
     */
    public GradeBook(String studentName, boolean isPaid) {
        this.studentName = studentName;
        this.isPaid = isPaid;
        this.records = new ArrayList<>();
        this.thesisGrade = null;
    }

    /**
     * Добавить запись об оценке.
     *
     * @param record запись
     */
    public void addRecord(Record record) {
        if (record.getControlType() == ControlType.THESIS) {
            this.thesisGrade = record.getGrade();
        }
        records.add(record);
    }

    /**
     * Вычислить средний балл за все время обучения.
     * Учитываются только оценки с числовым значением (исключая зачеты).
     *
     * @return средний балл
     */
    public double getAverageGrade() {
        List<Record> gradedRecords = records.stream()
                .filter(r -> r.getGrade() != Grade.PASS)
                .collect(Collectors.toList());

        if (gradedRecords.isEmpty()) {
            return 0.0;
        }

        int sum = gradedRecords.stream()
                .mapToInt(r -> r.getGrade().getValue())
                .sum();

        return (double) sum / gradedRecords.size();
    }

    /**
     * Проверить возможность перевода с платной на бюджетную форму обучения.
     * Требование: отсутствие оценок "удовлетворительно" за ЭКЗАМЕНЫ
     * в последние две сессии.
     *
     * @return true если возможен перевод
     */
    public boolean canTransferToBudget() {
        if (!isPaid) {
            return false;
        }

        // Найти последние две сессии с экзаменами
        List<Integer> semestersWithExams = records.stream()
                .filter(r -> r.getControlType() == ControlType.EXAM)
                .map(Record::getSemester)
                .distinct()
                .sorted((a, b) -> b - a)
                .collect(Collectors.toList());

        if (semestersWithExams.size() < 2) {
            // Если меньше двух сессий с экзаменами, проверяем все доступные
            return records.stream()
                    .filter(r -> r.getControlType() == ControlType.EXAM)
                    .noneMatch(r -> r.getGrade().isSatisfactory());
        }

        // Берем последние две сессии
        int lastSemester = semestersWithExams.get(0);
        int prevSemester = semestersWithExams.get(1);

        return records.stream()
                .filter(r -> r.getControlType() == ControlType.EXAM)
                .filter(r -> r.getSemester() == lastSemester || r.getSemester() == prevSemester)
                .noneMatch(r -> r.getGrade().isSatisfactory());
    }

    /**
     * Проверить возможность получения красного диплома.
     * Требования:
     * 1) 75% оценок в приложении к диплому (последняя оценка по предмету) – "отлично"
     * 2) Отсутствие итоговых оценок "удовлетворительно" по экзаменам и диф. зачетам
     * 3) ВКР на "отлично" (если уже защищена)
     *
     * @return true если возможен красный диплом
     */
    public boolean canGetRedDiploma() {
        // Получаем последние оценки по каждому предмету для приложения к диплому
        Map<String, Record> lastGrades = getLastGradesForDiploma();

        if (lastGrades.isEmpty()) {
            return false;
        }

        // Проверяем отсутствие удовлетворительных оценок
        boolean hasNoSatisfactory = lastGrades.values().stream()
                .noneMatch(r -> r.getGrade().isSatisfactory());

        if (!hasNoSatisfactory) {
            return false;
        }

        // Подсчитываем процент отличных оценок
        long excellentCount = lastGrades.values().stream()
                .filter(r -> r.getGrade().isExcellent())
                .count();

        double excellentPercentage = (double) excellentCount / lastGrades.size() * 100;

        // Проверяем ВКР (если уже защищена)
        if (thesisGrade != null && !thesisGrade.isExcellent()) {
            return false;
        }

        return excellentPercentage >= 75.0;
    }

    /**
     * Проверить возможность получения повышенной стипендии в текущем семестре.
     * Требование: только отличные оценки в последнем семестре по экзаменам и диф. зачетам.
     *
     * @return true если возможна повышенная стипендия
     */
    public boolean canGetIncreasedScholarship() {
        if (isPaid) {
            return false;
        }

        // Находим последний семестр
        int lastSemester = records.stream()
                .mapToInt(Record::getSemester)
                .max()
                .orElse(0);

        if (lastSemester == 0) {
            return false;
        }

        // Проверяем оценки последнего семестра по экзаменам и диф. зачетам
        List<Record> lastSemesterGrades = records.stream()
                .filter(r -> r.getSemester() == lastSemester)
                .filter(r -> r.getControlType() == ControlType.EXAM
                        || r.getControlType() == ControlType.DIFF_CREDIT)
                .collect(Collectors.toList());

        if (lastSemesterGrades.isEmpty()) {
            return false;
        }

        return lastSemesterGrades.stream()
                .allMatch(r -> r.getGrade().isExcellent());
    }

    /**
     * Получить последние оценки по каждому предмету для приложения к диплому.
     * Учитываются только экзамены, диф. зачеты и ВКР.
     *
     * @return карта предмет -> последняя запись
     */
    private Map<String, Record> getLastGradesForDiploma() {
        Map<String, Record> lastGrades = new HashMap<>();

        for (Record record : records) {
            if (record.getControlType().isInDiplomaSupplement()) {
                String subject = record.getSubjectName();
                Record existing = lastGrades.get(subject);

                if (existing == null || record.getSemester() > existing.getSemester()) {
                    lastGrades.put(subject, record);
                }
            }
        }

        return lastGrades;
    }

    /**
     * Получить имя студента.
     *
     * @return имя студента
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * Проверить, обучается ли студент на платной основе.
     *
     * @return true если на платной основе
     */
    public boolean isPaid() {
        return isPaid;
    }

    /**
     * Получить список всех записей.
     *
     * @return список записей
     */
    public List<Record> getRecords() {
        return new ArrayList<>(records);
    }
}

