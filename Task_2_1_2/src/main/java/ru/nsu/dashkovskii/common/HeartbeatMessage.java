package ru.nsu.dashkovskii.common;

import java.io.Serial;

/**
 * Периодический сигнал «живости» от worker'а к master'у.
 *
 * <p>Первый heartbeat после установления соединения используется master'ом
 * для идентификации подключившегося worker'а.
 *
 * @param workerId сгенерированный worker'ом стабильный идентификатор узла
 */
public record HeartbeatMessage(String workerId) implements Message {

    @Serial
    private static final long serialVersionUID = 1L;
}
