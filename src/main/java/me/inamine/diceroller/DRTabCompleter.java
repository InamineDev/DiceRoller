package me.inamine.diceroller;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DRTabCompleter implements TabCompleter {

    private final FileConfiguration config;

    public DRTabCompleter(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("diceroller")) {
            if (args.length == 1) {
                List<String> possibleCompletions = new ArrayList<>();
                if (sender.hasPermission("diceroller.reload")) {
                    possibleCompletions.add("reload");
                }
                List<String> completions = new ArrayList<>();
                StringUtil.copyPartialMatches(args[0], possibleCompletions, completions);
                Collections.sort(completions);
                return completions;
            }
        } else if (command.getName().equalsIgnoreCase("roll") || command.getName().equalsIgnoreCase("broll")) {
            List<String> possibleCompletions = new ArrayList<>();
            if (sender.hasPermission("diceroller.roll.player") || sender.hasPermission("diceroller.broadcast.player")) {
                possibleCompletions.add("player");
            }
            for (int roll : config.getIntegerList("accepted-rolls")) {
                possibleCompletions.add("d" + roll);
            }
            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], possibleCompletions, completions);
            Collections.sort(completions);
            return completions;
        }
        return null;
    }
}
