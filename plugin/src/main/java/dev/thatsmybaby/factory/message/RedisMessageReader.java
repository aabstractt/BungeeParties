package dev.thatsmybaby.factory.message;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedisMessageReader {

    @Getter private List<String> buffer = new ArrayList<>();

    private int index = 0;

    public RedisMessageReader(List<String> buffer) {
        this.buffer = buffer;
    }

    public RedisMessageReader(int id) {
        this.putInt(id);
    }

    public int readInt() {
        return Integer.parseInt(this.buffer.get(this.index++));
    }

    public void putInt(Integer value) {
        this.buffer.add(value.toString());
    }

    public boolean readBool() {
        return Boolean.parseBoolean(this.buffer.get(this.index++));
    }

    public void putBool(Boolean value) {
        this.buffer.add(value.toString());
    }

    public String readString() {
        return this.buffer.get(this.index++);
    }

    public void putString(String value) {
        this.buffer.add(value);
    }

    public Map<String, String> bufferMap() {
        return Maps.uniqueIndex(buffer, s -> String.valueOf(buffer.indexOf(s)));
    }
}