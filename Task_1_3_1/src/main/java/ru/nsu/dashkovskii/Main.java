// java
package ru.nsu.dashkovskii;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Главный класс программы для поиска подстроки в файле.
 */
public class Main {
    /**
     * Точка входа в программу.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SubstringFinder finder = new SubstringFinder();

        try {
            System.out.print("Введите имя файла: ");
            String filename = scanner.nextLine().trim();

            System.out.print("Введите подстроку для поиска: ");
            String pattern = scanner.nextLine();

            System.out.println("\nПоиск подстроки '" + pattern + "' в файле '" + filename + "'...");
            long startTime = System.currentTimeMillis();

            List<Long> positions = finder.find(filename, pattern);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.println("\nНайдено вхождений: " + positions.size());
            System.out.println("Время выполнения: " + duration + " мс");

            if (!positions.isEmpty()) {
                System.out.println("\nПозиции вхождений:");
                for (Long pos : positions) {
                    System.out.println(pos);
                }
            } else {
                System.out.println("\nПодстрока не найдена.");
            }

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
            System.exit(1);
        } finally {
            scanner.close();
        }
    }
}
