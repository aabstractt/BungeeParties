package dev.thatsmybaby.shared.provider;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import dev.thatsmybaby.shared.object.BungeePartyImpl;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

public abstract class RedisProvider {

    private RedisBungee redisBungee = null;

    public void init() {
        Plugin plugin = ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee");

        if (plugin == null) {
            ProxyServer.getInstance().getLogger().warning("RedisBungee disabled");
        } else {
            this.redisBungee = (RedisBungee) plugin;
        }
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

    public UUID getPlayerUniqueId(String name) {
        return this.redisBungee.getUuidTranslator().getTranslatedUuid(name, true);
    }

    public String getServerName(UUID uniqueId) {
        return this.redisBungee.getDataManager().getServer(uniqueId);
    }

    public boolean isRedisBungee() {
        return this.redisBungee != null;
    }

    public boolean isRedisEnabled() {
        return false;
    }
}