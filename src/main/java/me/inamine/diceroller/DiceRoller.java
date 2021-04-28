package me.inamine.diceroller;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiceRoller extends JavaPlugin {

    public DRFileManager fileManager;
    public DRCommandManager commandManager;

    @Override
    public void onEnable() {
        this.fileManager = new DRFileManager(this);
        this.saveDefaultConfig();
        fileManager.checkFiles();
        commandManager = new DRCommandManager(this, fileManager);
        commandManager.createAll();
        Bukkit.getLogger().info("Dice Roller started successfully!");
        int pluginId = 11171;
        DRMetrics metrics = new DRMetrics(this, pluginId);
    }

    @Override
    public void onDisable() {
    }
}
