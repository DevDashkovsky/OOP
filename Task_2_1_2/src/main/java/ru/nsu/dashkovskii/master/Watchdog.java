package ru.nsu.dashkovskii.master;

import java.util.concurrent.ConcurrentMap;

/**
 * Поток-надзиратель.
 *
 * <p>Раз в {@code tickMillis}:
 * <ul>
 *   <li>проходит по всем ASSIGNED-задачам и переназначает те, у кого истёк
 *       дедлайн;
 *   <li>проходит по всем worker'ам и помечает мёртвыми тех, от кого давно
 *       не было heartbeat'ов.
 * </ul>
 */
public final class Watchdog implements Runnable {

    private final TaskRegistry registry;
    private final ConcurrentMap<String, WorkerProxy> workers;
    private final long tickMillis;
    private final long heartbeatTimeoutMillis;

    /**
     * Создаёт watchdog.
     *
     * @param registry реестр задач
     * @param workers разделяемая карта workerId → proxy
     * @param tickMillis период проверок, мс
     * @param heartbeatTimeoutMillis допустимая пауза в heartbeat'ах, мс
     */
    public Watchdog(
            TaskRegistry registry,
            ConcurrentMap<String, WorkerProxy> workers,
            long tickMillis,
            long heartbeatTimeoutMillis) {
        this.registry = registry;
        this.workers = workers;
        this.tickMillis = tickMillis;
        this.heartbeatTimeoutMillis = heartbeatTimeoutMillis;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && !registry.isFinished()) {
            try {
                Thread.sleep(tickMillis);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return;
            }
            checkDeadlines();
            checkHeartbeats();
        }
    }

    private void checkDeadlines() {
        long now = System.currentTimeMillis();
        for (TaskInfo info : registry.snapshot()) {
            if (info.status() != TaskStatus.ASSIGNED) {
                continue;
            }
            if (now <= info.deadlineMillis()) {
                continue;
            }
            System.out.println(
                    "[watchdog] task " + info.taskId() + " deadline expired, requeue");
            WorkerProxy proxy = workers.get(info.assignedWorker());
            if (proxy != null && info.taskId() == nullToZero(proxy.currentTaskId())) {
                proxy.setCurrentTaskId(null);
            }
            registry.requeue(info.taskId());
        }
    }

    private void checkHeartbeats() {
        long now = System.currentTimeMillis();
        for (WorkerProxy proxy : workers.values()) {
            if (!proxy.isAlive()) {
                continue;
            }
            if (now - proxy.lastHeartbeatMillis() <= heartbeatTimeoutMillis) {
                continue;
            }
            System.out.println("[watchdog] worker " + proxy.workerId() + " unresponsive, killing");
            Long taskId = proxy.currentTaskId();
            proxy.markDead();
            if (taskId != null) {
                registry.requeue(taskId);
                proxy.setCurrentTaskId(null);
            }
        }
    }

    private long nullToZero(Long value) {
        return value == null ? -1L : value;
    }
}
