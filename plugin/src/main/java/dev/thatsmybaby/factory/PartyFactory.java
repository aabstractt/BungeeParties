package dev.thatsmybaby.factory;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import dev.thatsmybaby.factory.message.PartyRedisMessage;
import dev.thatsmybaby.factory.message.PlayerRedisMessage;
import dev.thatsmybaby.shared.object.BungeePartyImpl;
import dev.thatsmybaby.shared.provider.RedisProvider;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.*;

public final class PartyFactory extends RedisProvider {

    @Getter private static final PartyFactory instance = new PartyFactory();

    private final HashMap<UUID, Set<UUID>> pendingInvitesSent = new HashMap<>();

    private RedisBungee hook = null;

    public void init(String address, String password, boolean enabled, RedisBungee plugin) {
        super.init(address, password, enabled);

        if (!enabled && plugin != null) {
            throw new RuntimeException("Redis is disabled but RedisBungee tried hook...");
        }

        this.hook = plugin;

        registerMessage(new PlayerRedisMessage(), new PartyRedisMessage());
    }

    @Override
    public BungeePartyImpl initializeParty(UUID uniqueId) {
        if (this.hook != null) {
            String name = this.getTargetPlayer(uniqueId);

            if (name == null) {
                return null;
            }

            return new BungeePartyImpl(uniqueId.toString(), name);
        }

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);

        if (player == null) {
            return null;
        }

        return this.initializeParty(player);
    }

    public BungeePartyImpl initializeParty(ProxiedPlayer proxiedPlayer) {
        return new BungeePartyImpl(proxiedPlayer.getUniqueId().toString(), proxiedPlayer.getName());
    }

    /**
     * @param whoSent    Who sent the request
     * @param whoAccept Who accept the request
     */
    @Override
    public void invitePlayer(UUID whoSent, UUID whoAccept) {
        if (this.enabled()) {
            super.invitePlayer(whoSent, whoAccept);

            return;
        }

        Set<UUID> invitesSent = this.pendingInvitesSent.computeIfAbsent(whoSent, k -> new HashSet<>());

        invitesSent.add(whoAccept);
    }

    /**
     * @param whoSent    Who sent the request
     * @param whoAccept Who accept the request
     */
    @Override
    public void removePendingInvite(UUID whoSent, UUID whoAccept) {
        if (this.enabled()) {
            super.removePendingInvite(whoSent, whoAccept);

            return;
        }

        Set<UUID> invitesSent = this.pendingInvitesSent.get(whoSent);

        if (invitesSent == null || !invitesSent.contains(whoAccept)) {
            return;
        }

        invitesSent.remove(whoAccept);
    }

    /**
     * @param uniqueId    Who usually sent the invite request
     */
    @Override
    public void removePendingInvitesSent(UUID uniqueId) {
        if (this.enabled()) {
            super.removePendingInvitesSent(uniqueId);

            return;
        }

        Set<UUID> invitesSent = this.pendingInvitesSent.get(uniqueId);

        if (invitesSent == null) {
            return;
        }

        invitesSent.clear();
        this.pendingInvitesSent.remove(uniqueId);
    }

    /**
     * @param whoSent    Who sent the request
     * @param whoAccept Who accept the request
     */
    @Override
    public boolean isPendingInvite(UUID whoSent, UUID whoAccept) {
        if (this.enabled()) {
            return super.isPendingInvite(whoSent, whoAccept);
        }

        return this.pendingInvitesSent.getOrDefault(whoSent, new HashSet<>()).contains(whoAccept);
    }

    public BungeePartyImpl getPlayerParty(UUID uniqueId) {
        if (this.enabled()) {
            return super.getPlayerParty(uniqueId);
        }

        return this.getPlayerParty(uniqueId);
    }

    public void messagePlayer(UUID uuid, BaseComponent[] components) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

        if (player == null && !this.enabled()) {
            return;
        }

        if (player == null) {
            this.redisMessage(new PlayerRedisMessage() {{
                this.uniqueId = uuid;

                this.text = ComponentSerializer.toString(components);
            }}.toString());
        } else {
            player.sendMessage(components);
        }
    }

    public void messageParty(BungeePartyImpl party, BaseComponent[] components) {
        if (this.enabled()) {
            this.redisMessage(new PartyRedisMessage() {{
                this.uniqueId = party.getUniqueId();

                this.text = ComponentSerializer.toString(components);
            }}.toString());

            return;
        }

        for (String name : new ArrayList<String>(party.getMembersName()) {{
            add(party.getOwnerName());
        }}) {
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(name);

            if (proxiedPlayer == null) {
                continue;
            }

            proxiedPlayer.sendMessage(components);
        }
    }

    public UUID getTargetPlayer(String name) {
        if (this.hook != null) {
            return this.hook.getUuidTranslator().getTranslatedUuid(name, true);
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(name);

        return target != null ? target.getUniqueId() : null;
    }

    public String getTargetPlayer(UUID uniqueId) {
        if (this.hook != null) {
            return this.hook.getUuidTranslator().getNameFromUuid(uniqueId, true);
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uniqueId);

        return target != null ? target.getName() : null;
    }

    public String getTargetServer(UUID uniqueId) {
        if (this.hook != null) {
            return this.hook.getDataManager().getServer(uniqueId);
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uniqueId);

        return target != null ? target.getServer().getInfo().getName() : null;
    }
}