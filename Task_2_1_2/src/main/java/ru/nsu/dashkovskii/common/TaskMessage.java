package ru.nsu.dashkovskii.common;

/**
 * Сообщение о выдаче задачи worker'у.
 *
 * @param taskId уникальный идентификатор чанка в рамках одного запуска master'а
 * @param chunk массив чисел для проверки на наличие составного
 */
public record TaskMessage(long taskId, int[] chunk) implements Message {
}
