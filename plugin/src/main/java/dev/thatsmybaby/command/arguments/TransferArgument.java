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

public final class TransferArgument extends Argument {

    public TransferArgument(String name, String permission, String[] aliases, boolean async) {
        super(name, permission, aliases, async);
    }

    @Override
    public void execute(ProxiedPlayer proxiedPlayer, String commandLabel, String argumentLabel, String[] args) {
        if (args.length == 0) {
            proxiedPlayer.sendMessage(new ComponentBuilder(String.format("Usage: /%s %s <player>", commandLabel, argumentLabel)).color(ChatColor.RED).create());

            return;
        }

        UUID targetUniqueId = PartyFactory.getInstance().getTargetPlayer(args[0]);

        if (targetUniqueId == null) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("PLAYER_NOT_FOUND", args[0]));

            return;
        }

        String targetName = PartyFactory.getInstance().getTargetPlayer(targetUniqueId);

        if (targetName == null) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("PLAYER_NOT_FOUND", args[0]));

            return;
        }

        if (BungeePartiesPlugin.released() && targetUniqueId.equals(proxiedPlayer.getUniqueId())) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("YOU_CANNOT_TRANSFER_TO_THIS_PLAYER", args[0]));

            return;
        }

        BungeePartyImpl party = PartyFactory.getInstance().getPlayerParty(proxiedPlayer.getUniqueId());

        if (party == null) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("YOU_DONT_HAVE_PARTY"));

            return;
        }

        if (!party.getMembersUniqueId().contains(targetUniqueId.toString())) {
            proxiedPlayer.sendMessage(Placeholders.componentsPlaceholders("PLAYER_NOT_FOUND_PARTY"));

            return;
        }

        party.addMember(proxiedPlayer.getUniqueId().toString(), proxiedPlayer.getName());
        party.removeMember(targetUniqueId.toString(), targetName);

        party.transferTo(targetUniqueId.toString(), targetName);

        PartyFactory.getInstance().messageParty(party, Placeholders.componentsPlaceholders("PARTY_TRANSFER", targetName));
    }
}