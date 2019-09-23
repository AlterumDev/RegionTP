package dev.alterum.regiontp.utils;

import org.bukkit.ChatColor;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatUtils {
	
	private TextComponent textComp;
	
	public ChatUtils(String text, ClickEvent clickEvent, HoverEvent hoverEvent) {
		textComp = new TextComponent(ChatColor.translateAlternateColorCodes('&', text));
		
		if(clickEvent != null)
			textComp.setClickEvent(clickEvent);
		
		if(hoverEvent != null)
			textComp.setHoverEvent(hoverEvent);
	}
	
	public TextComponent getTextComponent() {
		return textComp;
	}
}
