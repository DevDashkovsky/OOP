package ru.nsu.dashkovskii.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.model.LabTask;
import ru.nsu.dashkovskii.model.TaskResult;

class ScoreCalculatorTest {

    private final ScoreCalculator calc = new ScoreCalculator();

    @Test
    void allFourStagesGiveMaxPoints() {
        LabTask task = new LabTask("1_1_1");
        task.setMaxPoints(2.0);
        TaskResult r = new TaskResult("1_1_1");
        r.setBuilt(true);
        r.setDocs(true);
        r.setStyleOk(true);
        r.setTestsPassed(8);

        calc.apply(task, 0, r);
        assertEquals(2.0, r.getTotal());
    }

    @Test
    void halfTestsGiveSeventhEighths() {
        LabTask task = new LabTask("1");
        task.setMaxPoints(1.0);
        TaskResult r = new TaskResult("1");
        r.setBuilt(true);
        r.setDocs(true);
        r.setStyleOk(true);
        r.setTestsPassed(1);
        r.setTestsFailed(1);

        calc.apply(task, 0, r);
        assertEquals(0.875, r.getTotal());
    }

    @Test
    void afterSoftDeadlineHalvesAndAddsBonus() {
        LabTask task = new LabTask("1");
        task.setMaxPoints(1.0);
        task.setSoftDeadline(LocalDate.of(2025, 1, 1));
        TaskResult r = new TaskResult("1");
        r.setBuilt(true);
        r.setDocs(true);
        r.setStyleOk(true);
        r.setTestsPassed(4);
        r.setSubmissionDate(LocalDate.of(2025, 1, 5));

        calc.apply(task, 0.25, r);
        assertEquals(0.75, r.getTotal());
    }

    @Test
    void afterHardDeadlineZerosOut() {
        LabTask task = new LabTask("1");
        task.setMaxPoints(1.0);
        task.setHardDeadline(LocalDate.of(2025, 1, 10));
        TaskResult r = new TaskResult("1");
        r.setBuilt(true);
        r.setDocs(true);
        r.setStyleOk(true);
        r.setTestsPassed(4);
        r.setSubmissionDate(LocalDate.of(2025, 1, 11));

        calc.apply(task, 0.1, r);
        assertEquals(0.1, r.getTotal());
    }
}
