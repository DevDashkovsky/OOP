package ru.nsu.dashkovskii.report;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.model.CheckerConfig;
import ru.nsu.dashkovskii.model.Checkpoint;
import ru.nsu.dashkovskii.model.GroupReport;
import ru.nsu.dashkovskii.model.LabTask;
import ru.nsu.dashkovskii.model.Student;
import ru.nsu.dashkovskii.model.StudentGroup;
import ru.nsu.dashkovskii.model.StudentReport;
import ru.nsu.dashkovskii.model.TaskResult;

class HtmlReportGeneratorTest {

    @Test
    void rendersGroupWithTaskResults() {
        CheckerConfig config = new CheckerConfig();
        LabTask task = new LabTask("2_1_1");
        task.setName("Простые числа");
        task.setMaxPoints(1);
        config.addTask(task);

        StudentGroup group = new StudentGroup("22213");
        Student s = new Student("ivanov");
        s.setFullName("Иванов Иван");
        group.addStudent(s);
        config.addGroup(group);

        TaskResult r = new TaskResult("2_1_1");
        r.setBuilt(true);
        r.setDocs(true);
        r.setStyleOk(true);
        r.setTestsPassed(10);
        r.setTotal(1.0);

        StudentReport sr = new StudentReport(s);
        sr.addResult(r);

        GroupReport gr = new GroupReport(group);
        gr.addStudentReport(sr);

        String html = new HtmlReportGenerator(config).render(List.of(gr));

        assertTrue(html.contains("Группа 22213"));
        assertTrue(html.contains("Простые числа"));
        assertTrue(html.contains("Иванов Иван"));
        assertTrue(html.contains("10/0/0"));
    }

    @Test
    void rendersErrorMessageInsteadOfStages() {
        CheckerConfig config = new CheckerConfig();
        LabTask task = new LabTask("2_1_1");
        task.setName("Простые числа");
        task.setMaxPoints(1);
        config.addTask(task);

        StudentGroup group = new StudentGroup("24216");
        Student s = new Student("DevDashkovsky");
        s.setFullName("Дашковский Егор");
        group.addStudent(s);
        config.addGroup(group);

        TaskResult r = new TaskResult("2_1_1");
        r.setErrorMessage("Не удалось клонировать репозиторий");

        StudentReport sr = new StudentReport(s);
        sr.addResult(r);
        GroupReport gr = new GroupReport(group);
        gr.addStudentReport(sr);

        String html = new HtmlReportGenerator(config).render(List.of(gr));

        assertTrue(html.contains("Не удалось клонировать репозиторий"));
        assertTrue(html.contains("colspan=\"6\""));
    }

    @Test
    void checkpointIncludesOnlySubmittedBeforeDate() {
        CheckerConfig config = new CheckerConfig();
        LabTask task = new LabTask("2_1_1");
        task.setName("Простые числа");
        task.setMaxPoints(1);
        config.addTask(task);
        config.addCheckpoint(new Checkpoint("АК1", LocalDate.of(2024, 11, 1)));
        config.getSettings().getGradeScale().put(1.0, 5);

        StudentGroup group = new StudentGroup("22213");
        Student s = new Student("ivanov");
        s.setFullName("Иванов Иван");
        group.addStudent(s);
        config.addGroup(group);

        // сдана до АК1 — должна учитываться
        TaskResult early = new TaskResult("2_1_1");
        early.setTotal(1.0);
        early.setSubmissionDate(LocalDate.of(2024, 10, 15));

        StudentReport sr = new StudentReport(s);
        sr.addResult(early);

        GroupReport gr = new GroupReport(group);
        gr.addStudentReport(sr);

        String html = new HtmlReportGenerator(config).render(List.of(gr));
        assertTrue(html.contains("АК1"));
        assertTrue(html.contains("Иванов Иван"));
        // оценка 5 по шкале
        assertTrue(html.contains(">5<"));

        // тот же студент, задача сдана ПОСЛЕ АК1
        StudentReport sr2 = new StudentReport(s);
        TaskResult late = new TaskResult("2_1_1");
        late.setTotal(1.0);
        late.setSubmissionDate(LocalDate.of(2024, 12, 1));
        sr2.addResult(late);
        GroupReport gr2 = new GroupReport(group);
        gr2.addStudentReport(sr2);

        String html2 = new HtmlReportGenerator(config).render(List.of(gr2));
        // задача сдана после КТ — в таблице КТ оценка "-" (grade=-1)
        assertTrue(html2.contains("<td>-</td>"));
    }
}
