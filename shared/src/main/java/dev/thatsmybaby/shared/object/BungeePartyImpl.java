package dev.thatsmybaby.shared.object;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BungeePartyImpl {

    @Getter private final UUID uniqueId;
    @Getter private String ownerUniqueId;
    @Getter private String ownerName;

    @Getter private final Set<String> membersUniqueId = new HashSet<>();
    @Getter private final Set<String> membersName = new HashSet<>();

    public BungeePartyImpl(String ownerUniqueId, String ownerName) {
        this.uniqueId = UUID.randomUUID();

        this.ownerUniqueId = ownerUniqueId;

        this.ownerName = ownerName;
    }

    public void transferTo(String targetUniqueId, String targetName) {
        // TODO: Move this to the promote command
        this.addMember(this.ownerUniqueId, this.ownerName);

        this.ownerUniqueId = targetUniqueId;

        this.ownerName = targetName;

        // TODO: Move this to the promote command
        this.removeMember(targetUniqueId, targetName);
    }

    public void addMember(String uniqueId, String name) {
        this.membersUniqueId.add(uniqueId);

        this.membersName.add(name);
    }

    public void removeMember(String uniqueId, String name) {
        this.membersUniqueId.remove(uniqueId);

        this.membersName.remove(name);
    }

    public void pushUpdate() {
        // TODO: Push the party status update to redis
    }
}