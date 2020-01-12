package com.gmail.filoghost.wildtowns.bridge;

import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.google.common.collect.Maps;

public class DynmapBridge {
	
	private static boolean useDynmap;
	private static MarkerSet markerSet;
	private static MarkerIcon markerIcon;
	private static Map<String, Marker> townMarkers;
	

	public static boolean setup() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("dynmap");

        if (plugin != null) {
        	MarkerAPI markerAPI = ((DynmapAPI) plugin).getMarkerAPI();
        	markerIcon = markerAPI.getMarkerIcon("purpleflag");
        	markerSet = markerAPI.getMarkerSet("wildtowns.home");
        	if (markerSet != null) {
        		markerSet.deleteMarkerSet(); // In questo modo è aggiornato se cambiamo le impostazioni sotto
        	}
        	markerSet = markerAPI.createMarkerSet("wildtowns.home", "Home delle città", null, true);
        	townMarkers = Maps.newHashMap();
        	useDynmap = true;
            return true;
        } else {
        	return false;
        }
    }

	public static void updateTownHome(Town town, Location home) {
		if (!useDynmap) {
			return;
		}
		
		try {
			String markerID = town.getName();
			Marker marker = townMarkers.get(markerID);
			
			if (home != null) {
				if (marker != null) {
					marker.setLocation(Settings.townsWorld, home.getX(), home.getY(), home.getZ());
				} else {
					marker = markerSet.createMarker(markerID, town.getName(), Settings.townsWorld, home.getX(), home.getY(), home.getZ(), markerIcon, true);
					townMarkers.put(markerID, marker);
				}
			} else {
				if (marker != null) {
					marker.deleteMarker();
					townMarkers.remove(markerID);
				}
			}
		} catch (Throwable t) {
			WildTowns.logError(Level.SEVERE, "Could not update home marker for " + town + " with new location: " + home, t);
		}
	}
	
}
