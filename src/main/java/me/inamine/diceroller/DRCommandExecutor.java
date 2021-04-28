package me.inamine.diceroller;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DRCommandExecutor implements CommandExecutor {

    private final DRRoller roller;
    private final DRFileManager fileManager;
    private final Plugin plugin;

    List<String> bCoolDown = new ArrayList<>();
    List<String> pCoolDown = new ArrayList<>();

    public DRCommandExecutor(DRFileManager fileManager, DRRoller roller, Plugin plugin) {
        this.roller = roller;
        this.fileManager = fileManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String commandName = command.getName();
        String prefix = fileManager.getMsg().getString("prefix", "&b&l[&eDiceRoller&b&l] ");
        if (prefix == null) prefix = "&b&l[&eDiceRoller&b&l] ";
        if (commandName.equalsIgnoreCase("diceroller")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!(sender instanceof Player) || sender.hasPermission("diceroller.reload")) {
                    plugin.reloadConfig();
                    fileManager.checkFiles();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("reload-message")
                            .replace("%prefix%", prefix)));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("no-permission")
                            .replace("%prefix%", prefix)));
                }
            } else {
                if (!(sender instanceof Player) || sender.hasPermission("diceroller.help")) {
                    List<String> usage = fileManager.getMsg().getStringList("help");
                    for (String s : usage) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("no-permission")
                            .replace("%prefix%", prefix)));
                }
            }
            return true;
        } else if (commandName.equalsIgnoreCase("roll")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console cannot run this command!");
                return true;
            }
            Player player = (Player) sender;
            if (player.hasPermission("diceroller.roll")) {
                if (args.length == 1 && args[0].equalsIgnoreCase("player") && (!pCoolDown.contains(player.getUniqueId().toString()))) {
                    if (player.hasPermission("diceroller.roll.player")) {
                        roller.rollPlayer(player, false);
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("no-permission")
                                .replace("%prefix%", prefix)));
                    }
                    return true;
                } else if (args.length >= 1 && args[0].startsWith("c") && (!pCoolDown.contains(player.getUniqueId().toString()))) {
                    if (player.hasPermission("diceroller.roll.custom")) {
                        int rollID;
                        try {
                            rollID = Integer.parseInt(args[0].replaceFirst("c", ""));
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("invalid-number")
                                    .replace("%prefix%", prefix)));
                            return true;
                        }
                        int rolls = 1;
                        if (args.length == 2) {
                            try {
                                rolls = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("invalid-number")
                                        .replace("%prefix%", prefix)));
                                return true;
                            }
                        }
                        roller.roll(player, rollID, true, false, rolls);
                        particles(player);
                        addCoolDown(player, false);
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("no-permission")
                                .replace("%prefix%", prefix)));
                    }
                } else if (args.length >= 1 && args[0].startsWith("d") && (!pCoolDown.contains(player.getUniqueId().toString()))) {
                    int rollID;
                    try {
                        rollID = Integer.parseInt(args[0].replaceFirst("d", ""));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("invalid-number")
                                .replace("%prefix%", prefix)));
                        return true;
                    }
                    if (plugin.getConfig().getIntegerList("accepted-rolls").contains(rollID)) {
                        int rolls = 1;
                        if (args.length == 2) {
                            try {
                                rolls = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("invalid-number")
                                        .replace("%prefix%", prefix)));
                                return true;
                            }
                        }
                        roller.roll(player, rollID, false, false, rolls);
                        particles(player);
                        addCoolDown(player, false);
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("invalid-roll")
                                .replace("%prefix%", prefix)));
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("roll-usage")
                            .replace("%prefix%", prefix)));
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("no-permission")
                        .replace("%prefix%", prefix)));
                return true;
            }
        } else if (commandName.equalsIgnoreCase("broll")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console cannot run this command!");
                return true;
            }
            Player player = (Player) sender;
            if (player.hasPermission("diceroller.broadcast")) {
                if (args.length == 1 && args[0].equalsIgnoreCase("player") && (!pCoolDown.contains(player.getUniqueId().toString()))) {
                    if (player.hasPermission("diceroller.broadcast.player")) {
                        roller.rollPlayer(player, true);
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("no-permission")
                                .replace("%prefix%", prefix)));
                    }
                    return true;
                } else if (args.length >= 1 && args[0].startsWith("c") && (!pCoolDown.contains(player.getUniqueId().toString()))) {
                    if (player.hasPermission("diceroller.broadcast.custom")) {
                        int rollID;
                        try {
                            rollID = Integer.parseInt(args[0].replaceFirst("c", ""));
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("invalid-number")
                                    .replace("%prefix%", prefix)));
                            return true;
                        }
                        int rolls = 1;
                        if (args.length == 2) {
                            try {
                                rolls = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("invalid-number")
                                        .replace("%prefix%", prefix)));
                                return true;
                            }
                        }
                        roller.roll(player, rollID, true, true, rolls);
                        particles(player);
                        addCoolDown(player, true);
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("no-permission")
                                .replace("%prefix%", prefix)));
                    }
                }  else if (args.length >= 1 && args[0].startsWith("d") && (!pCoolDown.contains(player.getUniqueId().toString()))) {
                    int rollID;
                    try {
                        rollID = Integer.parseInt(args[0].replaceFirst("d", ""));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("invalid-number")
                                .replace("%prefix%", prefix)));
                        return true;
                    }
                    if (plugin.getConfig().getIntegerList("accepted-rolls").contains(rollID)) {
                        int rolls = 1;
                        if (args.length == 2) {
                            try {
                                rolls = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("invalid-number")
                                        .replace("%prefix%", prefix)));
                                return true;
                            }
                        }
                        roller.roll(player, rollID, false, true, rolls);
                        particles(player);
                        addCoolDown(player, false);
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("invalid-roll")
                                .replace("%prefix%", prefix)));
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("broll-usage")
                            .replace("%prefix%", prefix)));
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', fileManager.getMsg().getString("no-permission")
                        .replace("%prefix%", prefix)));
                return true;
            }
        }
        return false;
    }


    public void addCoolDown(Player player, boolean broadcast) {
        if (broadcast) {
            if (!player.hasPermission("diceroller.bypass.broadcast")) {
                bCoolDown.add(player.getUniqueId().toString());
                int delay = (int) plugin.getConfig().getDouble("broadcast-cooldown") * 20;
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> bCoolDown.remove(player.getUniqueId().toString()), delay);
            }
        } else {
            if (!player.hasPermission("diceroller.bypass.roll")) {
                pCoolDown.add(player.getUniqueId().toString());
                int delay = (int) (plugin.getConfig().getDouble("personal-cooldown") * 20);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> pCoolDown.remove(player.getUniqueId().toString()), delay);
            }
        }
    }

    private void particles(Player player) {
        int count = plugin.getConfig().getInt("particle-count");
        if (Bukkit.getServer().getVersion().contains("1.7") || Bukkit.getServer().getVersion().contains("1.8")) return;
        try {
            Particle p = Particle.CRIT;
            Location location = player.getLocation();
            player.spawnParticle(p, location, count);
        } catch (Exception ignored) {
            return;
        }
    }
}