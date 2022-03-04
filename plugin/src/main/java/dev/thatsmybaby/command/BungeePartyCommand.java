package dev.thatsmybaby.command;

import dev.thatsmybaby.BungeePartiesPlugin;
import dev.thatsmybaby.command.arguments.AcceptArgument;
import dev.thatsmybaby.command.arguments.InviteArgument;
import dev.thatsmybaby.command.arguments.TransferArgument;
import dev.thatsmybaby.factory.PartyFactory;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class BungeePartyCommand extends Command {

    private final Set<Argument> arguments = new HashSet<>();

    public BungeePartyCommand(String name) {
        super(name, null, "p");

        this.addArguments(
                new InviteArgument("invite", null, null, PartyFactory.getInstance().enabled()),
                new AcceptArgument("accept", null, null, PartyFactory.getInstance().enabled()),
                new TransferArgument("transfer", null, null, PartyFactory.getInstance().enabled())
        );
    }

    private void addArguments(Argument... arguments) {
        this.arguments.addAll(Arrays.asList(arguments));
    }

    private Argument getArgument(String name) {
        return this.arguments.stream().filter(argument -> argument.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Run this command in-game").color(ChatColor.RED).create());

            return;
        }

        if (args.length == 0) {
            this.showHelpMessage((ProxiedPlayer) sender);

            return;
        }

        Argument argument = this.getArgument(args[0]);

        if (argument == null) {
            this.showHelpMessage((ProxiedPlayer) sender);

            return;
        }

        if (argument.getPermission() != null && !sender.hasPermission(argument.getPermission())) {
            sender.sendMessage(new ComponentBuilder("You don't have permissions to use this command.").color(ChatColor.RED).create());

            return;
        }

        if (argument.isAsync()) {
            ProxyServer.getInstance().getScheduler().runAsync(BungeePartiesPlugin.getInstance(), () -> argument.execute((ProxiedPlayer) sender, this.getName(), args[0], args));

            return;
        }

        argument.execute((ProxiedPlayer) sender, this.getName(), args[0], args);
    }

    private void showHelpMessage(ProxiedPlayer proxiedPlayer) {

    }
}