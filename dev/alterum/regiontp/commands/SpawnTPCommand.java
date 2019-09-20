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
import dev.alterum.regiontp.utils.Utils;
import dev.alterum.regiontp.utils.Configuration;

public class SpawnTPCommand implements CommandExecutor {

	private final RegionTP plugin;
	private final FileConfiguration config;
	private int playersTotal = 0;
	private Location spawnLoc = null;
	private String prefix = Utils.format(Configuration.prefix);

	public SpawnTPCommand(RegionTP plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		
		if (player.hasPermission(Configuration.spawntp_permission) || !player.hasPermission(Configuration.admin_permission)) {
			if (args == null || args.length < 1 || args.length == 0) {
				player.sendMessage(Utils.format(Configuration.spawntp_usage.replace("{PREFIX}", prefix)));
			} else {
				try {
					this.spawnLoc = new Location(Bukkit.getWorld(config.getString("spawnpoint.world")), config.getDouble("spawnpoint.x"), config.getDouble("spawnpoint.y"), config.getDouble("spawnpoint.z"));
				} catch (Exception e) {
					player.sendMessage(Utils.format(Configuration.spawn_not_set.replace("{PREFIX}", prefix)));
					e.printStackTrace();
					return false;
				}
				
				String originName = args[0];
				RegionManager regions = WGBukkit.getRegionManager((org.bukkit.World) player.getWorld());

				if (regions != null) {
					if (regions.hasRegion(originName)) {
						for (Player sPlayer : Bukkit.getServer().getOnlinePlayers()) {
							if (!sPlayer.hasPermission(Configuration.bypass_permission) || !sPlayer.hasPermission(Configuration.admin_permission)) {
								if (sPlayer.getWorld().equals(player.getWorld())) {
									if (regions.getRegion(originName).contains(
											(int) sPlayer.getLocation().getX(),
											(int) sPlayer.getLocation().getY(),
											(int) sPlayer.getLocation().getZ())) {
										sPlayer.sendMessage(Utils.format(Configuration.player_teleported.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
										sPlayer.teleport(spawnLoc);
										playersTotal += 1;
									}
								}
							}
						}
					
						if (playersTotal > 0)
							player.sendMessage(Configuration.prefix + Utils.format(Configuration.tp_success.replace("{PREFIX}", prefix).replace("{REGION}", originName).replace("{PLAYERS}", Integer.toString(playersTotal))));
						else player.sendMessage(Configuration.prefix + Utils.format(Configuration.none_in_region.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
					} else player.sendMessage(Utils.format(Configuration.no_origin_region.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
				} else player.sendMessage(Utils.format(Configuration.no_regions_found.replace("{PREFIX}", prefix)));
			}
		} else player.sendMessage(Utils.format(Configuration.missing_permission).replace("{PREFIX}", prefix));
		return true;
	}
}