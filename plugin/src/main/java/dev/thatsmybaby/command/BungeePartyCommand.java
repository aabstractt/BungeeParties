package dev.thatsmybaby.command;

import dev.thatsmybaby.command.arguments.InviteArgument;
import dev.thatsmybaby.factory.PartyFactory;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BungeePartyCommand extends Command {

    private final Set<Argument> arguments = new HashSet<>();

    public BungeePartyCommand(String name) {
        super(name);

        this.addArguments(
                new InviteArgument("invite", null, null, PartyFactory.getInstance().isRedisEnabled())
        );
    }

    private void addArguments(Argument... arguments) {
        this.arguments.addAll(Arrays.asList(arguments));
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

    }
}