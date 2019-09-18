package dev.alterum.regiontp.commands;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;

import dev.alterum.regiontp.RegionTP;
import dev.alterum.regiontp.utils.Messages;
import dev.alterum.regiontp.utils.Utils;

public class RegionTPCommand implements CommandExecutor {

	private final RegionTP plugin;
	private final FileConfiguration config;
	private int playersTotal = 0;
	private String prefix = ChatColor.translateAlternateColorCodes('&', Messages.prefix);

	public RegionTPCommand(RegionTP plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 1)
		{
			if(args[0] == "reload") {
				if(!player.hasPermission(Messages.reload_permission) || !player.hasPermission(Messages.admin_permission)) 
					player.sendMessage(Utils.format(Messages.missing_permission.replace("{PREFIX}", prefix))); 
				else {
					plugin.reloadConfig();
					plugin.loadMessages();
					player.sendMessage(Utils.format(Messages.plugin_reloaded));
				}
			}
			
			if(args[0] == "help") {

			}
			
			if(args[0] == "setspawn") {
				if(!player.hasPermission(Messages.setspawn_permission) || !player.hasPermission(Messages.admin_permission))
					player.sendMessage(Utils.format(Messages.missing_permission.replace("{PREFIX}", prefix)));
				else {
					Location pLoc = player.getLocation();
					config.set("spawnpoint.world", pLoc.getWorld());
					config.set("spawnpoint.x", pLoc.getX());
					config.set("spawnpoint.y", pLoc.getY());
					config.set("spawnpoint.z", pLoc.getZ());
					
					try {
						config.save(plugin.getConfigFile());
						player.sendMessage(Utils.format(Messages.spawn_set_success.replace("{PREFIX}", prefix)));
					} catch (IOException e) {
						plugin.getLogger().log(Level.SEVERE, "Unable to save spawn point to file.");
						e.printStackTrace();
					}
				}
			}
			
			if(args[0] == "about") {
				player.sendMessage("");
				player.sendMessage(Utils.format("&8&m- &8&m- &8&m- &8&m- &8&m-&6 &l&ofreeMenus&7 &o(Ver. 1.0.0)&8 &m- &8&m- &8&m- &8&m- &8&m-"));
				player.sendMessage("");
				player.sendMessage(Utils.format("&7 &7 &7 &oThanks so much for trying my plugin! &c&o❤"));
				player.sendMessage(Utils.format("&7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &oCome join us on &e&n&oDiscord&7&o! &a&o:3"));
				player.sendMessage(Utils.format(""));
				player.sendMessage(Utils.format("&7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7-&ei&bstar&dshine"));
				player.sendMessage("");
				player.sendMessage(Utils.format("&8&m-------------------------------------"));
				player.sendMessage("");
			}
		}

		
		if (player.hasPermission(Messages.tp_permission) || !player.hasPermission(Messages.admin_permission)) {
			if (args == null || args.length < 1 || args.length == 0) {
				player.sendMessage(Messages.prefix + ChatColor.translateAlternateColorCodes('&', Messages.regiontp_usage));
			} else {
				playersTotal = 0;
				String cmdArgs = Joiner.on(' ').join(args);
				RegionManager regions = WGBukkit.getRegionManager((org.bukkit.World) player.getWorld());

				if (regions != null) {
					if (regions.hasRegion(cmdArgs)) {
						Bukkit.getServer().getScheduler().runTask(plugin, new Runnable() {
							@Override
							public void run() {
								for (Player serverPlayer : Bukkit.getServer().getOnlinePlayers()) {
									if (!serverPlayer.hasPermission(Messages.bypass_permission) || !player.hasPermission(Messages.admin_permission)) {
										if (serverPlayer.getWorld().equals(player.getWorld())) {
											if (regions.getRegion(cmdArgs).contains(
													(int) serverPlayer.getLocation().getX(),
													(int) serverPlayer.getLocation().getY(),
													(int) serverPlayer.getLocation().getZ())) {
												serverPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
														Messages.player_teleported));
												Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
														"cmi spawn " + serverPlayer.getName());
												playersTotal = playersTotal + 1;
											}
										}
									}
								}
								
								if (playersTotal > 0) {
									player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
											Messages.tp_success.replace("{REGION}", cmdArgs)));
								} else {
									player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
											Messages.none_in_region.replace("{REGION}", cmdArgs)));
								}
							}
						});
					} else {
						player.sendMessage(prefix + Utils.format(Messages.no_origin_region.replace("{PREFIX}", prefix)));
					}
				} else {
					player.sendMessage(prefix + Utils.format(Messages.no_regions_found.replace("{PREFIX}", prefix)));
				}
			}
		} else {
			player.sendMessage(Utils.format(Messages.missing_permission.replace("{PREFIX}", prefix)));
		}
		return true;
	}
}