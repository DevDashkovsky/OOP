package ru.nsu.dashkovskii;

import ru.nsu.dashkovskii.enums.ControlType;
import ru.nsu.dashkovskii.enums.Grade;
import ru.nsu.dashkovskii.gradebook.GradeBook;
import ru.nsu.dashkovskii.model.Semester;
import ru.nsu.dashkovskii.model.Student;
import ru.nsu.dashkovskii.model.Subject;

/**
 * Главный класс для демонстрации работы электронной зачетной книжки.
 */
public class Main {
    /**
     * Точка входа в программу.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        demonstrateGradeBook();
    }

    /**
     * Демонстрация работы зачётной книжки с пересдачами.
     */
    private static void demonstrateGradeBook() {
        System.out.println("=== ЭЛЕКТРОННАЯ ЗАЧЁТНАЯ КНИЖКА ===\n");

        demonstrateRetake();
        demonstrateTransferToBudget();
        demonstrateNoTransferWithSatisfactory();
        demonstrateRedDiploma();
        demonstrateIncreasedScholarship();
        demonstrateMultipleRetakes();
    }

    /**
     * Пример 1: Пересдача экзамена.
     */
    private static void demonstrateRetake() {
        System.out.println("--- Пример 1: Пересдача экзамена ---");
        Student student = new Student("Петров Петр", false);
        GradeBook gradeBook = new GradeBook(student);

        gradeBook.addGrade(1, "Математический анализ",
                ControlType.EXAM, Grade.FAILED);
        System.out.println("Первая попытка: НЕУД");

        gradeBook.addGrade(1, "Математический анализ",
                ControlType.EXAM, Grade.GOOD);
        System.out.println("Вторая попытка: 4");

        gradeBook.addGrade(1, "Алгебра", ControlType.EXAM, Grade.EXCELLENT);
        gradeBook.addGrade(1, "Программирование",
                ControlType.EXAM, Grade.EXCELLENT);

        System.out.println("Средний балл: "
                + String.format("%.2f", gradeBook.getAverageGrade()));
        System.out.println(
                "(Учитывается только последняя положительная оценка)\n");
    }

    /**
     * Пример 2: Перевод на бюджет.
     */
    private static void demonstrateTransferToBudget() {
        System.out.println("--- Пример 2: Перевод на бюджет ---");
        Student student = new Student("Сидорова Анна", true);
        GradeBook gradeBook = new GradeBook(student);

        gradeBook.addGrade(1, "Математика",
                ControlType.EXAM, Grade.EXCELLENT);
        gradeBook.addGrade(1, "Физика",
                ControlType.EXAM, Grade.EXCELLENT);
        gradeBook.addGrade(1, "История",
                ControlType.DIFF_CREDIT, Grade.GOOD);

        gradeBook.addGrade(2, "Математика",
                ControlType.EXAM, Grade.EXCELLENT);
        gradeBook.addGrade(2, "Физика",
                ControlType.EXAM, Grade.GOOD);

        System.out.println("Студент: " + student.getName());
        System.out.println("Платная основа: " + student.isPaid());
        System.out.println("Может перейти на бюджет: "
                + gradeBook.canTransferToBudget());
        System.out.println();
    }

    /**
     * Пример 3: Студент с тройкой - не может на бюджет.
     */
    private static void demonstrateNoTransferWithSatisfactory() {
        System.out.println(
                "--- Пример 3: Неуд в последних сессиях ---");
        Student student = new Student("Кузнецов Алексей", true);
        GradeBook gradeBook = new GradeBook(student);

        gradeBook.addGrade(1, "Математика",
                ControlType.EXAM, Grade.EXCELLENT);
        gradeBook.addGrade(1, "Физика",
                ControlType.EXAM, Grade.GOOD);
        gradeBook.addGrade(2, "Математика",
                ControlType.EXAM, Grade.SATISFACTORY);

        System.out.println("Студент: " + student.getName());
        System.out.println("Может перейти на бюджет: "
                + gradeBook.canTransferToBudget());
        System.out.println(
                "(Есть тройка по экзамену во 2-м семестре)\n");
    }

    /**
     * Пример 4: Красный диплом.
     */
    private static void demonstrateRedDiploma() {
        System.out.println("--- Пример 4: Красный диплом ---");
        Student student = new Student("Смирнова Мария", false);
        GradeBook gradeBook = new GradeBook(student);

        gradeBook.addGrade(1, "Предмет 1",
                ControlType.EXAM, Grade.EXCELLENT);
        gradeBook.addGrade(1, "Предмет 2",
                ControlType.EXAM, Grade.EXCELLENT);
        gradeBook.addGrade(2, "Предмет 3",
                ControlType.DIFF_CREDIT, Grade.EXCELLENT);
        gradeBook.addGrade(2, "Предмет 4",
                ControlType.EXAM, Grade.EXCELLENT);
        gradeBook.addGrade(3, "Предмет 5",
                ControlType.EXAM, Grade.GOOD);
        gradeBook.addGrade(3, "Предмет 6",
                ControlType.EXAM, Grade.EXCELLENT);
        gradeBook.addGrade(4, "Предмет 7",
                ControlType.DIFF_CREDIT, Grade.EXCELLENT);
        gradeBook.addGrade(4, "Предмет 8",
                ControlType.EXAM, Grade.GOOD);

        gradeBook.setThesisGrade(Grade.EXCELLENT);

        System.out.println("Студент: " + student.getName());
        System.out.println("Средний балл: "
                + String.format("%.2f", gradeBook.getAverageGrade()));
        System.out.println("Может получить красный диплом: "
                + gradeBook.canGetRedDiploma());
        System.out.println();
    }

    /**
     * Пример 5: Повышенная стипендия.
     */
    private static void demonstrateIncreasedScholarship() {
        System.out.println("--- Пример 5: Повышенная стипендия ---");
        Student student = new Student("Васильев Василий", false);
        GradeBook gradeBook = new GradeBook(student);

        gradeBook.addGrade(1, "Математика",
                ControlType.EXAM, Grade.GOOD);
        gradeBook.addGrade(1, "Физика",
                ControlType.EXAM, Grade.EXCELLENT);
        gradeBook.addGrade(2, "Математика",
                ControlType.EXAM, Grade.EXCELLENT);
        gradeBook.addGrade(2, "Физика",
                ControlType.EXAM, Grade.EXCELLENT);
        gradeBook.addGrade(2, "Программирование",
                ControlType.DIFF_CREDIT, Grade.EXCELLENT);

        System.out.println("Студент: " + student.getName());
        System.out.println("Средний балл: "
                + String.format("%.2f", gradeBook.getAverageGrade()));
        System.out.println("Повышенная стипендия: "
                + gradeBook.canGetIncreasedScholarship());
        System.out.println(
                "(Все экзамены и диф.зачеты последнего семестра на 5)\n");
    }

    /**
     * Пример 6: Множественные пересдачи.
     */
    private static void demonstrateMultipleRetakes() {
        System.out.println(
                "--- Пример 6: Множественные пересдачи ---");
        Student student = new Student("Новиков Николай", false);
        GradeBook gradeBook = new GradeBook(student);

        gradeBook.addGrade(1, "Сложный предмет",
                ControlType.EXAM, Grade.FAILED);
        System.out.println("Попытка 1: НЕУД");

        gradeBook.addGrade(1, "Сложный предмет",
                ControlType.EXAM, Grade.FAILED);
        System.out.println("Попытка 2: НЕУД");

        gradeBook.addGrade(1, "Сложный предмет",
                ControlType.EXAM, Grade.SATISFACTORY);
        System.out.println("Попытка 3: 3");

        gradeBook.addGrade(1, "Легкий предмет",
                ControlType.EXAM, Grade.EXCELLENT);

        System.out.println("Средний балл: "
                + String.format("%.2f", gradeBook.getAverageGrade()));
        System.out.println(
                "(В расчете только последняя положительная оценка: 3)");

        Semester semester = gradeBook.getSemesters().get(1);
        Subject subject = semester.getSubject("Сложный предмет")
                .orElse(null);
        if (subject != null) {
            System.out.println("Всего попыток: "
                    + subject.getAttemptsCount());
            System.out.println("Проваленных попыток: "
                    + subject.getFailedAttemptsCount());
        }
    }
}
