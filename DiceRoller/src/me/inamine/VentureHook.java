package me.inamine;

import java.util.Set;

import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;

public class VentureHook 
{
	
	public static String getTalking(Player player)
	{
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(player);
		String channel = mcp.getCurrentChannel().getName();
		return channel;
	}
	
	public static Set<String> getListening(Player player)
	{
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(player);
		Set<String> channels = mcp.getListening();
		
		return channels;
	}
	
	public static boolean isListening(Player roller, Player listener)
	{
		boolean vChat = DiceRoller.getInst().getConfig().getBoolean("use-venturechat");
		if (!vChat) 
		{
			return true;
		}
		String rolledCh = getTalking(roller);
		Set<String> channels = getListening(listener);
		for ( String channel : channels) 
		{
			
			MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(roller);
			double radius = (double) mcp.getCurrentChannel().getDistance();
			double distance = listener.getLocation().distance(roller.getLocation());
			if ( channel.equals(rolledCh) && (radius >= distance)) 
			{
				return true;
			}
		}
		
		return false;
		
	}

}
