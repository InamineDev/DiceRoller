package me.inamine.diceroller;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DRRoller {

    private final DRFileManager fileManager;
    private final FileConfiguration config;

    public DRRoller(DRFileManager fileManager, FileConfiguration config) {
        this.fileManager = fileManager;
        this.config = config;
    }

    public void rollPlayer(Player player, boolean broadcast) {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        int result = ThreadLocalRandom.current().nextInt(0, onlinePlayers.size());
        String resultName = onlinePlayers.get(result).getName();
        String message = fileManager.getMsg().getString("player-roll-result");
        if (message == null) {
            Bukkit.getLogger().warning("Invalid string for 'player-roll-result'");
            return;
        }
        String prefix = fileManager.getMsg().getString("prefix");
        if (prefix == null) {
            Bukkit.getLogger().warning("Invalid string for 'prefix'");
            return;
        }
        message = message.replace("%result%", resultName)
                .replace("%prefix%", prefix)
                .replace("%player%", player.getName());
        if (broadcast) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target.hasPermission("diceroller.broadcast.view")) {
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public void roll(Player player, int faces, boolean custom, boolean broadcast, int rolls) {
        int result = ThreadLocalRandom.current().nextInt(1, faces + 1);
        int total = 0;
        StringBuilder results = new StringBuilder();
        String message;
        if (rolls > 1) {
            if (!player.hasPermission("diceroller.bypass.max")) {
                int max = config.getInt("max-rolls");
                if (rolls > max) rolls = max;
            }
            for (int i = 0; i < rolls; i++) {
                int singleResult = ThreadLocalRandom.current().nextInt(1, faces + 1);
                total += singleResult;
                results.append(singleResult).append(",");
            }
            results = new StringBuilder(results.substring(0, results.length() - 1));
            if (custom) {
                message = fileManager.getMsg().getString("custom-roll-result-multi");
            } else {
                message = fileManager.getMsg().getString("roll-result-multi");
            }
            if (message == null) {
                Bukkit.getLogger().warning("Invalid string for 'roll-result-multi' or 'custom-roll-result-multi'");
                return;
            }
        } else {
            if (custom) {
                message = fileManager.getMsg().getString("custom-roll-result");
            } else {
                message = fileManager.getMsg().getString("roll-result");
            }
            if (message == null) {
                Bukkit.getLogger().warning("Invalid string for 'roll-result' or 'custom-roll-result'");
                return;
            }
        }
        String prefix = fileManager.getMsg().getString("prefix");
        if (prefix == null) {
            Bukkit.getLogger().warning("Invalid string for 'prefix'");
            return;
        }
        message = message.replace("%result%", String.valueOf(result))
                .replace("%sides%", String.valueOf(faces))
                .replace("%prefix%", prefix)
                .replace("%player%", player.getName())
                .replace("%count%", String.valueOf(rolls))
                .replace("%results%", results.toString())
                .replace("%total%", String.valueOf(total));
        if (broadcast) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target.hasPermission("diceroller.broadcast.view")) {
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}
