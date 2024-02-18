package com.objecteffects.pinloc.plugin;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginEventHandler implements Listener {
	private final static Logger log = LoggerFactory.getLogger("PinLoc");
	
	private final static Set<Player> playerList = new HashSet<Player>();
	private final JavaPlugin plugin;

	public LoginEventHandler(JavaPlugin _plugin) {
	    this.plugin = _plugin;
	    
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		playerList.add(event.getPlayer());

		String playerName = event.getPlayer().getName();
		String eventName = event.getEventName();

		log.info("player: {}, event: {}", playerName, eventName);
	}
}
