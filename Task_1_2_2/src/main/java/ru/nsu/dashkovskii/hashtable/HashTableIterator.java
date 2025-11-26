package ru.nsu.dashkovskii.hashtable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ru.nsu.dashkovskii.exceptions.ConcurrentModificationException;

/**
 * Итератор для обхода элементов хеш-таблицы.
 * Выбрасывает {@link ConcurrentModificationException} при модификации таблицы во время итерации.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class HashTableIterator<K, V> implements Iterator<HashNode<K, V>> {
    private final HashTable<K, V> table;
    private final int expectedModCount;
    private int bucketIndex;
    private HashNode<K, V> current;

    /**
     * Создает новый итератор для заданной хеш-таблицы.
     *
     * @param table хеш-таблица для итерирования
     */
    public HashTableIterator(HashTable<K, V> table) {
        this.table = table;
        this.expectedModCount = table.getModCount();
        this.bucketIndex = 0;
        this.current = null;
    }

    /**
     * Проверяет наличие следующего элемента.
     *
     * @return true, если есть следующий элемент
     * @throws ConcurrentModificationException если таблица была модифицирована
     */
    @Override
    public boolean hasNext() {
        if (expectedModCount != table.getModCount()) {
            throw new ConcurrentModificationException("HashTable modified during iteration");
        }

        if (current != null && current.next != null) {
            return true;
        }

        HashNode<K, V>[] tableArray = table.getTable();
        while (bucketIndex < tableArray.length && tableArray[bucketIndex] == null) {
            bucketIndex++;
        }

        return bucketIndex < tableArray.length;
    }

    /**
     * Возвращает следующий элемент.
     *
     * @return следующий узел
     * @throws NoSuchElementException если больше нет элементов
     */
    @Override
    public HashNode<K, V> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        HashNode<K, V>[] tableArray = table.getTable();
        if (current == null || current.next == null) {
            current = tableArray[bucketIndex++];
        } else {
            current = current.next;
        }

        return current;
    }
}
