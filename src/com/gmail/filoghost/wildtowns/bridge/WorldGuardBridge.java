package com.gmail.filoghost.wildtowns.bridge;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardBridge {
	
	private static boolean useWorldGuard;
	
	public static boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            return false;
        }
        
        useWorldGuard = true;
        return true;
    }
	
	public static boolean isOverlappingRegions(Chunk chunk) {
		if (!useWorldGuard) {
			return false;
		}
		
		try {
			ProtectedRegion chunkRegion = BukkitUtil.toRegion(chunk);
			RegionManager regionManager = WGBukkit.getRegionManager(chunk.getWorld());
		
			if (regionManager == null) {
				// null = WorldGuard disabilitato in quel mondo
				return false;
			}
			
			return regionManager.getApplicableRegions(chunkRegion).size() > 0;
			
		} catch (Throwable t) {
			WildTowns.logError(Level.SEVERE, "Could not check if chunk " + chunk + " overlaps with WorldGuard region", t);
			return true; // Nel dubbio...
		}
	}
}
