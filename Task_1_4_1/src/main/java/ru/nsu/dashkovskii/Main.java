package ru.nsu.dashkovskii;

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
        // Пример 1: Студент на бюджете с хорошими оценками
        System.out.println("=== Пример 1: Студент на бюджете ===");
        GradeBook student1 = new GradeBook("Иванов Иван", false);

        // Первый семестр
        student1.addRecord(new Record("Математический анализ", ControlType.EXAM,
                Grade.EXCELLENT, 1));
        student1.addRecord(new Record("Алгебра", ControlType.EXAM, Grade.EXCELLENT, 1));
        student1.addRecord(new Record("Программирование", ControlType.EXAM,
                Grade.EXCELLENT, 1));
        student1.addRecord(new Record("История", ControlType.DIFF_CREDIT,
                Grade.EXCELLENT, 1));

        // Второй семестр
        student1.addRecord(new Record("Математический анализ", ControlType.EXAM,
                Grade.EXCELLENT, 2));
        student1.addRecord(new Record("Дискретная математика", ControlType.EXAM,
                Grade.EXCELLENT, 2));
        student1.addRecord(new Record("ООП", ControlType.EXAM, Grade.EXCELLENT, 2));
        student1.addRecord(new Record("Физическая культура", ControlType.CREDIT,
                Grade.PASS, 2));

        System.out.println("Студент: " + student1.getStudentName());
        System.out.println("Платная основа: " + (student1.isPaid() ? "Да" : "Нет"));
        System.out.println("Количество записей: " + student1.getRecords().size());
        System.out.println("Средний балл: "
                + String.format("%.2f", student1.getAverageGrade()));
        System.out.println("Повышенная стипендия: "
                + student1.canGetIncreasedScholarship());
        System.out.println("Возможен красный диплом: " + student1.canGetRedDiploma());
        System.out.println();

        // Пример 2: Студент на платной основе
        System.out.println("=== Пример 2: Студент на платной основе ===");
        GradeBook student2 = new GradeBook("Петров Петр", true);

        // Первый семестр
        student2.addRecord(new Record("Математический анализ", ControlType.EXAM,
                Grade.GOOD, 1));
        student2.addRecord(new Record("Алгебра", ControlType.EXAM, Grade.EXCELLENT, 1));
        student2.addRecord(new Record("Программирование", ControlType.EXAM,
                Grade.GOOD, 1));

        // Второй семестр
        student2.addRecord(new Record("Математический анализ", ControlType.EXAM,
                Grade.EXCELLENT, 2));
        student2.addRecord(new Record("Дискретная математика", ControlType.EXAM,
                Grade.EXCELLENT, 2));
        student2.addRecord(new Record("ООП", ControlType.EXAM, Grade.EXCELLENT, 2));

        System.out.println("Студент: " + student2.getStudentName());
        System.out.println("Платная основа: " + (student2.isPaid() ? "Да" : "Нет"));
        System.out.println("Средний балл: "
                + String.format("%.2f", student2.getAverageGrade()));
        System.out.println("Перевод на бюджет: " + student2.canTransferToBudget());
        System.out.println();

        // Пример 3: Студент с удовлетворительными оценками
        System.out.println("=== Пример 3: Студент с удовлетворительными оценками ===");
        GradeBook student3 = new GradeBook("Сидоров Сидор", true);

        // Первый семестр
        student3.addRecord(new Record("Математический анализ", ControlType.EXAM,
                Grade.SATISFACTORY, 1));
        student3.addRecord(new Record("Алгебра", ControlType.EXAM, Grade.GOOD, 1));
        student3.addRecord(new Record("Программирование", ControlType.EXAM,
                Grade.EXCELLENT, 1));

        // Второй семестр
        student3.addRecord(new Record("Математический анализ", ControlType.EXAM,
                Grade.GOOD, 2));
        student3.addRecord(new Record("Дискретная математика", ControlType.EXAM,
                Grade.EXCELLENT, 2));
        student3.addRecord(new Record("ООП", ControlType.EXAM, Grade.GOOD, 2));

        System.out.println("Студент: " + student3.getStudentName());
        System.out.println("Средний балл: "
                + String.format("%.2f", student3.getAverageGrade()));
        System.out.println("Перевод на бюджет: " + student3.canTransferToBudget());
        System.out.println("Возможен красный диплом: " + student3.canGetRedDiploma());
        System.out.println();

        // Пример 4: Проверка красного диплома с 75% отличных оценок
        System.out.println("=== Пример 4: Проверка красного диплома ===");
        GradeBook student4 = new GradeBook("Смирнова Анна", false);

        // Добавляем 8 предметов: 6 отлично, 2 хорошо (75% отлично)
        student4.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        student4.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.EXCELLENT, 1));
        student4.addRecord(new Record("Предмет 3", ControlType.EXAM, Grade.EXCELLENT, 2));
        student4.addRecord(new Record("Предмет 4", ControlType.DIFF_CREDIT,
                Grade.EXCELLENT, 2));
        student4.addRecord(new Record("Предмет 5", ControlType.EXAM, Grade.EXCELLENT, 3));
        student4.addRecord(new Record("Предмет 6", ControlType.EXAM, Grade.EXCELLENT, 3));
        student4.addRecord(new Record("Предмет 7", ControlType.DIFF_CREDIT, Grade.GOOD, 4));
        student4.addRecord(new Record("Предмет 8", ControlType.EXAM, Grade.GOOD, 4));

        System.out.println("Студент: " + student4.getStudentName());
        System.out.println("Средний балл: "
                + String.format("%.2f", student4.getAverageGrade()));
        System.out.println("Возможен красный диплом: " + student4.canGetRedDiploma());
        System.out.println();

        // Пример 5: Использование типа контроля OTHER
        System.out.println("=== Пример 5: Другие виды контроля (задания, контрольные) ===");
        GradeBook student5 = new GradeBook("Кузнецов Алексей", false);

        // Первый семестр - экзамены и другие виды контроля
        student5.addRecord(new Record("Математика", ControlType.EXAM, Grade.EXCELLENT, 1));
        student5.addRecord(new Record("Задание по математике №1", ControlType.OTHER,
                Grade.EXCELLENT, 1));
        student5.addRecord(new Record("Задание по математике №2", ControlType.OTHER,
                Grade.GOOD, 1));
        student5.addRecord(new Record("Контрольная работа", ControlType.OTHER,
                Grade.EXCELLENT, 1));
        student5.addRecord(new Record("Физика", ControlType.EXAM, Grade.EXCELLENT, 1));
        student5.addRecord(new Record("Коллоквиум по физике", ControlType.OTHER,
                Grade.GOOD, 1));
        student5.addRecord(new Record("Программирование", ControlType.DIFF_CREDIT,
                Grade.EXCELLENT, 1));

        System.out.println("Студент: " + student5.getStudentName());
        System.out.println("Всего записей в зачетке: " + student5.getRecords().size());
        System.out.println("Средний балл (включая все оценки): "
                + String.format("%.2f", student5.getAverageGrade()));
        System.out.println("Возможен красный диплом: " + student5.canGetRedDiploma());
        System.out.println("(Задания и контрольные учитываются в среднем балле,");
        System.out.println(" но не влияют на красный диплом)");
        System.out.println();
    }
}
