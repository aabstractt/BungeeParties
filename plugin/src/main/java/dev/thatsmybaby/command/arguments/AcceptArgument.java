package dev.thatsmybaby.command.arguments;

import dev.thatsmybaby.BungeePartiesPlugin;
import dev.thatsmybaby.command.Argument;
import dev.thatsmybaby.factory.PartyFactory;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.object.BungeePartyImpl;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public final class AcceptArgument extends Argument {

    public AcceptArgument(String name, String permission, String[] aliases, boolean async) {
        super(name, permission, aliases, async);
    }

    @Override
    public void execute(ProxiedPlayer proxiedPlayer, String commandLabel, String argumentLabel, String[] args) {
        if (args.length == 0) {
            proxiedPlayer.sendMessage(new ComponentBuilder("Use: /" + commandLabel + " accept <player>").color(ChatColor.RED).create());

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

        if (!factory.isPendingInvite(targetUniqueId, proxiedPlayer.getUniqueId())) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("PLAYER_ALREADY_INVITED", args[0]));

            return;
        }

        if (factory.getPlayerParty(proxiedPlayer.getUniqueId()) != null) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("YOU_ALREADY_IN_PARTY"));

            return;
        }

        BungeePartyImpl party = factory.getPlayerParty(targetUniqueId);

        if (party == null && (party = factory.initializeParty(targetUniqueId)) == null) {
            return;
        }

        factory.removePendingInvite(targetUniqueId, proxiedPlayer.getUniqueId());

        party.addMember(proxiedPlayer.getUniqueId().toString(), proxiedPlayer.getName());

        if (factory.enabled()) {
            party.pushUpdate();
        }

        factory.messageParty(party, Placeholders.componentsPlaceholders("PLAYER_JOINED", proxiedPlayer.getName()));
    }
}