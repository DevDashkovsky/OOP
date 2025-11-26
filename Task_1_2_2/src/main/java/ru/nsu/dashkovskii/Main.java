package ru.nsu.dashkovskii;

import ru.nsu.dashkovskii.hashtable.HashTable;

/**
 * Главный класс для демонстрации работы хеш-таблицы.
 * Показывает базовые операции: добавление, обновление и получение значений.
 */
public class Main {
    /**
     * Точка входа в программу.
     * Демонстрирует создание хеш-таблицы, добавление и обновление значений.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        HashTable<String, Number> hashTable = new HashTable<>();
        hashTable.put("one", 1);
        hashTable.update("one", 1.0);
        System.out.println(hashTable.get("one"));
    }
}
