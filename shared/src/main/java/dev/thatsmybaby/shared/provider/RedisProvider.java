package dev.thatsmybaby.shared.provider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.thatsmybaby.shared.object.BungeePartyImpl;
import dev.thatsmybaby.shared.provider.message.RedisMessage;
import dev.thatsmybaby.shared.provider.message.RedisMessageReader;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class RedisProvider {

    public static String HASH_PLAYER_PARTY_INVITES = "player#party#invites:%s";
    public static String HASH_PLAYER_PARTY_INVITE_SENT = "player#party#invite#sent:%s";

    protected static Map<Integer, Class<? extends RedisMessage>> messagesPool = new HashMap<>();

    protected boolean enabled = false;

    protected JedisPool jedisPool = null;
    protected Subscription jedisPubSub = null;

    private String password;

    @SuppressWarnings("deprecation")
    protected void init(String address, String password, boolean enabled) {
        if (!(this.enabled = enabled)) {
            return;
        }

        String[] addressSplit = address.split(":");
        String host = addressSplit[0];
        int port = addressSplit.length > 1 ? Integer.parseInt(addressSplit[1]) : Protocol.DEFAULT_PORT;

        this.jedisPool = new JedisPool(new JedisPoolConfig() {{
            setMaxWaitMillis(1000 * 200000);

            setMaxTotal(8);
        }}, host, port, 1000 * 10, password, false);

        this.password = password;

        new Thread(() -> runTransaction(jedis -> {
            jedis.subscribe(this.jedisPubSub = new Subscription(), "BungeeParties");
        })).start();
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

    protected void redisMessage(String string) {
        if (!this.enabled()) {
            return;
        }

        runTransaction(jedis -> {
            jedis.publish("BungeeParties", string);
        });
    }

    private  <T> T runTransaction(Function<Jedis, T> action) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (this.password != null && !this.password.isEmpty()) {
                jedis.auth(this.password);
            }

            return action.apply(jedis);
        }
    }

    protected void runTransaction(Consumer<Jedis> action) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            if (this.password != null && !this.password.isEmpty()) {
                jedis.auth(this.password);
            }

            action.accept(jedis);
        }
    }

    public boolean enabled() {
        return this.enabled;
    }

    public void close() {
        if (this.jedisPool != null) {
            this.jedisPubSub.unsubscribe();
        }

        if (this.jedisPool != null) {
            this.jedisPool.destroy();
        }
    }

    protected void registerMessage(RedisMessage... pools) {
        for (RedisMessage pool : pools) {
            messagesPool.put(pool.getId(), pool.getClass());
        }
    }

    protected RedisMessage constructMessage(int id) {
        Class<? extends RedisMessage> instance = messagesPool.get(id);

        if (instance == null) {
            return null;
        }

        try {
            return instance.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected class Subscription extends JedisPubSub {

        @Override
        public void onMessage(String channel, String message) {
            Map<String, String> buffer = new Gson().fromJson(message, new TypeToken<Map<String, String>>(){}.getType());
            RedisMessageReader reader = new RedisMessageReader(new ArrayList<>(buffer.values()));

            RedisMessage pk = constructMessage(reader.readInt());

            if (pk == null) {
                System.out.println("Packet " + reader.current() + " not found");

                return;
            }

            pk.decode(reader);
            pk.handle();
        }
    }
}