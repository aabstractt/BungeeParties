package dev.thatsmybaby;

import dev.thatsmybaby.factory.PartyFactory;
import dev.thatsmybaby.shared.provider.RedisProvider;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class PartiesPlugin extends Plugin {

    @Getter private static PartiesPlugin instance;

    public static Set<String> serversBlockedAction = new HashSet<>();

    @Override
    public void onEnable() {
        instance = this;

        PartyFactory.getInstance().init();
    }
}