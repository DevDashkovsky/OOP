package ru.nsu.dashkovskii.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Двунаправленный канал обмена {@link Message}-сообщениями поверх TCP-сокета.
 *
 * <p>Один экземпляр держит {@link Socket}, {@link ObjectOutputStream} и
 * {@link ObjectInputStream}. Запись синхронизирована — несколько потоков могут
 * безопасно одновременно слать сообщения. Чтение не синхронизировано: канал
 * подразумевает одного читателя.
 *
 * <p>ВАЖНО: {@link ObjectOutputStream} создаётся до {@link ObjectInputStream}.
 * Это нужно потому, что конструктор {@code ObjectInputStream} блокируется,
 * пока не прочитает stream header от противоположной стороны.
 */
public final class MessageChannel implements AutoCloseable {

    private final Socket socket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;

    /**
     * Создаёт канал поверх уже подключённого сокета.
     *
     * @param socket подключённый TCP-сокет
     * @throws IOException при ошибке инициализации потоков
     */
    public MessageChannel(Socket socket) throws IOException {
        this.socket = socket;
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.oos.flush();
        this.ois = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Отправляет сообщение в канал. Метод синхронизирован — безопасен для
     * нескольких писателей.
     *
     * @param message сообщение для отправки
     * @throws IOException при ошибке записи в сокет (например, разрыв)
     */
    public synchronized void send(Message message) throws IOException {
        oos.writeObject(message);
        oos.flush();
        oos.reset();
    }

    /**
     * Читает следующее сообщение из канала.
     * Блокируется до прихода данных.
     *
     * @return следующее сообщение
     * @throws IOException при разрыве соединения или EOF
     * @throws ClassNotFoundException если десериализация натолкнулась на
     *     неизвестный тип (быть не должно при корректном протоколе)
     */
    public Message receive() throws IOException, ClassNotFoundException {
        return (Message) ois.readObject();
    }

    /**
     * Возвращает строковое представление удалённого адреса, удобное для логов.
     *
     * @return адрес противоположной стороны или "?" если недоступен
     */
    public String remoteAddress() {
        return socket.getRemoteSocketAddress() == null
                ? "?"
                : socket.getRemoteSocketAddress().toString();
    }

    @Override
    public void close() {
        try {
            oos.close();
        } catch (IOException ignored) {
            // already closed or broken — нечего делать
        }
        try {
            ois.close();
        } catch (IOException ignored) {
            // already closed or broken — нечего делать
        }
        try {
            socket.close();
        } catch (IOException ignored) {
            // already closed or broken — нечего делать
        }
    }
}
