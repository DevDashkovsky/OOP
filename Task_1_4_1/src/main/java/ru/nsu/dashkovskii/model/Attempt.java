package ru.nsu.dashkovskii.model;

import ru.nsu.dashkovskii.enums.Grade;

/**
 * Попытка сдачи предмета (может быть несколько попыток - пересдачи).
 */
public class Attempt {
    private final Grade grade;

    /**
     * Конструктор попытки сдачи.
     *
     * @param grade оценка
     */
    public Attempt(Grade grade) {
        this.grade = grade;
    }

    /**
     * Получить оценку.
     *
     * @return оценка
     */
    public Grade getGrade() {
        return grade;
    }

    /**
     * Проверяет, является ли попытка успешной (не провалена).
     *
     * @return true если попытка успешна
     */
    public boolean isSuccessful() {
        return grade.isPassing();
    }
}
