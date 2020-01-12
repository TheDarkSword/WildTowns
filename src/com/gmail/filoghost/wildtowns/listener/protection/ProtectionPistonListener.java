package com.gmail.filoghost.wildtowns.listener.protection;

import java.util.List;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.google.common.collect.Sets;

public class ProtectionPistonListener implements Listener {
	
	/**
	 * Con gli eventi dei pistoni non si capisce chi l'ha mandato.
	 * Si controlla solo se tutti i chunk affetti sono dello stessa città, o tutti wilderness.
	 * Se sono diversi si cancella l'evento. Anche se due città alleate fossero vicine, gli alleati non possono costruire lì.
	 */
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPistonExtend(BlockPistonExtendEvent event) {
		if (Settings.isTownWorld(event.getBlock())) {
			onPiston(event, true, event.getBlocks());
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPistonRetract(BlockPistonRetractEvent event) {
		if (Settings.isTownWorld(event.getBlock())) {
			onPiston(event, false, event.getBlocks());
		}
	}
	
	/**
	 * extend = true -> piston extend
	 * extend = false -> piston retract
	 */
	private void onPiston(BlockPistonEvent event, boolean extend, List<Block> blocks) {
		ChunkCoords pistonChunk = ChunkCoords.of(event.getBlock()); // Poco intuitivo, getBlock() è il pistone
		Set<ChunkCoords> affectedChunks = Sets.newHashSet();
		
		for (Block block : blocks) {
			ChunkCoords from = ChunkCoords.of(block);
			ChunkCoords to = ChunkCoords.of(block.getRelative(event.getDirection()));
			
			if (!from.equals(pistonChunk)) {
				affectedChunks.add(from);
			}
			if (!to.equals(pistonChunk)) {
				affectedChunks.add(to);
			}
		}
		
		// Il blocco che occuperà il pistone, se si estende. Se si ritrae, si compatta da 2x1 a 1x1 blocchi.
		if (extend) {
			ChunkCoords pistonTo = ChunkCoords.of(event.getBlock().getRelative(event.getDirection()));
			
			if (!pistonTo.equals(pistonChunk)) {
				affectedChunks.add(pistonTo);
			}
		}
		
		for (ChunkCoords otherChunk : affectedChunks) {
			if (!ProtectionManager.canBlockMove(pistonChunk, otherChunk)) {
				event.setCancelled(true);
				return;
			}
		}
	}

}
