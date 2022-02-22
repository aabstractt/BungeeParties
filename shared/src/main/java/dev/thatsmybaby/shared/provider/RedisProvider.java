package dev.thatsmybaby.shared.provider;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import dev.thatsmybaby.shared.object.BungeePartyImpl;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class RedisProvider {

    public static String HASH_PLAYER_PARTY_INVITES = "player#party#invites:%s";
    public static String HASH_PLAYER_PARTY_INVITE_SENT = "player#party#invite#sent:%s";

    protected RedisBungee hook = null;

    protected JedisPool jedisPool;
    private String password;

    @SuppressWarnings("deprecation")
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

    /**
     * @param whoSent    Who sent the request
     * @param whoAccept Who accept the request
     */
    public void invitePlayer(UUID whoSent, UUID whoAccept) {
        this.runTransaction(jedis -> {
            String hash = String.format(HASH_PLAYER_PARTY_INVITES, whoAccept.toString());

            if (jedis.sismember(hash, whoSent.toString())) {
                return;
            }

            jedis.sadd(hash, whoSent.toString());

            hash = String.format(HASH_PLAYER_PARTY_INVITE_SENT, whoSent);

            if (jedis.sismember(hash, whoAccept.toString())) {
                return;
            }

            jedis.sadd(hash, whoAccept.toString());
        });
    }

    /**
     * @param whoSent    Who sent the request
     * @param whoAccept Who accept the request
     */
    public void removePendingInvite(UUID whoSent, UUID whoAccept) {

    }

    public void removePendingInvitesSent(UUID uniqueId) {

    }

    /**
     * @param whoSent    Who sent the request
     * @param whoAccept Who accept the request
     */
    public boolean isPendingInvite(UUID whoSent, UUID whoAccept) {
        return this.runTransaction(jedis -> {
            return jedis.sismember(String.format(HASH_PLAYER_PARTY_INVITES, whoAccept.toString()), whoSent.toString());
        });
    }

    public BungeePartyImpl getPlayerParty(UUID uniqueId) {
        return null;
    }

    public void messagePlayer(UUID uniqueId, String message) {

    }

    public void messageParty(UUID partyUniqueId, String message) {

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