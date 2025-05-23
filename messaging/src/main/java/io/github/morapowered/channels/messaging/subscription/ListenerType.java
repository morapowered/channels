package io.github.morapowered.channels.messaging.subscription;

import io.github.morapowered.channels.messaging.Message;
import io.github.morapowered.channels.messaging.ReceivedMessage;

import java.util.function.Consumer;

public interface ListenerType<T extends Message> extends Subscription {

    Consumer<ReceivedMessage<T>> listener();
    boolean isAcceptable(Message message);

}
