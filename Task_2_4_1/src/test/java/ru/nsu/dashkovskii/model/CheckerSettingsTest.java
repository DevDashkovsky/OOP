package ru.nsu.dashkovskii.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CheckerSettingsTest {

    @Test
    void gradeScaleReturnsHighestMatch() {
        CheckerSettings s = new CheckerSettings();
        s.getGradeScale().put(2.5, 5);
        s.getGradeScale().put(1.5, 4);
        s.getGradeScale().put(0.5, 3);
        assertEquals(5, s.grade(10));
        assertEquals(4, s.grade(2.0));
        assertEquals(3, s.grade(0.5));
        assertEquals(-1, s.grade(0.1));
    }

    @Test
    void activityMultiplierUsesDescendingThresholds() {
        CheckerSettings s = new CheckerSettings();
        s.getActivityScale().put(0.8, 1.0);
        s.getActivityScale().put(0.5, 0.9);
        s.getActivityScale().put(0.0, 0.8);
        assertEquals(1.0, s.activityMultiplier(0.9));
        assertEquals(0.9, s.activityMultiplier(0.6));
        assertEquals(0.8, s.activityMultiplier(0.0));
    }

    @Test
    void bonusForSumsMatchingEntries() {
        CheckerSettings s = new CheckerSettings();
        s.addBonus(new CheckerSettings.BonusPoint("ivanov", "2_1_1", 0.5));
        s.addBonus(new CheckerSettings.BonusPoint("ivanov", "2_1_1", 0.25));
        s.addBonus(new CheckerSettings.BonusPoint("petrov", "2_1_1", 1.0));
        assertEquals(0.75, s.bonusFor("ivanov", "2_1_1"));
        assertEquals(0.0, s.bonusFor("ivanov", "2_3_1"));
    }
}
