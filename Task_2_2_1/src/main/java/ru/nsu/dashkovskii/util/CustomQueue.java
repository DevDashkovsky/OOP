package ru.nsu.dashkovskii.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Кастомная реализация блокирующей очереди.
 *
 * @param <T> тип элементов в очереди
 */
public class CustomQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;
    private final Object notFull = new Object();
    private final Object notEmpty = new Object();

    public CustomQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Вставляет элемент и выполняет действие.
     *
     * @param item элемент для добавления
     * @param action действие, выполняемое при успешном добавлении
     * @throws InterruptedException если ожидание прервано
     */
    public void enqueue(T item, Runnable action) throws InterruptedException {
        synchronized (notFull) {
            while (queue.size() >= capacity) {
                notFull.wait();
            }
            if (action != null) {
                action.run();
            }
            synchronized (notEmpty) {
                queue.add(item);
                notEmpty.notifyAll();
            }
        }
    }

    /**
     * Извлекает и удаляет первый элемент очереди, ожидая при необходимости.
     *
     * @return первый элемент очереди
     * @throws InterruptedException если ожидание прервано
     */
    public T dequeue() throws InterruptedException {
        T item;
        synchronized (notEmpty) {
            while (queue.isEmpty()) {
                notEmpty.wait();
            }
            item = queue.poll();
        }
        synchronized (notFull) {
            notFull.notifyAll();
        }
        return item;
    }

    /**
     * Извлекает и удаляет первый элемент очереди или возвращает null, если она пуста.
     *
     * @return первый элемент очереди или null
     */
    public T poll() {
        T item;
        synchronized (notEmpty) {
            if (queue.isEmpty()) {
                return null;
            }
            item = queue.poll();
        }
        synchronized (notFull) {
            notFull.notifyAll();
        }
        return item;
    }

    /**
     * Извлекает и удаляет до maxCount элементов из очереди.
     *
     * @param maxCount максимальное количество элементов для извлечения
     * @return список извлеченных элементов
     * @throws InterruptedException если ожидание прервано
     */
    public List<T> dequeueBatch(int maxCount) throws InterruptedException {
        List<T> batch = new LinkedList<>();
        synchronized (notEmpty) {
            while (queue.isEmpty()) {
                notEmpty.wait();
            }
            while (!queue.isEmpty() && batch.size() < maxCount) {
                batch.add(queue.poll());
            }
        }
        synchronized (notFull) {
            notFull.notifyAll();
        }
        return batch;
    }

    /**
     * Возвращает количество элементов в очереди.
     *
     * @return количество элементов
     */
    public synchronized int size() {
        return queue.size();
    }
}
