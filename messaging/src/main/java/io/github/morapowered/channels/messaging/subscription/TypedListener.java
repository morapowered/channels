package io.github.morapowered.channels.messaging.subscription;

import io.github.morapowered.channels.messaging.Message;
import io.github.morapowered.channels.messaging.ReceivedMessage;

import java.util.function.Consumer;

public class TypedListener<T extends Message> implements ListenerType<T> {

    public final Class<T> type;
    private final Consumer<ReceivedMessage<T>> listener;

    public TypedListener(Class<T> type, Consumer<ReceivedMessage<T>> listener) {
        this.type = type;
        this.listener = listener;
    }

    @Override
    public Consumer<ReceivedMessage<T>> listener() {
        return listener;
    }

    @Override
    public boolean isAcceptable(Message message) {
        return message.getClass().isAssignableFrom(type);
    }
}
