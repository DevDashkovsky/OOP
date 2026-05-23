package ru.nsu.dashkovskii.master;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Потокобезопасный реестр чанков, который видят все компоненты master'а.
 *
 * <p>{@link Dispatcher} берёт из {@code pendingQueue} следующий id для выдачи,
 * {@link Watchdog} ходит по всем ASSIGNED и проверяет дедлайны, а
 * {@link ResultCollector} помечает чанки DONE по приходу
 * {@link ru.nsu.dashkovskii.common.ResultMessage} от worker'ов.
 *
 * <p>Все мутации идут через {@link ConcurrentHashMap#compute} — это гарантирует
 * атомарность относительно одного {@code taskId}.
 */
public final class TaskRegistry {

    private final ConcurrentHashMap<Long, TaskInfo> tasks = new ConcurrentHashMap<>();
    private final BlockingQueue<Long> pendingQueue = new LinkedBlockingQueue<>();
    private final AtomicLong nextId = new AtomicLong(0);
    private final AtomicInteger totalCount = new AtomicInteger(0);
    private final AtomicInteger doneCount = new AtomicInteger(0);
    private final AtomicBoolean answerFound = new AtomicBoolean(false);

    /**
     * Регистрирует новый чанк и кладёт его в очередь PENDING.
     *
     * @param chunk массив чисел
     * @return id зарегистрированной задачи
     */
    public long enqueue(int[] chunk) {
        long taskId = nextId.getAndIncrement();
        tasks.put(taskId, TaskInfo.pending(taskId, chunk));
        totalCount.incrementAndGet();
        pendingQueue.add(taskId);
        return taskId;
    }

    /**
     * Берёт следующий PENDING-id из очереди, ждёт не дольше указанного времени.
     *
     * @param timeoutMillis сколько ждать, в мс
     * @return id или null, если время вышло
     * @throws InterruptedException при прерывании ожидания
     */
    public Long takeNextPending(long timeoutMillis) throws InterruptedException {
        return pendingQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Помечает задачу как назначенную worker'у.
     *
     * @param taskId id задачи
     * @param workerId id worker'а
     * @param deadlineMillis момент дедлайна в epoch-ms
     */
    public void markAssigned(long taskId, String workerId, long deadlineMillis) {
        tasks.computeIfPresent(taskId, (id, info) -> info.assignTo(workerId, deadlineMillis));
    }

    /**
     * Помечает задачу как выполненную, обновляет счётчики и short-circuit-флаг.
     *
     * <p>Идемпотентно: повторный вызов для уже DONE-задачи не инкрементит
     * счётчик. Это важно, потому что один и тот же чанк может прийти дважды,
     * если watchdog успел переназначить его, а первый worker всё-таки прислал
     * ответ.
     *
     * @param taskId id задачи
     * @param hasComposite результат проверки чанка
     */
    public void markDone(long taskId, boolean hasComposite) {
        AtomicBoolean transitioned = new AtomicBoolean(false);
        tasks.computeIfPresent(taskId, (id, info) -> {
            if (info.status() == TaskStatus.DONE) {
                return info;
            }
            transitioned.set(true);
            return info.asDone();
        });
        if (transitioned.get()) {
            doneCount.incrementAndGet();
        }
        if (hasComposite) {
            answerFound.set(true);
        }
    }

    /**
     * Возвращает задачу в очередь PENDING после сбоя worker'а
     * (таймаут, обрыв соединения). Транзитит ASSIGNED → PENDING и добавляет
     * id в очередь. Идемпотентно: если задача уже DONE или уже PENDING —
     * ничего не делает. Это защищает от двойного добавления в очередь, когда
     * watchdog и ResultCollector одновременно увидели смерть одного worker'а.
     *
     * @param taskId id задачи
     */
    public void requeue(long taskId) {
        AtomicBoolean transitioned = new AtomicBoolean(false);
        tasks.computeIfPresent(taskId, (id, info) -> {
            if (info.status() != TaskStatus.ASSIGNED) {
                return info;
            }
            transitioned.set(true);
            return info.asPending();
        });
        if (transitioned.get()) {
            pendingQueue.add(taskId);
        }
    }

    /**
     * Кладёт PENDING-задачу обратно в очередь, не меняя её статус.
     *
     * <p>Используется Dispatcher'ом, когда он успел снять id с очереди,
     * но не нашёл свободного живого worker'а. В отличие от {@link #requeue},
     * этот метод НЕ переводит ASSIGNED → PENDING — он только повторно ставит
     * в очередь уже PENDING-задачу. Если статус не PENDING (например, чанк
     * успели завершить параллельно), повторного добавления не происходит.
     *
     * @param taskId id задачи
     */
    public void returnToQueue(long taskId) {
        TaskInfo info = tasks.get(taskId);
        if (info != null && info.status() == TaskStatus.PENDING) {
            pendingQueue.add(taskId);
        }
    }

    /**
     * Возвращает true, если ответ уже найден или все чанки обработаны.
     *
     * @return флаг завершения работы
     */
    public boolean isFinished() {
        return answerFound.get() || doneCount.get() >= totalCount.get();
    }

    /**
     * Возвращает текущее значение short-circuit-флага.
     *
     * @return true, если хотя бы один worker нашёл составное число
     */
    public boolean answerFound() {
        return answerFound.get();
    }

    /**
     * Снимок всех задач для Watchdog'а (просмотр дедлайнов).
     *
     * @return неизменяемая копия списка
     */
    public Collection<TaskInfo> snapshot() {
        return List.copyOf(new ArrayList<>(tasks.values()));
    }

    /**
     * Возвращает информацию о задаче по id.
     *
     * @param taskId id задачи
     * @return TaskInfo или null, если такой задачи нет
     */
    public TaskInfo get(long taskId) {
        return tasks.get(taskId);
    }
}
