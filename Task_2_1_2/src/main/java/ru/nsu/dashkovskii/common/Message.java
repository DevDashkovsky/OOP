package ru.nsu.dashkovskii.common;

import java.io.Serializable;

/**
 * Корневой sealed-тип всех сообщений протокола между мастером и worker'ом.
 *
 * <p>Сообщения сериализуются через {@link java.io.ObjectOutputStream} и
 * передаются по TCP. Конкретные подтипы — {@link TaskMessage},
 * {@link ResultMessage}, {@link HeartbeatMessage}, {@link StopMessage}.
 */
public sealed interface Message extends Serializable
        permits TaskMessage, ResultMessage, HeartbeatMessage, StopMessage {
}
