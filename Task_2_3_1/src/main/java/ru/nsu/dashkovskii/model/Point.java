package ru.nsu.dashkovskii.model;

import java.util.Objects;

/**
 * Неизменяемая двумерная точка на игровом поле.
 */
public class Point {
    private final int xx;
    private final int yy;

    public Point(int xx, int yy) {
        this.xx = xx;
        this.yy = yy;
    }

    public int getX() {
        return xx;
    }

    public int getY() {
        return yy;
    }

    /**
     * Возвращает новую точку, смещённую в заданном направлении.
     *
     * @param dir направление смещения
     * @return новая точка, смещённая на указанное направление
     */
    public Point move(Direction dir) {
        return new Point(xx + dir.getDx(), yy + dir.getDy());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point other = (Point) obj;
        return xx == other.xx && yy == other.yy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xx, yy);
    }

    @Override
    public String toString() {
        return "(" + xx + ", " + yy + ")";
    }
}
