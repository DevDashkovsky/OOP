package ru.nsu.dashkovskii.worker;

import ru.nsu.dashkovskii.common.HeartbeatMessage;
import ru.nsu.dashkovskii.common.MessageChannel;

/**
 * Поток, периодически шлющий {@link HeartbeatMessage} master'у.
 *
 * <p>Останавливается при прерывании потока или при {@link java.io.IOException}
 * во время отправки (значит соединение мертво — master сам разберётся).
 */
public final class HeartbeatSender implements Runnable {

    private final MessageChannel channel;
    private final String workerId;
    private final long intervalMillis;

    /**
     * Создаёт sender для указанного канала.
     *
     * @param channel канал к мастеру
     * @param workerId идентификатор этого worker'а
     * @param intervalMillis период между ударами сердца, в миллисекундах
     */
    public HeartbeatSender(MessageChannel channel, String workerId, long intervalMillis) {
        this.channel = channel;
        this.workerId = workerId;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                channel.send(new HeartbeatMessage(workerId));
                Thread.sleep(intervalMillis);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception ex) {
                return;
            }
        }
    }
}
