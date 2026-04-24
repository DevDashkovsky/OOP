package ru.nsu.dashkovskii.checker;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Считает долю «активных» недель между двумя датами.
 */
public final class ActivityAnalyzer {

    private ActivityAnalyzer() {
    }

    /**
     * Возвращает долю активных недель в заданном интервале.
     *
     * @param repoDir локальная копия репозитория
     * @param from   начало учебного периода
     * @param to     конец учебного периода
     * @return доля недель, в которых был хотя бы один коммит, в [0..1]
     */
    public static double activityRatio(File repoDir, LocalDate from, LocalDate to) {
        if (!from.isBefore(to)) {
            return 0.0;
        }
        List<Long> timestamps = GitClient.listCommitTimestamps(repoDir);
        Set<Integer> activeWeeks = new HashSet<>();
        ZoneId zone = ZoneId.systemDefault();
        for (long ts : timestamps) {
            LocalDate d = Instant.ofEpochSecond(ts).atZone(zone).toLocalDate();
            if (d.isBefore(from) || d.isAfter(to)) {
                continue;
            }
            activeWeeks.add(weekKey(d));
        }
        long totalWeeks = Math.max(1, countWeeks(from, to));
        return Math.min(1.0, (double) activeWeeks.size() / totalWeeks);
    }

    private static int weekKey(LocalDate d) {
        int year = d.get(IsoFields.WEEK_BASED_YEAR);
        int week = d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return year * 100 + week;
    }

    private static long countWeeks(LocalDate from, LocalDate to) {
        LocalDate cursor = from;
        long count = 0;
        while (!cursor.isAfter(to)) {
            count++;
            cursor = cursor.plusWeeks(1);
        }
        return count;
    }
}
