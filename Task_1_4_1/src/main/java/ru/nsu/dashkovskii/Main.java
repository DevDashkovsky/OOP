package ru.nsu.dashkovskii;

import java.time.LocalDate;

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
        demonstrateNewApi();
    }

    /**
     * Демонстрация API с поддержкой пересдач.
     */
    private static void demonstrateNewApi() {
        System.out.println("=== ЭЛЕКТРОННАЯ ЗАЧЁТНАЯ КНИЖКА ===\n");

        // Пример 1: Студент с пересдачей
        System.out.println("--- Пример 1: Пересдача экзамена ---");
        Student student1 = new Student("Петров Петр", false);

        // Первая попытка - провалил
        student1.addGrade(1, "Математический анализ", ControlType.EXAM,
                Grade.FAILED, LocalDate.of(2024, 1, 15));
        System.out.println("Первая попытка: НЕУД");

        // Вторая попытка - сдал на 4
        student1.addGrade(1, "Математический анализ", ControlType.EXAM,
                Grade.GOOD, LocalDate.of(2024, 1, 22));
        System.out.println("Вторая попытка: 4");

        student1.addGrade(1, "Алгебра", ControlType.EXAM,
                Grade.EXCELLENT, LocalDate.of(2024, 1, 16));
        student1.addGrade(1, "Программирование", ControlType.EXAM,
                Grade.EXCELLENT, LocalDate.of(2024, 1, 17));

        System.out.println("Средний балл: "
                + String.format("%.2f", student1.getAverageGrade()));
        System.out.println("(Учитывается только последняя положительная оценка)\n");

        // Пример 2: Проверка перевода на бюджет
        System.out.println("--- Пример 2: Перевод на бюджет ---");
        Student student2 = new Student("Сидорова Анна", true);

        // Первый семестр - все отлично
        student2.addGrade(1, "Математика", ControlType.EXAM, Grade.EXCELLENT);
        student2.addGrade(1, "Физика", ControlType.EXAM, Grade.EXCELLENT);
        student2.addGrade(1, "История", ControlType.DIFF_CREDIT, Grade.GOOD);

        // Второй семестр - тоже все хорошо
        student2.addGrade(2, "Математика", ControlType.EXAM, Grade.EXCELLENT);
        student2.addGrade(2, "Физика", ControlType.EXAM, Grade.GOOD);

        System.out.println("Студент: " + student2.getName());
        System.out.println("Платная основа: " + student2.isPaid());
        System.out.println("Может перейти на бюджет: "
                + student2.canTransferToBudget());
        System.out.println();

        // Пример 3: Студент с неудовлетворительной - не может на бюджет
        System.out.println("--- Пример 3: Неуд в последних сессиях ---");
        Student student3 = new Student("Кузнецов Алексей", true);

        student3.addGrade(1, "Математика", ControlType.EXAM, Grade.EXCELLENT);
        student3.addGrade(1, "Физика", ControlType.EXAM, Grade.GOOD);
        student3.addGrade(2, "Математика", ControlType.EXAM,
                Grade.SATISFACTORY);

        System.out.println("Студент: " + student3.getName());
        System.out.println("Может перейти на бюджет: "
                + student3.canTransferToBudget());
        System.out.println("(Есть тройка по экзамену во 2-м семестре)\n");

        // Пример 4: Красный диплом
        System.out.println("--- Пример 4: Красный диплом ---");
        Student student4 = new Student("Смирнова Мария", false);

        // 8 предметов: 6 отлично (75%), 2 хорошо
        student4.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        student4.addGrade(1, "Предмет 2", ControlType.EXAM, Grade.EXCELLENT);
        student4.addGrade(2, "Предмет 3", ControlType.DIFF_CREDIT,
                Grade.EXCELLENT);
        student4.addGrade(2, "Предмет 4", ControlType.EXAM, Grade.EXCELLENT);
        student4.addGrade(3, "Предмет 5", ControlType.EXAM, Grade.GOOD);
        student4.addGrade(3, "Предмет 6", ControlType.EXAM, Grade.EXCELLENT);
        student4.addGrade(4, "Предмет 7", ControlType.DIFF_CREDIT,
                Grade.EXCELLENT);
        student4.addGrade(4, "Предмет 8", ControlType.EXAM, Grade.GOOD);

        student4.setThesisGrade(Grade.EXCELLENT);

        System.out.println("Студент: " + student4.getName());
        System.out.println("Средний балл: "
                + String.format("%.2f", student4.getAverageGrade()));
        System.out.println("Может получить красный диплом: "
                + student4.canGetRedDiploma());
        System.out.println();

        // Пример 5: Повышенная стипендия
        System.out.println("--- Пример 5: Повышенная стипендия ---");
        Student student5 = new Student("Васильев Василий", false);

        student5.addGrade(1, "Математика", ControlType.EXAM, Grade.GOOD);
        student5.addGrade(1, "Физика", ControlType.EXAM, Grade.EXCELLENT);
        student5.addGrade(2, "Математика", ControlType.EXAM, Grade.EXCELLENT);
        student5.addGrade(2, "Физика", ControlType.EXAM, Grade.EXCELLENT);
        student5.addGrade(2, "Программирование", ControlType.DIFF_CREDIT,
                Grade.EXCELLENT);

        System.out.println("Студент: " + student5.getName());
        System.out.println("Средний балл: "
                + String.format("%.2f", student5.getAverageGrade()));
        System.out.println("Повышенная стипендия: "
                + student5.canGetIncreasedScholarship());
        System.out.println("(Все экзамены и диф.зачеты последнего семестра на 5)\n");

        // Пример 6: Множественные пересдачи
        System.out.println("--- Пример 6: Множественные пересдачи ---");
        Student student6 = new Student("Новиков Николай", false);

        student6.addGrade(1, "Сложный предмет", ControlType.EXAM,
                Grade.FAILED, LocalDate.of(2024, 1, 15));
        System.out.println("Попытка 1: НЕУД");

        student6.addGrade(1, "Сложный предмет", ControlType.EXAM,
                Grade.FAILED, LocalDate.of(2024, 1, 22));
        System.out.println("Попытка 2: НЕУД");

        student6.addGrade(1, "Сложный предмет", ControlType.EXAM,
                Grade.SATISFACTORY, LocalDate.of(2024, 1, 29));
        System.out.println("Попытка 3: 3");

        student6.addGrade(1, "Легкий предмет", ControlType.EXAM,
                Grade.EXCELLENT);

        System.out.println("Средний балл: "
                + String.format("%.2f", student6.getAverageGrade()));
        System.out.println("(В расчете только последняя положительная оценка: 3)");

        Semester semester = student6.getSemesters().get(1);
        Subject subject = semester.getSubject("Сложный предмет").orElse(null);
        if (subject != null) {
            System.out.println("Всего попыток: " + subject.getAttemptsCount());
            System.out.println("Проваленных попыток: "
                    + subject.getFailedAttemptsCount());
        }
    }
}
