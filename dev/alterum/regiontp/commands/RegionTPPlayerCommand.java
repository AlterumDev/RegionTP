package dev.alterum.regiontp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;

import dev.alterum.regiontp.RegionTP;
import dev.alterum.regiontp.utils.Configuration;
import dev.alterum.regiontp.utils.Utils;

public class RegionTPPlayerCommand implements CommandExecutor {

	private final RegionTP plugin;
	private final FileConfiguration config;
	private int playersTotal = 0;
	private String prefix = Utils.format(Configuration.prefix);

	public RegionTPPlayerCommand (RegionTP plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;

		// Will revamp how this works in the future to handle simple structure without multiple WorldGuard check methods.
		if (player.hasPermission(Configuration.tpplayer_permission) || !player.hasPermission(Configuration.admin_permission)) {
			if (args == null || args.length < 1 || args.length == 0) {
				player.sendMessage(Utils.format(Configuration.tpplayer_usage.replace("{PREFIX}", prefix)));
			} else {
				// Introduce check for args[1] as player name against UUID to then getPlayer() and check they are online.
				// Fetch player position for teleportation.

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
													
													// Teleport to specified player location
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
		} else {
			player.sendMessage(Utils.format(Configuration.missing_permission.replace("{PREFIX}", prefix)));
		}
		return true;
	}
}
