package com.gmail.filoghost.wildtowns.listener.protection;

import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;

public class ProtectionWorldListener implements Listener {
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent event) {
		ProtectionManager.cancelInsideTown(event, event.getBlock());
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onFireSpread(BlockSpreadEvent event) {
		if (event.getNewState().getType() == Material.FIRE) {
			if (ProtectionManager.cancelInsideTown(event, event.getBlock())) {
				if (event.getSource().getRelative(BlockFace.DOWN).getType() != Material.NETHERRACK) {
					// In questo modo si estingue sempre il fuoco (altrimenti si accumula)
					event.getSource().setType(Material.AIR);
				}
			}
		}
	}

	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockExplode(BlockExplodeEvent event) {
		if (Settings.isTownWorld(event.getBlock())) {
			ProtectionManager.filterTownBlocks(event.blockList());
		}
	}
	
	// Implementazione di WorldGuard
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getTo() == null) return; // Apparently this counts as a cancelled event, implementation specific though
        if (!Settings.isTownWorld(event.getTo())) return;
        if (event.getCause() != TeleportCause.NETHER_PORTAL) return;
        if (!event.useTravelAgent()) return; // Either end travel (even though we checked cause) or another plugin is fucking with us, shouldn't create a portal though

        TravelAgent pta = event.getPortalTravelAgent();
        if (pta == null) return; // Possible, but shouldn't create a portal
        if (pta.findPortal(event.getTo()) != null) return; // Portal exists... It shouldn't make a new one
        
        event.setCancelled(true);
        event.getPlayer().sendMessage(Lang.color_error + "Non puoi creare portali nel Nether verso il mondo città, devi utilizzare quelli esistenti o crearli nel mondo città.");
	}
	
}
