package me.inamine.diceroller;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class DRCommandManager {

    private final Plugin plugin;
    private final DRCommandExecutor commandExecutor;
    private final DRTabCompleter tabCompleter;


    public DRCommandManager(Plugin plugin, DRFileManager fileManager) {
        this.plugin = plugin;
        commandExecutor = new DRCommandExecutor(fileManager, new DRRoller(fileManager, plugin.getConfig()), plugin);
        tabCompleter = new DRTabCompleter(plugin.getConfig());
    }

    private void registerCommand(String name, String description, String usage, String permission) {
        PluginCommand command = getCommand(name, plugin);
        command.setName(name);
        command.setDescription(description);
        command.setUsage(usage);
        command.setPermission(permission);
        command.setExecutor(commandExecutor);
        command.setTabCompleter(tabCompleter);
        getCommandMap().register(plugin.getDescription().getName(), command);
    }

    private void registerCommand(String name, String description, String usage, String permission, String... aliases) {
        PluginCommand command = getCommand(name, plugin);
        command.setName(name);
        command.setDescription(description);
        command.setUsage(usage);
        command.setPermission(permission);
        command.setAliases(Arrays.asList(aliases));
        command.setExecutor(commandExecutor);
        command.setTabCompleter(tabCompleter);
        getCommandMap().register(plugin.getDescription().getName(), command);
    }

    private static PluginCommand getCommand(String name, Plugin plugin) {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);
            command = c.newInstance(name, plugin);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return command;
    }

    private static CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);

                commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return commandMap;
    }

    public void createAll() {
        String mainPermission = "diceroller.";
        // Base Command
        String baseCommand = "diceroller";
        String baseDescription = "Dice Roller base command";
        String baseUsage = "/diceroller [reload]";
        String basePermission = mainPermission + "help";
        registerCommand(baseCommand, baseDescription, baseUsage, basePermission, "dr", "droller");
        // Roll
        String rollCommand = "roll";
        String rollDescription = "Roll dice to yourself";
        String rollUsage = "/roll d<#>|player [quantity]";
        String rollPermission = mainPermission + "roll";
        registerCommand(rollCommand, rollDescription, rollUsage, rollPermission);
        // Broadcast Roll
        String brollCommand = "broll";
        String brollDescription = "Roll dice to the server";
        String brollUsage = "/broll d<#>|player [quantity]";
        String brollPermission = mainPermission + "broadcast";
        registerCommand(brollCommand, brollDescription, brollUsage, brollPermission, "broadcastroll");
    }
}