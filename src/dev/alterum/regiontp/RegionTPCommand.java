package dev.alterum.regiontp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.google.common.base.Joiner;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import dev.alterum.regiontp.Messages;

public class RegionTPCommand implements CommandExecutor {

	private final RegionTP plugin;
	private int playersTotal = 0;

	public RegionTPCommand(RegionTP plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if (player.hasPermission(Messages.command_permission)) {
			if (args == null || args.length < 1 || args.length == 0) {
				player.sendMessage(plugin.prefix + ChatColor.translateAlternateColorCodes('&', Messages.usage));
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
									if (!serverPlayer.hasPermission(Messages.bypass_permission)) {
										if (serverPlayer.getWorld().equals(player.getWorld())) {
											if (regions.getRegion(cmdArgs).contains(
													(int) serverPlayer.getLocation().getX(),
													(int) serverPlayer.getLocation().getY(),
													(int) serverPlayer.getLocation().getZ())) {
												serverPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
														Messages.player_forced_tp));
												Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
														"cmi spawn " + serverPlayer.getName());
												playersTotal = playersTotal + 1;
											}
										}
									}
								}
								
								if (playersTotal > 0) {
									player.sendMessage(plugin.prefix + ChatColor.translateAlternateColorCodes('&',
											Messages.tp_successful.replace("{REGION}", cmdArgs)));
								} else {
									player.sendMessage(plugin.prefix + ChatColor.translateAlternateColorCodes('&',
											Messages.none_in_region.replace("{REGION}", cmdArgs)));
								}
							}
						});
					} else {
						player.sendMessage(plugin.prefix + ChatColor.translateAlternateColorCodes('&', Messages.region_not_found));
					}
				} else {
					player.sendMessage(plugin.prefix + ChatColor.translateAlternateColorCodes('&', Messages.no_regions_found));
				}
			}
		} else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.missing_permission));
		}
		return true;
	}
}