package dev.thatsmybaby.command;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class Argument {

    @Getter private final String name;
    @Getter private final String permission;
    @Getter private final String[] aliases;
    @Getter private final boolean async;

    public abstract void execute(ProxiedPlayer proxiedPlayer, String commandLabel, String argumentLabel, String[] args);
}