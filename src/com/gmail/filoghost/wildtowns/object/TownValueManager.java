package com.gmail.filoghost.wildtowns.object;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.Utils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import wild.api.math.MathExpression;
import wild.api.math.MathExpression.MathParseException;

public class TownValueManager {
	
	private static LinkedHashMap<Town, Long> sortedTownValues;
	
	
	public static void update() {
		List<Entry<Town, Long>> entries = Lists.newArrayList();
		for (Town town : WTManager.getTowns()) {
			long value = calculateValue(town);
			entries.add(new SimpleEntry<>(town, value));
		}
		
		Collections.sort(entries, (entry1, entry2) -> {
			long diff = entry2.getValue() - entry1.getValue();
			if (diff == 0) {
				diff = entry1.getKey().getCreationTimestamp() - entry2.getKey().getCreationTimestamp();
			}
			if (diff == 0) {
				diff = entry2.getKey().getID() - entry1.getKey().getID();
			}
			return Utils.signum(diff);
		});
		
		sortedTownValues = new LinkedHashMap<>();
		
		for (Entry<Town, Long> entry : entries) {
			sortedTownValues.put(entry.getKey(), entry.getValue());
		}
	}
	
	public static long getValue(Town town) {
		Long value = sortedTownValues.get(town);
		return value != null ? value : 0;
	}
	
	public static List<Entry<Town, Long>> getTop(int from, int to) {
		List<Entry<Town, Long>> allTowns = Lists.newArrayList(sortedTownValues.entrySet());
		return allTowns.subList(Math.min(from, allTowns.size()), Math.min(to, allTowns.size()));
	}
	
	public static void delete(Town town) {
		sortedTownValues.remove(town);
	}
	
	private static long calculateValue(Town town) {
		int totalPlots = town.getPlotsCount();
		int outpostPlots = totalPlots - town.getHomeGroupPlotsCount();
		int residents = town.getResidentsCount();
		
		Map<String, Number> variables = Maps.newHashMap();
		variables.put("plots", totalPlots);
		variables.put("outposts", outpostPlots);
		variables.put("residents", residents);
		try {
			return (long) MathExpression.eval(Settings.town_valueFormula, variables);
		} catch (MathParseException e) {
			WildTowns.logError(Level.SEVERE, "Could calculate value for town " + town + ".", e);
			return 0;
		}
	}
	

}
