package io.github.morapowered.channels.messaging.messengers.listener;

import io.github.morapowered.channels.messaging.Message;
import io.github.morapowered.channels.messaging.messengers.message.ReceivedMessage;

import java.util.function.Consumer;

public class AllTypeListener implements ListenerType<Message> {

    private final Consumer<ReceivedMessage<Message>> listener;

    public AllTypeListener(Consumer<ReceivedMessage<Message>> listener) {
        this.listener = listener;
    }

    @Override
    public Consumer<ReceivedMessage<Message>> listener() {
        return listener;
    }

    @Override
    public boolean isAcceptable(Message message) {
        return true;
    }

}
