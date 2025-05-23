package io.github.morapowered.channels.messaging.messengers;

import io.github.morapowered.channels.messaging.Message;
import io.github.morapowered.channels.messaging.messengers.message.ReceivedMessage;
import io.github.morapowered.channels.messaging.codec.MessageCodec;
import io.github.morapowered.channels.messaging.messengers.handler.MessagingHandler;
import io.github.morapowered.channels.messaging.messengers.listener.AllTypeListener;
import io.github.morapowered.channels.messaging.messengers.listener.Subscription;
import io.github.morapowered.channels.messaging.messengers.listener.TypedListener;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SyncMessenger extends AbstractMessenger {

    public SyncMessenger(RedisClient client) {
        super(client);
    }

    public SyncMessenger(RedisClient client, @Nullable MessagingHandler handler) {
        super(client, handler);
    }

    public SyncMessenger(RedisClient client, MessageCodec codec) {
        super(client, codec);
    }

    public SyncMessenger(RedisClient client, MessageCodec codec, @Nullable MessagingHandler handler) {
        super(client, codec, handler);
    }

    public SyncMessenger(@NotNull StatefulRedisPubSubConnection<String, Message> connection, @Nullable MessagingHandler handler) {
        super(connection, handler);
    }

    public Subscription subscribe(Consumer<ReceivedMessage<Message>> listener, String channel) {
        AllTypeListener listenerType = this.internalSub(listener, channel);
        getConnection().sync().subscribe(channel);
        return listenerType;
    }

    public <K extends Message> Subscription subscribe(Class<K> type, Consumer<ReceivedMessage<K>> listener, String channel) {
        TypedListener<K> listenerType = this.internalSub(type, listener, channel);
        getConnection().sync().subscribe(channel);
        return listenerType;
    }

    public void unsubscribe(String... channels) {
        this.internalUnsub(channels);
        getConnection().sync().unsubscribe(channels);
    }

    public Long publish(String channel, Message message) {
        return getConnection().sync().publish(channel, message);
    }
}
