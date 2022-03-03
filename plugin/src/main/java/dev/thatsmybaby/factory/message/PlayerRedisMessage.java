package dev.thatsmybaby.factory.message;

import java.util.Map;
import java.util.UUID;

public class PlayerRedisMessage extends RedisMessage {

    public UUID uniqueId;
    public String text;

    public PlayerRedisMessage() {
        super(0);
    }

    @Override
    public void decode(RedisMessageReader reader) {
        this.uniqueId = UUID.fromString(reader.readString());

        this.text = reader.readString();
    }

    @Override
    public Map<String, String> encode(RedisMessageReader reader) {
        reader.putString(this.uniqueId.toString());

        reader.putString(this.text);

        return reader.bufferMap();
    }

    @Override
    public void handle() {

    }

    @Override
    public String toString() {
        return "PlayerRedisMessage{" + "uniqueId=" + uniqueId + ", text='" + text + '\'' + ", id=" + id + '}';
    }
}