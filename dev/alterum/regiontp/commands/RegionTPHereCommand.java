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

public class RegionTPHereCommand implements CommandExecutor {

	private final RegionTP plugin;
	private final FileConfiguration config;
	private int playersTotal = 0;
	private String prefix = Utils.format(Messages.prefix);

	public RegionTPHereCommand (RegionTP plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;

		// Will revamp how this works in the future to handle simple structure without multiple WorldGuard check methods.
		if (player.hasPermission(Messages.tphere_permission) || !player.hasPermission(Messages.admin_permission)) {
			if (args == null || args.length < 1 || args.length == 0) {
				player.sendMessage(Utils.format(Messages.regiontphere_usage.replace("{PREFIX}", prefix));
			} else {
				Location hereLoc = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getX(), player.getLocation().getX());
				playersTotal = 0;
				String cmdArgs = Joiner.on(' ').join(args);
				RegionManager regions = WGBukkit.getRegionManager((org.bukkit.World) player.getWorld());

				if (regions != null) {
					if (regions.hasRegion(cmdArgs)) {
						Bukkit.getServer().getScheduler().runTask(plugin, new Runnable() {
							@Override
							public void run() {
								for (Player sPlayer : Bukkit.getServer().getOnlinePlayers()) {
									if (!sPlayer.hasPermission(Messages.bypass_permission) || !sPlayer.hasPermission(Messages.admin_permission)) {
										if (sPlayer.getWorld().equals(player.getWorld())) {
											if (regions.getRegion(cmdArgs).contains(
												(int) sPlayer.getLocation().getX(),
												(int) sPlayer.getLocation().getY(),
												(int) sPlayer.getLocation().getZ())) {
													sPlayer.sendMessage(Utils.format(Messages.player_teleported.replace("{PREFIX}", prefix)));
													
													sPlayer.teleport(hereLoc);
													playersTotal = playersTotal + 1;
											}
										}
									}
								}

								if (playersTotal > 0) {
									player.sendMessage(Utils.format(Messages.tp_success.replace("{REGION}", cmdArgs).replace("{PREFIX}", prefix)));
								} else {
									player.sendMessage(Utils.format(Messages.none_in_region.replace("{REGION}", cmdArgs).replace("{PREFIX}", prefix)));
								}
							}
							});
						} else {
							player.sendMessage(Utils.format(Messages.no_origin_region.replace("{PREFIX}", prefix).replace("{REGION}", cmdArgs));
						}
					} else {
						player.sendMessage(Utils.format(Messages.no_regions_found.replace("{PREFIX}", prefix)));
					}
				}
			} else {
				player.sendMessage(Utils.format(Messages.missing_permission.replace("{PREFIX}", prefix)));
		}
		return true;
	}
}
