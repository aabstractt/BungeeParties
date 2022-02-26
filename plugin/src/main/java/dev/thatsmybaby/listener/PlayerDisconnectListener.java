package dev.thatsmybaby.listener;

import dev.thatsmybaby.factory.PartyFactory;
import dev.thatsmybaby.shared.object.BungeePartyImpl;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

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
            party.findNewLeader();
        }

        if (PartyFactory.getInstance().enabled()) {
            party.pushUpdate();
        }
    }
}