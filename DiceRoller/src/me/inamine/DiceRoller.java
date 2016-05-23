package me.inamine;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class DiceRoller extends JavaPlugin implements Listener {

	ArrayList<Player> broadcastcooldown = new ArrayList<Player>();
	ArrayList<Player> personalcooldown = new ArrayList<Player>();
		
	@Override
	public void onEnable(){
		PluginDescriptionFile desc = getDescription();
		Logger logger = Logger.getLogger("Minecraft");
		logger.info(desc.getName() + " V. " + desc.getVersion() + " is working!");
		this.saveDefaultConfig();
		
		
		
	}
	
	@Override
	public void onDisable(){
		PluginDescriptionFile desc = getDescription();
		Logger logger = Logger.getLogger("Minecraft");
		
		logger.info(desc.getName() + " has been disabled.");
	}

	@SuppressWarnings("deprecation")
	public void particles(Player player){
		if (this.getConfig().getBoolean("particle-effect") == true) {
			Location loc = player.getLocation();
			Effect potion = Effect.POTION_SWIRL;
			player.playEffect(loc, potion, 10);
			player.playEffect(loc, potion, 10);
			player.playEffect(loc, potion, 10);
			player.playEffect(loc, potion, 10);
			return;
			
		}
		else {
			return;
		}
	}
	
	public void addplayerPersonal(final Player player) {
		if (!player.hasPermission("diceroller.roll.bypass")) {
			personalcooldown.add(player);
			int pdown = this.getConfig().getInt("personal-cooldown");
			int pcooldown = pdown * 20;
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					personalcooldown.remove(player);
				}
			}, pcooldown);
		}
		else {
			return;
		}
	}
	public void addplayerBroadcast(final Player player) {
		if (!player.hasPermission("diceroller.broadcast.bypass")) {
			broadcastcooldown.add(player);
			int bdown = this.getConfig().getInt("broadcast-cooldown");
			int bcooldown = bdown * 20;
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					broadcastcooldown.remove(player);
				}
			}, bcooldown);
		}
		else {
			return;
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("diceroller")){
			Player player = (Player) sender;
			
			if (args.length == 0) {
				if (player.hasPermission("diceroller.info")) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Dice Roller info:"));
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Author: &e&oInamine"));
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Version: &a&o1.3.0"));
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6/diceroller reload - Reloads config file"));
				}
				
				if (!player.hasPermission("diceroller.info")) {
					List<String> usage = this.getConfig().getStringList("help");
					for (String s : usage) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
					}
				}
				return true;
			}
			
			if (player.hasPermission("diceroller.reload")) {
				if (args[0].equalsIgnoreCase("reload")) {
					this.reloadConfig();
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("reload-message")
							.replace("%prefix%", this.getConfig().getString("prefix"))));
				}
				return true;
			}
		
			
		}
		
		if (cmd.getName().equalsIgnoreCase("roll") && sender instanceof Player){
			Player player = (Player) sender;
			if (player.hasPermission("diceroller.roll")) {
				if (args.length == 0|| args.length > 1) {
					List<String> usage = this.getConfig().getStringList("usage");
					for (String s : usage) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
	            
					}
				return true;
				}
						
				if (args[0].equalsIgnoreCase("d4") && (!personalcooldown.contains(player))) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("die-rolled"))
							.replace("%sides%", "4")
							.replace("%prefix%", this.getConfig().getString("prefix")));
					int result = ThreadLocalRandom.current().nextInt(1, 5);
					String resultstr = String.valueOf(result);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("roll-result"))
							.replace("%result%", resultstr)
							.replace("%prefix%", this.getConfig().getString("prefix")));
					particles(player);
					addplayerPersonal(player);
					return true;
					
					
				}

				if (args[0].equalsIgnoreCase("d6") && (!personalcooldown.contains(player))) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("die-rolled"))
							.replace("%sides%", "6")
							.replace("%prefix%", this.getConfig().getString("prefix")));
					int result = ThreadLocalRandom.current().nextInt(1, 7);
					String resultstr = String.valueOf(result);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("roll-result"))
							.replace("%result%", resultstr)
							.replace("%prefix%", this.getConfig().getString("prefix")));
					particles(player);
					addplayerPersonal(player);
					return true;
				}

				if (args[0].equalsIgnoreCase("d8") && (!personalcooldown.contains(player))) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("die-rolled"))
							.replace("%sides%", "8")
							.replace("%prefix%", this.getConfig().getString("prefix")));
					int result = ThreadLocalRandom.current().nextInt(1, 9);
					String resultstr = String.valueOf(result);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("roll-result"))
							.replace("%result%", resultstr)
							.replace("%prefix%", this.getConfig().getString("prefix")));
					particles(player);
					addplayerPersonal(player);
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("d10") && (!personalcooldown.contains(player))) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("die-rolled"))
							.replace("%sides%", "10")
							.replace("%prefix%", this.getConfig().getString("prefix")));
					int result = ThreadLocalRandom.current().nextInt(1, 11);
					String resultstr = String.valueOf(result);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("roll-result"))
							.replace("%result%", resultstr)
							.replace("%prefix%", this.getConfig().getString("prefix")));
					particles(player);
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("d12") && (!personalcooldown.contains(player))) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("die-rolled"))
							.replace("%sides%", "12")
							.replace("%prefix%", this.getConfig().getString("prefix")));
					int result = ThreadLocalRandom.current().nextInt(1, 13);
					String resultstr = String.valueOf(result);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("roll-result"))
							.replace("%result%", resultstr)
							.replace("%prefix%", this.getConfig().getString("prefix")));
					particles(player);
					addplayerPersonal(player);
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("d20") && (!personalcooldown.contains(player))) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("die-rolled"))
							.replace("%sides%", "20")
							.replace("%prefix%", this.getConfig().getString("prefix")));
					int result = ThreadLocalRandom.current().nextInt(1, 21);
					String resultstr = String.valueOf(result);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("roll-result"))
							.replace("%result%", resultstr)
							.replace("%prefix%", this.getConfig().getString("prefix")));
					particles(player);
					addplayerPersonal(player);
					return true;
				}
					
				if (args[0].equalsIgnoreCase("d100") && (!personalcooldown.contains(player))) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("die-rolled")
							.replace("%sides%", "100")
							.replace("%prefix%", this.getConfig().getString("prefix"))));
					int result = ThreadLocalRandom.current().nextInt(1, 101);
					String resultstr = String.valueOf(result);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("roll-result")
							.replace("%result%", resultstr)
							.replace("%prefix%", this.getConfig().getString("prefix"))));
					particles(player);
					addplayerPersonal(player);
					return true;
				}
				

				if (personalcooldown.contains(player)) {
					int cooldownint = this.getConfig().getInt("personal-cooldown");
					String cooldown = String.valueOf(cooldownint);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("personal-cooldown-message")
							.replace("%pcooldown%", cooldown)));
					return true;
				}
					
				else {
					List<String> usage = this.getConfig().getStringList("usage");
					for (String s : usage) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
					}
					return true;
						
	            }
						
			
				}
			else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("noPermission"))
						.replace("%prefix%", this.getConfig().getString("prefix"))
						.replace("%player%", player.getDisplayName()));
				return true;
			}
			}

	    
	    
	    
	    if (cmd.getName().equalsIgnoreCase("broll") && sender instanceof Player){
			final Player player = (Player) sender;
			if (player.hasPermission("diceroller.broadcast")) {
				if (args.length == 0|| args.length > 1) {
					List<String> usage = this.getConfig().getStringList("broll-usage");
					for (String s : usage) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
					}
				return true;
				}
				
				if (args[0].equalsIgnoreCase("d4") && (!broadcastcooldown.contains(player))) {
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-die-rolled")
							.replace("%sides%", "4")
							.replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					int result = ThreadLocalRandom.current().nextInt(1, 5);
					String resultstr = String.valueOf(result);
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-roll-result")
							.replace("%result%", resultstr)
							.replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					particles(player);
					addplayerBroadcast(player);
					return true;
					
				}

				if (args[0].equalsIgnoreCase("d6") && (!broadcastcooldown.contains(player))) {
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-die-rolled")
							.replace("%sides%", "6")
							.replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					int result = ThreadLocalRandom.current().nextInt(1, 7);
					String resultstr = String.valueOf(result);
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-roll-result")
							.replace("%result%", resultstr)
							.replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					particles(player);
					addplayerBroadcast(player);
					
					return true;
					
				}

				if (args[0].equalsIgnoreCase("d8") && (!broadcastcooldown.contains(player))) {
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-die-rolled")
							.replace("%sides%", "8")
							.replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					int result = ThreadLocalRandom.current().nextInt(1, 9);
					String resultstr = String.valueOf(result);
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-roll-result")
							.replace("%result%", resultstr)
							.replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					particles(player);
					addplayerBroadcast(player);
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("d10") && (!broadcastcooldown.contains(player))) {
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-die-rolled")
							.replace("%sides%", "10")
							.replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					int result = ThreadLocalRandom.current().nextInt(1, 11);
					String resultstr = String.valueOf(result);
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-roll-result")
							.replace("%result%", resultstr)
							.replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					particles(player);
					addplayerBroadcast(player);
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("d12") && (!broadcastcooldown.contains(player))) {
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-die-rolled")
							.replace("%sides%", "12")
							.replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					int result = ThreadLocalRandom.current().nextInt(1, 13);
					String resultstr = String.valueOf(result);
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-roll-result")
							.replace("%result%", resultstr).replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					particles(player);
					addplayerBroadcast(player);
					return true;
				}
				
				if (args[0].equalsIgnoreCase("d20") && (!broadcastcooldown.contains(player))) {
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-die-rolled")
							.replace("%sides%", "20")
							.replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					int result = ThreadLocalRandom.current().nextInt(1, 21);
					String resultstr = String.valueOf(result);
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-roll-result")
							.replace("%result%", resultstr)
							.replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					particles(player);
					addplayerBroadcast(player);
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("d100") && (!broadcastcooldown.contains(player))) {
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-die-rolled")
							.replace("%sides%", "100")
							.replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					int result = ThreadLocalRandom.current().nextInt(1, 101);
					String resultstr = String.valueOf(result);
					Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-roll-result")
							.replace("%result%", resultstr)
							.replace("%prefix%", this.getConfig().getString("prefix"))
							.replace("%player%", player.getDisplayName())), "diceroller.broadcast.view");
					particles(player);
					addplayerBroadcast(player);
					return true;
				}
				
				if (broadcastcooldown.contains(player)) {
					int cooldownint = this.getConfig().getInt("broadcast-cooldown");
					String cooldown = String.valueOf(cooldownint);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("broadcast-cooldown-message")
							.replace("%bccooldown%", cooldown)));
					return true;
					
				}
				
					
				else {
					List<String> brollusage = this.getConfig().getStringList("broll-usage");
					for (String s : brollusage) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
	            }
						
					return true;
				}
			}
			
			else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("noPermission"))
						.replace("%prefix%", this.getConfig().getString("prefix"))
						.replace("%player%", player.getDisplayName()));
				
				return true;
			}
			}
		return false;

	}
}