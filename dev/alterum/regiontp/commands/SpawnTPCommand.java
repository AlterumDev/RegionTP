package dev.alterum.regiontp.commands;

import org.bukkit.Bukkit;
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

public class SpawnTPCommand implements CommandExecutor {

	private final RegionTP plugin;
	private final FileConfiguration config;
	private int playersTotal = 0;
	private Location spawnLoc = null;
	private String prefix = Utils.format(Messages.prefix);

	public SpawnTPCommand(RegionTP plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		
		if (player.hasPermission(Messages.spawntp_permission) || !player.hasPermission(Messages.admin_permission)) {
			if (args == null || args.length < 1 || args.length == 0) {
				player.sendMessage(Utils.format(Messages.spawntp_usage.replace("{PREFIX}", prefix)));
			} else {
				try {
					this.spawnLoc = new Location(Bukkit.getWorld(config.getString("spawnpoint.world")), config.getDouble("spawnpoint.x"), config.getDouble("spawnpoint.y"), config.getDouble("spawnpoint.z"));
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				
				String originName = Joiner.on(' ').join(args);
				RegionManager regions = WGBukkit.getRegionManager((org.bukkit.World) player.getWorld());

				if (regions != null) {
					if (regions.hasRegion(originName)) {
						for (Player sPlayer : Bukkit.getServer().getOnlinePlayers()) {
							if (!sPlayer.hasPermission(Messages.bypass_permission) || !sPlayer.hasPermission(Messages.admin_permission)) {
								if (sPlayer.getWorld().equals(player.getWorld())) {
									if (regions.getRegion(originName).contains(
											(int) sPlayer.getLocation().getX(),
											(int) sPlayer.getLocation().getY(),
											(int) sPlayer.getLocation().getZ())) {
										sPlayer.sendMessage(Utils.format(Messages.player_teleported.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
										sPlayer.teleport(spawnLoc);
										playersTotal += 1;
									}
								}
							}
						}
					
						if (playersTotal > 0)
							player.sendMessage(Messages.prefix + Utils.format(Messages.tp_success.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
						else player.sendMessage(Messages.prefix + Utils.format(Messages.none_in_region.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
					} else player.sendMessage(Utils.format(Messages.no_origin_region.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
				} else player.sendMessage(Utils.format(Messages.no_regions_found.replace("{PREFIX}", prefix)));
			}
		} else player.sendMessage(Utils.format(Messages.missing_permission).replace("{PREFIX}", prefix));
		return true;
	}
}