package com.gmail.filoghost.wildtowns.listener.protection;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.gmail.filoghost.wildtowns.bridge.PetsBridge;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.listener.protection.ProtectionManager.Action;

public class ProtectionInventoryListener implements Listener {
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent event) {
		Inventory inventory = event.getInventory();
		
		if (!Settings.containerInventoryTypes.contains(inventory.getType())) {
			return;
		}
		
		InventoryHolder holder = inventory.getHolder();
		if (holder != null) {
			
			if (holder instanceof DoubleChest) {
				DoubleChest doubleChest = (DoubleChest) holder;
				Block left = ((BlockState) doubleChest.getLeftSide()).getBlock();
				Block right = ((BlockState) doubleChest.getRightSide()).getBlock();
				if (Settings.isTownWorld(left)) {
					ProtectionManager.processAction(event, (Player) event.getPlayer(), Action.OPEN_CONTAINER, left);
					ProtectionManager.processAction(event, (Player) event.getPlayer(), Action.OPEN_CONTAINER, right);
				}
				
			} else if (holder instanceof Entity) {
				if (PetsBridge.isPetInventory(holder, inventory.getType(), inventory.getTitle())) {
					return;
				}
				Location location = ((Entity) holder).getLocation();
				if (Settings.isTownWorld(location)) {
					ProtectionManager.processAction(event, (Player) event.getPlayer(), Action.OPEN_CONTAINER, location);
				}
				
			} else if (holder instanceof BlockState) {
				Block block = ((BlockState) holder).getBlock();
				if (Settings.isTownWorld(block)) {
					ProtectionManager.processAction(event, (Player) event.getPlayer(), Action.OPEN_CONTAINER, block);
				}
			}
		}
	}

}
