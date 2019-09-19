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

public class RegionTPPlayerCommand implements CommandExecutor {

	private final RegionTP plugin;
	private final FileConfiguration config;
	private int playersTotal = 0;
	private String prefix = Utils.format(Messages.prefix);

	public RegionTPPlayerCommand (RegionTP plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;

		// Will revamp how this works in the future to handle simple structure without multiple WorldGuard check methods.
		if (player.hasPermission(Messages.tpplayer_permission) || !player.hasPermission(Messages.admin_permission)) {
			if (args == null || args.length < 1 || args.length == 0) {
				player.sendMessage(Utils.format(Messages.regiontpplayer_usage.replace("{PREFIX}", prefix));
			} else {
				// Introduce check for args[1] as player name against UUID to then getPlayer() and check they are online.
				// Fetch player position for teleportation.

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
													sPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
													Messages.player_teleported));
													
													// Teleport to specified player location
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
