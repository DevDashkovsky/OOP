package ru.nsu.dashkovskii.master;

import ru.nsu.dashkovskii.common.HeartbeatMessage;
import ru.nsu.dashkovskii.common.Message;
import ru.nsu.dashkovskii.common.ResultMessage;

/**
 * Поток, читающий сообщения от одного конкретного worker'а.
 *
 * <p>На каждый канал — свой ResultCollector. При обрыве соединения помечает
 * worker'а мёртвым и возвращает его текущую задачу в очередь PENDING.
 */
public final class ResultCollector implements Runnable {

    private final WorkerProxy proxy;
    private final TaskRegistry registry;

    /**
     * Создаёт коллектор для одного worker'а.
     *
     * @param proxy прокси worker'а
     * @param registry реестр задач master'а
     */
    public ResultCollector(WorkerProxy proxy, TaskRegistry registry) {
        this.proxy = proxy;
        this.registry = registry;
    }

    @Override
    public void run() {
        while (proxy.isAlive() && !Thread.currentThread().isInterrupted()) {
            Message msg;
            try {
                msg = proxy.channel().receive();
            } catch (Exception ex) {
                handleDisconnect(ex);
                return;
            }
            dispatch(msg);
        }
    }

    private void dispatch(Message msg) {
        if (msg instanceof ResultMessage result) {
            registry.markDone(result.taskId(), result.hasComposite());
            proxy.setCurrentTaskId(null);
        } else if (msg instanceof HeartbeatMessage) {
            proxy.touchHeartbeat();
        } else {
            System.out.println("[receiver " + proxy.workerId() + "] unexpected: " + msg);
        }
    }

    private void handleDisconnect(Exception ex) {
        if (proxy.isAlive()) {
            System.out.println("[receiver " + proxy.workerId() + "] disconnected: " + ex);
        }
        Long taskId = proxy.currentTaskId();
        proxy.markDead();
        if (taskId != null) {
            registry.requeue(taskId);
            proxy.setCurrentTaskId(null);
        }
    }
}
