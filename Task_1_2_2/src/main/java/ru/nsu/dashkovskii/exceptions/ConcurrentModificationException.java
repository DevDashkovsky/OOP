package ru.nsu.dashkovskii.exceptions;

/**
 * Исключение, выбрасываемое при попытке модификации хеш-таблицы
 * во время итерирования по её элементам.
 */
public class ConcurrentModificationException extends RuntimeException {
    /**
     * Создает новое исключение с заданным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public ConcurrentModificationException(String message) {
        super(message);
    }
}
