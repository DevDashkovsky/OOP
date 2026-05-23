package ru.nsu.dashkovskii.common;

import java.io.Serial;

/**
 * Ответ worker'а с результатом обработки чанка.
 *
 * @param taskId идентификатор обработанного чанка
 * @param hasComposite true, если в чанке найдено хотя бы одно составное число
 */
public record ResultMessage(long taskId, boolean hasComposite) implements Message {

    @Serial
    private static final long serialVersionUID = 1L;
}
