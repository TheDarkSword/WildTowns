package com.gmail.filoghost.wildtowns.disk;

import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

public class MapConfiguration extends YamlConfiguration {
	
	public MapConfiguration(Map<?, ?> map) {
		convertMapsToSections(map, this);
	}

}
