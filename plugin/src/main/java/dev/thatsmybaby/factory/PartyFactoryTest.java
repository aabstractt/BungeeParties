package dev.thatsmybaby.factory;

import dev.thatsmybaby.shared.provider.RedisProvider;
import lombok.Getter;

import java.util.*;

public class PartyFactoryTest extends RedisProvider {

    @Getter private final static PartyFactoryTest instance = new PartyFactoryTest();

    private final Map<UUID, Set<UUID>> invites = new HashMap<>();

    public PartyFactoryTest() {
        System.out.println("PartyFactory was initialized");
    }

    public static void main(String[] args) {
        UUID whoSent = UUID.randomUUID();
        UUID whoReceive = UUID.randomUUID();

        instance.invitePlayer(whoSent, whoReceive);
    }

    public void invitePlayer(UUID whoSent, UUID whoReceive) {
        if (this.enabled()) {
            System.out.println("Redis enabled");

            super.invitePlayer(whoSent, whoReceive);

            return;
        }

        Set<UUID> invitesSent = invites.computeIfAbsent(whoSent, k -> new HashSet<>());

        System.out.println(invites);

        invitesSent.add(whoReceive);

        System.out.println(invites);
    }
}