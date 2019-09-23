package dev.alterum.regiontp.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;

import dev.alterum.regiontp.RegionTP;
import dev.alterum.regiontp.utils.Configuration;
import dev.alterum.regiontp.utils.Utils;

public class RegionTPPlayerCommand implements CommandExecutor {

	private final RegionTP plugin;
	private int playersTotal = 0;
	private String prefix = Utils.format(Configuration.prefix);

	public RegionTPPlayerCommand (RegionTP plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;

		// Will revamp how this works in the future to handle simple structure without multiple WorldGuard check methods.
		if (player.hasPermission(Configuration.tpplayer_permission) || player.hasPermission(Configuration.admin_permission)) {
			if (args == null || args.length < 2) {
				player.sendMessage(Utils.format(Configuration.tpplayer_usage.replace("{PREFIX}", prefix)));
			} else {
				
				if(Bukkit.getPlayer(args[1]) == null || Bukkit.getPlayer(args[1]).isOnline() == false) {
					player.sendMessage(Utils.format(Configuration.player_not_found.replace("{PREFIX}", prefix).replace("{PLAYER}", args[1])));
					return false;
				}
				
				Player destPlayer = Bukkit.getPlayer(args[1]);
				Location destLoc = new Location(destPlayer.getWorld(), destPlayer.getLocation().getX(), destPlayer.getLocation().getY(), destPlayer.getLocation().getZ());
				
				String originName = args[0];
				RegionManager regions = WGBukkit.getRegionManager((org.bukkit.World) player.getWorld());
				playersTotal = 0;

				if (regions != null) {
					if (regions.hasRegion(originName)) {
						for (Player sPlayer : Bukkit.getServer().getOnlinePlayers()) {
							if (!sPlayer.hasPermission(Configuration.bypass_permission) || !sPlayer.hasPermission(Configuration.admin_permission)) {
								if (sPlayer.getWorld().equals(player.getWorld())) {
									if (regions.getRegion(originName).contains(
										(int) sPlayer.getLocation().getX(),
										(int) sPlayer.getLocation().getY(),
										(int) sPlayer.getLocation().getZ())) {
										if(Configuration.send_player_message)
											sPlayer.sendMessage(Utils.format(Configuration.player_teleported.replace("{PREFIX}", prefix).replace("{REGION}", originName)));											
										
										sPlayer.teleport(destLoc);
										playersTotal += 1;	
									}
								}
							}
						}

						if (playersTotal > 0) {
							player.sendMessage(Utils.format(Configuration.tp_success.replace("{PREFIX}", prefix).replace("{REGION}", originName).replace("{PLAYERS}", Integer.toString(playersTotal))));
						} else {
							player.sendMessage(Utils.format(Configuration.none_in_region.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
						}
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
