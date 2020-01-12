package com.gmail.filoghost.wildtowns.listener;

import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.gui.map.MapGenerator;
import com.gmail.filoghost.wildtowns.object.ChatManager;
import com.gmail.filoghost.wildtowns.object.base.ChatMode;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class ChatListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChatLowest(AsyncPlayerChatEvent event) {
		Resident resident = WTManager.getOnlineResident(event.getPlayer());
		Town town = resident.getTown();
		
		if (town != null && resident.getChatMode() == ChatMode.TOWN) {
			ChatManager.sendTownChatMessage(event.getPlayer(), resident, town, event.getMessage());
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChatMonitor(AsyncPlayerChatEvent event) {
		Resident resident = WTManager.getOnlineResident(event.getPlayer());
		Town town = resident.getTown();
		
		if (town == null) {
			event.setFormat(event.getFormat().replace("[TOWN]", ""));
			
		} else if (resident.getChatMode() == ChatMode.PUBLIC) {
			
			String formattedTownTag = Lang.format(Lang.format_townTag, "{town}", town);
			
			if (Settings.chatRelationColor) {
				String replaceableMessage = event.getFormat().replace("%1$2s", event.getPlayer().getDisplayName());
				
				String ownMessage = replaceableMessage.replace("[TOWN]", MapGenerator.COLOR_OWN_TOWN + formattedTownTag).replace("%2$2s", event.getMessage());
				String allyMessage = replaceableMessage.replace("[TOWN]", MapGenerator.COLOR_ALLY + formattedTownTag).replace("%2$2s", event.getMessage());
				String neutralMessage = replaceableMessage.replace("[TOWN]", formattedTownTag).replace("%2$2s", event.getMessage());
				String enemyMessage = replaceableMessage.replace("[TOWN]", MapGenerator.COLOR_ENEMY + formattedTownTag).replace("%2$2s", event.getMessage());
				
				for (Entry<Player, Resident> entry : WTManager.getOnlineResidentsMap().entrySet()) {
					Player recipientPlayer = entry.getKey();
					Resident recipientResident = entry.getValue();

					if (!event.getRecipients().contains(recipientPlayer)) {
						// Qualche altro plugin l'aveva già escluso dal ricevere il messaggio
						continue;
					}

					if (recipientResident.getTown() != null) {
						if (recipientResident.getTown() == town) {
							recipientPlayer.sendMessage(ownMessage);
							continue;
						} else if (recipientResident.getTown().getEnemies().contains(town)) {
							recipientPlayer.sendMessage(enemyMessage);
							continue;
						} else if (recipientResident.getTown().isReciprocalAlly(town)) {
							recipientPlayer.sendMessage(allyMessage);
							continue;
						}
					}
					
					// Default
					recipientPlayer.sendMessage(neutralMessage);
				}
				
				event.getRecipients().clear(); // Siccome abbiamo mandato i messaggi manualmente
				
			}
				
			event.setFormat(event.getFormat().replace("[TOWN]", formattedTownTag)); // Di default e per la console
		}
	}

}
