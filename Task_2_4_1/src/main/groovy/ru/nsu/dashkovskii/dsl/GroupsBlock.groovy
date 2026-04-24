package ru.nsu.dashkovskii.dsl

import ru.nsu.dashkovskii.model.CheckerConfig
import ru.nsu.dashkovskii.model.Student
import ru.nsu.dashkovskii.model.StudentGroup

/**
 * Блок описания групп и студентов.
 */
class GroupsBlock {
    CheckerConfig config

    def group(String name, Closure closure) {
        def grp = config.groups.getOrDefault(name, new StudentGroup(name))
        def delegate = new GroupDsl(group: grp)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = delegate
        closure.call()
        config.addGroup(grp)
    }

    static class GroupDsl {
        StudentGroup group

        def student(String nick, Closure closure) {
            def existing = group.findByNick(nick)
            def s = existing ?: new Student(nick)
            def delegate = new StudentDsl(student: s)
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.delegate = delegate
            closure.call()
            if (existing == null) {
                group.addStudent(s)
            }
        }
    }

    static class StudentDsl {
        Student student

        def fullName(String v) { student.fullName = v }

        def repoUrl(String v) { student.repoUrl = v }
    }
}
