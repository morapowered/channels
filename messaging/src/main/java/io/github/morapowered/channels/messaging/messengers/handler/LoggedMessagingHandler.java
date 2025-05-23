package io.github.morapowered.channels.messaging.messengers.handler;

import io.github.morapowered.channels.messaging.Message;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Objects;

public class LoggedMessagingHandler implements MessagingHandler {

    @NotNull
    private final Logger logger;

    private final boolean subscriberLogged;
    private final boolean receivedUnexpectedChannelLogged;
    private final boolean processingMessageExceptionLogged;

    public LoggedMessagingHandler(Logger logger) {
        this(logger, true, true, true);
    }

    public LoggedMessagingHandler(@NotNull Logger logger, boolean subscriberLogged, boolean receivedUnexpectedChannelLogged, boolean processingMessageExceptionLogged) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.subscriberLogged = subscriberLogged;
        this.receivedUnexpectedChannelLogged = receivedUnexpectedChannelLogged;
        this.processingMessageExceptionLogged = processingMessageExceptionLogged;
    }

    @Override
    public void onSubscribed(String channel, long count) {
        if (subscriberLogged) {
            logger.info("[+1] -> Subscribed to channel {} (now has {} subscribed channels)", channel, count);
        }

    }

    @Override
    public void onUnsubscribed(String channel, long count) {
        if (subscriberLogged) {
            logger.info("[+1] -> Unsubscribed to channel {} (now has {} subscribed channels)", channel, count);
        }
    }

    @Override
    public void onProcessingMessageException(String channel, Message message, Throwable throwable) {
        if (processingMessageExceptionLogged) {
            logger.error("Failure performing a listener of message on channel {}", channel, throwable);
        }
    }

    @Override
    public void onReceiveUnexpectedChannel(String channel, Message message) {
        if (receivedUnexpectedChannelLogged) {
            logger.error("Received message from unexpected channel: {}", channel);
        }
    }


}
