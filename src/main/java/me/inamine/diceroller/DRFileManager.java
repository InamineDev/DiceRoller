package me.inamine.diceroller;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class DRFileManager {

    Plugin plugin;

    public DRFileManager(Plugin plugin) {
        this.plugin = plugin;
    }

    private static YamlConfiguration msg;


    public void checkFiles() {
        if(!plugin.getDataFolder().exists()) {
            if (!plugin.getDataFolder().mkdir()) {
                Bukkit.getLogger().warning("Error loading plugin folder. Disabling plugin.");
                plugin.getPluginLoader().disablePlugin(plugin);
            }
        }

        File m = new File(plugin.getDataFolder(), "messages.yml");
        if(!m.exists()){
            plugin.saveResource("messages.yml", true);
        }
        msg = YamlConfiguration.loadConfiguration(m);
    }

    public YamlConfiguration getMsg() {
        return msg;
    }
}
