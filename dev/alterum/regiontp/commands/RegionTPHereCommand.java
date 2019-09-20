package dev.alterum.regiontp.commands;

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
import dev.alterum.regiontp.utils.Configuration;
import dev.alterum.regiontp.utils.Utils;

public class RegionTPHereCommand implements CommandExecutor {

	private final RegionTP plugin;
	private final FileConfiguration config;
	private int playersTotal = 0;
	private String prefix = Utils.format(Configuration.prefix);

	public RegionTPHereCommand (RegionTP plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;

		// Will revamp how this works in the future to handle simple structure.
		if (player.hasPermission(Configuration.tphere_permission) || !player.hasPermission(Configuration.admin_permission)) {
			if (args == null || args.length < 1 || args.length == 0) {
				player.sendMessage(Utils.format(Configuration.tphere_usage.replace("{PREFIX}", prefix)));
			} else {
				playersTotal = 0;

				Location hereLoc = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getX(), player.getLocation().getX());
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
													
													sPlayer.teleport(hereLoc);
													playersTotal = playersTotal + 1;
											}
										}
									}
								}

								if (playersTotal > 0) {
									player.sendMessage(Utils.format(Configuration.tp_success.replace("{REGION}", originName).replace("{PREFIX}", prefix)));
								} else {
									player.sendMessage(Utils.format(Configuration.none_in_region.replace("{REGION}", originName).replace("{PREFIX}", prefix)));
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
