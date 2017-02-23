package me.inamine;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

public class FM {

	private static YamlConfiguration msg;
	
	
	public static void checkFiles() {
		if(!DiceRoller.getInst().getDataFolder().exists()) {
			DiceRoller.getInst().getDataFolder().mkdir();
		}
		
		File m = new File(DiceRoller.getInst().getDataFolder(), "messages.yml");
		if(!m.exists()){
			DiceRoller.getInst().saveResource("messages.yml", true);
		}
		
		
		msg = YamlConfiguration.loadConfiguration(m);
	}
	
	public static YamlConfiguration getMsg() {
		return msg;
	}
	
}
