package com.gmail.filoghost.wildtowns.object;

import lombok.Getter;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.gmail.filoghost.wildtowns.disk.ColoniesConfig;
import com.gmail.filoghost.wildtowns.disk.Settings;

public final class ChunkCoords {

	@Getter	private final int x, z;
	private final int hashcode;

	private ChunkCoords(int x, int z) {
		this.x = x;
		this.z = z;
		this.hashcode = (x << 16) | z;
	}
	
	private static void checkWorld(World world) {
		if (!Settings.isTownWorld(world)) {
			throw new IllegalArgumentException("Not town world");
		}
	}
	
	public static ChunkCoords of(int x, int z) {
		return new ChunkCoords(x, z);
	}
	
	public static ChunkCoords of(Location location) {
		checkWorld(location.getWorld());
		return new ChunkCoords(location.getBlockX() >> 4, location.getBlockZ() >> 4);
	}
	
	public static ChunkCoords of(Block block) {
		checkWorld(block.getWorld());
		return new ChunkCoords(block.getX() >> 4, block.getZ() >> 4);
	}
	
	public static ChunkCoords of(Chunk chunk) {
		checkWorld(chunk.getWorld());
		return new ChunkCoords(chunk.getX(), chunk.getZ());
	}
	
	public boolean containsLocation(Location location) {
		ChunkCoords locationCoords = of(location);
		return this.x == locationCoords.x && this.z == locationCoords.z;
	}
	
	public boolean containsBlock(Block block) {
		ChunkCoords blockCoords = of(block);
		return this.x == blockCoords.x && this.z == blockCoords.z;
	}
	
	public boolean isAdjacent(ChunkCoords other) {
		if (this.x == other.x) {
			int zDiff = this.z - other.z;
			if (-1 <= zDiff && zDiff <= 1) {
				return true;
			}
		}
		if (this.z == other.z) {
			int xDiff = this.x - other.x;
			if (-1 <= xDiff && xDiff <= 1) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isColony() {
		return ColoniesConfig.isColonyChunk(this);
	}
	
	@Override
	public int hashCode() {
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChunkCoords) {
			ChunkCoords other = (ChunkCoords) obj;
			return this.x == other.x && this.z == other.z;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return x + ", " + z;
	}
	
}
