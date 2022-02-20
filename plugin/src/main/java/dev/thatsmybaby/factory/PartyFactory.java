package dev.thatsmybaby.factory;

import dev.thatsmybaby.shared.object.BungeePartyImpl;
import dev.thatsmybaby.shared.provider.RedisProvider;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.*;

public class PartyFactory extends RedisProvider {

    @Getter private static final PartyFactory instance = new PartyFactory();

    private final HashMap<UUID, Set<UUID>> pendingInvitesSent = new HashMap<>();

    public BungeePartyImpl initializeParty(UUID uniqueId) {
        if (this.enabled()) {
            return super.initializeParty(uniqueId);
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
     * @param whoSent    Who sent the invite request
     * @param whoReceive Who need accept the invite request
     */
    @Override
    public void invitePlayer(UUID whoSent, UUID whoReceive) {
        if (this.enabled()) {
            super.invitePlayer(whoSent, whoReceive);

            return;
        }

        Set<UUID> invitesSent = this.pendingInvitesSent.computeIfAbsent(whoSent, k -> new HashSet<>());

        invitesSent.add(whoReceive);
    }

    /**
     * @param whoSent    Who sent the invite request
     * @param whoReceive Who need accept the invite request
     */
    @Override
    public void removePendingInvite(UUID whoSent, UUID whoReceive) {
        if (this.enabled()) {
            super.removePendingInvite(whoSent, whoReceive);

            return;
        }

        Set<UUID> invitesSent = this.pendingInvitesSent.get(whoSent);

        if (invitesSent == null || !invitesSent.contains(whoReceive)) {
            return;
        }

        invitesSent.remove(whoReceive);
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
     * @param whoSent    Who sent the invite request
     * @param whoReceive Who need accept the invite request
     */
    @Override
    public boolean isPendingInvite(UUID whoSent, UUID whoReceive) {
        if (this.enabled()) {
            return super.isPendingInvite(whoSent, whoReceive);
        }

        return this.pendingInvitesSent.getOrDefault(whoSent, new HashSet<>()).contains(whoReceive);
    }

    public BungeePartyImpl getPlayerParty(UUID uniqueId) {
        if (this.enabled()) {
            return super.getPlayerParty(uniqueId);
        }

        return this.getPlayerParty(uniqueId);
    }

    public void messagePlayer(UUID uniqueId, BaseComponent[] components) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);

        if (player == null && !this.enabled()) {
            return;
        }

        if (player == null) {
            super.messagePlayer(uniqueId, ComponentSerializer.toString(components));
        } else {
            player.sendMessage(components);
        }
    }

    public void messageParty(BungeePartyImpl party, BaseComponent[] components) {
        if (this.enabled()) {
            super.messageParty(party, ComponentSerializer.toString(components));

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
        if (this.hooked()) {
            return super.getTargetPlayer(name);
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(name);

        return target != null ? target.getUniqueId() : null;
    }

    public String getTargetServer(UUID uniqueId) {
        if (this.hooked()) {
            return super.getTargetServer(uniqueId);
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uniqueId);

        return target != null ? target.getServer().getInfo().getName() : null;
    }
}