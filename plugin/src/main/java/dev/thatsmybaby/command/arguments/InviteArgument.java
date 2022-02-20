package dev.thatsmybaby.command.arguments;

import dev.thatsmybaby.PartiesPlugin;
import dev.thatsmybaby.command.Argument;
import dev.thatsmybaby.factory.PartyFactory;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.object.BungeePartyImpl;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InviteArgument extends Argument {

    public InviteArgument(String name, String permission, String[] aliases, boolean async) {
        super(name, permission, aliases, async);
    }

    @Override
    public void execute(ProxiedPlayer proxiedPlayer, String commandLabel, String argumentLabel, String[] args) {
        if (args.length == 0) {
            proxiedPlayer.sendMessage(new ComponentBuilder("Use: /" + commandLabel + " invite <player>").color(ChatColor.RED).create());

            return;
        }

        UUID targetUniqueId = getTargetPlayer(args[0]);

        if (targetUniqueId == null) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("PLAYER_NOT_FOUND", args[0]));

            return;
        }

        if (targetUniqueId.equals(proxiedPlayer.getUniqueId())) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("YOU_CANNOT_INVITE_THIS_PLAYER", args[0]));

            return;
        }

        String serverName = this.getTargetServer(targetUniqueId);

        if (serverName == null || PartiesPlugin.serversBlockedAction.contains(serverName)) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("YOU_CANNOT_INVITE_THIS_PLAYER", args[0]));

            return;
        }

        if (PartyFactory.getInstance().isPendingInvite(proxiedPlayer.getUniqueId(), targetUniqueId)) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("PLAYER_ALREADY_INVITED", args[0]));

            return;
        }

        if (PartyFactory.getInstance().getPlayerParty(targetUniqueId) != null) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("PLAYER_ALREADY_IN_PARTY", args[0]));

            return;
        }

        BungeePartyImpl party = PartyFactory.getInstance().getPlayerParty(proxiedPlayer.getUniqueId());

        if (party != null) {
            party.broadcastMessage(Placeholders.componentsPlaceholders("PLAYER_SUCCESSFULLY_INVITED", args[0]));
        } else {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("PLAYER_SUCCESSFULLY_INVITED", args[0]));
        }

        PartyFactory.getInstance().invitePlayer(proxiedPlayer.getUniqueId(), targetUniqueId);

        ProxyServer.getInstance().getScheduler().schedule(PartiesPlugin.getInstance(), () -> PartyFactory.getInstance().removePendingInvite(proxiedPlayer.getUniqueId(), targetUniqueId), 20, TimeUnit.SECONDS);
    }
}