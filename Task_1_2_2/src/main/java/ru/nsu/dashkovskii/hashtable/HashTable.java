package ru.nsu.dashkovskii.hashtable;

import java.util.Iterator;
import java.util.Objects;

/**
 * Параметризованная хеш-таблица, реализующая отображение ключ-значение.
 * Использует метод цепочек для разрешения коллизий.
 * Поддерживает итерирование с защитой от concurrent modification.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
@SuppressWarnings("unchecked")
public class HashTable<K, V> implements Iterable<HashNode<K, V>> {
    private HashNode<K, V>[] table;
    private int size;
    private int modCount;

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    /**
     * Создает новую пустую хеш-таблицу с начальной емкостью по умолчанию.
     */
    public HashTable() {
        this.table = new HashNode[DEFAULT_CAPACITY];
        this.size = 0;
        this.modCount = 0;
    }

    /**
     * Добавляет пару ключ-значение в таблицу.
     * Если ключ уже существует, обновляет его значение.
     *
     * @param key   ключ
     * @param value значение
     */
    public void put(K key, V value) {
        int i = index(key);
        for (HashNode<K, V> node = table[i]; node != null; node = node.next) {
            if (Objects.equals(node.key, key)) {
                node.value = value;
                modCount++;
                return;
            }
        }
        table[i] = new HashNode<>(key, value, table[i]);
        size++;
        modCount++;
        if ((double) size / table.length > LOAD_FACTOR) {
            resize();
        }
    }

    /**
     * Обновляет значение существующего ключа.
     *
     * @param key   ключ
     * @param value новое значение
     * @throws IllegalArgumentException если ключ не найден
     */
    public void update(K key, V value) {
        if (!containsKey(key)) {
            throw new IllegalArgumentException("Key not found: " + key);
        }
        put(key, value);
    }

    /**
     * Получает значение по ключу.
     *
     * @param key ключ
     * @return значение или null, если ключ не найден
     */
    public V get(K key) {
        int i = index(key);
        for (HashNode<K, V> node = table[i]; node != null; node = node.next) {
            if (Objects.equals(node.key, key)) {
                return node.value;
            }
        }
        return null;
    }

    /**
     * Проверяет наличие ключа в таблице.
     *
     * @param key ключ для проверки
     * @return true, если ключ присутствует
     */
    public boolean containsKey(K key) {
        int i = index(key);
        for (HashNode<K, V> node = table[i]; node != null; node = node.next) {
            if (Objects.equals(node.key, key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Удаляет пару ключ-значение из таблицы.
     *
     * @param key ключ для удаления
     * @return удаленное значение или null, если ключ не найден
     */
    public V remove(K key) {
        int i = index(key);
        HashNode<K, V> prev = null;
        for (HashNode<K, V> node = table[i]; node != null; node = node.next) {
            if (Objects.equals(node.key, key)) {
                if (prev == null) {
                    table[i] = node.next;
                } else {
                    prev.next = node.next;
                }
                size--;
                modCount++;
                return node.value;
            }
            prev = node;
        }
        return null;
    }

    /**
     * Возвращает количество пар ключ-значение в таблице.
     *
     * @return размер таблицы
     */
    public int size() {
        return size;
    }

    /**
     * Возвращает итератор для обхода элементов таблицы.
     *
     * @return итератор
     */
    @Override
    public Iterator<HashNode<K, V>> iterator() {
        return new HashTableIterator<>(this);
    }

    /**
     * Преобразует таблицу в строковое представление.
     *
     * @return строка вида "{key1=value1, key2=value2, ...}"
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (HashNode<K, V> node : this) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(node.key).append("=").append(node.value);
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Сравнивает эту таблицу с другим объектом на равенство.
     * Две таблицы равны, если содержат одинаковые пары ключ-значение.
     *
     * @param obj объект для сравнения
     * @return true, если объекты равны
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HashTable<?, ?> other)) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        for (HashNode<K, V> node : this) {
            Object val = other.getRaw(node.key);
            if (!Objects.equals(node.value, val)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Вычисляет хеш-код таблицы.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        int hash = 0;
        for (HashNode<K, V> node : this) {
            hash += Objects.hashCode(node.key) ^ Objects.hashCode(node.value);
        }
        return hash;
    }

    /**
     * Возвращает счетчик модификаций таблицы.
     *
     * @return счетчик модификаций
     */
    int getModCount() {
        return modCount;
    }

    /**
     * Возвращает внутренний массив (для использования итератором).
     *
     * @return массив bucket'ов
     */
    HashNode<K, V>[] getTable() {
        return table;
    }

    /**
     * Вычисляет индекс в таблице для заданного ключа.
     *
     * @param key ключ
     * @return индекс в таблице
     */
    private int index(K key) {
        if (key == null) {
            return 0;
        }
        return Math.abs(key.hashCode()) % table.length;
    }

    /**
     * Увеличивает размер внутреннего массива и перехеширует все элементы.
     */
    private void resize() {
        HashNode<K, V>[] old = table;
        table = new HashNode[old.length * 2];
        size = 0;
        for (HashNode<K, V> head : old) {
            for (HashNode<K, V> node = head; node != null; node = node.next) {
                put(node.key, node.value);
            }
        }
    }

    /**
     * Вспомогательный метод для получения значения без учета типов (для equals).
     *
     * @param key ключ
     * @return значение или null
     */
    private Object getRaw(Object key) {
        int i;
        if (key == null) {
            i = 0;
        } else {
            i = Math.abs(key.hashCode()) % table.length;
        }
        for (HashNode<K, V> node = table[i]; node != null; node = node.next) {
            if (Objects.equals(node.key, key)) {
                return node.value;
            }
        }
        return null;
    }
}
