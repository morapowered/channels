package io.github.morapowered.channels.gson.codec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class GsonCodec implements RedisCodec<String, JsonElement> {

    private final Gson gson;
    private final StringCodec stringCodec;

    public GsonCodec() {
        this(new GsonBuilder().create(), Charset.defaultCharset());
    }

    public GsonCodec(Gson gson) {
        this(gson, Charset.defaultCharset());
    }

    public GsonCodec(Charset charset) {
        this(new GsonBuilder().create(), charset);
    }

    public GsonCodec(Gson gson, Charset charset) {
        this.gson = gson;
        this.stringCodec = new StringCodec(charset);
    }

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return stringCodec.decodeKey(bytes);
    }

    @Override
    public JsonElement decodeValue(ByteBuffer bytes) {
        String stringedJson = stringCodec.decodeValue(bytes);
        return gson.fromJson(stringedJson, JsonElement.class);
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return stringCodec.encodeKey(key);
    }

    @Override
    public ByteBuffer encodeValue(JsonElement value) {
        String stringedJson = gson.toJson(value, JsonElement.class);
        return stringCodec.encodeValue(stringedJson);
    }



}
