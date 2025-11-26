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
}

