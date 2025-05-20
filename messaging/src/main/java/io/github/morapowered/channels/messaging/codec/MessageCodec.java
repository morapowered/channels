package io.github.morapowered.channels.messaging.codec;

import com.google.gson.*;
import io.github.morapowered.channels.messaging.Message;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class MessageCodec implements RedisCodec<String, Message> {

    private final Gson gson;
    private final StringCodec stringCodec;

    public MessageCodec() {
        this(new GsonBuilder().create(), Charset.defaultCharset());
    }

    public MessageCodec(Gson gson) {
        this(gson, Charset.defaultCharset());
    }

    public MessageCodec(Charset charset) {
        this(new GsonBuilder().create(), charset);
    }

    public MessageCodec(Gson gson, Charset charset) {
        this.gson = gson;
        this.stringCodec = new StringCodec(charset);
    }

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return stringCodec.decodeKey(bytes);
    }

    @Override
    public Message decodeValue(ByteBuffer bytes) {
        JsonObject object = gson.fromJson(stringCodec.decodeValue(bytes), JsonObject.class);
        String messageType = object.get("_type").getAsString();
        JsonElement content = object.get("content");
        Class<?> clazz;
        try {
            clazz = Class.forName(messageType);
        } catch (ClassNotFoundException ex) {
            throw new JsonSyntaxException("invalid message _type " + messageType, ex);
        }
        Object obj = gson.fromJson(content, clazz);
        return (Message) obj;
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return stringCodec.encodeKey(key);
    }

    @Override
    public ByteBuffer encodeValue(Message value) {
        JsonObject object = new JsonObject();
        object.addProperty("_type", value.getClass().getName());
        object.add("content", gson.toJsonTree(value));
        return stringCodec.encodeValue(gson.toJson(object));
    }
}
