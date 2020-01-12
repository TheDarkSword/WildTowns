package com.gmail.filoghost.wildtowns.object.base;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.util.Validate;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import wild.api.util.CaseInsensitiveMap;
import wild.api.uuid.UUIDRegistry;


public class WTManager {
	
	@Getter	private static Map<Player, Resident> onlineResidentsMap = Maps.newConcurrentMap();
	
	// Dati salvati
	private static Map<UUID, Resident> residentsMap = Maps.newHashMap();
	private static Map<Integer, Town> townsIDsMap = Maps.newHashMap();
	private static Map<String, Town> townsNamesMap = new CaseInsensitiveMap<>();
	private static Map<ChunkCoords, Plot> plotsMap = Maps.newHashMap();

	public static Resident getOnlineResident(@NonNull Player player) {
		return onlineResidentsMap.get(player);
	}
	
	public static Resident getOfflineResident(@NonNull UUID uuid) {
		return residentsMap.get(uuid);
	}
	
	public static Resident getOfflineResident(@NonNull String name) {
		UUID uuid = UUIDRegistry.getUUID(name);
		if (uuid != null) {
			return getOfflineResident(uuid);
		} else {
			return null;
		}
	}
	
	public static Town getTown(@NonNull String name) {
		return townsNamesMap.get(name);
	}
	
	public static Town getTown(int id) {
		return townsIDsMap.get(id);
	}
	
	public static Plot getPlot(ChunkCoords coords) {
		Plot plot = plotsMap.get(coords);
		if (plot != null) {
			Validate.notNull(plot.town, "Plot has no town!");
		}
		return plot;
	}
	
	public static Collection<Resident> getOnlineResidents() {
		return Collections.unmodifiableCollection(onlineResidentsMap.values());
	}
	
	public static Collection<Resident> getOfflineResidents() {
		return Collections.unmodifiableCollection(residentsMap.values());
	}
	
	public static Collection<Town> getTowns() {
		return Collections.unmodifiableCollection(townsIDsMap.values());
	}
	
	public static Collection<Plot> getPlots() {
		return Collections.unmodifiableCollection(plotsMap.values());
	}
	
	// Registrazione
	public static void register(@NonNull Resident resident) {
		Validate.isTrue(!residentsMap.containsKey(resident.getUUID()), "Resident " + resident + " is already registered");
		residentsMap.put(resident.getUUID(), resident);
	}
	
	public static void register(@NonNull Town town) {
		Validate.isTrue(!townsIDsMap.containsKey(town.getID()), "ID " + town.getID() + " of town " + town + " is already registered");
		Validate.isTrue(!townsNamesMap.containsKey(town.getName()), "Name of town " + town + " is already registered");
		townsIDsMap.put(town.getID(), town);
		townsNamesMap.put(town.getName(), town);
	}
	
	public static int getNextTownID() {
		int id = 0;
		for (Integer key : townsIDsMap.keySet()) {
			if (id <= key) {
				id = key + 1;
			}
		}
		return id;
	}
	
	public static void rename(@NonNull Town town, @NonNull String oldName, @NonNull String newName) {
		Validate.isTrue(townsNamesMap.containsKey(oldName), "Old name " + oldName + " is not registered");
		Validate.isTrue(!townsNamesMap.containsKey(newName), "New name " + newName + " is already registered");
		townsNamesMap.remove(oldName);
		townsNamesMap.put(newName, town);
	}

	public static void register(@NonNull Plot plot) {
		Validate.isTrue(!plotsMap.containsKey(plot.getChunkCoords()), "Plot " + plot.getChunkCoords() + " is already registered");
		plotsMap.put(plot.getChunkCoords(), plot);
	}
	
	public static void unregister(@NonNull Town town) {
		Validate.isTrue(townsIDsMap.containsKey(town.getID()), "ID " + town.getID() + " of town " + town + " is not registered");
		Validate.isTrue(townsNamesMap.containsKey(town.getName()), "Name of town " + town + " is not registered");
		townsIDsMap.remove(town.getID());
		townsNamesMap.remove(town.getName());
	}
	
	public static void unregister(@NonNull Plot plot) {
		Validate.isTrue(plotsMap.containsKey(plot.getChunkCoords()), "Plot " + plot.getChunkCoords() + " is not registered");
		plotsMap.remove(plot.getChunkCoords());
	}
	
	public static void consistencyCheck() {
		for (Town town : townsIDsMap.values()) {
			Validate.isTrue(townsNamesMap.containsValue(town), "Town " + town + " has registered ID but not name");
		}
		for (Town town : townsNamesMap.values()) {
			Validate.isTrue(townsIDsMap.containsValue(town), "Town " + town + " has registered name but not ID");
		}
		
		for (val entry : residentsMap.entrySet()) {
			UUID key = entry.getKey();
			Resident resident = entry.getValue();
			Validate.isTrue(key.equals(resident.getUUID()), "Resident " + resident + " was mapped as " + key);
			if (resident.town != null) {
				Validate.isTrue(resident.town.residentsMap.containsKey(resident), "Resident " + resident + " has a town, but town " + resident.town + " has not the resident");
				Validate.isTrue(townsIDsMap.values().contains(resident.town), "Resident " + resident + " has a town, but ID of town " + resident.town + " is not registered");
				Validate.isTrue(townsNamesMap.values().contains(resident.town), "Resident " + resident + " has a town, but name of town " + resident.town + " is not registered");
			}
		}
		
		for (val entry : plotsMap.entrySet()) {
			ChunkCoords key = entry.getKey();
			Plot plot = entry.getValue();
			Validate.isTrue(key.equals(plot.getChunkCoords()), "Plot " + plot.getChunkCoords() + " was mapped as " + key);
			Validate.notNull(plot.town, "Plot " + plot.getChunkCoords() + " has no town");
			Validate.isTrue(plot.town.plotsMap.containsKey(plot.getChunkCoords()), "Plot " + plot.getChunkCoords() + " has a town, but town " + plot.town + " has not the plot");
			Validate.isTrue(plot.town.plotsMap.get(plot.getChunkCoords()) == plot, "Plot " + plot.getChunkCoords() + " has a town, but town " + plot.town + " has a different plot");
			Validate.isTrue(townsIDsMap.values().contains(plot.town), "Plot " + plot.getChunkCoords() + " has a town, but ID of town " + plot.town + " is not registered");
			Validate.isTrue(townsNamesMap.values().contains(plot.town), "Plot " + plot.getChunkCoords() + " has a town, but name of town " + plot.town + " is not registered");
			if (plot.getOwner() != null) {
				Validate.isTrue(plot.town.residentsMap.containsKey(plot.getOwner()), "Plot " + plot.getChunkCoords() + " has owner " + plot.getOwner() + ", but town " + plot.town + " has not the owner");
			}
		}
		
		for (val entry : townsIDsMap.entrySet()) {
			int key = entry.getKey();
			Town town = entry.getValue();
			Validate.isTrue(key == town.getID(), "Town " + town + " was mapped as " + key);
			for (Resident resident : town.residentsMap.keySet()) {
				Validate.isTrue(residentsMap.values().contains(resident), "Town " + town + " has resident " + resident + " that is not registered");
				Validate.isTrue(town == resident.town, "Town " + town + " has resident " + resident + " that has different town (" + resident.town + ")");
			}
			for (Plot plot : town.plotsMap.values()) {
				Validate.isTrue(plotsMap.values().contains(plot), "Town " + town + " has plot " + plot.getChunkCoords() + " that is not registered");
				Validate.isTrue(town == plot.town, "Town " + town + " has plot " + plot.getChunkCoords() + " that has different town (" + plot.town + ")");
			}
			Validate.notNull(town.mayor, "Town " + town + " has no mayor");
			Validate.isTrue(town.residentsMap.containsKey(town.mayor), "Town " + town + " has mayor (" + town.mayor + ") that is not in town");
		}
		
		for (val entry : townsNamesMap.entrySet()) {
			String key = entry.getKey();
			Town town = entry.getValue();
			Validate.isTrue(key.equalsIgnoreCase(town.getName()), "Town " + town + " was mapped as " + key);
		}
	}
	
}
