package dev.alterum.regiontp;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import dev.alterum.regiontp.commands.RegionTPCommand;
import dev.alterum.regiontp.commands.RegionTPHereCommand;
import dev.alterum.regiontp.commands.RegionTPPlayerCommand;
import dev.alterum.regiontp.commands.SpawnTPCommand;
import dev.alterum.regiontp.utils.Configuration;

public class RegionTP extends JavaPlugin implements Listener {

	private File configfile;
	public FileConfiguration configdata;
	public String version = "0.6.2";

	String prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix") + " ");

	public void onEnable() {
		createFiles();
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("regiontp").setExecutor(new RegionTPCommand(this)); 
		getCommand("regiontpplayer").setExecutor(new RegionTPPlayerCommand(this));
		getCommand("regiontphere").setExecutor(new RegionTPHereCommand(this)); // Completed
		getCommand("spawntp").setExecutor(new SpawnTPCommand(this)); // Completed
		loadMessages();
	}

	public void onDisable() {
		getServer().getLogger().info("Saving main configuration file.");
	}

	private void createFiles() {
		try {
			if (!getDataFolder().exists()) {
				getDataFolder().mkdirs();
			}
			this.configfile = new File(getDataFolder(), "config.yml");
			this.configdata = new YamlConfiguration();
			if (!this.configfile.exists()) {
				getLogger().info("Configuration file not found, generating a new one now.");
				saveDefaultConfig();
				this.configdata.load(this.configfile);
			} else {
				getLogger().info("Valid configuration file located, loading information now.");
				this.configdata.load(this.configfile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public File getConfigFile() {
		return this.configfile;
	}

	public void loadMessages() {
		FileConfiguration config = getConfig();
		
		Configuration.prefix = config.getString("prefix");
		Configuration.send_player_message = config.getBoolean("send-player-message");
		
		Configuration.regiontp_usage = config.getString("regiontp-usage");
		Configuration.tpcoords_usage = config.getString("tpcoords-usage");
		Configuration.tphere_usage = config.getString("tphere-usage");
		Configuration.tpplayer_usage = config.getString("tpplayer-usage");
		Configuration.spawntp_usage = config.getString("spawntp-usage");

		Configuration.no_regions_found = config.getString("no-regions-found");
		Configuration.no_origin_region = config.getString("origin-region-not-found");
		Configuration.no_dest_region = config.getString("dest-region-not-found");
		Configuration.region_lacks_point = config.getString("region-lacks-tp-point");
		Configuration.none_in_region = config.getString("none-found-in-origin");
		Configuration.player_not_found = config.getString("tphere-player-not-found");
		Configuration.spawn_not_set = config.getString("spawnpoint-not-set");
		
		Configuration.tp_success = config.getString("tp-successful");
		Configuration.player_teleported = config.getString("player-tp-message");
		Configuration.spawn_set_success = config.getString("spawn-set-success");
		Configuration.plugin_reloaded = config.getString("plugin-reloaded");
		Configuration.missing_permission = config.getString("missing-permission");
		
		Configuration.tp_permission = config.getString("regiontp-permission");
		Configuration.tpcoords_permission = config.getString("tpcoords-permission");
		Configuration.tphere_permission = config.getString("tphere-permission");
		Configuration.tpplayer_permission = config.getString("tpplayer-permission");
		Configuration.spawntp_permission = config.getString("spawntp-permission");
		
		Configuration.bypass_permission = config.getString("bypass-permission");
		Configuration.setspawn_permission = config.getString("setspawn-permission");
		Configuration.reload_permission = config.getString("reload-permission");
		Configuration.admin_permission = config.getString("admin-permission");
	}
}
