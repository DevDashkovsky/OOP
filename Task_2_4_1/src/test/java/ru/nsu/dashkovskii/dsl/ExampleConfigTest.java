package ru.nsu.dashkovskii.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.model.CheckerConfig;

class ExampleConfigTest {

    @Test
    void loadsBundledExamples() {
        File examples = new File("examples");
        CheckerConfig config = ConfigLoader.loadFromDir(examples);

        assertEquals(2, config.getTasks().size());
        assertNotNull(config.getTasks().get("2_1_1"));
        assertNotNull(config.getTasks().get("2_3_1"));

        assertNotNull(config.getGroups().get("22213"));
        assertEquals(2, config.getGroups().get("22213").getStudents().size());

        assertEquals(3, config.getCheckpoints().size());

        assertTrue(config.getAssignment().getTasksFor("22213", "ivanov").contains("2_1_1"));
        assertTrue(config.getAssignment().getTasksFor("22213", "ivanov").contains("2_3_1"));
        assertEquals(1, config.getAssignment().getTasksFor("22213", "petrov").size());

        assertEquals(5, config.getSettings().grade(2.5));
        assertEquals(0.5, config.getSettings().bonusFor("petrov", "2_1_1"));
    }
}
