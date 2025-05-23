package io.github.morapowered.channels.messaging.messengers;


import io.github.morapowered.channels.messaging.Message;
import io.github.morapowered.channels.messaging.messengers.message.ReceivedMessage;
import io.github.morapowered.channels.messaging.codec.MessageCodec;
import io.github.morapowered.channels.messaging.messengers.handler.LoggedMessengerHandler;
import io.github.morapowered.channels.messaging.messengers.handler.MessengerHandler;
import io.github.morapowered.channels.messaging.messengers.listener.AllTypeListener;
import io.github.morapowered.channels.messaging.messengers.listener.ListenerType;
import io.github.morapowered.channels.messaging.messengers.listener.Subscription;
import io.github.morapowered.channels.messaging.messengers.listener.TypedListener;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractMessenger implements AutoCloseable {

    @NotNull
    private final MessengerHandler handler;

    @NotNull
    private final StatefulRedisPubSubConnection<String, Message> connection;
    private final HashMap<String, Set<ListenerType<Message>>> listeners = new HashMap<>();

    public AbstractMessenger(RedisClient client) {
        this(client, new MessageCodec());
    }

    public AbstractMessenger(RedisClient client, @Nullable MessengerHandler handler) {
        this(client, new MessageCodec(), handler);
    }

    public AbstractMessenger(RedisClient client, MessageCodec codec) {
        this(client, codec, null);
    }

    public AbstractMessenger(RedisClient client, MessageCodec codec, @Nullable MessengerHandler handler) {
        this(client.connectPubSub(codec), handler);
    }

    public AbstractMessenger(@NotNull StatefulRedisPubSubConnection<String, Message> connection, @Nullable MessengerHandler handler) {
        this.handler = Optional.ofNullable(handler).orElse(new LoggedMessengerHandler(LoggerFactory.getLogger(getClass())));
        this.connection = Objects.requireNonNull(connection, "connection cannot be null");
        this.connection.addListener(constructListener());
    }

    protected RedisPubSubListener<String, Message> constructListener() {
        return new RedisPubSubListener<String, Message>() {
            @Override
            public void message(String channel, Message message) {
                handler.onReceiveRawMessage(channel, message);
                Set<ListenerType<Message>> list = listeners.get(channel);
                if (list != null && !list.isEmpty()) {
                    boolean accepted = false;
                    for (ListenerType<Message> listener : list) {
                        if (listener.isAcceptable(message)) {
                            accepted = true;
                            try {
                                listener.listener().accept(new ReceivedMessage<>(channel, message));
                            } catch (Exception ex) {
                                handler.onProcessingMessageException(channel, message, ex);
                            }
                        }
                    }
                    if (!accepted) {
                        handler.onReceiveUnexpectedChannel(channel, message);
                    }
                } else {
                    handler.onReceiveUnexpectedChannel(channel, message);
                }
            }

            @Override
            public void message(String pattern, String channel, Message message) {
                // not used
            }

            @Override
            public void subscribed(String channel, long count) {
                handler.onSubscribed(channel, count);
            }

            @Override
            public void psubscribed(String pattern, long count) {
                // not used
            }

            @Override
            public void unsubscribed(String channel, long count) {
                handler.onUnsubscribed(channel, count);
            }

            @Override
            public void punsubscribed(String pattern, long count) {
                // not used
            }
        };
    }

    @Override
    public void close() throws Exception {
        this.listeners.clear();
        this.connection.close();
    }

    protected @NotNull StatefulRedisPubSubConnection<String, Message> getConnection() {
        return connection;
    }

    public boolean unsubscribe(Subscription subscription) {
        for (Set<ListenerType<Message>> value : listeners.values()) {
            return value.removeIf(listenerType -> listenerType.equals(subscription));
        }
        return false;
    }

    protected AllTypeListener internalSub(Consumer<ReceivedMessage<Message>> listener, String channel) {
        AllTypeListener listenerType = new AllTypeListener(listener);
        listeners.computeIfAbsent(channel, k -> new HashSet<>()).add(listenerType);
        return listenerType;
    }

    @SuppressWarnings("unchecked")
    protected <K extends Message> TypedListener<K> internalSub(Class<K> type, Consumer<ReceivedMessage<K>> listener, String channel) {
        TypedListener<K> listenerType = new TypedListener<>(type, listener);
        listeners.computeIfAbsent(channel, k -> new HashSet<>()).add((ListenerType<Message>) listenerType);
        return listenerType;
    }

    protected void internalUnsub(String... channels) {
        for (String channel : channels) {
            listeners.remove(channel);
        }
    }

}
