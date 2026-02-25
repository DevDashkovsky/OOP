package ru.nsu.dashkovskii.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Кастомная реализация блокирующей очереди.
 *
 * @param <T> тип элементов в очереди
 */
public class CustomQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;

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
    public synchronized void enqueue(T item, Runnable action) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait();
        }
        if (action != null) {
            action.run();
        }
        queue.add(item);
        notifyAll();
    }

    /**
     * Извлекает и удаляет первый элемент очереди, ожидая при необходимости.
     *
     * @return первый элемент очереди
     * @throws InterruptedException если ожидание прервано
     */
    public synchronized T dequeue() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.poll();
        notifyAll();
        return item;
    }

    /**
     * Извлекает и удаляет первый элемент очереди или возвращает null, если она пуста.
     *
     * @return первый элемент очереди или null
     */
    public synchronized T poll() {
        if (queue.isEmpty()) {
            return null;
        }
        T item = queue.poll();
        notifyAll();
        return item;
    }

    /**
     * Извлекает и удаляет до maxCount элементов из очереди.
     *
     * @param maxCount максимальное количество элементов для извлечения
     * @return список извлеченных элементов
     * @throws InterruptedException если ожидание прервано
     */
    public synchronized List<T> dequeueBatch(int maxCount) throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        List<T> batch = new LinkedList<>();
        while (!queue.isEmpty() && batch.size() < maxCount) {
            batch.add(queue.poll());
        }
        notifyAll();
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
