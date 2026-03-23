package ru.nsu.dashkovskii;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import ru.nsu.dashkovskii.model.Configuration;
import ru.nsu.dashkovskii.model.Order;
import ru.nsu.dashkovskii.service.Baker;
import ru.nsu.dashkovskii.service.Courier;
import ru.nsu.dashkovskii.service.PizzeriaClient;
import ru.nsu.dashkovskii.service.Warehouse;
import ru.nsu.dashkovskii.strategy.GracefulShutdown;
import ru.nsu.dashkovskii.strategy.ShutdownStrategy;
import ru.nsu.dashkovskii.util.CustomQueue;
import ru.nsu.dashkovskii.util.JsonConfigLoader;

/**
 * Управляет жизненным циклом симуляции пиццерии.
 */
public class PizzeriaManager {

    private final List<Thread> bakers = new ArrayList<>();
    private final List<Thread> couriers = new ArrayList<>();
    private Thread clientThread;
    private PizzeriaClient client;
    private final ShutdownStrategy shutdownStrategy = new GracefulShutdown();

    /**
     * Точка входа для прямого запуска менеджера.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        PizzeriaManager manager = new PizzeriaManager();
        manager.start();
    }

    /**
     * Запускает симуляцию пиццерии.
     */
    public void start() {
        JsonConfigLoader loader = new JsonConfigLoader();
        Configuration config = loader.load("config.json");

        CustomQueue<Order> orderQueue = new CustomQueue<>(config.getStorageCapacity());
        Warehouse warehouse = new Warehouse(config.getWarehouseCapacity());

        for (int i = 0; i < config.getBakersCount(); i++) {
            Baker baker = new Baker(i, config.getCookingTimeMs(), orderQueue, warehouse);
            Thread t = new Thread(baker, "Baker-" + i);
            t.start();
            bakers.add(t);
        }

        for (int i = 0; i < config.getCouriersCount(); i++) {
            Courier courier = new Courier(warehouse, config.getTrunkCapacity(),
                    config.getDeliveryTimeMs());
            Thread t = new Thread(courier, "Courier-" + i);
            t.start();
            couriers.add(t);
        }

        client = new PizzeriaClient(orderQueue);
        clientThread = new Thread(client, "ClientGenerator");
        clientThread.start();

        System.out.println("Pizzeria started! Type 'stop' to shutdown.");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if ("stop".equalsIgnoreCase(line.trim())) {
                stopPizzeria();
                break;
            }
        }
    }

    private void stopPizzeria() {
        System.out.println("Stopping pizzeria...");

        List<Thread> allWorkers = new ArrayList<>();
        allWorkers.addAll(bakers);
        allWorkers.addAll(couriers);

        shutdownStrategy.shutdown(client, clientThread, allWorkers);

        System.out.println("Pizzeria stopped.");
    }
}
