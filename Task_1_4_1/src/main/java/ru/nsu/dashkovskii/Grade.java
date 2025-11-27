package ru.nsu.dashkovskii;

/**
 * Перечисление возможных оценок.
 */
public enum Grade {
    /**
     * Оценка "отлично" (5).
     */
    EXCELLENT(5),

    /**
     * Оценка "хорошо" (4).
     */
    GOOD(4),

    /**
     * Оценка "удовлетворительно" (3).
     */
    SATISFACTORY(3),

    /**
     * Оценка "неудовлетворительно" (2) - не сдал.
     */
    FAILED(2),

    /**
     * Зачет.
     */
    PASS(0);

    private final int value;

    Grade(int value) {
        this.value = value;
    }

    /**
     * Получить числовое значение оценки.
     *
     * @return числовое значение
     */
    public int getValue() {
        return value;
    }

    /**
     * Проверяет, является ли оценка отличной.
     *
     * @return true если оценка "отлично"
     */
    public boolean isExcellent() {
        return this == EXCELLENT;
    }

    /**
     * Проверяет, является ли оценка удовлетворительной.
     *
     * @return true если оценка "удовлетворительно"
     */
    public boolean isSatisfactory() {
        return this == SATISFACTORY;
    }

    /**
     * Проверяет, является ли оценка неудовлетворительной.
     *
     * @return true если оценка "неудовлетворительно"
     */
    public boolean isFailed() {
        return this == FAILED;
    }

    /**
     * Проверяет, является ли оценка положительной (не неуд).
     *
     * @return true если оценка положительная
     */
    public boolean isPassing() {
        return this != FAILED;
    }
}
