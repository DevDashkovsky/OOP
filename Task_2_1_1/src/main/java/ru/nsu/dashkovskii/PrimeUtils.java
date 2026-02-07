package ru.nsu.dashkovskii;

/**
 * Утилитарный класс, содержащий методы, связанные с простыми числами.
 */
public class PrimeUtils {
    private PrimeUtils() {}

    /**
     * Проверяет, является ли заданное число простым.
     *
     * @param n число для проверки
     * @return true, если число простое, иначе false
     */
    public static boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        if (n == 2 || n == 3) {
            return true;
        }
        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}
