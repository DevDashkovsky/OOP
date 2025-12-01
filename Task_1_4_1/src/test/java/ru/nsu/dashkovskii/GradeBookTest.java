package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.enums.ControlType;
import ru.nsu.dashkovskii.enums.Grade;
import ru.nsu.dashkovskii.gradebook.GradeBook;
import ru.nsu.dashkovskii.model.Student;

/**
 * Тесты для класса GradeBook.
 */
public class GradeBookTest {

    @Test
    public void testAverageGrade() {
        Student student = new Student("Тестовый студент", false);
        GradeBook book = new GradeBook(student);
        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 2", ControlType.EXAM, Grade.GOOD);
        book.addGrade(1, "Предмет 3", ControlType.EXAM, Grade.SATISFACTORY);

        assertEquals(4.0, book.getAverageGrade(), 0.01);
    }

    @Test
    public void testAverageGradeWithPass() {
        Student student = new Student("Тестовый студент", false);
        GradeBook book = new GradeBook(student);
        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 2", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 3", ControlType.CREDIT, Grade.PASS);

        assertEquals(5.0, book.getAverageGrade(), 0.01);
    }

    @Test
    public void testCanTransferToBudgetSuccess() {
        Student student = new Student("Тестовый студент", true);
        GradeBook book = new GradeBook(student);

        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 2", ControlType.EXAM, Grade.GOOD);

        book.addGrade(2, "Предмет 3", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(2, "Предмет 4", ControlType.EXAM, Grade.GOOD);

        assertTrue(book.canTransferToBudget());
    }

    @Test
    public void testCanTransferToBudgetFailWithSatisfactory() {
        Student student = new Student("Тестовый студент", true);
        GradeBook book = new GradeBook(student);

        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 2", ControlType.EXAM, Grade.SATISFACTORY);

        book.addGrade(2, "Предмет 3", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(2, "Предмет 4", ControlType.EXAM, Grade.GOOD);

        assertFalse(book.canTransferToBudget());
    }

    @Test
    public void testCanTransferToBudgetFailNotPaid() {
        Student student = new Student("Тестовый студент", false);
        GradeBook book = new GradeBook(student);

        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(2, "Предмет 2", ControlType.EXAM, Grade.EXCELLENT);

        assertFalse(book.canTransferToBudget());
    }

    @Test
    public void testCanTransferToBudgetWithSatisfactoryInDiffCredit() {
        Student student = new Student("Тестовый студент", true);
        GradeBook book = new GradeBook(student);

        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 2", ControlType.DIFF_CREDIT, Grade.SATISFACTORY);

        book.addGrade(2, "Предмет 3", ControlType.EXAM, Grade.GOOD);
        book.addGrade(2, "Предмет 4", ControlType.EXAM, Grade.EXCELLENT);

        assertTrue(book.canTransferToBudget());
    }

    @Test
    public void testCanGetRedDiplomaSuccess() {
        Student student = new Student("Тестовый студент", false);
        GradeBook book = new GradeBook(student);

        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 2", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(2, "Предмет 3", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(2, "Предмет 4", ControlType.DIFF_CREDIT, Grade.EXCELLENT);
        book.addGrade(3, "Предмет 5", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(3, "Предмет 6", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(4, "Предмет 7", ControlType.DIFF_CREDIT, Grade.GOOD);
        book.addGrade(4, "Предмет 8", ControlType.EXAM, Grade.GOOD);

        assertTrue(book.canGetRedDiploma());
    }

    @Test
    public void testCanGetRedDiplomaFailWithSatisfactory() {
        Student student = new Student("Тестовый студент", false);
        GradeBook book = new GradeBook(student);

        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 2", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(2, "Предмет 3", ControlType.EXAM, Grade.SATISFACTORY);

        assertFalse(book.canGetRedDiploma());
    }

    @Test
    public void testCanGetRedDiplomaFailLessThan75Percent() {
        Student student = new Student("Тестовый студент", false);
        GradeBook book = new GradeBook(student);

        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 2", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(2, "Предмет 3", ControlType.EXAM, Grade.GOOD);
        book.addGrade(2, "Предмет 4", ControlType.EXAM, Grade.GOOD);

        assertFalse(book.canGetRedDiploma());
    }

    @Test
    public void testCanGetRedDiplomaWithThesisExcellent() {
        Student student = new Student("Тестовый студент", false);
        GradeBook book = new GradeBook(student);

        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 2", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(2, "Предмет 3", ControlType.EXAM, Grade.EXCELLENT);
        book.setThesisGrade(Grade.EXCELLENT);

        assertTrue(book.canGetRedDiploma());
    }

    @Test
    public void testCanGetRedDiplomaFailWithThesisGood() {
        Student student = new Student("Тестовый студент", false);
        GradeBook book = new GradeBook(student);

        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 2", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(2, "Предмет 3", ControlType.EXAM, Grade.EXCELLENT);
        book.setThesisGrade(Grade.GOOD);

        assertFalse(book.canGetRedDiploma());
    }

    @Test
    public void testCanGetIncreasedScholarshipSuccess() {
        Student student = new Student("Тестовый студент", false);
        GradeBook book = new GradeBook(student);

        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 2", ControlType.DIFF_CREDIT, Grade.EXCELLENT);

        assertTrue(book.canGetIncreasedScholarship());
    }

    @Test
    public void testCanGetIncreasedScholarshipFailWithGood() {
        Student student = new Student("Тестовый студент", false);
        GradeBook book = new GradeBook(student);

        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 2", ControlType.EXAM, Grade.GOOD);

        assertFalse(book.canGetIncreasedScholarship());
    }

    @Test
    public void testCanGetIncreasedScholarshipFailPaidStudent() {
        Student student = new Student("Тестовый студент", true);
        GradeBook book = new GradeBook(student);

        book.addGrade(1, "Предмет 1", ControlType.EXAM, Grade.EXCELLENT);
        book.addGrade(1, "Предмет 2", ControlType.EXAM, Grade.EXCELLENT);

        assertFalse(book.canGetIncreasedScholarship());
    }

    @Test
    public void testAverageGradeWithRetakes() {
        Student student = new Student("Тестовый студент", false);
        GradeBook book = new GradeBook(student);

        book.addGrade(1, "Математика", ControlType.EXAM, Grade.FAILED);
        book.addGrade(1, "Математика", ControlType.EXAM, Grade.GOOD);
        book.addGrade(1, "Физика", ControlType.EXAM, Grade.EXCELLENT);

        assertEquals(4.5, book.getAverageGrade(), 0.01);
    }
}
