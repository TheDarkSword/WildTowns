package com.gmail.filoghost.wildtowns.bridge;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public class PetsBridge {
	
	public static boolean isPetInventory(InventoryHolder holder, InventoryType inventoryType, String title) {
		return 
				holder instanceof Player && 
				inventoryType == InventoryType.CHEST && 
				title.equals("EchoPet DataMenu");
	}

}
