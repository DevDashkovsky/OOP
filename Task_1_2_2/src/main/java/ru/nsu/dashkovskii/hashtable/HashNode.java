package ru.nsu.dashkovskii.hashtable;

/**
 * Узел хеш-таблицы, содержащий ключ, значение и ссылку на следующий узел.
 * Используется для разрешения коллизий методом цепочек.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class HashNode<K, V> {
    /**
     * Ключ узла.
     */
    public final K key;

    /**
     * Значение узла.
     */
    public V value;

    /**
     * Ссылка на следующий узел в цепочке.
     */
    public HashNode<K, V> next;

    /**
     * Создает новый узел с заданным ключом, значением и ссылкой на следующий узел.
     *
     * @param key   ключ
     * @param value значение
     * @param next  следующий узел в цепочке
     */
    public HashNode(K key, V value, HashNode<K, V> next) {
        this.key = key;
        this.value = value;
        this.next = next;
    }
}
