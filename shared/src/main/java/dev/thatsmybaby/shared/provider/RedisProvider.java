package dev.thatsmybaby.shared.provider;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.Jedis;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.JedisPool;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.JedisPoolConfig;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.Protocol;
import dev.thatsmybaby.shared.object.BungeePartyImpl;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class RedisProvider {

    private RedisBungee hook = null;

    protected JedisPool jedisPool;
    private String password;

    public void init(String address, String password, boolean enabled, RedisBungee plugin) {
        if (!enabled && plugin != null) {
            throw new RuntimeException("Redis is disabled but RedisBungee was hooked...");
        }

        this.hook = plugin;

        String[] addressSplit = address.split(":");
        String host = addressSplit[0];
        int port = addressSplit.length > 1 ? Integer.parseInt(addressSplit[1]) : Protocol.DEFAULT_PORT;

        this.jedisPool = new JedisPool(new JedisPoolConfig() {{
            setMaxWaitMillis(1000 * 200000);
            setMaxTotal(8);
        }}, host, port, 1000 * 10, password, false);

        this.password = password;
    }

    public BungeePartyImpl initializeParty(UUID uniqueId) {
        return null;
    }

    public void invitePlayer(UUID whoSent, UUID whoReceive) {
    }

    public void removePendingInvite(UUID whoSent, UUID whoReceive) {

    }

    public void removePendingInvitesSent(UUID uniqueId) {

    }

    public boolean isPendingInvite(UUID whoSent, UUID whoReceive) {
        return false;
    }

    public BungeePartyImpl getPlayerParty(UUID uniqueId) {
        return null;
    }

    public void messagePlayer(UUID uniqueId, String message) {

    }

    public void messageParty(BungeePartyImpl party, String message) {

    }

    public UUID getTargetPlayer(String name) {
        return RedisBungeeAPI.getRedisBungeeApi().getUuidFromName(name, true);
    }

    public String getTargetServer(UUID uniqueId) {
        return this.hook.getDataManager().getServer(uniqueId);
    }

    private  <T> T runTransaction(Function<Jedis, T> action) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (this.password != null && !this.password.isEmpty()) {
                jedis.auth(this.password);
            }

            return action.apply(jedis);
        }
    }

    public void runTransaction(Consumer<Jedis> action) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            if (this.password != null && !this.password.isEmpty()) {
                jedis.auth(this.password);
            }

            action.accept(jedis);
        }
    }

    public boolean hooked() {
        return this.hook != null;
    }

    public boolean enabled() {
        return false;
    }
}