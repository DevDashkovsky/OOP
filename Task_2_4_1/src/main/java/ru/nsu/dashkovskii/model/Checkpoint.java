package ru.nsu.dashkovskii.model;

import java.time.LocalDate;

/**
 * Контрольная точка курса.
 */
public record Checkpoint(String name, LocalDate date) {
}
