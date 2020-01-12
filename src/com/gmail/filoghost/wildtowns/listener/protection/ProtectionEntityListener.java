package com.gmail.filoghost.wildtowns.listener.protection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.listener.protection.ProtectionManager.Action;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.Utils;

import wild.api.WildCommons;

/**
 * Nota generale: solo le entità decorative (armor stand, cornici, quadri, etc.) devono essere protette.
 */
public class ProtectionEntityListener implements Listener {
	
	// TODO flag per bloccare lo spawn dei mob?
//	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
//	public void onCreatureSpawn(CreatureSpawnEvent event) {
//		if (isHostile(event.getEntity()) && Settings.isTownWorld(event.getLocation())) {
//			Plot plot = WTManager.getPlot(ChunkCoords.of(event.getEntity().getLocation()));
//			if (plot != null) {
//				event.setCancelled(true);
//			}
//		}
//	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.CHORUS_FRUIT) {
			if (isInsideTown(event.getFrom()) || isInsideTown(event.getTo())) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(Lang.cantUseChorusFruit);
			}
		}
	}
	
	private boolean isInsideTown(Location loc) {
		return Settings.isTownWorld(loc) && ProtectionManager.isChunkProtected(ChunkCoords.of(loc));
	}
	
	private boolean isInsideTown(Block block) {
		return Settings.isTownWorld(block) && ProtectionManager.isChunkProtected(ChunkCoords.of(block));
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		EntityType entityType = event.getEntityType();
		Material materialType = event.getBlock().getType();

		if (entityType == EntityType.WITHER || 		// Esplosioni Wither
			entityType == EntityType.ENDERMAN || 	// Raccolta di blocchi degli enderman
			materialType == Material.SOIL || 		// Salti sulle coltivazioni (TODO: non funziona con i conigli, aggiornare)
			//materialType == Material.WATER_LILY || 	// Barche che distruggono le ninfee (TODO: disabilitato per bug visivo, sul client sembrano distrutte)
			materialType == Material.TNT || 		// Tnt che vengono accese con le frecce
			materialType == Material.FIRE) { 		// Fuoco che viene spento dalle pozioni
			
			if (isInsideTown(event.getBlock())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerFish(PlayerFishEvent event) {
		if (event.getState() == State.CAUGHT_ENTITY) {
			Entity caught = event.getCaught();
			if (caught != null && isTownProtected(caught)) {
				if (!checkEntityEdit(event, event.getPlayer(), caught)) {
					WildCommons.removeFishingHook(event.getHook());
				}
			}
		}
	}

	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (Settings.isTownWorld(event.getLocation())) {
			ProtectionManager.filterTownBlocks(event.blockList());
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.VOID && isTownProtected(event.getEntity())) {
			checkEntityEdit(event, null, event.getEntity());
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		// Questo evento viene chiamato al posto di EntityDamageEvent
		checkEntityEdit(event, null, event.getVehicle());
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHangingBreak(HangingBreakEvent event) {
		// Questo evento viene chiamato al posto di EntityDamageEvent
		if (event.getCause() != RemoveCause.OBSTRUCTION && event.getCause() != RemoveCause.PHYSICS) {
			checkEntityEdit(event, null, event.getEntity());
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHangingPlace(HangingPlaceEvent event) {
		checkEntityEdit(event, event.getPlayer(), event.getEntity());
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		EntityType type = event.getRightClicked().getType();
		if (type == EntityType.ITEM_FRAME) {
			// Previene la rotazione
			checkEntityEdit(event, event.getPlayer(), event.getRightClicked());
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityInteractAt(PlayerInteractAtEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
			// Previene la modifica dell'equipaggiamento
			checkEntityEdit(event, event.getPlayer(), event.getRightClicked());
		}
	}
	
	private boolean checkEntityEdit(Cancellable event, Player player, Entity modifiedEntity) {
		Location location = modifiedEntity.getLocation();
		
		if (!Settings.isTownWorld(location)) {
			return true; // Tutto ok
		}
		
		ChunkCoords coords = ChunkCoords.of(location);
		Plot plot = WTManager.getPlot(coords);
		if (plot != null || coords.isColony()) {
			if (player == null) {
				if (event instanceof EntityDamageEvent) {
					if (event instanceof EntityDamageByEntityEvent) {
						player = Utils.getRootPlayerAttacker(((EntityDamageByEntityEvent) event).getDamager());
					}
				} else if (event instanceof HangingBreakEvent) {
					if (event instanceof HangingBreakByEntityEvent) {
						player = Utils.getRootPlayerAttacker(((HangingBreakByEntityEvent) event).getRemover());
					}
				} else if (event instanceof VehicleDestroyEvent) {
					player = Utils.getRootPlayerAttacker(((VehicleDestroyEvent) event).getAttacker());
				} else {
					throw new IllegalArgumentException("Unhandled event type: " + event.getClass().getSimpleName());
				}
			}
			
			if (player == null) {
				if (isTownProtected(modifiedEntity)) {
					// Qualunque causa naturale viene bloccata (esempi: scheletri che sparano frecce, tnt, creeper, etc)
					event.setCancelled(true);
					return false;
				} else {
					// Le altre entità non sono protette dai danni
					return true;
				}
			}
			
			return ProtectionManager.processAction(event, player, Action.BUILD, plot, coords, false);
		}
		
		return true; // Tutto ok
	}
	
	private boolean isTownProtected(Entity entity) {
		switch (entity.getType()) {
			case ARMOR_STAND:
			case ITEM_FRAME:
			case PAINTING:
			case MINECART_CHEST:
			case MINECART_FURNACE:
			case MINECART_HOPPER:
			case MINECART_TNT:
				return true;
			default:
				return false;
		}
	}
	
//	private boolean isHostile(Entity entity) {
//		EntityType type = entity.getType();
//		return entity instanceof Monster ||
//				type == EntityType.GHAST ||
//				type == EntityType.MAGMA_CUBE ||
//				type == EntityType.SLIME;
//	}

}
