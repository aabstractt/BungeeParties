package dev.thatsmybaby;

import com.google.common.io.ByteStreams;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import dev.thatsmybaby.command.BungeePartyCommand;
import dev.thatsmybaby.factory.PartyFactory;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public final class BungeePartiesPlugin extends Plugin {

    @Getter private static BungeePartiesPlugin instance;

    public static Set<String> serversBlockedAction = new HashSet<>();

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.getProxy().getPluginManager().registerCommand(this, new BungeePartyCommand("party"));

        Plugin plugin = this.getProxy().getPluginManager().getPlugin("RedisBungee");

        if (plugin != null) {
            this.getLogger().info("RedisBungee hooked!");
        }

        try {
            File file = new File(this.getDataFolder(), "config.yml");

            if (!file.exists()) {
                return;
            }

            Configuration section = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file).getSection("redis");

            PartyFactory.getInstance().init(section.getString("address"), section.getString("password"), section.getBoolean("enabled"), (RedisBungee) plugin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
    private void saveDefaultConfig() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        for (String fileName : new String[]{"messages", "config"}) {

            File file = new File(this.getDataFolder(), fileName + ".yml");

            if (file.exists()) {
                return;
            }

            try {
                file.createNewFile();

                try (InputStream is = this.getResourceAsStream( fileName + ".yml");
                     OutputStream os = new FileOutputStream(file)) {
                    ByteStreams.copy(is, os);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean released() {
        return false;
    }
}