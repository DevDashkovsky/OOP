package ru.nsu.dashkovskii;

import java.time.LocalDate;

/**
 * Попытка сдачи предмета (может быть несколько попыток - пересдачи).
 */
public class Attempt {
    private final Grade grade;
    private final LocalDate date;

    /**
     * Конструктор попытки сдачи.
     *
     * @param grade оценка
     * @param date дата попытки
     */
    public Attempt(Grade grade, LocalDate date) {
        this.grade = grade;
        this.date = date;
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
     * Получить дату попытки.
     *
     * @return дата
     */
    public LocalDate getDate() {
        return date;
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

