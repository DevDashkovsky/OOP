package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.model.Order;
import ru.nsu.dashkovskii.model.OrderState;
import ru.nsu.dashkovskii.service.Warehouse;

/**
 * Тесты для проверки логики работы склада.
 */
public class WarehouseTest {

    @Test
    public void testAddAndTakePizza() throws InterruptedException {
        Warehouse warehouse = new Warehouse(5);
        Order order = new Order(1);

        warehouse.addPizza(order);

        List<Order> taken = warehouse.takePizzas(1);
        assertEquals(1, taken.size());
        assertEquals(order, taken.get(0));
        assertEquals(OrderState.IN_WAREHOUSE, taken.get(0).getState());
    }

    @Test
    public void testTakeMultiplePizzas() throws InterruptedException {
        Warehouse warehouse = new Warehouse(5);
        warehouse.addPizza(new Order(1));
        warehouse.addPizza(new Order(2));
        warehouse.addPizza(new Order(3));

        List<Order> taken = warehouse.takePizzas(2);
        assertEquals(2, taken.size());

        taken = warehouse.takePizzas(2);
        assertEquals(1, taken.size());
    }
}
