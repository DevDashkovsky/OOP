package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.model.Student;

/**
 * Тесты для класса Student.
 */
public class StudentTest {

    @Test
    public void testStudentCreation() {
        Student student = new Student("Иванов Иван", false);
        assertEquals("Иванов Иван", student.getName());
        assertFalse(student.isPaid());
    }

    @Test
    public void testPaidStudent() {
        Student student = new Student("Петров Петр", true);
        assertTrue(student.isPaid());
    }
}

