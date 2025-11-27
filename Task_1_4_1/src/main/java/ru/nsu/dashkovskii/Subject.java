package ru.nsu.dashkovskii;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Предмет с возможными несколькими попытками сдачи.
 */
public class Subject {
    private final String name;
    private final ControlType controlType;
    private final List<Attempt> attempts;

    /**
     * Конструктор предмета.
     *
     * @param name название предмета
     * @param controlType тип контроля
     */
    public Subject(String name, ControlType controlType) {
        this.name = name;
        this.controlType = controlType;
        this.attempts = new ArrayList<>();
    }

    /**
     * Добавить попытку сдачи предмета.
     *
     * @param grade оценка
     * @param date дата попытки
     */
    public void addAttempt(Grade grade, LocalDate date) {
        attempts.add(new Attempt(grade, date));
    }

    /**
     * Получить последнюю попытку сдачи.
     *
     * @return последняя попытка или empty если попыток не было
     */
    public Optional<Attempt> getLastAttempt() {
        if (attempts.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(attempts.get(attempts.size() - 1));
    }

    /**
     * Получить последнюю положительную оценку (не учитывая неуды).
     *
     * @return последняя положительная оценка или empty если таких нет
     */
    public Optional<Grade> getLastPassingGrade() {
        for (int i = attempts.size() - 1; i >= 0; i--) {
            Attempt attempt = attempts.get(i);
            if (attempt.isSuccessful()) {
                return Optional.of(attempt.getGrade());
            }
        }
        return Optional.empty();
    }

    /**
     * Проверяет, сдан ли предмет (есть ли хотя бы одна положительная оценка).
     *
     * @return true если предмет сдан
     */
    public boolean isPassed() {
        return getLastPassingGrade().isPresent();
    }

    /**
     * Получить количество попыток сдачи.
     *
     * @return количество попыток
     */
    public int getAttemptsCount() {
        return attempts.size();
    }

    /**
     * Получить количество проваленных попыток.
     *
     * @return количество проваленных попыток
     */
    public int getFailedAttemptsCount() {
        return (int) attempts.stream()
                .filter(a -> !a.isSuccessful())
                .count();
    }

    /**
     * Получить название предмета.
     *
     * @return название
     */
    public String getName() {
        return name;
    }

    /**
     * Получить тип контроля.
     *
     * @return тип контроля
     */
    public ControlType getControlType() {
        return controlType;
    }

    /**
     * Получить все попытки.
     *
     * @return список попыток
     */
    public List<Attempt> getAttempts() {
        return new ArrayList<>(attempts);
    }
}

