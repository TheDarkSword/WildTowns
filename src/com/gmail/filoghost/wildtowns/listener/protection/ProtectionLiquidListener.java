package com.gmail.filoghost.wildtowns.listener.protection;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;

public class ProtectionLiquidListener implements Listener {
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onLiquid(BlockFromToEvent event) {
		if (Settings.isTownWorld(event.getBlock())) {
			if (Settings.lava_disableFlow && isLava(event.getBlock().getType())) {
				event.setCancelled(true);
				return;
			}
		
			if (event.getBlock().getChunk() != event.getToBlock().getChunk()) {
				// Chunk (e quindi plot) cambiato
				
				ChunkCoords fromChunk = ChunkCoords.of(event.getBlock());
				ChunkCoords toChunk = ChunkCoords.of(event.getToBlock());
				
				if (!ProtectionManager.canBlockMove(fromChunk, toChunk)) {
					event.setCancelled(true);
				}
			}
		}
	}

	private boolean isLava(Material type) {
		return type == Material.STATIONARY_LAVA || type == Material.LAVA;
	}

}
