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
}
