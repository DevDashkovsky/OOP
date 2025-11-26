package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Тесты для класса GradeBook.
 */
public class GradeBookTest {

    @Test
    public void testAverageGrade() {
        GradeBook book = new GradeBook("Тестовый студент", false);
        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.GOOD, 1));
        book.addRecord(new Record("Предмет 3", ControlType.EXAM, Grade.SATISFACTORY, 1));

        assertEquals(4.0, book.getAverageGrade(), 0.01);
    }

    @Test
    public void testAverageGradeWithPass() {
        GradeBook book = new GradeBook("Тестовый студент", false);
        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 3", ControlType.CREDIT, Grade.PASS, 1));

        assertEquals(5.0, book.getAverageGrade(), 0.01);
    }

    @Test
    public void testCanTransferToBudgetSuccess() {
        GradeBook book = new GradeBook("Тестовый студент", true);

        // Первый семестр
        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.GOOD, 1));

        // Второй семестр
        book.addRecord(new Record("Предмет 3", ControlType.EXAM, Grade.EXCELLENT, 2));
        book.addRecord(new Record("Предмет 4", ControlType.EXAM, Grade.GOOD, 2));

        assertTrue(book.canTransferToBudget());
    }

    @Test
    public void testCanTransferToBudgetFailWithSatisfactory() {
        GradeBook book = new GradeBook("Тестовый студент", true);

        // Первый семестр
        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.SATISFACTORY, 1));

        // Второй семестр
        book.addRecord(new Record("Предмет 3", ControlType.EXAM, Grade.EXCELLENT, 2));
        book.addRecord(new Record("Предмет 4", ControlType.EXAM, Grade.GOOD, 2));

        assertFalse(book.canTransferToBudget());
    }

    @Test
    public void testCanTransferToBudgetFailNotPaid() {
        GradeBook book = new GradeBook("Тестовый студент", false);

        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.EXCELLENT, 2));

        assertFalse(book.canTransferToBudget());
    }

    @Test
    public void testCanTransferToBudgetWithSatisfactoryInDiffCredit() {
        GradeBook book = new GradeBook("Тестовый студент", true);

        // Первый семестр
        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.DIFF_CREDIT, Grade.SATISFACTORY, 1));

        // Второй семестр
        book.addRecord(new Record("Предмет 3", ControlType.EXAM, Grade.GOOD, 2));
        book.addRecord(new Record("Предмет 4", ControlType.EXAM, Grade.EXCELLENT, 2));

        assertTrue(book.canTransferToBudget());
    }

    @Test
    public void testCanGetRedDiplomaSuccess() {
        GradeBook book = new GradeBook("Тестовый студент", false);

        // 6 из 8 оценок отлично (75%)
        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 3", ControlType.EXAM, Grade.EXCELLENT, 2));
        book.addRecord(new Record("Предмет 4", ControlType.DIFF_CREDIT, Grade.EXCELLENT, 2));
        book.addRecord(new Record("Предмет 5", ControlType.EXAM, Grade.EXCELLENT, 3));
        book.addRecord(new Record("Предмет 6", ControlType.EXAM, Grade.EXCELLENT, 3));
        book.addRecord(new Record("Предмет 7", ControlType.DIFF_CREDIT, Grade.GOOD, 4));
        book.addRecord(new Record("Предмет 8", ControlType.EXAM, Grade.GOOD, 4));

        assertTrue(book.canGetRedDiploma());
    }

    @Test
    public void testCanGetRedDiplomaFailWithSatisfactory() {
        GradeBook book = new GradeBook("Тестовый студент", false);

        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 3", ControlType.EXAM, Grade.SATISFACTORY, 2));

        assertFalse(book.canGetRedDiploma());
    }

    @Test
    public void testCanGetRedDiplomaFailLessThan75Percent() {
        GradeBook book = new GradeBook("Тестовый студент", false);

        // Только 50% отлично
        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 3", ControlType.EXAM, Grade.GOOD, 2));
        book.addRecord(new Record("Предмет 4", ControlType.EXAM, Grade.GOOD, 2));

        assertFalse(book.canGetRedDiploma());
    }

    @Test
    public void testCanGetRedDiplomaWithThesisExcellent() {
        GradeBook book = new GradeBook("Тестовый студент", false);

        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 3", ControlType.EXAM, Grade.EXCELLENT, 2));
        book.addRecord(new Record("ВКР", ControlType.THESIS, Grade.EXCELLENT, 8));

        assertTrue(book.canGetRedDiploma());
    }

    @Test
    public void testCanGetRedDiplomaFailWithThesisGood() {
        GradeBook book = new GradeBook("Тестовый студент", false);

        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 3", ControlType.EXAM, Grade.EXCELLENT, 2));
        book.addRecord(new Record("ВКР", ControlType.THESIS, Grade.GOOD, 8));

        assertFalse(book.canGetRedDiploma());
    }

    @Test
    public void testCanGetIncreasedScholarshipSuccess() {
        GradeBook book = new GradeBook("Тестовый студент", false);

        // Предыдущий семестр
        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.GOOD, 1));

        // Текущий семестр - все отлично
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.EXCELLENT, 2));
        book.addRecord(new Record("Предмет 3", ControlType.EXAM, Grade.EXCELLENT, 2));
        book.addRecord(new Record("Предмет 4", ControlType.DIFF_CREDIT, Grade.EXCELLENT, 2));

        assertTrue(book.canGetIncreasedScholarship());
    }

    @Test
    public void testCanGetIncreasedScholarshipFailWithGood() {
        GradeBook book = new GradeBook("Тестовый студент", false);

        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.GOOD, 1));

        assertFalse(book.canGetIncreasedScholarship());
    }

    @Test
    public void testCanGetIncreasedScholarshipFailPaidStudent() {
        GradeBook book = new GradeBook("Тестовый студент", true);

        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.EXCELLENT, 1));

        assertFalse(book.canGetIncreasedScholarship());
    }

    @Test
    public void testCanGetIncreasedScholarshipIgnoresCredit() {
        GradeBook book = new GradeBook("Тестовый студент", false);

        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.DIFF_CREDIT, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 3", ControlType.CREDIT, Grade.PASS, 1));

        assertTrue(book.canGetIncreasedScholarship());
    }

    @Test
    public void testLastGradeUsedForDiploma() {
        GradeBook book = new GradeBook("Тестовый студент", false);

        // Первая попытка - удовлетворительно
        book.addRecord(new Record("Математика", ControlType.EXAM, Grade.SATISFACTORY, 1));
        // Пересдача - отлично
        book.addRecord(new Record("Математика", ControlType.EXAM, Grade.EXCELLENT, 2));

        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 3", ControlType.EXAM, Grade.EXCELLENT, 2));

        assertTrue(book.canGetRedDiploma());
    }

    @Test
    public void testGetStudentName() {
        GradeBook book = new GradeBook("Иванов Иван Иванович", false);
        assertEquals("Иванов Иван Иванович", book.getStudentName());
    }

    @Test
    public void testIsPaid() {
        GradeBook paidStudent = new GradeBook("Платный студент", true);
        GradeBook budgetStudent = new GradeBook("Бюджетный студент", false);

        assertTrue(paidStudent.isPaid());
        assertFalse(budgetStudent.isPaid());
    }

    @Test
    public void testGetRecords() {
        GradeBook book = new GradeBook("Тестовый студент", false);
        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.DIFF_CREDIT, Grade.GOOD, 1));
        book.addRecord(new Record("Предмет 3", ControlType.CREDIT, Grade.PASS, 1));

        assertEquals(3, book.getRecords().size());
        assertEquals("Предмет 1", book.getRecords().get(0).getSubjectName());
        assertEquals("Предмет 2", book.getRecords().get(1).getSubjectName());
        assertEquals("Предмет 3", book.getRecords().get(2).getSubjectName());
    }

    @Test
    public void testOtherControlTypeNotInDiploma() {
        GradeBook book = new GradeBook("Тестовый студент", false);

        // Добавляем оценки, включая OTHER
        book.addRecord(new Record("Контрольная работа", ControlType.OTHER, Grade.SATISFACTORY, 1));
        book.addRecord(new Record("Предмет 1", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 2", ControlType.EXAM, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Предмет 3", ControlType.EXAM, Grade.EXCELLENT, 2));

        // OTHER не должен влиять на красный диплом
        assertTrue(book.canGetRedDiploma());

        // Но должен учитываться в среднем балле
        assertEquals(4.5, book.getAverageGrade(), 0.01);
    }

    @Test
    public void testAverageGradeWithOtherControlType() {
        GradeBook book = new GradeBook("Тестовый студент", false);
        book.addRecord(new Record("Задание", ControlType.OTHER, Grade.EXCELLENT, 1));
        book.addRecord(new Record("Контрольная", ControlType.OTHER, Grade.GOOD, 1));
        book.addRecord(new Record("Коллоквиум", ControlType.OTHER, Grade.SATISFACTORY, 1));

        assertEquals(4.0, book.getAverageGrade(), 0.01);
    }
}
