package io.github.morapowered.channels.messaging.messengers.handler;

import io.github.morapowered.channels.messaging.Message;

public interface MessengerHandler {

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
