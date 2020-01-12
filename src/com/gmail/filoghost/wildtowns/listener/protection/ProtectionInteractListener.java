package com.gmail.filoghost.wildtowns.listener.protection;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminBypassCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.listener.protection.ProtectionManager.Action;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;

public class ProtectionInteractListener implements Listener {
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onForm(EntityBlockFormEvent event) {
		if (event.getNewState().getType() == Material.FROSTED_ICE) {
			if (event.getEntity() instanceof Player) {
				if (Settings.isTownWorld(event.getBlock())) {
					ProtectionManager.processAction(event, (Player) event.getEntity(), Action.BUILD, event.getBlock(), true);
				}
			} else {
				ProtectionManager.cancelInsideTown(event, event.getBlock());
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		if (Settings.isTownWorld(event.getBlock())) {
			ProtectionManager.processAction(event, event.getPlayer(), Action.BUILD, event.getBlock());
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		if (Settings.isTownWorld(event.getBlock())) {
			ProtectionManager.processAction(event, event.getPlayer(), Action.BUILD, event.getBlock());
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		Block newLiquidBlock = event.getBlockClicked().getRelative(event.getBlockFace());
		if (Settings.lava_restrictToTowns && event.getBucket() == Material.LAVA_BUCKET && !isInsideTown(newLiquidBlock) && !AdminBypassCommand.hasBypass(event.getPlayer())) {
			event.getPlayer().sendMessage(Lang.color_error + "Puoi posizionare lava solo all'interno delle città.");
			event.setCancelled(true);
			return;
		}
		
		if (Settings.isTownWorld(newLiquidBlock)) {
			ProtectionManager.processAction(event, event.getPlayer(), Action.BUILD, newLiquidBlock);
		}
	}
	
	private boolean isInsideTown(Block block) {
		return Settings.isTownWorld(block) && ProtectionManager.isChunkProtected(ChunkCoords.of(block));
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBucketFill(PlayerBucketFillEvent event) {
		if (Settings.isTownWorld(event.getBlockClicked())) {
			ProtectionManager.processAction(event, event.getPlayer(), Action.BUILD, event.getBlockClicked());
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (event.hasBlock() && Settings.isTownWorld(event.getClickedBlock())) {
			Material type = event.getClickedBlock().getType();
			
			if (Settings.useMaterials.contains(type)) {
				ProtectionManager.processAction(event, event.getPlayer(), Action.USE, event.getClickedBlock());
			} else if (Settings.containerMaterials.contains(type)) {
				ProtectionManager.processAction(event, event.getPlayer(), Action.OPEN_CONTAINER, event.getClickedBlock());
			} else if (Settings.interactBuildMaterials.contains(type)) {
				ProtectionManager.processAction(event, event.getPlayer(), Action.BUILD, event.getClickedBlock());
			}
			
			if (event.hasItem()) {
				if (Settings.checkPlaceAsBlocksMaterials.contains(event.getItem().getType())) {
					ProtectionManager.processAction(event, event.getPlayer(), Action.BUILD, event.getClickedBlock().getRelative(event.getBlockFace()));
				}
			}
		}
	}

}
