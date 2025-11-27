package ru.nsu.dashkovskii;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс электронной зачетной книжки студента ФИТ.
 * Является фасадом над классом Student для обеспечения обратной совместимости.
 */
public class GradeBook {
    private final Student student;
    private final List<Record> records; // Для обратной совместимости

    /**
     * Конструктор зачетной книжки.
     *
     * @param studentName имя студента
     * @param isPaid обучается ли студент на платной основе
     */
    public GradeBook(String studentName, boolean isPaid) {
        this.student = new Student(studentName, isPaid);
        this.records = new ArrayList<>();
    }

    /**
     * Добавить запись об оценке.
     *
     * @param record запись
     */
    public void addRecord(Record record) {
        records.add(record);
        student.addGrade(
            record.getSemester(),
            record.getSubjectName(),
            record.getControlType(),
            record.getGrade(),
            LocalDate.now()
        );
    }

    /**
     * Вычислить средний балл за все время обучения.
     * Учитываются только последние положительные оценки по предметам.
     *
     * @return средний балл
     */
    public double getAverageGrade() {
        return student.getAverageGrade();
    }

    /**
     * Проверить возможность перевода с платной на бюджетную форму обучения.
     * Требование: отсутствие оценок "удовлетворительно" и "неуд" за ЭКЗАМЕНЫ
     * в последние две сессии.
     *
     * @return true если возможен перевод
     */
    public boolean canTransferToBudget() {
        return student.canTransferToBudget();
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
        return student.canGetRedDiploma();
    }

    /**
     * Проверить возможность получения повышенной стипендии в текущем семестре.
     * Требование: только отличные оценки в последнем семестре по экзаменам и диф. зачетам.
     *
     * @return true если возможна повышенная стипендия
     */
    public boolean canGetIncreasedScholarship() {
        return student.canGetIncreasedScholarship();
    }

    /**
     * Получить имя студента.
     *
     * @return имя студента
     */
    public String getStudentName() {
        return student.getName();
    }

    /**
     * Проверить, обучается ли студент на платной основе.
     *
     * @return true если на платной основе
     */
    public boolean isPaid() {
        return student.isPaid();
    }

    /**
     * Получить список всех записей.
     *
     * @return список записей
     */
    public List<Record> getRecords() {
        return new ArrayList<>(records);
    }

    /**
     * Получить объект студента для прямой работы с новой моделью.
     *
     * @return студент
     */
    public Student getStudent() {
        return student;
    }
}
