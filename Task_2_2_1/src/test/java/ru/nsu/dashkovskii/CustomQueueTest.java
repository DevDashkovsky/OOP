package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.util.CustomQueue;

/**
 * Тесты для проверки корректности работы CustomQueue.
 */
public class CustomQueueTest {

    @Test
    public void testEnqueueDequeue() throws InterruptedException {
        CustomQueue<Integer> queue = new CustomQueue<>(5);
        queue.enqueue(1, null);
        queue.enqueue(2, null);

        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
    }

    @Test
    public void testBlockingDequeue() throws InterruptedException {
        CustomQueue<Integer> queue = new CustomQueue<>(5);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean consumed = new AtomicBoolean(false);

        Thread consumer = new Thread(() -> {
            try {
                latch.countDown();
                queue.dequeue();
                consumed.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumer.start();
        latch.await();
        // Give the consumer a moment to block
        Thread.sleep(100);
        assertFalse(consumed.get(), "Consumer should be blocked on empty queue");

        queue.enqueue(42, null);
        consumer.join(1000);
        assertTrue(consumed.get(), "Consumer should have consumed the item");
    }

    @Test
    public void testBlockingEnqueue() throws InterruptedException {
        int capacity = 2;
        CustomQueue<Integer> queue = new CustomQueue<>(capacity);
        queue.enqueue(1, null);
        queue.enqueue(2, null);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean produced = new AtomicBoolean(false);

        Thread producer = new Thread(() -> {
            try {
                latch.countDown();
                queue.enqueue(3, null); // Should block
                produced.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        latch.await();
        // Give the producer a moment to block
        Thread.sleep(100);
        assertFalse(produced.get(), "Producer should be blocked on full queue");

        queue.dequeue(); // Make space
        producer.join(1000);
        assertTrue(produced.get(), "Producer should have produced the item");
        assertEquals(2, queue.dequeue());
        assertEquals(3, queue.dequeue());
    }

    @Test
    public void testDequeueBatch() throws InterruptedException {
        CustomQueue<Integer> queue = new CustomQueue<>(10);
        queue.enqueue(1, null);
        queue.enqueue(2, null);
        queue.enqueue(3, null);

        List<Integer> batch = queue.dequeueBatch(2);
        assertEquals(2, batch.size());
        assertEquals(1, batch.get(0));
        assertEquals(2, batch.get(1));

        assertEquals(3, queue.dequeue());
    }

    @Test
    public void testPoll() throws InterruptedException {
        CustomQueue<Integer> queue = new CustomQueue<>(5);
        assertEquals(null, queue.poll());

        queue.enqueue(1, null);
        assertEquals(1, queue.poll());
        assertEquals(null, queue.poll());
    }
}
