package ru.nsu.dashkovskii.dsl

import ru.nsu.dashkovskii.model.CheckerConfig

/**
 * Блок задания на проверку.
 */
class AssignmentBlock {
    CheckerConfig config

    /** Форма: {@code check group: '12345', student: 'ivanov', tasks: ['2_1_1']}. */
    def check(Map attrs) {
        config.assignment.addCheck(
                (String) attrs.group,
                (String) attrs.student,
                (List<String>) attrs.tasks
        )
    }
}
