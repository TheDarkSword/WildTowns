package com.gmail.filoghost.wildtowns.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.listener.protection.ProtectionManager;
import com.gmail.filoghost.wildtowns.listener.protection.ProtectionManager.Action;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class WildTownsAPI {
	
	public static boolean canModifyBlock(Player player, Block block) {
		if (!Settings.isTownWorld(block)) {
			return true;
		}
		
		ChunkCoords coords = ChunkCoords.of(block);
		Plot plot = WTManager.getPlot(coords);
		
		String buildDenyMessage = ProtectionManager.getActionErrorMessage(player, Action.BUILD, plot, coords);
		return buildDenyMessage == null;
	}

}
