package ru.nsu.dashkovskii.master;

/**
 * Состояние одного чанка в реестре задач master'а.
 */
public enum TaskStatus {
    /** Лежит в очереди, ждёт назначения. */
    PENDING,
    /** Выдан worker'у, ждём результата до истечения дедлайна. */
    ASSIGNED,
    /** Worker вернул результат. */
    DONE
}
