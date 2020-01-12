package com.gmail.filoghost.wildtowns.listener.protection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class ProtectionCommandListener implements Listener {
	
	@EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Location location = event.getPlayer().getLocation();
		if (!Settings.isTownWorld(location)) {
			return;
		}
		
		String command = event.getMessage().substring(1).split(" ", 2)[0].toLowerCase();
		if (!Settings.disabledCommandsInsideNonAllies.contains(command)) {
			return;
		}
		
		Plot locationPlot = WTManager.getPlot(ChunkCoords.of(location));
		if (locationPlot == null) {
			return;
		}
		
		Town locationTown = locationPlot.getTown();
		Town senderTown = WTManager.getOnlineResident(event.getPlayer()).getTown();
		
		if (senderTown == null || (senderTown != locationTown && !senderTown.isReciprocalAlly(locationTown))) {
			if (senderTown == null) {
				event.getPlayer().sendMessage(ChatColor.RED + "Non puoi usare questo comando nei territori delle città.");
			} else {
				event.getPlayer().sendMessage(ChatColor.RED + "Non puoi usare questo comando nei territori di città non alleate.");
			}
			event.setCancelled(true);
		}
	}
	
}
