package ru.nsu.dashkovskii.master;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import ru.nsu.dashkovskii.common.TaskMessage;

/**
 * Поток, раздающий PENDING-задачи свободным worker'ам.
 *
 * <p>В цикле: тянет следующий taskId из очереди, ищет свободного живого
 * worker'а и шлёт ему {@link TaskMessage}. Если свободных нет — кладёт задачу
 * обратно и спит немного. Если отправка падает с IOException — worker
 * помечается мёртвым, задача переназначается.
 */
public final class Dispatcher implements Runnable {

    private final TaskRegistry registry;
    private final ConcurrentMap<String, WorkerProxy> workers;
    private final long taskDeadlineMillis;
    private final long takeTimeoutMillis;
    private final long backoffMillis;

    /**
     * Создаёт dispatcher.
     *
     * @param registry реестр задач
     * @param workers разделяемая карта workerId → proxy
     * @param taskDeadlineMillis длительность дедлайна на выполнение задачи
     * @param takeTimeoutMillis сколько ждать новую PENDING-задачу за итерацию
     * @param backoffMillis пауза, если нет свободных worker'ов
     */
    public Dispatcher(
            TaskRegistry registry,
            ConcurrentMap<String, WorkerProxy> workers,
            long taskDeadlineMillis,
            long takeTimeoutMillis,
            long backoffMillis) {
        this.registry = registry;
        this.workers = workers;
        this.taskDeadlineMillis = taskDeadlineMillis;
        this.takeTimeoutMillis = takeTimeoutMillis;
        this.backoffMillis = backoffMillis;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && !registry.isFinished()) {
            try {
                Long taskId = registry.takeNextPending(takeTimeoutMillis);
                if (taskId == null) {
                    continue;
                }
                if (!tryAssign(taskId)) {
                    registry.returnToQueue(taskId);
                    Thread.sleep(backoffMillis);
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private boolean tryAssign(long taskId) {
        TaskInfo info = registry.get(taskId);
        if (info == null || info.status() == TaskStatus.DONE) {
            return true;
        }
        WorkerProxy proxy = findIdleWorker();
        if (proxy == null) {
            return false;
        }
        proxy.setCurrentTaskId(taskId);
        long deadline = System.currentTimeMillis() + taskDeadlineMillis;
        registry.markAssigned(taskId, proxy.workerId(), deadline);
        try {
            proxy.send(new TaskMessage(taskId, info.chunk()));
            return true;
        } catch (IOException ex) {
            System.out.println(
                    "[dispatcher] send failed for " + proxy.workerId() + ": " + ex);
            proxy.markDead();
            proxy.setCurrentTaskId(null);
            registry.requeue(taskId);
            return true;
        }
    }

    private WorkerProxy findIdleWorker() {
        for (WorkerProxy proxy : workers.values()) {
            if (proxy.isIdle()) {
                return proxy;
            }
        }
        return null;
    }
}
