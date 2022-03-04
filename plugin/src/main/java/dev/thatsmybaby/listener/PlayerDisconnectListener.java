package dev.thatsmybaby.listener;

import dev.thatsmybaby.factory.PartyFactory;
import dev.thatsmybaby.shared.object.BungeePartyImpl;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public final class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent ev) {
        ProxiedPlayer player = ev.getPlayer();

        BungeePartyImpl party = PartyFactory.getInstance().getPlayerParty(player.getUniqueId());

        if (party == null) {
            return;
        }

        if (party.getMembersName().size() == 1) {
            party.forceDisband();

            return;
        }

        if (!party.equals(player.getUniqueId())) {
            party.removeMember(player.getUniqueId().toString(), player.getName());
        } else {
            party.removeMember(party.getOwnerUniqueId(), party.getOwnerName());

            String targetUniqueId = party.findFirstLeader();

            if (targetUniqueId == null) {
                return;
            }

            String targetName = PartyFactory.getInstance().getTargetPlayer(UUID.fromString(targetUniqueId));

            party.removeMember(targetUniqueId, targetName);
            party.transferTo(targetUniqueId, targetName);
        }

        if (PartyFactory.getInstance().enabled()) {
            party.pushUpdate();
        }
    }
}