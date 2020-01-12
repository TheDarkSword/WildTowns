package com.gmail.filoghost.wildtowns.object;

import java.util.Map.Entry;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class ChatManager {
	
	public static void sendTownChatMessage(Player player, Resident resident, Town town, String message) {
		TownRank rank = town.getRank(resident);
		String formattedRankName;
		if (rank != null) {
			formattedRankName = Lang.format(Lang.format_rankTag, "{rank}", WordUtils.capitalize(rank.getUserFriendlyName()));
		} else {
			formattedRankName = "";
		}
		
		message = Lang.format(Lang.format_townChat, "{name}", player.getName(), "{town}", town.getName(), "{rank}", formattedRankName) + message;
		
		// Manda il messaggio ai giocatori online della città
		for (Entry<Player, Resident> onlineEntry : WTManager.getOnlineResidentsMap().entrySet()) {
			if (onlineEntry.getValue().getTown() == town) {
				onlineEntry.getKey().sendMessage(message);
			}
		}
		// Manda anche il messaggio alla console
		Bukkit.getConsoleSender().sendMessage(message);
	}

}
