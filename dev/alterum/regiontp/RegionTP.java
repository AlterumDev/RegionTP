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
		getCommand("regiontpplayer").setExecutor(new RegionTPPlayerCommand(this));
		getCommand("regiontphere").setExecutor(new RegionTPHereCommand(this));
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

	public File getConfigFile() {
		return this.configfile;
	}

	public void loadMessages() {
		FileConfiguration config = getConfig();
		
		Messages.prefix = config.getString("prefix");
		Messages.send_player_message = config.getBoolean("send-player-message");
		
		Messages.regiontp_usage = config.getString("regiontp-usage");
		Messages.tpcoords_usage = config.getString("tpcoords-usage");
		Messages.tphere_usage = config.getString("tphere-usage");
		Messages.tpplayer_usage = config.getString("tpplayer-usage");
		Messages.spawntp_usage = config.getString("spawntp-usage");

		Messages.no_regions_found = config.getString("no-regions-found");
		Messages.no_origin_region = config.getString("origin-region-not-found");
		Messages.no_dest_region = config.getString("dest-region-not-found");
		Messages.region_lacks_point = config.getString("region-lacks-tp-point");
		Messages.none_in_region = config.getString("none-found-in-origin");
		Messages.player_not_found = config.getString("tphere-player-not-found");
		Messages.spawn_not_set = config.getString("spawnpoint-not-set");
		
		Messages.tp_success = config.getString("tp-successful");
		Messages.player_teleported = config.getString("player-tp-message");
		Messages.spawn_set_success = config.getString("spawn-set-success");
		Messages.plugin_reloaded = config.getString("plugin-reloaded");
		Messages.missing_permission = config.getString("missing-permission");
		
		Messages.tp_permission = config.getString("regiontp-permission");
		Messages.tpcoords_permission = config.getString("tpcoords-permission");
		Messages.tphere_permission = config.getString("tphere-permission");
		Messages.tpplayer_permission = config.getString("tpplayer-permission");
		Messages.spawntp_permission = config.getString("spawntp-permission");
		
		Messages.bypass_permission = config.getString("bypass-permission");
		Messages.setspawn_permission = config.getString("setspawn-permission");
		Messages.reload_permission = config.getString("reload-permission");
		Messages.admin_permission = config.getString("admin-permission");
	}
}
