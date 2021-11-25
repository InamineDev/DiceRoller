package me.inamine.diceroller;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DiceRoller extends JavaPlugin {

    public DRFileManager fileManager;
    public DRCommandManager commandManager;

    @Override
    public void onEnable() {
        this.fileManager = new DRFileManager(this);
        this.saveDefaultConfig();
        fileManager.checkFiles();
        commandManager = new DRCommandManager(this, fileManager);
        commandManager.createAll();
        int pluginId = 11171;
        new DRMetrics(this, pluginId);
        Bukkit.getLogger().info("Dice Roller started successfully!");
    }

    @Override
    public void onDisable() {
    }

}
