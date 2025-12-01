package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.enums.ControlType;
import ru.nsu.dashkovskii.enums.Grade;
import ru.nsu.dashkovskii.model.Subject;

/**
 * Тесты для класса Subject.
 */
public class SubjectTest {

    @Test
    public void testAddAttempt() {
        Subject subject = new Subject("Математика", ControlType.EXAM);
        subject.addAttempt(Grade.EXCELLENT);

        assertEquals(1, subject.getAttemptsCount());
        assertEquals(Grade.EXCELLENT, subject.getLastPassingGrade().orElse(null));
    }

    @Test
    public void testMultipleAttempts() {
        Subject subject = new Subject("Математика", ControlType.EXAM);
        subject.addAttempt(Grade.FAILED);
        subject.addAttempt(Grade.FAILED);
        subject.addAttempt(Grade.GOOD);

        assertEquals(3, subject.getAttemptsCount());
        assertEquals(2, subject.getFailedAttemptsCount());
        assertEquals(Grade.GOOD, subject.getLastPassingGrade().orElse(null));
    }

    @Test
    public void testLastPassingGradeWithRetakes() {
        Subject subject = new Subject("Математика", ControlType.EXAM);
        subject.addAttempt(Grade.SATISFACTORY);
        subject.addAttempt(Grade.FAILED);
        subject.addAttempt(Grade.EXCELLENT);

        assertEquals(Grade.EXCELLENT, subject.getLastPassingGrade().orElse(null));
    }

    @Test
    public void testNoPassingGrade() {
        Subject subject = new Subject("Математика", ControlType.EXAM);
        subject.addAttempt(Grade.FAILED);
        subject.addAttempt(Grade.FAILED);

        Optional<Grade> lastGrade = subject.getLastPassingGrade();
        assertFalse(lastGrade.isPresent());
    }

    @Test
    public void testGetName() {
        Subject subject = new Subject("Физика", ControlType.DIFF_CREDIT);
        assertEquals("Физика", subject.getName());
    }

    @Test
    public void testGetControlType() {
        Subject subject = new Subject("Математика", ControlType.EXAM);
        assertEquals(ControlType.EXAM, subject.getControlType());
    }
}

