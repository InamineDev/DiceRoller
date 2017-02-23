package me.inamine;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DiceRoller extends JavaPlugin implements Listener {

	ArrayList<Player> broadcastcooldown = new ArrayList<Player>();
	ArrayList<Player> personalcooldown = new ArrayList<Player>();
		
	
	private static DiceRoller inst;
	
	public DiceRoller(){
		inst = this;
	}
	
	public static DiceRoller getInst() {
		return inst;
	}
	
	
	@Override
	public void onEnable(){
		this.saveDefaultConfig();
		FM.checkFiles();		
		
	}
	
	@Override
	public void onDisable(){}

	public void roll(Player p, int i, String type)
	{
		boolean personal = true;
		boolean custom = true;
		if (type.equals("no")) 
		{
			personal = true;
			custom = false;
		}
		if (type.equals("cno"))
		{
			personal = true;
			custom = true;
		}
		if (type.equals("yes"))
		{
			personal = false;
			custom = false;
		}
		if (type.equals("cyes"))
		{
			personal = false;
			custom = true;
		}
		
		
		int top = (i + 1);
		int bottom = 1;
		
		Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
		
		

		if ( personal  && !custom) 
		{
			int result = ThreadLocalRandom.current().nextInt(bottom, top);
			String resultstr = String.valueOf(result);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("die-rolled")
					.replace("%result%", resultstr)
					.replace("%sides%", String.valueOf(i))
					.replace("%prefix%", FM.getMsg().getString("prefix"))
					.replace("%player%", p.getDisplayName())));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("roll-result")
					.replace("%result%", resultstr)
					.replace("%sides%", String.valueOf(i))
					.replace("%prefix%", FM.getMsg().getString("prefix"))
					.replace("%player%", p.getDisplayName())));
			return;
		}
		
		if ( personal  && custom) 
		{
			int result = ThreadLocalRandom.current().nextInt(bottom, top);
			String resultstr = String.valueOf(result);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("custom-rolled")
					.replace("%result%", resultstr)
					.replace("%sides%", String.valueOf(i))
					.replace("%prefix%", FM.getMsg().getString("prefix"))
					.replace("%player%", p.getDisplayName())));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("custom-result")
					.replace("%result%", resultstr)
					.replace("%sides%", String.valueOf(i))
					.replace("%prefix%", FM.getMsg().getString("prefix"))
					.replace("%player%", p.getDisplayName())));
			return;
		}
		
		if ( !personal && !custom) 
		{
			int result = ThreadLocalRandom.current().nextInt(bottom, top);
			String resultstr = String.valueOf(result);
			String msg1 = ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("broadcast-die-rolled")
					.replace("%result%", resultstr)
					.replace("%sides%", String.valueOf(i))
					.replace("%prefix%", FM.getMsg().getString("prefix"))
					.replace("%player%", p.getDisplayName()));
			
			String msg2 = ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("broadcast-roll-result")
					.replace("%result%", resultstr)
					.replace("%sides%", String.valueOf(i))
					.replace("%prefix%", FM.getMsg().getString("prefix"))
					.replace("%player%", p.getDisplayName()));
			for (Player ps : players) 
			{
				boolean listening = VentureHook.isListening(p, ps);
				if ( listening )
				{
					ps.sendMessage(msg1);
					ps.sendMessage(msg2);
				}
				
			}
			return;
		}
		
	}
	
	
	@SuppressWarnings("deprecation")
	public void particles(Player player){
		boolean particles = this.getConfig().getBoolean("use-particles");
		if (particles) {
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
	
	String yes = "yes";
	String no = "no";
	String cno = "cno";
	String cyes = "cyes";
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		
		if (cmd.getName().equalsIgnoreCase("diceroller") && !(sender instanceof Player))
		{
			if (args[0].equalsIgnoreCase("reload")) {
				this.reloadConfig();
				FM.checkFiles();
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("reload-message")
						.replace("%prefix%", FM.getMsg().getString("prefix"))));
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("diceroller") && sender instanceof Player)
		{
			Player player = (Player) sender;
			
			if (args.length == 0) {
				if (player.hasPermission("diceroller.info")) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Dice Roller info:"));
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Author: &e&oInamine"));
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Version: &a&o1.4.0"));
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6/diceroller reload - Reloads config file"));
				}
				
				if (!player.hasPermission("diceroller.info")) {
					List<String> usage = FM.getMsg().getStringList("help");
					for (String s : usage) 
					{
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
					}
				}
				return true;
			}
			
			if (player.hasPermission("diceroller.reload")) {
				if (args[0].equalsIgnoreCase("reload")) {
					this.reloadConfig();
					FM.checkFiles();
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("reload-message")
							.replace("%prefix%", FM.getMsg().getString("prefix"))));
				}
				return true;
			}
		
			
		}
		
		if (cmd.getName().equalsIgnoreCase("roll") && sender instanceof Player){
			Player player = (Player) sender;
			if (player.hasPermission("diceroller.roll")) {
				if (args.length == 0|| args.length > 1) {
					List<String> usage = FM.getMsg().getStringList("usage");
					for (String s : usage) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
	            
					}
				return true;
				}
						
				if (args[0].equalsIgnoreCase("d4") && (!personalcooldown.contains(player))) {
					roll(player, 4, yes);
					particles(player);
					addplayerPersonal(player);
					return true;
					
					
				}

				if (args[0].equalsIgnoreCase("d6") && (!personalcooldown.contains(player))) {
					roll(player, 6, "yes");
					particles(player);
					addplayerPersonal(player);
					return true;
				}

				if (args[0].equalsIgnoreCase("d8") && (!personalcooldown.contains(player))) {
					roll(player, 8, "yes");
					particles(player);
					addplayerPersonal(player);
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("d10") && (!personalcooldown.contains(player))) {
					roll(player, 10, yes);
					particles(player);
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("d12") && (!personalcooldown.contains(player))) {
					roll(player, 12, yes);
					particles(player);
					addplayerPersonal(player);
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("d20") && (!personalcooldown.contains(player))) {
					roll(player, 20, yes);
					particles(player);
					addplayerPersonal(player);
					return true;
				}
					
				if (args[0].equalsIgnoreCase("d100") && (!personalcooldown.contains(player))) {
					roll(player, 100, yes);
					particles(player);
					addplayerPersonal(player);
					return true;
				}
				

				if (personalcooldown.contains(player)) {
					int cooldownint = this.getConfig().getInt("personal-cooldown");
					String cooldown = String.valueOf(cooldownint);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("personal-cooldown-message")
							.replace("%prefix%", FM.getMsg().getString("prefix"))
							.replace("%pcooldown%", cooldown)));
					return true;
				}
					
				else 
				{
					List<String> usage = FM.getMsg().getStringList("usage");
					for (String s : usage) 
					{
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
					}
					return true;
						
	            }
				
			}
			else 
			{
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("noPermission"))
						.replace("%prefix%", FM.getMsg().getString("prefix"))
						.replace("%player%", player.getDisplayName()));
				return true;
			}
			
		}

		
	    if (cmd.getName().equalsIgnoreCase("broll") && sender instanceof Player)
	    {
			final Player player = (Player) sender;
			if (player.hasPermission("diceroller.broadcast")) 
			{
				if (args.length == 0|| args.length > 1) 
				{
					List<String> usage = FM.getMsg().getStringList("broll-usage");
					for (String s : usage) 
					{
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
					}
					return true;
				}
				
				if (args[0].equalsIgnoreCase("d4") && (!broadcastcooldown.contains(player))) 
				{
					roll(player, 4, no);
					particles(player);
					addplayerBroadcast(player);
					return true;
					
				}

				if (args[0].equalsIgnoreCase("d6") && (!broadcastcooldown.contains(player))) 
				{
					roll(player, 6, no);
					particles(player);
					addplayerBroadcast(player);
					return true;
					
				}

				if (args[0].equalsIgnoreCase("d8") && (!broadcastcooldown.contains(player))) 
				{
					roll(player, 8, no);
					particles(player);
					addplayerBroadcast(player);
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("d10") && (!broadcastcooldown.contains(player))) 
				{
					roll(player, 10, no);
					particles(player);
					addplayerBroadcast(player);
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("d12") && (!broadcastcooldown.contains(player))) 
				{
					roll(player, 12, no);
					particles(player);
					addplayerBroadcast(player);
					return true;
				}
				
				if (args[0].equalsIgnoreCase("d20") && (!broadcastcooldown.contains(player))) 
				{
					roll(player, 20, no);
					particles(player);
					addplayerBroadcast(player);
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("d100") && (!broadcastcooldown.contains(player))) 
				{
					roll(player, 100, no);
					particles(player);
					addplayerBroadcast(player);
					return true;
				}
				
				if (broadcastcooldown.contains(player)) 
				{
					int cooldownint = this.getConfig().getInt("broadcast-cooldown");
					String cooldown = String.valueOf(cooldownint);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("broadcast-cooldown-message")
							.replace("%prefix%", FM.getMsg().getString("prefix"))
							.replace("%bccooldown%", cooldown)));
					return true;
				}
				
					
				else 
				{
					List<String> brollusage = this.getConfig().getStringList("broll-usage");
					for (String s : brollusage) 
					{
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
					}
					return true;
				}
				
			}
			
			else 
			{
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("noPermission"))
						.replace("%prefix%", FM.getMsg().getString("prefix"))
						.replace("%player%", player.getDisplayName()));
				
				return true;
			}
			
	    }
	    
		return false;

	}
}