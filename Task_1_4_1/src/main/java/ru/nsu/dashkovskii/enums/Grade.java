package ru.nsu.dashkovskii.enums;

/**
 * Перечисление возможных оценок.
 */
public enum Grade {
    /** Отлично (5). */
    EXCELLENT(5),
    /** Хорошо (4). */
    GOOD(4),
    /** Удовлетворительно (3). */
    SATISFACTORY(3),
    /** Неудовлетворительно (2). */
    FAILED(2),
    /** Зачтено. */
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
     * Проверить, является ли оценка отличной.
     *
     * @return true если отлично
     */
    public boolean isExcellent() {
        return this == EXCELLENT;
    }

    /**
     * Проверить, является ли оценка удовлетворительной.
     *
     * @return true если удовлетворительно
     */
    public boolean isSatisfactory() {
        return this == SATISFACTORY;
    }

    /**
     * Проверить, является ли оценка неудовлетворительной.
     *
     * @return true если неудовлетворительно
     */
    public boolean isFailed() {
        return this == FAILED;
    }

    /**
     * Проверить, является ли оценка положительной (не провал).
     *
     * @return true если оценка положительная
     */
    public boolean isPassing() {
        return this != FAILED;
    }
}

