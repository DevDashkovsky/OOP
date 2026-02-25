package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.model.Configuration;
import ru.nsu.dashkovskii.util.JsonConfigLoader;

/**
 * Тесты для проверки загрузки конфигурации.
 */
public class ConfigurationTest {

    @Test
    public void testConfigLoading() {
        JsonConfigLoader loader = new JsonConfigLoader();
        Configuration config = loader.load("config.json");

        assertNotNull(config, "Configuration should be loaded");
        assertEquals(3, config.getBakersCount(), "Bakers count should be 3");
        assertEquals(2, config.getCouriersCount(), "Couriers count should be 2");
        assertEquals(10, config.getWarehouseCapacity(), "Warehouse capacity should be 10");
        assertEquals(20, config.getStorageCapacity(), "Storage capacity should be 20");
        assertEquals(2000, config.getCookingTimeMs(), "Cooking time should be 2000");
        assertEquals(3000, config.getDeliveryTimeMs(), "Delivery time should be 3000");
        assertEquals(2, config.getTrunkCapacity(), "Trunk capacity should be 2");
    }
}
