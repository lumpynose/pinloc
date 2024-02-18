package com.objecteffects.pinloc.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PinLocExecutor implements CommandExecutor {
	private final static Logger log = LoggerFactory.getLogger("PinLoc");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		log.info("sender: {}, command: {}", sender.getName(), cmd.getName());
		
		if (cmd.getName().equalsIgnoreCase("pinloc")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player.");
			}
			
			return true;
		}

		return false;
	}
}
