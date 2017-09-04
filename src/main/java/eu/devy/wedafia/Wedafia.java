package eu.devy.wedafia;

import eu.devy.wedafia.http.WebServer;

import com.lishid.openinv.OpenInv;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import org.apache.commons.lang.RandomStringUtils;

import java.util.logging.Logger;

public final class Wedafia extends JavaPlugin {
    private Logger log = getLogger();
    private FileConfiguration config = this.getConfig();
    private Wedafia wedafia = this;
    public OpenInv openinv;

    @Override
    public void onEnable() {
        openinv = (OpenInv) getServer().getPluginManager().getPlugin("OpenInv");
        log.info(openinv.getName() + " detected");

        config.addDefault("api_key", RandomStringUtils.randomAlphanumeric(20));
        config.options().copyDefaults(true);
        saveConfig();

        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                WebServer.run(wedafia);
            }
        });
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
    }
}
