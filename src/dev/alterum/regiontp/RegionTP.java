package dev.alterum.regiontp;

import java.util.List;
import java.util.UUID;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.bringholm.nametagchanger.NameTagChanger;
import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.CrateType;
import com.hazebyte.crate.api.event.CrateInteractEvent;

import dev.alterum.regiontp.Messages;
import net.haoshoku.nick.NickPlugin;

public class RegionTP extends JavaPlugin implements Listener {

	private File configfile;
	public FileConfiguration configdata;
	private String version = "0.4";

	String prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix") + " ");

	public void onEnable() {
		createFiles();
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("spawnall").setExecutor(new RegionTPCommand(this));
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
		Messages.prefix = getConfigFile().getString("prefix");
		Messages.usage = getConfigFile().getString("usage");
		Messages.no_regions_found = getConfigFile().getString("no-regions-found");
		Messages.region_not_found = getConfigFile().getString("region-not-found");
		Messages.tp_successful = getConfigFile().getString("tp-successful");
		Messages.none_in_region = getConfigFile().getString("none-in-region");
		Messages.player_forced_tp = getConfigFile().getString("player-forced-tp");
		Messages.plugin_reloaded = getConfigFile().getString("plugin-reloaded");
		Messages.missing_permission = getConfigFile().getString("missing-permission");
		Messages.command_permission = getConfigFile().getString("command-permission");
		Messages.bypass_permission = getConfigFile().getString("bypass-permission");
		Messages.reload_permission = getConfigFile().getString("reload-permission");
	}

	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("rtpreload")) {
			  if (!(sender instanceof org.bukkit.entity.Player)) {
			    try {
			      this.configdata.load(this.configfile);
			    } catch (FileNotFoundException e) {
			      e.printStackTrace();
			    } catch (IOException e) {
			      e.printStackTrace();
			    } catch (InvalidConfigurationException e) {
			      e.printStackTrace();
			    }
			    getServer().getConsoleSender().sendMessage(this.prefix + ChatColor.GRAY + "Configuration file reloaded.");
			  } else if (sender.hasPermission(Messages.reload_permission)) {
			    try {
			      this.configdata.load(this.configfile);
			    } catch (FileNotFoundException e) {
			      e.printStackTrace();
			    } catch (IOException e) {
			      e.printStackTrace();
			    } catch (InvalidConfigurationException e) {
			      e.printStackTrace();
			    }
			    sender.sendMessage(this.prefix + ChatColor.translateAlternateColorCodes('&', Messages.plugin_reloaded.replace("{VERSION}", version)));
			  } else {
			    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.missing_permission));
			  }
		}
		return true;
	}
}
