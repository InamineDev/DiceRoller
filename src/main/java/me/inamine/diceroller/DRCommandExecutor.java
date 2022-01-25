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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DRCommandExecutor implements CommandExecutor {

  private final DRFileManager fileManager;
  private final Plugin plugin;
  private final DRRoller roller;

  String baseCommand;
  String rollCommand;
  String brollCommand;
  String prefix;
  HashMap<UUID, Long> broadcastCooldown = new HashMap<>();
  HashMap<UUID, Long> personalCooldown = new HashMap<>();

  public DRCommandExecutor(DRFileManager fileManager, DRRoller roller, Plugin plugin) {
    this.fileManager = fileManager;
    this.plugin = plugin;
    this.roller = roller;
    baseCommand = plugin.getConfig().getString("commands.base.command", "diceroller");
    rollCommand = plugin.getConfig().getString("commands.roll.command", "roll");
    brollCommand = plugin.getConfig().getString("commands.broadcast-roll.command", "broll");
    prefix = fileManager.getMsg().getString("prefix", "&b&l[&eDiceRoller&b&l] ");
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    String commandName = command.getName();
    if (commandName.equalsIgnoreCase(baseCommand)) {
      if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
        if (!(sender instanceof Player) || sender.hasPermission("diceroller.reload")) {
          plugin.reloadConfig();
          fileManager.checkFiles();
          baseCommand = plugin.getConfig().getString("commands.base.command", "diceroller");
          rollCommand = plugin.getConfig().getString("commands.roll.command", "roll");
          brollCommand = plugin.getConfig().getString("commands.broadcast-roll.command", "broll");
          prefix = fileManager.getMsg().getString("prefix", "&b&l[&eDiceRoller&b&l] ");
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
    } else if (commandName.equalsIgnoreCase(rollCommand)) {
      if (!(sender instanceof Player)) {
        sender.sendMessage("Console cannot run this command!");
        return true;
      }
      Player player = (Player) sender;
      long cooldown = isCoolingDown(player, false);
      if (cooldown > 0) {
        String cooldownMessage = fileManager.getMsg().getString("personal-cooldown");
        if (cooldownMessage == null)
          cooldownMessage = "&cYou cannot use %roll-command% for another %seconds% seconds";
        cooldownMessage = cooldownMessage.replace("%roll-command%", rollCommand);
        cooldownMessage = cooldownMessage.replace("%seconds%", String.valueOf(round((double) cooldown / 1000)));
        cooldownMessage = ChatColor.translateAlternateColorCodes('&', cooldownMessage);
        player.sendMessage(cooldownMessage);
        return true;
      } else {
        if (args.length == 1 && args[0].equalsIgnoreCase("player") && player.hasPermission("diceroller.roll.player")) {
          roller.rollPlayer(player, false);
          particles(player);
          addCoolDown(player, false);
        } else if (args.length >= 1 && args[0].startsWith("c") && player.hasPermission("diceroller.roll.custom")) {
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
        } else if (args.length >= 1 && args[0].startsWith("d") && player.hasPermission("diceroller.roll")) {
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
            return true;
          } else if (player.hasPermission("diceroller.roll")) {
            String rollUsage = fileManager.getMsg().getString("roll-usage");
            if (rollUsage == null) rollUsage = "%prefix%&e&lUSAGE: &e/%roll-command% (d<#>|c<#>|player) [quantity]";
            rollUsage = rollUsage.replace("%roll-command%", rollCommand);
            rollUsage = rollUsage.replace("%prefix%", prefix);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', rollUsage));
            return true;
          } else {
            String noPermission = fileManager.getMsg().getString("no-permission");
            if (noPermission == null) noPermission = "%prefix%&cYou don't have permission to do that!";
            noPermission = noPermission.replace("%prefix%", prefix);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermission));
            return true;
          }
        }
      }
    } else if (commandName.equalsIgnoreCase(brollCommand)) {
      if (!(sender instanceof Player)) {
        sender.sendMessage("Console cannot run this command!");
        return true;
      }
      Player player = (Player) sender;
      long cooldown = isCoolingDown(player, true);
      if (cooldown > 0) {
        String cooldownMessage = fileManager.getMsg().getString("broadcast-cooldown");
        if (cooldownMessage == null)
          cooldownMessage = "&cYou cannot use %broll-command% for another %seconds% seconds";
        cooldownMessage = cooldownMessage.replace("%broll-command%", brollCommand);
        cooldownMessage = cooldownMessage.replace("%seconds%", String.valueOf(round((double) cooldown / 1000)));
        cooldownMessage = ChatColor.translateAlternateColorCodes('&', cooldownMessage);
        player.sendMessage(cooldownMessage);
        return true;
      } else {
        if (args.length == 1 && args[0].equalsIgnoreCase("player") && player.hasPermission("diceroller.broadcast.player")) {
          roller.rollPlayer(player, true);
          particles(player);
          addCoolDown(player, true);
          return true;
        } else if (args.length >= 1 && args[0].startsWith("c") && player.hasPermission("diceroller.broadcast.custom")) {
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
        } else if (args.length >= 1 && args[0].startsWith("d") && player.hasPermission("diceroller.broadcast")) {
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
            addCoolDown(player, true);
            return true;
          } else if (player.hasPermission("diceroller.broadcast")) {
            String rollUsage = fileManager.getMsg().getString("broll-usage");
            if (rollUsage == null) rollUsage = "%prefix%&e&lUSAGE: &e/%broll-command% (d<#>|c<#>|player) [quantity]";
            rollUsage = rollUsage.replace("%broll-command%", brollCommand);
            rollUsage = rollUsage.replace("%prefix%", prefix);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', rollUsage));
            return true;
          } else {
            String noPermission = fileManager.getMsg().getString("no-permission");
            if (noPermission == null) noPermission = "%prefix%&cYou don't have permission to do that!";
            noPermission = noPermission.replace("%prefix%", prefix);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermission));
            return true;
          }
        }
      }
    }
    return true;
  }


  public void addCoolDown(Player player, boolean broadcast) {
    if (broadcast) {
      if (!player.hasPermission("diceroller.bypass.broadcast")) {
        double cooldown = plugin.getConfig().getDouble("broadcast-cooldown");
        broadcastCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (long) cooldown * 1000);
      }
    } else {
      if (!player.hasPermission("diceroller.bypass.roll")) {
        double cooldown = plugin.getConfig().getDouble("personal-cooldown");
        personalCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (long) cooldown * 1000);
      }
    }
  }

  private long isCoolingDown(Player player, boolean broadcast) {
    UUID uuid = player.getUniqueId();
    long neededTime = 0;
    if (broadcast) {
      if (!broadcastCooldown.containsKey(uuid)) return 0;
      else neededTime = broadcastCooldown.get(uuid);
    } else {
      if (!personalCooldown.containsKey(uuid)) return 0;
      else neededTime = personalCooldown.get(uuid);
    }
    long timeNow = System.currentTimeMillis();
    if (neededTime <= timeNow) return 0;
    else {
      return (neededTime - timeNow);
    }
  }
  private static double round(double value) {
    int scale = (int) Math.pow(10, 1);
    return (double) Math.round(value * scale) / scale;
  }
  private void particles(Player player) {
    int count = plugin.getConfig().getInt("particle-count");
    if (Bukkit.getServer().getVersion().contains("1.7") || Bukkit.getServer().getVersion().contains("1.8"))
      return;
    try {
      Particle p = Particle.CRIT;
      Location location = player.getLocation();
      player.spawnParticle(p, location, count);
    } catch (Exception ignored) {
    }
  }
}