package ru.nsu.dashkovskii;

/**
 * Перечисление типов контроля знаний.
 */
public enum ControlType {
    /**
     * Экзамен.
     */
    EXAM,

    /**
     * Дифференцированный зачёт.
     */
    DIFF_CREDIT,

    /**
     * Зачёт.
     */
    CREDIT,

    /**
     * Защита ВКР.
     */
    THESIS,

    /**
     * Другие виды контроля (задание, контрольная, коллоквиум, практика).
     */
    OTHER;

    /**
     * Проверяет, входит ли оценка в приложение к диплому.
     *
     * @return true если входит в приложение
     */
    public boolean isInDiplomaSupplement() {
        return this == EXAM || this == DIFF_CREDIT || this == THESIS;
    }
}

