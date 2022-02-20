package dev.thatsmybaby.shared.object;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BungeePartyImpl {

    @Getter private UUID uniqueId;
    @Getter private String ownerUniqueId;
    @Getter private String ownerName;

    @Getter private final Set<String> membersUniqueId = new HashSet<>();
    @Getter private final Set<String> membersName = new HashSet<>();

    private boolean redis;

    public BungeePartyImpl(String ownerUniqueId, String ownerName, boolean redis) {
        this.ownerUniqueId = ownerUniqueId;

        this.ownerName = ownerName;

        this.redis = redis;
    }

    public void transferTo(String targetUniqueId, String targetName) {
        // TODO: Move this to the promote command
        this.addMember(this.ownerUniqueId, this.ownerName);

        this.ownerUniqueId = targetUniqueId;

        this.ownerName = targetName;

        // TODO: Move this to the promote command
        this.removeMember(targetUniqueId, targetName);
    }

    public void addMember(ProxiedPlayer proxiedPlayer) {
        this.addMember(proxiedPlayer.getUniqueId().toString(), proxiedPlayer.getName());
    }

    public void addMember(String uniqueId, String name) {
        this.membersUniqueId.add(uniqueId);

        this.membersName.add(name);
    }

    public void removeMember(String uniqueId, String name) {
        this.membersUniqueId.remove(uniqueId);

        this.membersName.remove(name);
    }

    public void broadcastMessage(BaseComponent[] components) {
        if (this.redis) {
            // TODO: Broadcast message using redis

            return;
        }

        for (String name : new ArrayList<String>(this.membersName) {{
            add(BungeePartyImpl.this.ownerName);
        }}) {
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(name);

            if (proxiedPlayer == null) {
                continue;
            }

            proxiedPlayer.sendMessage(components);
        }
    }
}