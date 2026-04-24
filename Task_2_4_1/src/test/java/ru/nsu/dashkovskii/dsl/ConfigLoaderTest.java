package ru.nsu.dashkovskii.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.nsu.dashkovskii.model.CheckerConfig;

class ConfigLoaderTest {

    @Test
    void loadsTasksGroupsAssignmentAndSettings(@TempDir Path dir) throws Exception {
        Files.writeString(dir.resolve("tasks.groovy"), """
                tasks {
                    task '2_1_1', {
                        name 'Простые числа'
                        maxPoints 1
                        softDeadline '2024-10-01'
                        hardDeadline '2024-10-15'
                    }
                }
                """);
        Files.writeString(dir.resolve("checker.groovy"), """
                importConfig 'tasks.groovy'
                groups {
                    group('22213') {
                        student('ivanov') {
                            fullName 'Иванов'
                            repoUrl 'https://example.org/ivanov.git'
                        }
                    }
                }
                assignment {
                    check group: '22213', student: 'ivanov', tasks: ['2_1_1']
                }
                checkpoints {
                    checkpoint 'АК1', '2024-11-01'
                }
                settings {
                    testTimeoutMs 30_000
                    gradeScale([5: 2.5, 4: 1.5, 3: 0.5])
                    bonus student: 'ivanov', task: '2_1_1', points: 0.5
                }
                """);

        CheckerConfig config = ConfigLoader.loadFromDir(dir.toFile());

        assertEquals(1, config.getTasks().size());
        assertEquals("Простые числа", config.getTasks().get("2_1_1").getName());
        assertEquals(LocalDate.of(2024, 10, 15),
                config.getTasks().get("2_1_1").getHardDeadline());

        assertNotNull(config.getGroups().get("22213"));
        assertEquals("Иванов",
                config.getGroups().get("22213").findByNick("ivanov").getFullName());

        assertEquals(1, config.getCheckpoints().size());

        assertEquals(1, config.getAssignment().getTasksFor("22213", "ivanov").size());

        assertEquals(30_000, config.getSettings().getTestTimeoutMs());
        assertEquals(0.5, config.getSettings().bonusFor("ivanov", "2_1_1"));
        assertEquals(5, config.getSettings().grade(2.5));
        assertEquals(4, config.getSettings().grade(1.5));
        assertEquals(3, config.getSettings().grade(0.5));
        assertEquals(-1, config.getSettings().grade(0.0));
    }

    @Test
    void importResolvesRelativeToScript(@TempDir Path root) throws Exception {
        File shared = root.resolve("shared").toFile();
        shared.mkdirs();
        Files.writeString(shared.toPath().resolve("tasks.groovy"), """
                tasks {
                    task '1_1_1', { name 'Hello'; maxPoints 1 }
                }
                """);
        File semester = root.resolve("semester").toFile();
        semester.mkdirs();
        Files.writeString(semester.toPath().resolve("checker.groovy"), """
                importConfig '../shared/tasks.groovy'
                """);

        CheckerConfig config = ConfigLoader.loadFromDir(semester);
        assertTrue(config.getTasks().containsKey("1_1_1"));
        assertEquals("Hello", config.getTasks().get("1_1_1").getName());
    }
}
