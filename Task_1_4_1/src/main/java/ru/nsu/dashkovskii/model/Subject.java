package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ru.nsu.dashkovskii.enums.ControlType;
import ru.nsu.dashkovskii.enums.Grade;

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
     */
    public void addAttempt(Grade grade) {
        attempts.add(new Attempt(grade));
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
}
