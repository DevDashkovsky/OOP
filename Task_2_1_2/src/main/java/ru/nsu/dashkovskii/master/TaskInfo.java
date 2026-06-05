package ru.nsu.dashkovskii.master;

/**
 * Описание одного чанка в реестре master'а. Иммутабельная запись —
 * любая модификация состояния создаёт новый экземпляр и кладётся в
 * {@link TaskRegistry} через {@code compute}.
 *
 * @param taskId уникальный id чанка
 * @param chunk сам массив чисел
 * @param status текущее состояние
 * @param assignedWorker id worker'а, которому выдан чанк, или null
 * @param deadlineMillis момент истечения дедлайна в epoch-ms, либо 0 если не ASSIGNED
 */
public record TaskInfo(
        long taskId,
        int[] chunk,
        TaskStatus status,
        String assignedWorker,
        long deadlineMillis) {

    /**
     * Создаёт новую PENDING-задачу.
     *
     * @param taskId уникальный id чанка
     * @param chunk массив чисел
     * @return новая TaskInfo в состоянии PENDING
     */
    public static TaskInfo pending(long taskId, int[] chunk) {
        return new TaskInfo(taskId, chunk, TaskStatus.PENDING, null, 0L);
    }

    /**
     * Возвращает копию с обновлённым статусом и атрибутами назначения.
     *
     * @param worker id worker'а
     * @param deadline момент истечения дедлайна в epoch-ms
     * @return новая ASSIGNED-задача
     */
    public TaskInfo assignTo(String worker, long deadline) {
        return new TaskInfo(taskId, chunk, TaskStatus.ASSIGNED, worker, deadline);
    }

    /**
     * Возвращает копию в состоянии DONE.
     *
     * @return новая DONE-задача
     */
    public TaskInfo asDone() {
        return new TaskInfo(taskId, chunk, TaskStatus.DONE, assignedWorker, deadlineMillis);
    }

    /**
     * Возвращает копию обратно в PENDING (например, при сбое worker'а).
     *
     * @return новая PENDING-задача
     */
    public TaskInfo asPending() {
        return new TaskInfo(taskId, chunk, TaskStatus.PENDING, null, 0L);
    }
}
