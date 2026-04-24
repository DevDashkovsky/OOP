package ru.nsu.dashkovskii.dsl

import ru.nsu.dashkovskii.model.CheckerConfig
import ru.nsu.dashkovskii.model.LabTask

import java.time.LocalDate

/**
 * Блок описания задач.
 */
class TasksBlock {
    CheckerConfig config

    def task(String id, Closure closure) {
        def lab = config.tasks.getOrDefault(id, new LabTask(id))
        def delegate = new LabTaskDsl(lab: lab)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = delegate
        closure.call()
        config.addTask(lab)
    }

    def task(Map attrs, String id) {
        task(id) {
            if (attrs.name != null) name attrs.name
            if (attrs.maxPoints != null) maxPoints attrs.maxPoints
            if (attrs.softDeadline != null) softDeadline attrs.softDeadline
            if (attrs.hardDeadline != null) hardDeadline attrs.hardDeadline
        }
    }

    static class LabTaskDsl {
        LabTask lab

        def name(String v) { lab.name = v }

        def maxPoints(Number v) { lab.maxPoints = v.doubleValue() }

        def softDeadline(String v) { lab.softDeadline = LocalDate.parse(v) }

        def softDeadline(LocalDate v) { lab.softDeadline = v }

        def hardDeadline(String v) { lab.hardDeadline = LocalDate.parse(v) }

        def hardDeadline(LocalDate v) { lab.hardDeadline = v }
    }
}
