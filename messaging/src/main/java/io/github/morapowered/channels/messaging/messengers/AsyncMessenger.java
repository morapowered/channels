package io.github.morapowered.channels.messaging.messengers;

import io.github.morapowered.channels.messaging.Message;
import io.github.morapowered.channels.messaging.messengers.message.ReceivedMessage;
import io.github.morapowered.channels.messaging.codec.MessageCodec;
import io.github.morapowered.channels.messaging.messengers.handler.MessengerHandler;
import io.github.morapowered.channels.messaging.messengers.listener.AllTypeListener;
import io.github.morapowered.channels.messaging.messengers.listener.Subscription;
import io.github.morapowered.channels.messaging.messengers.listener.TypedListener;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AsyncMessenger extends AbstractMessenger {

    public AsyncMessenger(@NotNull StatefulRedisPubSubConnection<String, Message> connection, @Nullable MessengerHandler handler) {
        super(connection, handler);
    }

    public AsyncMessenger(RedisClient client, MessageCodec codec, @Nullable MessengerHandler handler) {
        super(client, codec, handler);
    }

    public AsyncMessenger(RedisClient client, MessageCodec codec) {
        super(client, codec);
    }

    public AsyncMessenger(RedisClient client, @Nullable MessengerHandler handler) {
        super(client, handler);
    }

    public AsyncMessenger(RedisClient client) {
        super(client);
    }

    public CompletableFuture<Subscription> subscribe(Consumer<ReceivedMessage<Message>> listener, String channel) {
        AllTypeListener listenerType = this.internalSub(listener, channel);
        return getConnection().async().subscribe(channel).thenApply(unused -> (Subscription) listenerType).toCompletableFuture();
    }

    public <K extends Message> CompletableFuture<Subscription> subscribe(Class<K> type, Consumer<ReceivedMessage<K>> listener, String channel) {
        TypedListener<K> listenerType = this.internalSub(type, listener, channel);
        return getConnection().async().subscribe(channel).thenApply(unused -> (Subscription) listenerType).toCompletableFuture();
    }

    public CompletableFuture<Void> unsubscribe(String... channels) {
        this.internalUnsub(channels);
        return getConnection().async().unsubscribe(channels).toCompletableFuture();
    }

    public RedisFuture<Long> publish(String channel, Message message) {
        return getConnection().async().publish(channel, message);
    }

}
