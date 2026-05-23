package ru.nsu.dashkovskii.master;

/**
 * Парсер строки вида {@code "1,2,3"} в массив {@code int[]}.
 *
 * <p>Отдельный класс вместо приватного статического метода: парсинг — это
 * самостоятельная ответственность с собственным состоянием (исходная строка).
 */
public final class CsvIntArray {

    private final String csv;

    /**
     * Создаёт парсер для указанной строки.
     *
     * @param csv строка значений через запятую
     */
    public CsvIntArray(String csv) {
        this.csv = csv;
    }

    /**
     * Парсит строку в массив целых чисел. Пустая строка → пустой массив.
     *
     * @return массив значений
     */
    public int[] parse() {
        if (csv.isEmpty()) {
            return new int[0];
        }
        String[] parts = csv.split(",");
        int[] result = new int[parts.length];
        for (int idx = 0; idx < parts.length; idx++) {
            result[idx] = Integer.parseInt(parts[idx].trim());
        }
        return result;
    }
}
