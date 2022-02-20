package dev.thatsmybaby.command;

import dev.thatsmybaby.factory.PartyFactory;
import dev.thatsmybaby.shared.object.BungeePartyImpl;
import dev.thatsmybaby.shared.provider.RedisProvider;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class Argument {

    @Getter private final String name;
    @Getter private final String permission;
    @Getter private final String[] aliases;
    @Getter private final boolean async;

    public abstract void execute(ProxiedPlayer proxiedPlayer, String commandLabel, String argumentLabel, String[] args);

    protected UUID getTargetPlayer(String name) {
        if (PartyFactory.getInstance().isRedisBungee()) {
            return PartyFactory.getInstance().getPlayerUniqueId(name);
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(name);

        return target != null ? target.getUniqueId() : null;
    }

    protected String getTargetServer(UUID uniqueId) {
        if (PartyFactory.getInstance().isRedisBungee()) {
            return PartyFactory.getInstance().getServerName(uniqueId);
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(name);

        return target != null ? target.getServer().getInfo().getName() : null;
    }
}