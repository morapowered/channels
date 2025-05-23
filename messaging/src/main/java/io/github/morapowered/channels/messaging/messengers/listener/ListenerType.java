package io.github.morapowered.channels.messaging.messengers.listener;

import io.github.morapowered.channels.messaging.Message;
import io.github.morapowered.channels.messaging.messengers.message.ReceivedMessage;

import java.util.function.Consumer;

public interface ListenerType<T extends Message> extends Subscription {

    Consumer<ReceivedMessage<T>> listener();
    boolean isAcceptable(Message message);

}
