package io.github.morapowered.channels.messaging;

public class ReceivedMessage<T extends Message> {

    private final String channel;
    private final T message;

    public ReceivedMessage(String channel, T message) {
        this.channel = channel;
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public T getMessage() {
        return message;
    }
}
