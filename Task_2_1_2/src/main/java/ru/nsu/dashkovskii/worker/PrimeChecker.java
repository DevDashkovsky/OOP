package ru.nsu.dashkovskii.worker;

import java.util.stream.IntStream;

/**
 * Проверка чанка чисел на наличие составного числа.
 *
 * <p>Класс хранит ссылку на текущий поток, в котором запущена проверка, чтобы
 * корректно реагировать на прерывание извне (например, при получении
 * {@code StopMessage} от master'а).
 *
 * <p>Семантика «составного» совпадает с задачей 2.1.1: число считается
 * составным, если {@link #isPrime(int)} возвращает false. То есть числа
 * {@code n <= 1} тоже трактуются как «не простые», и их наличие в массиве
 * приведёт к ответу {@code true}.
 */
public final class PrimeChecker {

    /**
     * Возвращает true, если в массиве есть хотя бы одно непростое число.
     *
     * <p>Вычисление параллельное (parallel stream) и поддерживает быструю
     * остановку: при {@link Thread#isInterrupted()} текущего worker-потока
     * бросается {@link InterruptedRuntimeException}, который вызывающий
     * код должен распознать как сигнал остановки.
     *
     * @param chunk массив чисел
     * @return true, если хотя бы одно число непростое
     */
    public boolean hasComposite(int[] chunk) {
        return IntStream.of(chunk).parallel().anyMatch(this::isNonPrimeOrThrow);
    }

    private boolean isNonPrimeOrThrow(int n) {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedRuntimeException();
        }
        return !isPrime(n);
    }

    private boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        if (n == 2 || n == 3) {
            return true;
        }
        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }
        for (int i = 5; (long) i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Сигнальное исключение, бросаемое из тела параллельного вычисления при
     * прерывании потока. Превращает прерывание в раскрутку стека через
     * {@link IntStream#anyMatch}.
     */
    public static final class InterruptedRuntimeException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        /**
         * Создаёт исключение прерывания.
         */
        public InterruptedRuntimeException() {
            super("computation interrupted");
        }
    }
}
