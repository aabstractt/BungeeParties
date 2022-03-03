package dev.thatsmybaby.shared.provider.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
public abstract class RedisMessage {

    @Getter protected int id;

    public abstract void decode(RedisMessageReader reader);

    public abstract Map<String, String> encode(RedisMessageReader reader);

    public abstract void handle();
}