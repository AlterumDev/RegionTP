package dev.alterum.regiontp;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import dev.alterum.regiontp.commands.RegionTPCommand;
import dev.alterum.regiontp.commands.SpawnTPCommand;
import dev.alterum.regiontp.utils.Messages;

public class RegionTP extends JavaPlugin implements Listener {

	private File configfile;
	public FileConfiguration configdata;
	public String version = "0.5.2";

	String prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix") + " ");

	public void onEnable() {
		createFiles();
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("regiontp").setExecutor(new RegionTPCommand(this));
		getCommand("spawntp").setExecutor(new SpawnTPCommand(this));
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

	public FileConfiguration getConfigFile() {
		return this.configdata;
	}

	public void loadMessages() {
		FileConfiguration config = getConfigFile();
		
		Messages.prefix = config.getString("prefix");
		
		Messages.regiontp_usage = config.getString("regiontp-usage");
		Messages.tpcoords_usage = config.getString("tpcoords-usage");
		Messages.spawntp_usage = config.getString("spawntp-usage");

		Messages.no_regions_found = config.getString("no-regions-found");
		Messages.no_origin_region = config.getString("origin-region-not-found");
		Messages.no_dest_region = config.getString("dest-region-not-found");
		Messages.region_lacks_point = config.getString("region-lacks-tp-point");
		Messages.none_in_region = config.getString("none-found-in-origin");
		Messages.tp_success = getConfigFile().getString("tp-successful");
		Messages.player_teleported = config.getString("player-tp-message");
		Messages.spawn_set_success = config.getString("spawn-set-success");
		Messages.plugin_reloaded = config.getString("plugin-reloaded");
		Messages.missing_permission = config.getString("missing-permission");
		
		Messages.tp_permission = getConfigFile().getString("regiontp-permission");
		Messages.tpcoords_permission = getConfigFile().getString("tpcoords-permission");
		Messages.spawntp_permission = getConfigFile().getString("spawntp-permission");
		Messages.bypass_permission = getConfigFile().getString("bypass-permission");
		Messages.setspawn_permission = getConfigFile().getString("setspawn-permission");
		Messages.reload_permission = getConfigFile().getString("reload-permission");
		Messages.admin_permission = getConfigFile().getString("admin-permission");
	}
}
