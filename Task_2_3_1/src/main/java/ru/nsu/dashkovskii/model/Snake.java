package ru.nsu.dashkovskii.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Представляет змейку в виде упорядоченной деки сегментов тела с явно выделенными головой и хвостом.
 */
public class Snake {
    private final Deque<Point> body;
    private final Set<Point> bodySet;
    private Direction direction;
    private int growthPending;
    private boolean alive;

    /**
     * Создаёт новую змейку в заданной начальной позиции с указанным направлением движения.
     *
     * @param start начальная позиция головы
     * @param direction начальное направление движения
     */
    public Snake(Point start, Direction direction) {
        this.body = new ArrayDeque<>();
        this.bodySet = new HashSet<>();
        this.body.addFirst(start);
        this.bodySet.add(start);
        this.direction = direction;
        this.growthPending = 0;
        this.alive = true;
    }

    public Point getHead() {
        return body.getFirst();
    }

    public Deque<Point> getBody() {
        return body;
    }

    public Set<Point> getBodySet() {
        return bodySet;
    }

    public Direction getDirection() {
        return direction;
    }

    /**
     * Устанавливает новое направление, игнорируя разворот на 180 градусов.
     *
     * @param newDir желаемое новое направление
     */
    public void setDirection(Direction newDir) {
        if (body.size() > 1 && newDir == direction.opposite()) {
            return;
        }
        this.direction = newDir;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        this.alive = false;
    }

    public int length() {
        return body.size();
    }

    /**
     * Ставит в очередь сегменты роста для добавления в последующих ходах.
     *
     * @param amount количество добавляемых сегментов
     */
    public void grow(int amount) {
        this.growthPending += amount;
    }

    /**
     * Вычисляет следующую позицию головы на основе текущего направления.
     *
     * @return точка, в которую переместится голова
     */
    public Point nextHead() {
        return getHead().move(direction);
    }

    /**
     * Перемещает змейку на один шаг в текущем направлении.
     * Добавляет новую голову и удаляет хвост, если нет ожидающего роста.
     *
     * @param wrappedHead позиция головы (возможно, обёрнутая по границам поля)
     */
    public void move(Point wrappedHead) {
        body.addFirst(wrappedHead);
        bodySet.add(wrappedHead);
        if (growthPending > 0) {
            growthPending--;
        } else {
            Point tail = body.removeLast();
            bodySet.remove(tail);
        }
    }

    /**
     * Проверяет, пересекается ли заданная точка с телом этой змейки.
     * Если excludeHead равно true, голова не учитывается (используется для проверки
     * самостолкновения после хода).
     *
     * @param point точка для проверки
     * @param excludeHead нужно ли исключить голову из проверки
     * @return true, если точка находится на теле змейки
     */
    public boolean collidesWith(Point point, boolean excludeHead) {
        if (excludeHead) {
            int idx = 0;
            for (Point pp : body) {
                if (idx > 0 && pp.equals(point)) {
                    return true;
                }
                idx++;
            }
            return false;
        }
        return bodySet.contains(point);
    }
}
