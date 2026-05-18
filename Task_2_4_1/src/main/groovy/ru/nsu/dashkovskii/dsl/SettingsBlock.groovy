package ru.nsu.dashkovskii.dsl

import ru.nsu.dashkovskii.model.CheckerSettings

/**
 * Блок дополнительных настроек.
 */
class SettingsBlock {
    CheckerSettings settings

    def testTimeoutMs(Number v) {
        settings.testTimeoutMs = v.longValue()
    }

    /** Шкала: {@code gradeScale 5: 15, 4: 10, 3: 5} — порог суммарных баллов для оценки. */
    def gradeScale(Map<Integer, Number> scale) {
        scale.each { grade, threshold ->
            settings.gradeScale.put(threshold.doubleValue(), grade.intValue())
        }
    }

    /** Шкала активности: {@code activityScale 1.0: 0.8, 1.1: 0.5} — порог активности -> множитель. */
    def activityScale(Map<Number, Number> scale) {
        scale.each { threshold, mult ->
            settings.activityScale.put(threshold.doubleValue(), mult.doubleValue())
        }
    }

    def bonus(Map attrs) {
        settings.addBonus(new CheckerSettings.BonusPoint(
                (String) attrs.student,
                (String) attrs.task,
                ((Number) attrs.points).doubleValue()
        ))
    }
}
