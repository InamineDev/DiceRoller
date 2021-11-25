package me.inamine.diceroller;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class DRVentureChatLink {
    private final FileConfiguration config;

    public DRVentureChatLink(FileConfiguration config) {
        this.config = config;
    }

    private String getTalking(Player player) {
        MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(player);
        return mcp.getCurrentChannel().getName();
    }

    private boolean isWhitelisted(String ch) {
        boolean useWhitelist = config.getBoolean("venturechat.use-whitelist");
        if (useWhitelist) return true;
        List<String> whitelist = config.getStringList("venturechat.whitelist-channels");
        return whitelist.contains(ch);
    }

    public boolean isListening(Player sender, Player listener) {
        boolean usingVChat = config.getBoolean("venturechat.use");
        if (!usingVChat) {
            return true;
        }
        if (!Bukkit.getPluginManager().isPluginEnabled("VentureChat")) {
            return true;
        }
        String rolledCh = getTalking(sender);
        if (!isWhitelisted(rolledCh)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("cannot-emote-vchat", "&cYou cannot use an Emote in this channel")));
            return false;
        }
        MineverseChatPlayer mcListener = MineverseChatAPI.getOnlineMineverseChatPlayer(listener);
        MineverseChatPlayer mcSender = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
        if (mcListener.isListening(rolledCh)) {
            double radius = mcSender.getCurrentChannel().getDistance();
            return listener.getLocation().distance(sender.getLocation()) <= radius;
        }
        else return false;
    }
}
