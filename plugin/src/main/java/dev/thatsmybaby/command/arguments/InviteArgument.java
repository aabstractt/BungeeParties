package dev.thatsmybaby.command.arguments;

import dev.thatsmybaby.BungeePartiesPlugin;
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

public final class InviteArgument extends Argument {

    public InviteArgument(String name, String permission, String[] aliases, boolean async) {
        super(name, permission, aliases, async);
    }

    @Override
    public void execute(ProxiedPlayer proxiedPlayer, String commandLabel, String argumentLabel, String[] args) {
        if (args.length == 0) {
            proxiedPlayer.sendMessage(new ComponentBuilder("Use: /" + commandLabel + " invite <player>").color(ChatColor.RED).create());

            return;
        }

        PartyFactory factory = PartyFactory.getInstance();
        UUID targetUniqueId = factory.getTargetPlayer(args[0]);

        if (targetUniqueId == null) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("PLAYER_NOT_FOUND", args[0]));

            return;
        }

        if (BungeePartiesPlugin.released() && targetUniqueId.equals(proxiedPlayer.getUniqueId())) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("YOU_CANNOT_INVITE_THIS_PLAYER", args[0]));

            return;
        }

        String serverName = factory.getTargetServer(targetUniqueId);

        if (serverName == null || BungeePartiesPlugin.serversBlockedAction.contains(serverName)) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("YOU_CANNOT_INVITE_THIS_PLAYER", args[0]));

            return;
        }

        if (factory.isPendingInvite(proxiedPlayer.getUniqueId(), targetUniqueId)) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("PLAYER_ALREADY_INVITED", args[0]));

            return;
        }

        if (factory.getPlayerParty(targetUniqueId) != null) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("PLAYER_ALREADY_IN_PARTY", args[0]));

            return;
        }

        BungeePartyImpl party = factory.getPlayerParty(proxiedPlayer.getUniqueId());

        if (party != null) {
            factory.messageParty(party, Placeholders.componentsPlaceholders("PLAYER_SUCCESSFULLY_INVITED", args[0]));
        } else {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("PLAYER_SUCCESSFULLY_INVITED", args[0]));
        }

        factory.invitePlayer(proxiedPlayer.getUniqueId(), targetUniqueId);
        factory.messagePlayer(targetUniqueId, Placeholders.componentsPlaceholders("PARTY_INVITE_RECEIVED", proxiedPlayer.getName()));

        ProxyServer.getInstance().getScheduler().schedule(BungeePartiesPlugin.getInstance(), () -> factory.removePendingInvite(proxiedPlayer.getUniqueId(), targetUniqueId), 20, TimeUnit.SECONDS);
    }
}