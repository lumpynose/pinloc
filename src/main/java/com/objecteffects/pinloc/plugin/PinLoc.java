package com.objecteffects.pinloc.plugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.objecteffects.pinloc.db.H2Db;

public class PinLoc extends JavaPlugin implements Listener {
	private final static Logger log = LoggerFactory.getLogger("PinLoc");

	private final static Set<Player> playerList = new HashSet<Player>();

	@Override
	public void onEnable() {
		log.info("onEnable");

		File dataFolder = getDataFolder();

		(new H2Db()).dbSetup(dataFolder, "PinLoc");

		getServer().getPluginManager().registerEvents(this, this);
		getCommand("pinloc").setExecutor(new PinLocExecutor());
		
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			playerList.add(player);

			log.info("added player: {}", player.getName());
		}
	}

	@Override
	public void onDisable() {
		log.info("onDisable");

		(new H2Db()).shutdown();
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		playerList.add(event.getPlayer());

		String playerName = event.getPlayer().getName();
		String eventName = event.getEventName();

		log.info("player: {}, event: {}", playerName, eventName);
	}

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
