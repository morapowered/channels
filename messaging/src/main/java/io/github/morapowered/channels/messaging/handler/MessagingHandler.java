package io.github.morapowered.channels.messaging.handler;

import io.github.morapowered.channels.messaging.Message;

public interface MessagingHandler {

    default void onSubscribed(String channel, long count) {
    }

    default void onUnsubscribed(String channel, long count) {
    }

    default void onProcessingMessageException(String channel, Message message, Throwable throwable) {
    }

    default void onReceiveRawMessage(String channel, Message message) {
    }

    default void onReceiveUnexpectedChannel(String channel, Message message) {
    }

}
