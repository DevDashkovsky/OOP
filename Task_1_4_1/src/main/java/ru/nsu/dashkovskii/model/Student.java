package ru.nsu.dashkovskii.model;

/**
 * Студент - информация о личности студента.
 * Содержит только базовую информацию, не связанную с успеваемостью.
 */
public class Student {
    private final String name;
    private final boolean isPaid;

    /**
     * Конструктор студента.
     *
     * @param name имя студента
     * @param isPaid обучается ли на платной основе
     */
    public Student(String name, boolean isPaid) {
        this.name = name;
        this.isPaid = isPaid;
    }

    /**
     * Получить имя студента.
     *
     * @return имя студента
     */
    public String getName() {
        return name;
    }

    /**
     * Проверить, обучается ли студент на платной основе.
     *
     * @return true если на платной основе
     */
    public boolean isPaid() {
        return isPaid;
    }

    @Override
    public String toString() {
        return "Student{name='" + name + "', isPaid=" + isPaid + '}';
    }
}
