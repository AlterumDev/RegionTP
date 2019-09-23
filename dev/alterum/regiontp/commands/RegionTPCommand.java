package dev.alterum.regiontp.commands;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

import dev.alterum.regiontp.RegionTP;
import dev.alterum.regiontp.utils.Utils;
import dev.alterum.regiontp.utils.ChatUtils;
import dev.alterum.regiontp.utils.Configuration;

public class RegionTPCommand implements CommandExecutor {

	private final RegionTP plugin;
	private Location destLoc = null;
	private final FileConfiguration config;
	private int playersTotal = 0;
	private String prefix = Utils.format(Configuration.prefix);

	public RegionTPCommand(RegionTP plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		
		if(!player.hasPermission(Configuration.tp_permission) || !player.hasPermission(Configuration.admin_permission)) {
			player.sendMessage(Utils.format(Configuration.missing_permission.replace("{PREFIX}", prefix)));
			return false;
		}

		if(args.length == 0) {
			player.sendMessage(Utils.format(Configuration.regiontp_usage.replace("{PREFIX}", prefix)));
			return false;
		}	
		if(args[0].equals("reload")) {
			if(!player.hasPermission(Configuration.reload_permission) || !player.hasPermission(Configuration.admin_permission)) {
				player.sendMessage(Utils.format(Configuration.missing_permission.replace("{PREFIX}", prefix))); 
				return false;
			} else {
				plugin.createFiles();
		        plugin.loadMessages();
				player.sendMessage(Utils.format(Configuration.plugin_reloaded.replace("{PREFIX}", prefix).replace("{VERSION}", plugin.version)));
			}
			return true;
		} else if(args[0].equals("help")) {
			displayHelp(player);
			return true;
		} else if(args[0].equals("setspawn")) {
			if(!player.hasPermission(Configuration.setspawn_permission) || !player.hasPermission(Configuration.admin_permission)) {
				player.sendMessage(Utils.format(Configuration.missing_permission.replace("{PREFIX}", prefix)));
				return false;
			} else {
				Location pLoc = player.getLocation();
				config.set("spawnpoint.world", pLoc.getWorld().getName());
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
			
			return true;
		} else if(args[0].equals("about")) {
			player.sendMessage("");
			player.sendMessage(Utils.format("&8&m- &8&m- &8&m- &8&m- &8&m-&6 &l&oRegionTP &7 &o(Ver. " + plugin.version + ")&8 &m- &8&m- &8&m- &8&m- &8&m-"));
			player.sendMessage("");
			player.sendMessage(Utils.format("&7 &7 &7 &oThanks so much for trying my plugin! &c&o❤"));
			player.spigot().sendMessage(new ChatUtils("&7 &7 &7 &7 &7 &7 &7 &7&oCome join us on ", 
										null,
										null).getTextComponent(),
										new ChatUtils("&7&n&oDiscord&7&o! &8&o(Click)",
										new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/GajsFZj"),
										new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder(Utils.format("&eClick to join our Discord! :D"))
											.create())).getTextComponent());			
			player.sendMessage(Utils.format(""));
			player.sendMessage(Utils.format("&7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7 &7-&e99&dkate"));
			player.sendMessage("");
			player.sendMessage(Utils.format("&8&m-------------------------------------"));
			player.sendMessage("");
			
			return true;
		}
		
		if (args.length == 1) {
			player.sendMessage(Utils.format(Configuration.regiontp_usage.replace("{PREFIX}", prefix))); 
			return false;
		}
		
		// Will revamp how this works in the future to handle simple structure without multiple WorldGuard check methods.
		if(args.length == 4) {	
			double x = 0, y = 0, z = 0;
			try {
				x = Integer.parseInt(args[1]);
				y = Integer.parseInt(args[2]);
				z = Integer.parseInt(args[3]);
			} catch (Exception e) {
				player.sendMessage(Utils.format(Configuration.tpcoords_usage.replace("{PREFIX}", prefix)));
			}
			
			destLoc = new Location(player.getWorld(), x, y, z);
			playersTotal = 0;
			
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
									if(Configuration.send_player_message)
										sPlayer.sendMessage(Utils.format(Configuration.player_teleported.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
									
									sPlayer.teleport(destLoc);
									playersTotal += 1;
								}
							}
						}
					}
	
					if (playersTotal > 0) 
						player.sendMessage(Utils.format(Configuration.tp_success.replace("{PREFIX}", prefix).replace("{REGION}", originName).replace("{PLAYERS}", Integer.toString(playersTotal))));
					else
						player.sendMessage(Utils.format(Configuration.none_in_region.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
				}
			}
		}
		
		if(args.length > 4) {
			player.sendMessage(Utils.format(Configuration.command_unrecognised).replace("{PREFIX}", prefix));
			return false;
		}
		
		if(args.length == 2) {
			playersTotal = 0;
			
			String originName = args[0];
			String destName = args[1];
			RegionManager regions = WGBukkit.getRegionManager((org.bukkit.World) player.getWorld());

			if (regions != null) {
				if (regions.hasRegion(originName)) {
					if(regions.hasRegion(destName)) {
						if(regions.getRegion(destName).getFlags().containsKey(DefaultFlag.TELE_LOC)) {
							for (Player sPlayer : Bukkit.getServer().getOnlinePlayers()) {
								if (!sPlayer.hasPermission(Configuration.bypass_permission) || !sPlayer.hasPermission(Configuration.admin_permission)) {
									if (sPlayer.getWorld().equals(player.getWorld())) {
										if (regions.getRegion(originName).contains(
												(int) sPlayer.getLocation().getX(),
												(int) sPlayer.getLocation().getY(),
												(int) sPlayer.getLocation().getZ())) {
											if(Configuration.send_player_message)
												sPlayer.sendMessage(Utils.format(Configuration.player_teleported.replace("{PREFIX}", prefix).replace("{REGION}", originName)));

											double x = regions.getRegion(destName).getFlag(DefaultFlag.TELE_LOC).getPosition().getX();
											double y = regions.getRegion(destName).getFlag(DefaultFlag.TELE_LOC).getPosition().getY();
											double z = regions.getRegion(destName).getFlag(DefaultFlag.TELE_LOC).getPosition().getZ();
											World world = Bukkit.getWorld(regions.getRegion(destName).getFlag(DefaultFlag.TELE_LOC).getWorld().getName());
											
											sPlayer.teleport(new Location(world, x, y, z));
											playersTotal += 1;
										}
									}
								}
							}
						
							if (playersTotal > 0)
								player.sendMessage(Utils.format(Configuration.tp_success.replace("{PREFIX}", prefix).replace("{REGION}", originName).replace("{PLAYERS}", Integer.toString(playersTotal))));
							else
								player.sendMessage(Utils.format(Configuration.none_in_region.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
						} else {
							player.sendMessage(Utils.format(Configuration.region_lacks_point.replace("{PREFIX}", prefix).replace("{REGION}", destName)));
						}
					} else {
					player.sendMessage(Utils.format(Configuration.no_dest_region.replace("{PREFIX}", prefix).replace("{REGION}", destName)));
					}
				} else {
					player.sendMessage(Utils.format(Configuration.no_origin_region.replace("{PREFIX}", prefix).replace("{REGION}", originName)));
				}
			} else {
				player.sendMessage(Utils.format(Configuration.no_regions_found.replace("{PREFIX}", prefix)));
			}
		}
		return true;
	}
	
	private void displayHelp(Player player) {
		player.sendMessage(Utils.format("&8&m-----------&r &6&l&oRegionTP &8&m-----------"));
		player.spigot().sendMessage(new ChatUtils("&e/regiontp &7(originRegion) (destRegion)",
									new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/regiontp (originRegion) (destRegion)"),
									new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ComponentBuilder(Utils.format("&7The base command for RegionTP.\n"
											+ "&7Teleports players from one region to the tp point of the other region."))
										.create())).getTextComponent());
		player.spigot().sendMessage(new ChatUtils("&e/regiontp &7(originRegion) (x) (y) (z)",
									new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/regiontp (originRegion) (x) (y) (z)"),
									new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ComponentBuilder(Utils.format("&7The secondary base command for RegionTP.\n"
											+ "&7Teleports players from one region to the specified coordinates."))
										.create())).getTextComponent());
		player.spigot().sendMessage(new ChatUtils("&e/regiontpplayer &7(originRegion) (player)",
									new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/regiontpplayer (originRegion) (player)"),
									new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ComponentBuilder(Utils.format("&7Teleports players from one region to the current\n"
											+ "&7location of the player specified."))
										.create())).getTextComponent());
		player.spigot().sendMessage(new ChatUtils("&e/regiontphere &7(originRegion)",
									new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/regiontphere (originRegion)"),
									new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ComponentBuilder(Utils.format("&7Teleports players from one region to the current\n"
											+ "&7location of the player who executes the command."))
										.create())).getTextComponent());
		player.spigot().sendMessage(new ChatUtils("&e/spawntp &7(originRegion)",
									new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/spawntp (originRegion)"),
									new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ComponentBuilder(Utils.format("&7Spawn point teleportation command.\n"
											+ "&7Teleports players from one region to the configuration-set spawn point position."))
										.create())).getTextComponent());
		player.spigot().sendMessage(new ChatUtils("&e/regiontp &7setspawn",
									new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/regiontp setspawn"),
									new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ComponentBuilder(Utils.format("&7Sets the designated spawn point.\n"
											+ "&7For the /spawntp command to use."))
										.create())).getTextComponent());
		player.spigot().sendMessage(new ChatUtils("&e/regiontp &7reload",
									new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/regiontp reload"),
									new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ComponentBuilder(Utils.format("&7Reloads the plugin's config and lang files.\n"
											+ "&7However it does not reload the plugin itself."))
										.create())).getTextComponent());
		player.spigot().sendMessage(new ChatUtils("&e/regiontp &7help",
									new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/regiontp help"),
									new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ComponentBuilder(Utils.format("&7Displays this help menu!"))
										.create())).getTextComponent());
		player.spigot().sendMessage(new ChatUtils("&e/regiontp &7about",
									new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/regiontp about"),
									new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ComponentBuilder(Utils.format("&7View the plugin version.\n"
											+ "&7as well as a link to our\n&7Discord server."))
										.create())).getTextComponent());
		player.sendMessage("");
		player.spigot().sendMessage(new ChatUtils("&8&o(Hover for more info!)",
									null,
									new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder(Utils.format("&7Click the command to use it!"))
										.create())).getTextComponent());
		player.sendMessage(Utils.format("&8&m----------------------------------"));
	}
}
