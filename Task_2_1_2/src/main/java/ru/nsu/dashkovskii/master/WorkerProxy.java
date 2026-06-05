package ru.nsu.dashkovskii.master;

import java.io.IOException;
import ru.nsu.dashkovskii.common.Message;
import ru.nsu.dashkovskii.common.MessageChannel;

/**
 * Состояние одного удалённого worker'а с точки зрения master'а.
 *
 * <p>Хранит TCP-канал, момент последнего heartbeat'а, id текущей задачи и
 * флаг живости. Объект делят между собой акцептор, диспетчер, watchdog и
 * receiver-поток данного worker'а — поэтому изменяемые поля помечены
 * {@code volatile}.
 */
public final class WorkerProxy {

    private final String workerId;
    private final MessageChannel channel;

    private volatile long lastHeartbeatMillis;
    private volatile Long currentTaskId;
    private volatile boolean alive;

    /**
     * Создаёт прокси для подключившегося worker'а.
     *
     * @param workerId идентификатор worker'а (присланный в первом heartbeat'е)
     * @param channel TCP-канал к этому worker'у
     */
    public WorkerProxy(String workerId, MessageChannel channel) {
        this.workerId = workerId;
        this.channel = channel;
        this.lastHeartbeatMillis = System.currentTimeMillis();
        this.alive = true;
    }

    /**
     * Отправляет сообщение worker'у.
     *
     * @param message сообщение
     * @throws IOException при ошибке записи в сокет
     */
    public void send(Message message) throws IOException {
        channel.send(message);
    }

    /**
     * Помечает worker'а мёртвым и закрывает канал. Идемпотентно.
     */
    public void markDead() {
        alive = false;
        channel.close();
    }

    /**
     * Обновляет момент последнего heartbeat'а текущим временем.
     */
    public void touchHeartbeat() {
        lastHeartbeatMillis = System.currentTimeMillis();
    }

    /**
     * Возвращает id worker'а.
     *
     * @return идентификатор
     */
    public String workerId() {
        return workerId;
    }

    /**
     * Возвращает канал worker'а (нужен receiver-потоку для чтения).
     *
     * @return канал
     */
    public MessageChannel channel() {
        return channel;
    }

    /**
     * Время последнего heartbeat'а в epoch-ms.
     *
     * @return миллисекунды
     */
    public long lastHeartbeatMillis() {
        return lastHeartbeatMillis;
    }

    /**
     * Id текущей задачи или null, если worker свободен.
     *
     * @return id или null
     */
    public Long currentTaskId() {
        return currentTaskId;
    }

    /**
     * Задаёт id текущей задачи. Передача null означает «свободен».
     *
     * @param taskId id задачи или null
     */
    public void setCurrentTaskId(Long taskId) {
        this.currentTaskId = taskId;
    }

    /**
     * Жив ли worker.
     *
     * @return true, если жив
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Свободен ли worker для назначения задачи.
     *
     * @return true, если жив и без текущей задачи
     */
    public boolean isIdle() {
        return alive && currentTaskId == null;
    }
}
