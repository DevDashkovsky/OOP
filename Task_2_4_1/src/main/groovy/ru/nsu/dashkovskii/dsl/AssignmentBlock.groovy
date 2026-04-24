package ru.nsu.dashkovskii.dsl

import ru.nsu.dashkovskii.model.CheckerConfig

/**
 * Блок задания на проверку.
 */
class AssignmentBlock {
    CheckerConfig config

    def check(String groupName, String studentNick, List<String> taskIds) {
        config.assignment.addCheck(groupName, studentNick, taskIds)
    }

    /** Удобная форма: {@code check group: '12345', student: 'ivanov', tasks: ['2_1_1']}. */
    def check(Map attrs) {
        check((String) attrs.group, (String) attrs.student, (List<String>) attrs.tasks)
    }

    /** Все студенты группы получают набор задач. */
    def checkGroup(String groupName, List<String> taskIds) {
        def grp = config.groups[groupName]
        if (grp == null) {
            throw new IllegalArgumentException("Группа не найдена: ${groupName}")
        }
        grp.students.each { s ->
            config.assignment.addCheck(groupName, s.githubNick, taskIds)
        }
    }
}
