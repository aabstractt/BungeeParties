package dev.thatsmybaby.factory;

import dev.thatsmybaby.shared.object.BungeePartyImpl;
import dev.thatsmybaby.shared.provider.RedisProvider;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PartyFactory extends RedisProvider {

    @Getter private static final PartyFactory instance = new PartyFactory();

    private final HashMap<UUID, Set<UUID>> pendingInvitesSent = new HashMap<>();

    public BungeePartyImpl initializeParty(ProxiedPlayer proxiedPlayer) {
        return new BungeePartyImpl(proxiedPlayer.getUniqueId().toString(), proxiedPlayer.getName(), PartyFactory.getInstance().isRedisEnabled());
    }

    /**
     * @param whoSent    Who sent the invite request
     * @param whoReceive Who need accept the invite request
     */
    @Override
    public void invitePlayer(UUID whoSent, UUID whoReceive) {
        if (this.isRedisEnabled()) {
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
        if (this.isRedisEnabled()) {
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
        if (this.isRedisEnabled()) {
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
        if (this.isRedisEnabled()) {
            return super.isPendingInvite(whoSent, whoReceive);
        }

        return this.pendingInvitesSent.getOrDefault(whoSent, new HashSet<>()).contains(whoReceive);
    }

    public BungeePartyImpl getPlayerParty(UUID uniqueId) {
        if (this.isRedisEnabled()) {
            return super.getPlayerParty(uniqueId);
        }

        return this.getPlayerParty(uniqueId);
    }
}