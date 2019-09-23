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
	public File langfile;
	public FileConfiguration configdata;
	public FileConfiguration langdata;
	public String version = "1.0.0";

	String prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix") + " ");

	public void onEnable() {
		createFiles();
		getServer().getPluginManager().registerEvents(this, this);
		loadMessages();
		
		getCommand("regiontp").setExecutor(new RegionTPCommand(this)); 
		getCommand("regiontpplayer").setExecutor(new RegionTPPlayerCommand(this));
		getCommand("regiontphere").setExecutor(new RegionTPHereCommand(this)); // Completed
		getCommand("spawntp").setExecutor(new SpawnTPCommand(this)); // Completed
		
		getLogger().info("RegionTP " + version + " successfully loaded!");
	}

	public void onDisable() {
		getServer().getLogger().info("Saving main configuration file.");
	}

	public void createFiles() {
		try {
			if (!getDataFolder().exists()) {
				getDataFolder().mkdirs();
			}
			
			this.configfile = new File(getDataFolder(), "config.yml");
			this.configdata = new YamlConfiguration();
			
			if (!this.configfile.exists()) {
				getLogger().info("Configuration file not found, generating a new one now.");
				saveResource("config.yml", true);				
				this.configdata.load(this.configfile);
			} else {
				getLogger().info("Valid configuration file located, loading information now.");
				this.configdata.load(this.configfile);
			}
			
			this.langfile = new File(getDataFolder(), "lang.yml");
			this.langdata = new YamlConfiguration();
			
			if(!this.langfile.exists()) {
				getLogger().info("Language file not found, generating a new one now.");
				
				this.langfile.getParentFile().mkdirs();
				saveResource("lang.yml", true);
				this.langdata.load(this.langfile);
			} else {
				getLogger().info("Valid language file located, loading information now.");
				this.langdata.load(this.langfile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public File getConfigFile() {
		return this.configfile;
	}
	
	public FileConfiguration getLang() {
		return this.langdata;
	}

	public void loadMessages() {
		FileConfiguration config = this.configdata;
		FileConfiguration lang = this.langdata;
		
		Configuration.prefix = config.getString("prefix");
		Configuration.send_player_message = config.getBoolean("send-player-message");
		
		Configuration.regiontp_usage = lang.getString("regiontp-usage");
		Configuration.tpcoords_usage = lang.getString("tpcoords-usage");
		Configuration.tphere_usage = lang.getString("tphere-usage");
		Configuration.tpplayer_usage = lang.getString("tpplayer-usage");
		Configuration.spawntp_usage = lang.getString("spawntp-usage");

		Configuration.no_regions_found = lang.getString("no-regions-found");
		Configuration.no_origin_region = lang.getString("origin-region-not-found");
		Configuration.no_dest_region = lang.getString("dest-region-not-found");
		Configuration.region_lacks_point = lang.getString("region-lacks-tp-point");
		Configuration.none_in_region = lang.getString("none-found-in-region");
		Configuration.player_not_found = lang.getString("tphere-player-not-found");
		Configuration.spawn_not_set = lang.getString("spawnpoint-not-set");
		
		Configuration.tp_success = lang.getString("tp-successful");
		Configuration.player_teleported = lang.getString("player-tp-message");
		Configuration.spawn_set_success = lang.getString("spawn-set-success");
		Configuration.plugin_reloaded = lang.getString("plugin-reloaded");
		Configuration.command_unrecognised = lang.getString("command-unrecognised");
		Configuration.missing_permission = lang.getString("missing-permission");
		
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
