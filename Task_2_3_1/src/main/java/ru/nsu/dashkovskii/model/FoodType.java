package ru.nsu.dashkovskii.model;

/**
 * Типы еды с различными эффектами для змейки.
 */
public enum FoodType {
    NORMAL(1),
    BONUS(3),
    SPEED_UP(1);

    private final int growthAmount;

    FoodType(int growthAmount) {
        this.growthAmount = growthAmount;
    }

    public int getGrowthAmount() {
        return growthAmount;
    }
}
