package net.pl3x.bukkit.ridableturtles.command;

import net.pl3x.bukkit.ridableturtles.RidableTurtles;
import net.pl3x.bukkit.ridableturtles.configuration.Config;
import net.pl3x.bukkit.ridableturtles.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CmdRidableTurtles implements TabExecutor {
    private final RidableTurtles plugin;

    public CmdRidableTurtles(RidableTurtles plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Stream.of("reload")
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("command.turtle.reload")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            Config.reload();
            Lang.reload();

            Lang.send(sender, Lang.RELOAD
                    .replace("{plugin}", plugin.getName())
                    .replace("{version}", plugin.getDescription().getVersion()));
            return true;
        }

        Lang.send(sender, Lang.VERSION
                .replace("{version}", plugin.getDescription().getVersion())
                .replace("{plugin}", plugin.getName()));
        return true;
    }
}
