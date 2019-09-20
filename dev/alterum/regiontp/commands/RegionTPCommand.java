package dev.alterum.regiontp.commands;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;

import dev.alterum.regiontp.RegionTP;
import dev.alterum.regiontp.utils.Utils;
import dev.alterum.regiontp.utils.Configuration;

public class RegionTPCommand implements CommandExecutor {

	private final RegionTP plugin;
	private final FileConfiguration config;
	private int playersTotal = 0;
	private String prefix = Utils.format(Configuration.prefix);

	public RegionTPCommand(RegionTP plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 1)
		{
			if(args[0] == "reload") {
				if(!player.hasPermission(Configuration.reload_permission) || !player.hasPermission(Configuration.admin_permission)) 
					player.sendMessage(Utils.format(Configuration.missing_permission.replace("{PREFIX}", prefix))); 
				else {
					plugin.reloadConfig();
					plugin.loadMessages();
					player.sendMessage(Utils.format(Configuration.plugin_reloaded));
				}
			}
			
			if(args[0] == "help") {

			}
			
			if(args[0] == "setspawn") {
				if(!player.hasPermission(Configuration.setspawn_permission) || !player.hasPermission(Configuration.admin_permission))
					player.sendMessage(Utils.format(Configuration.missing_permission.replace("{PREFIX}", prefix)));
				else {
					Location pLoc = player.getLocation();
					config.set("spawnpoint.world", pLoc.getWorld());
					config.set("spawnpoint.x", pLoc.getX());
					config.set("spawnpoint.y", pLoc.getY());
					config.set("spawnpoint.z", pLoc.getZ());
					
					try {
						config.save(plugin.getConfigFile());
						player.sendMessage(Utils.format(Configuration.spawn_set_success.replace("{PREFIX}", prefix).replace("{X}",
								Double.toString(pLoc.getX())).replace("{Y}", Double.toString(pLoc.getY())).replace("{Z}", Double.toString(pLoc.getZ()))));
					} catch (IOException e) {
						plugin.getLogger().log(Level.SEVERE, "Unable to save spawn point to file.");
						e.printStackTrace();
					}
				}
			}
			
			if(args[0] == "about") {
				player.sendMessage("");
				player.sendMessage(Utils.format("&8&m- &8&m- &8&m- &8&m- &8&m-&6 &l&oRegionTP &7 &o(Ver. 0.6.2)&8 &m- &8&m- &8&m- &8&m- &8&m-"));
				player.sendMessage("");
				player.sendMessage(Utils.format("&7 &7 &7 &oThanks so much for trying my plugin! &c&o❤"));
				player.sendMessage(Utils.format("&7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &oCome join us on &e&n&oDiscord&7&o! &a&o:3"));
				player.sendMessage(Utils.format(""));
				player.sendMessage(Utils.format("&7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7-&e99&dkate"));
				player.sendMessage("");
				player.sendMessage(Utils.format("&8&m-------------------------------------"));
				player.sendMessage("");
			}
		}

		// Will revamp how this works in the future to handle simple structure without multiple WorldGuard check methods.
		if (player.hasPermission(Configuration.tp_permission) || !player.hasPermission(Configuration.admin_permission)) {
			if (args == null || args.length < 1 || args.length == 0) {
				player.sendMessage(Utils.format(Configuration.regiontp_usage.replace("{PREFIX}", prefix)));
			} else {
				if(args.length > 2) {
					if(!(args.length == 4)) 
						player.sendMessage(Utils.format(Configuration.regiontp_usage.replace("{PREFIX}", "prefix")));
					else {
						try {
							int x = Integer.parseInt(args[1]);
							int y = Integer.parseInt(args[2]);
							int z = Integer.parseInt(args[3]);

							Location destLoc = new Location(player.getWorld(), x, y, z);
						} catch (Exception e) {
							player.sendMessage(Utils.format(Configuration.regiontp_usage.replace("{PREFIX}", prefix)));
						}
						// Destination teleportation code here.
					}
				} else {
					playersTotal = 0;
					
					String originName = args[0];
					RegionManager regions = WGBukkit.getRegionManager((org.bukkit.World) player.getWorld());

					if (regions != null) {
						if (regions.hasRegion(originName)) {
							Bukkit.getServer().getScheduler().runTask(plugin, new Runnable() {
								@Override
								public void run() {
									for (Player sPlayer : Bukkit.getServer().getOnlinePlayers()) {
										if (!sPlayer.hasPermission(Configuration.bypass_permission) || !sPlayer.hasPermission(Configuration.admin_permission)) {
											if (sPlayer.getWorld().equals(player.getWorld())) {
												if (regions.getRegion(originName).contains(
														(int) sPlayer.getLocation().getX(),
														(int) sPlayer.getLocation().getY(),
														(int) sPlayer.getLocation().getZ())) {
													sPlayer.sendMessage(Utils.format(Configuration.player_teleported.replace("{PREFIX}", prefix)));
													// Region teleportation point code here.
													playersTotal = playersTotal + 1;
												}
											}
										}
									}

									if (playersTotal > 0) {
										player.sendMessage(Utils.format(Configuration.tp_success.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
									} else {
										player.sendMessage(Utils.format(Configuration.none_in_region.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
									}
								}
							});
						} else {
							player.sendMessage(Utils.format(Configuration.no_origin_region.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
						}
					} else {
						player.sendMessage(Utils.format(Configuration.no_regions_found.replace("{PREFIX}", prefix)));
					}
				}
			}
		} else {
			player.sendMessage(Utils.format(Configuration.missing_permission.replace("{PREFIX}", prefix)));
		}
		return true;
	}
}
