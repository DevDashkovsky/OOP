package ru.nsu.dashkovskii.model;

/**
 * Класс конфигурации пиццерии, загружаемый из JSON файла.
 */
public class Configuration {
    private int bakersCount;
    private int couriersCount;
    private int warehouseCapacity;
    private int storageCapacity;
    private int cookingTimeMs;
    private int deliveryTimeMs;
    private int trunkCapacity;

    public int getBakersCount() {
        return bakersCount;
    }

    public int getCouriersCount() {
        return couriersCount;
    }

    public int getWarehouseCapacity() {
        return warehouseCapacity;
    }

    public int getStorageCapacity() {
        return storageCapacity;
    }

    public int getCookingTimeMs() {
        return cookingTimeMs;
    }

    public int getDeliveryTimeMs() {
        return deliveryTimeMs;
    }

    public int getTrunkCapacity() {
        return trunkCapacity;
    }
}
