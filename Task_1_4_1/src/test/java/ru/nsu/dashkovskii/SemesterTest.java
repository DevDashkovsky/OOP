package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.enums.ControlType;
import ru.nsu.dashkovskii.enums.Grade;
import ru.nsu.dashkovskii.model.Semester;

/**
 * Тесты для класса Semester.
 */
public class SemesterTest {

    @Test
    public void testAddGrade() {
        Semester semester = new Semester(1);
        semester.addGrade("Математика", ControlType.EXAM, Grade.EXCELLENT);

        Map<String, Grade> grades = semester.getLastPassingGrades();
        assertEquals(1, grades.size());
        assertEquals(Grade.EXCELLENT, grades.get("Математика"));
    }

    @Test
    public void testAllExamsAndDiffCreditsExcellent() {
        Semester semester = new Semester(1);
        semester.addGrade("Математика", ControlType.EXAM, Grade.EXCELLENT);
        semester.addGrade("Физика", ControlType.DIFF_CREDIT, Grade.EXCELLENT);

        assertTrue(semester.allExamsAndDiffCreditsExcellent());
    }

    @Test
    public void testNotAllExamsExcellent() {
        Semester semester = new Semester(1);
        semester.addGrade("Математика", ControlType.EXAM, Grade.EXCELLENT);
        semester.addGrade("Физика", ControlType.EXAM, Grade.GOOD);

        assertFalse(semester.allExamsAndDiffCreditsExcellent());
    }

    @Test
    public void testHasSatisfactoryInDiplomaSubjects() {
        Semester semester = new Semester(1);
        semester.addGrade("Математика", ControlType.EXAM, Grade.SATISFACTORY);

        assertTrue(semester.hasSatisfactoryInDiplomaSubjects());
    }

    @Test
    public void testCountExcellentInDiplomaSubjects() {
        Semester semester = new Semester(1);
        semester.addGrade("Математика", ControlType.EXAM, Grade.EXCELLENT);
        semester.addGrade("Физика", ControlType.EXAM, Grade.EXCELLENT);
        semester.addGrade("История", ControlType.EXAM, Grade.GOOD);

        assertEquals(2, semester.countExcellentInDiplomaSubjects());
    }

    @Test
    public void testCountDiplomaSubjects() {
        Semester semester = new Semester(1);
        semester.addGrade("Математика", ControlType.EXAM, Grade.EXCELLENT);
        semester.addGrade("Физика", ControlType.DIFF_CREDIT, Grade.GOOD);
        semester.addGrade("Спорт", ControlType.CREDIT, Grade.PASS);

        assertEquals(2, semester.countDiplomaSubjects());
    }

    @Test
    public void testGetDiplomaGrades() {
        Semester semester = new Semester(1);
        semester.addGrade("Математика", ControlType.EXAM, Grade.EXCELLENT);
        semester.addGrade("Физика", ControlType.DIFF_CREDIT, Grade.GOOD);
        semester.addGrade("Спорт", ControlType.CREDIT, Grade.PASS);

        Map<String, Grade> diplomaGrades = semester.getDiplomaGrades();
        assertEquals(2, diplomaGrades.size());
        assertTrue(diplomaGrades.containsKey("Математика"));
        assertTrue(diplomaGrades.containsKey("Физика"));
        assertFalse(diplomaGrades.containsKey("Спорт"));
    }
}

