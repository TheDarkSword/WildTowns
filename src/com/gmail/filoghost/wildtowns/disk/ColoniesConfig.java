package com.gmail.filoghost.wildtowns.disk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;

public class ColoniesConfig {
	
	
	private static Set<ChunkCoords> coloniesChunks;
	
	
	public static boolean isColonyChunk(ChunkCoords chunkCoords) {
		return coloniesChunks.contains(chunkCoords);
	}
	
	public static void setColonyChunk(ChunkCoords chunkCoords) {
		coloniesChunks.add(chunkCoords);
	}
	
	public static void removeColonyChunk(ChunkCoords chunkCoords) {
		coloniesChunks.remove(chunkCoords);
	}
	
	public static void load() throws Exception {
		PluginConfig config = new PluginConfig(WildTowns.getInstance(), WildTowns.getColoniesFile());
		config.load();
		
		Set<ChunkCoords> coloniesChunks = new HashSet<>();
		
		for (String serializedChunkCoords : config.getStringList("colonies-chunks")) {
			String[] parts = serializedChunkCoords.split(",");
			int x = Integer.parseInt(parts[0].trim());
			int z = Integer.parseInt(parts[1].trim());
			
			coloniesChunks.add(ChunkCoords.of(x, z));
		}
		
		ColoniesConfig.coloniesChunks = coloniesChunks;
	}
	
	public static void save() throws IOException {
		YamlConfiguration config = new YamlConfiguration();
		
		List<String> serializedChunks = new ArrayList<>();
		
		for (ChunkCoords chunkCoords : coloniesChunks) {
			serializedChunks.add(chunkCoords.getX() + ", " + chunkCoords.getZ());
		}
		
		config.set("colonies-chunks", serializedChunks);		
		config.save(WildTowns.getColoniesFile());
	}

}
