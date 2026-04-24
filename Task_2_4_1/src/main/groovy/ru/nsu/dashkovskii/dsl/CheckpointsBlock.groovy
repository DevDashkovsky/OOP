package ru.nsu.dashkovskii.dsl

import ru.nsu.dashkovskii.model.Checkpoint
import ru.nsu.dashkovskii.model.CheckerConfig

import java.time.LocalDate

/**
 * Блок контрольных точек.
 */
class CheckpointsBlock {
    CheckerConfig config

    def checkpoint(String name, String date) {
        config.addCheckpoint(new Checkpoint(name, LocalDate.parse(date)))
    }

    def checkpoint(Map attrs) {
        config.addCheckpoint(new Checkpoint(
                (String) attrs.name,
                attrs.date instanceof LocalDate ? (LocalDate) attrs.date : LocalDate.parse((String) attrs.date)
        ))
    }
}
