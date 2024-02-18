package com.objecteffects.pinloc.plugin;

import java.io.File;

import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.objecteffects.pinloc.db.H2Db;

public class PinLoc extends JavaPlugin implements Listener {
    private final static Logger log = LoggerFactory.getLogger("PinLoc");

    private H2Db h2db;

    @Override
    public void onEnable() {
        log.info("onEnable");

        h2db = new H2Db(this);
        
        File dataFolder = getDataFolder();

        h2db.dbSetup(dataFolder, "PinLoc");
        
        new LoginEventHandler(this);

        PluginCommand cmd = getCommand("pinloc");
        log.debug("command: {}, permission: {}", cmd.getName(),
                cmd.getPermission());

        cmd.setExecutor(new PinLocCommand(this));
    }

    @Override
    public void onDisable() {
        log.info("onDisable");

        h2db.shutdown();
    }
}
