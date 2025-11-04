package ru.nsu.dashkovskii;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для поиска всех вхождений подстроки в файле.
 * Использует буферизированный ввод для работы с большими файлами.
 */
public class SubstringFinder {
    private static final int BUFFER_SIZE = 8192;

    /**
     * Находит все вхождения подстроки в файле.
     *
     * @param filename путь к файлу
     * @param pattern  искомая подстрока
     * @return список индексов начала каждого вхождения
     * @throws IOException если возникла ошибка при чтении файла
     */
    public List<Long> find(String filename, String pattern) throws IOException {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Паттерн не может быть пустым");
        }

        List<Long> indices = new ArrayList<>();
        byte[] patternBytes = pattern.getBytes(StandardCharsets.UTF_8);
        byte[] buffer = new byte[BUFFER_SIZE + patternBytes.length - 1];
        int bufferSize = 0;
        long globalPosition = 0;

        try (BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(filename), BUFFER_SIZE)) {

            byte[] chunk = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = bis.read(chunk)) != -1) {
                // Копируем новые данные в конец буфера
                System.arraycopy(chunk, 0, buffer, bufferSize, bytesRead);
                bufferSize += bytesRead;

                // Ищем все вхождения в текущем буфере
                int searchLimit = bufferSize - patternBytes.length + 1;
                for (int i = 0; i < searchLimit; i++) {
                    if (matches(buffer, i, patternBytes)) {
                        indices.add(globalPosition + i);
                    }
                }

                // Сдвигаем буфер: оставляем только последние (pattern.length - 1) байтов
                if (bufferSize >= patternBytes.length) {
                    int keepSize = patternBytes.length - 1;
                    globalPosition += bufferSize - keepSize;
                    System.arraycopy(buffer, bufferSize - keepSize, buffer, 0, keepSize);
                    bufferSize = keepSize;
                }
            }

            // Проверяем оставшуюся часть буфера
            for (int i = 0; i <= bufferSize - patternBytes.length; i++) {
                if (matches(buffer, i, patternBytes)) {
                    indices.add(globalPosition + i);
                }
            }
        }

        return indices;
    }

    /**
     * Проверяет, совпадает ли подстрока в буфере с паттерном.
     *
     * @param buffer       буфер с байтами текста
     * @param start        начальная позиция в буфере
     * @param patternBytes байты искомого паттерна
     * @return true, если найдено совпадение
     */
    private boolean matches(byte[] buffer, int start, byte[] patternBytes) {
        for (int i = 0; i < patternBytes.length; i++) {
            if (buffer[start + i] != patternBytes[i]) {
                return false;
            }
        }
        return true;
    }
}
