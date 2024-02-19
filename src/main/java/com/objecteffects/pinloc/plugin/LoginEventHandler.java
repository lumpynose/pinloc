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
	
    @SuppressWarnings("unused")
    private final JavaPlugin plugin;

    private final static Set<Player> playerList = new HashSet<Player>();

	public LoginEventHandler(JavaPlugin _plugin) {
        _plugin.getServer().getPluginManager().registerEvents(this, _plugin);

	    this.plugin = _plugin;
	}

    private void doNothing() {
        // nothing
    }
    
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		playerList.add(event.getPlayer());

		String playerName = event.getPlayer().getName();
		String eventName = event.getEventName();

		log.info("player: {}, event: {}", playerName, eventName);
	}
}
