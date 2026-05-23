package ru.nsu.dashkovskii.master;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TaskRegistryTest {

    @Test
    void enqueueMakesTaskPendingAndPollable() throws InterruptedException {
        TaskRegistry registry = new TaskRegistry();
        long taskId = registry.enqueue(new int[] {1, 2, 3});

        TaskInfo info = registry.get(taskId);
        assertNotNull(info);
        assertEquals(TaskStatus.PENDING, info.status());

        Long polled = registry.takeNextPending(100L);
        assertEquals(taskId, polled);
    }

    @Test
    void happyPathPendingAssignedDone() {
        TaskRegistry registry = new TaskRegistry();
        long taskId = registry.enqueue(new int[] {7});

        registry.markAssigned(taskId, "worker-A", System.currentTimeMillis() + 30_000);
        assertEquals(TaskStatus.ASSIGNED, registry.get(taskId).status());

        registry.markDone(taskId, false);
        assertEquals(TaskStatus.DONE, registry.get(taskId).status());
        assertTrue(registry.isFinished());
        assertFalse(registry.answerFound());
    }

    @Test
    void markDoneWithCompositeSetsAnswerFound() {
        TaskRegistry registry = new TaskRegistry();
        long taskId = registry.enqueue(new int[] {6});

        registry.markAssigned(taskId, "worker-A", System.currentTimeMillis() + 30_000);
        registry.markDone(taskId, true);

        assertTrue(registry.answerFound());
        assertTrue(registry.isFinished());
    }

    /**
     * Регрессионный тест на баг №1: повторный {@code markDone} для уже DONE-задачи
     * не должен инкрементить doneCount. До фикса второй вызов прибавлял счётчик,
     * и {@code isFinished()} мог сработать раньше, чем все чанки обработаны.
     */
    @Test
    void doubleMarkDoneDoesNotOvercount() {
        TaskRegistry registry = new TaskRegistry();
        long id1 = registry.enqueue(new int[] {6});
        long id2 = registry.enqueue(new int[] {7});

        registry.markAssigned(id1, "worker-A", System.currentTimeMillis() + 30_000);
        registry.markDone(id1, false);
        registry.markDone(id1, false);

        assertFalse(registry.isFinished(),
                "id2 ещё не обработан — isFinished не должен срабатывать от двойного markDone");

        registry.markAssigned(id2, "worker-B", System.currentTimeMillis() + 30_000);
        registry.markDone(id2, false);
        assertTrue(registry.isFinished());
    }

    /**
     * Регрессионный тест на баг №2: повторный {@code requeue} ASSIGNED-задачи
     * не должен дублировать taskId в очереди. До фикса второй вызов клал тот же
     * id в pendingQueue ещё раз, и Dispatcher мог выдать один чанк двум worker'ам.
     */
    @Test
    void doubleRequeueDoesNotDuplicateInQueue() throws InterruptedException {
        TaskRegistry registry = new TaskRegistry();
        long taskId = registry.enqueue(new int[] {1});

        Long first = registry.takeNextPending(100L);
        assertEquals(taskId, first);

        registry.markAssigned(taskId, "worker-A", System.currentTimeMillis() + 30_000);
        registry.requeue(taskId);
        registry.requeue(taskId);

        Long polledAgain = registry.takeNextPending(100L);
        assertEquals(taskId, polledAgain);
        assertNull(registry.takeNextPending(50L),
                "после двойного requeue в очереди не должно быть дубля");
    }

    @Test
    void requeueIsNoOpForDoneTask() throws InterruptedException {
        TaskRegistry registry = new TaskRegistry();
        long taskId = registry.enqueue(new int[] {2});

        registry.takeNextPending(100L);
        registry.markAssigned(taskId, "worker-A", System.currentTimeMillis() + 30_000);
        registry.markDone(taskId, false);

        registry.requeue(taskId);

        assertEquals(TaskStatus.DONE, registry.get(taskId).status());
        assertNull(registry.takeNextPending(50L));
    }

    @Test
    void returnToQueueReaddsPendingTask() throws InterruptedException {
        TaskRegistry registry = new TaskRegistry();
        long taskId = registry.enqueue(new int[] {3});

        Long polled = registry.takeNextPending(100L);
        assertEquals(taskId, polled);
        assertEquals(TaskStatus.PENDING, registry.get(taskId).status());

        registry.returnToQueue(taskId);

        Long polledAgain = registry.takeNextPending(100L);
        assertEquals(taskId, polledAgain);
    }

    @Test
    void returnToQueueIsNoOpForAssigned() throws InterruptedException {
        TaskRegistry registry = new TaskRegistry();
        long taskId = registry.enqueue(new int[] {4});

        registry.takeNextPending(100L);
        registry.markAssigned(taskId, "worker-A", System.currentTimeMillis() + 30_000);

        registry.returnToQueue(taskId);

        assertNull(registry.takeNextPending(50L),
                "returnToQueue не должна работать для ASSIGNED — там должен использоваться requeue");
    }

    @Test
    void returnToQueueIsNoOpForDone() throws InterruptedException {
        TaskRegistry registry = new TaskRegistry();
        long taskId = registry.enqueue(new int[] {5});

        registry.takeNextPending(100L);
        registry.markAssigned(taskId, "worker-A", System.currentTimeMillis() + 30_000);
        registry.markDone(taskId, false);

        registry.returnToQueue(taskId);

        assertNull(registry.takeNextPending(50L));
    }

    @Test
    void emptyRegistryIsFinished() {
        TaskRegistry registry = new TaskRegistry();
        assertTrue(registry.isFinished(),
                "0 задач = 0 done = isFinished true (Master корректно отработает пустой массив)");
    }
}
