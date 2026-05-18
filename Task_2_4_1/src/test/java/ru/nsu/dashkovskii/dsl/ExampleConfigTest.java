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
        CheckerConfig config = new ConfigLoader().loadFromDir(examples);

        assertEquals(5, config.getTasks().size());
        assertNotNull(config.getTasks().get("2_1_1"));
        assertNotNull(config.getTasks().get("2_1_2"));
        assertNotNull(config.getTasks().get("2_2_1"));
        assertNotNull(config.getTasks().get("2_3_1"));
        assertNotNull(config.getTasks().get("2_4_1"));

        assertNotNull(config.getGroups().get("24216"));
        assertEquals(14, config.getGroups().get("24216").getStudents().size());

        assertEquals(3, config.getCheckpoints().size());

        assertTrue(config.getAssignment().getTasksFor("24216", "DevDashkovsky").contains("2_1_1"));
        assertTrue(config.getAssignment().getTasksFor("24216", "DevDashkovsky").contains("2_3_1"));
        assertEquals(1, config.getAssignment().getTasksFor("24216", "vylegzhaninn").size());

        assertEquals(5, config.getSettings().grade(2.5));
        assertEquals(0.5, config.getSettings().bonusFor("vylegzhaninn", "2_1_1"));
    }
}
