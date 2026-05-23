package ru.nsu.dashkovskii.common;

import java.io.Serial;

/**
 * Команда «остановиться» от master'а к worker'у.
 *
 * <p>Шлётся когда ответ уже найден (short-circuit) или работа завершена.
 * Worker должен прервать текущий счёт и закрыть соединение.
 */
public record StopMessage() implements Message {

    @Serial
    private static final long serialVersionUID = 1L;
}
