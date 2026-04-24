package ru.nsu.dashkovskii.report;

import java.util.List;
import java.util.Map;
import ru.nsu.dashkovskii.model.CheckerConfig;
import ru.nsu.dashkovskii.model.Checkpoint;
import ru.nsu.dashkovskii.model.GroupReport;
import ru.nsu.dashkovskii.model.LabTask;
import ru.nsu.dashkovskii.model.StudentReport;
import ru.nsu.dashkovskii.model.TaskResult;

/**
 * Генератор HTML-отчёта по результатам проверки.
 */
public final class HtmlReportGenerator {

    private final CheckerConfig config;

    public HtmlReportGenerator(CheckerConfig config) {
        this.config = config;
    }

    /** Возвращает HTML документ. */
    public String render(List<GroupReport> reports) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html lang=\"ru\"><head><meta charset=\"utf-8\">")
                .append("<title>OOP checker report</title>")
                .append("<style>body{font-family:sans-serif;}")
                .append("table{border-collapse:collapse;margin:12px 0;}")
                .append("th,td{border:1px solid #999;padding:4px 8px;}")
                .append("th{background:#eef;}.ok{color:#060}.fail{color:#a00}</style>")
                .append("</head><body>");
        sb.append("<h1>Отчёт проверки</h1>");

        for (GroupReport gr : reports) {
            renderGroup(sb, gr);
        }
        renderCheckpoints(sb, reports);
        sb.append("</body></html>");
        return sb.toString();
    }

    private void renderGroup(StringBuilder sb, GroupReport gr) {
        sb.append("<h2>Группа ").append(escape(gr.getGroup().getName())).append("</h2>");
        Map<String, LabTask> tasks = config.getTasks();
        for (Map.Entry<String, LabTask> e : tasks.entrySet()) {
            String taskId = e.getKey();
            boolean usedInGroup = gr.getStudentReports().stream()
                    .flatMap(sr -> sr.getResults().stream())
                    .anyMatch(r -> r.getTaskId().equals(taskId));
            if (!usedInGroup) {
                continue;
            }
            renderTaskTable(sb, gr, e.getValue());
        }
        renderSummaryTable(sb, gr);
    }

    private void renderTaskTable(StringBuilder sb, GroupReport gr, LabTask task) {
        String tname = task.getName() == null ? "" : task.getName();
        sb.append("<h3>Лабораторная ").append(escape(task.getId()))
                .append(" (").append(escape(tname)).append(")</h3>");
        sb.append("<table><tr><th>Студент</th><th>Сборка</th>")
                .append("<th>Документация</th><th>Style guide</th>")
                .append("<th>Тесты</th><th>Доп. балл</th><th>Общий балл</th></tr>");
        for (StudentReport sr : gr.getStudentReports()) {
            TaskResult r = sr.getResults().stream()
                    .filter(it -> it.getTaskId().equals(task.getId()))
                    .findFirst().orElse(null);
            if (r == null) {
                continue;
            }
            sb.append("<tr><td>").append(escape(displayName(sr))).append("</td>")
                    .append(mark(r.isBuilt())).append(mark(r.isDocs()))
                    .append(mark(r.isStyleOk()))
                    .append("<td>").append(r.getTestsPassed()).append('/')
                    .append(r.getTestsFailed()).append('/')
                    .append(r.getTestsSkipped()).append("</td>")
                    .append("<td>").append(fmt(r.getBonus())).append("</td>")
                    .append("<td>").append(fmt(r.getTotal())).append("</td></tr>");
        }
        sb.append("</table>");
    }

    private void renderSummaryTable(StringBuilder sb, GroupReport gr) {
        sb.append("<h3>Общая статистика группы ")
                .append(escape(gr.getGroup().getName())).append("</h3>");
        sb.append("<table><tr><th>Студент</th>");
        for (String id : config.getTasks().keySet()) {
            sb.append("<th>").append(escape(id)).append("</th>");
        }
        sb.append("<th>Сумма</th><th>Активность</th><th>Оценка</th></tr>");
        for (StudentReport sr : gr.getStudentReports()) {
            sb.append("<tr><td>").append(escape(displayName(sr))).append("</td>");
            for (String id : config.getTasks().keySet()) {
                TaskResult r = sr.getResults().stream()
                        .filter(it -> it.getTaskId().equals(id))
                        .findFirst().orElse(null);
                sb.append("<td>").append(r == null ? "-" : fmt(r.getTotal())).append("</td>");
            }
            double sum = sr.totalPoints();
            double mult = config.getSettings().activityMultiplier(sr.getActivityRatio());
            double weighted = sum * mult;
            int grade = config.getSettings().grade(weighted);
            String activity = String.format(java.util.Locale.ROOT,
                    "%.0f%%", sr.getActivityRatio() * 100);
            sb.append("<td>").append(fmt(sum)).append("</td>")
                    .append("<td>").append(activity).append("</td>")
                    .append("<td>").append(grade < 0 ? "-" : grade).append("</td></tr>");
        }
        sb.append("</table>");
    }

    private static String displayName(StudentReport sr) {
        String full = sr.getStudent().getFullName();
        return full == null ? sr.getStudent().getGithubNick() : full;
    }

    private void renderCheckpoints(StringBuilder sb, List<GroupReport> reports) {
        if (config.getCheckpoints().isEmpty()) {
            return;
        }
        sb.append("<h2>Контрольные точки</h2>");
        for (Checkpoint cp : config.getCheckpoints()) {
            sb.append("<h3>").append(escape(cp.name()))
                    .append(" — ").append(cp.date()).append("</h3>");
            for (GroupReport gr : reports) {
                renderCheckpointTable(sb, cp, gr);
            }
        }
    }

    private void renderCheckpointTable(StringBuilder sb, Checkpoint cp, GroupReport gr) {
        sb.append("<h4>Группа ").append(escape(gr.getGroup().getName())).append("</h4>");
        sb.append("<table><tr><th>Студент</th>");
        for (String id : config.getTasks().keySet()) {
            sb.append("<th>").append(escape(id)).append("</th>");
        }
        sb.append("<th>Сумма</th><th>Оценка</th></tr>");
        for (StudentReport sr : gr.getStudentReports()) {
            sb.append("<tr><td>").append(escape(displayName(sr))).append("</td>");
            double sum = 0;
            for (String id : config.getTasks().keySet()) {
                TaskResult r = sr.getResults().stream()
                        .filter(it -> it.getTaskId().equals(id)
                                && it.getSubmissionDate() != null
                                && !it.getSubmissionDate().isAfter(cp.date()))
                        .findFirst().orElse(null);
                double pts = r == null ? 0 : r.getTotal();
                sum += pts;
                sb.append("<td>").append(r == null ? "-" : fmt(pts)).append("</td>");
            }
            double mult = config.getSettings().activityMultiplier(sr.getActivityRatio());
            int grade = config.getSettings().grade(sum * mult);
            sb.append("<td>").append(fmt(sum)).append("</td>")
                    .append("<td>").append(grade < 0 ? "-" : grade).append("</td></tr>");
        }
        sb.append("</table>");
    }

    private static String mark(boolean ok) {
        return ok ? "<td class=\"ok\">+</td>" : "<td class=\"fail\">-</td>";
    }

    private static String fmt(double v) {
        if (v == Math.floor(v)) {
            return Integer.toString((int) v);
        }
        return String.format(java.util.Locale.ROOT, "%.2f", v);
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
