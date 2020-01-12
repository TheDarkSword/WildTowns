package com.gmail.filoghost.wildtowns.disk;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.cubespace.yamler.PreserveStatic;
import net.cubespace.yamler.YamlerConfig;
import net.cubespace.yamler.YamlerConfigurationException;
import wild.api.math.MathExpression;
import wild.api.math.MathExpression.MathParseException;

@PreserveStatic
public class Settings extends YamlerConfig {
	
	public static String townsWorld = "world";
	
	public static int economy_newTown = 10000;
	public static int economy_newTownBank = 1000;
	public static int economy_claim = 1000;
	public static int economy_outpost = 5000;
	public static int economy_warp = 1000;
	public static int economy_sethome = 1000;
	public static int economy_addAlly = 1000;
	public static String economy_townTaxFormula_plots = "plots * 10";
	public static String economy_townTaxFormula_outposts = "outposts * 10";
	public static String economy_townTaxFormula_residents = "0";
	
	public static int economy_maxTaxPerPlot = 50;
	public static int economy_maxTaxPerResident = 50;
	public static int economy_maxPlotPrice = 1000;
	
	public static String town_name_regex = "[a-zA-Z]+";
	public static int town_name_minLength = 3;
	public static int town_name_maxLength = 12;
	
	public static int town_minDistanceChunk_newTown = 5;
	public static int town_minDistanceChunk_claim = 1;
	public static int town_minDistanceChunk_outpost = 5;
	
	public static int town_warpsLimit = 5;
	public static int town_allyLimit = 10;
	public static int town_enemyLimit = 25;
	public static int town_friendsLimit = 50;
	public static String town_valueFormula = "plots * 10 + outposts * 30 + residents * 10";
	
	public static int inviteExpirationSeconds = 60;
	
	public static int newCityProtectionDays = 30;
	
	public static int taxes_collectHour = 15;
	public static int taxes_warnOnPossibleCollectAmountsLessEqualThan = 3;
	
	public static Set<Material> interactBuildMaterials = Sets.newHashSet(Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON);
	public static Set<Material> useMaterials = Sets.newHashSet(Material.ACACIA_DOOR);
	public static Set<InventoryType> containerInventoryTypes = Sets.newHashSet(InventoryType.CHEST);
	public static Set<Material> containerMaterials = Sets.newHashSet(Material.CHEST);
	
	public static Set<Material> checkPlaceAsBlocksMaterials = Sets.newHashSet(Material.BOAT, Material.ARMOR_STAND);
	
	public static List<String> pvpBetweenResidentsWorlds = Lists.newArrayList("world_pvp_all");
	
	public static List<String> disabledCommandsInsideNonAllies = Lists.newArrayList("disabled_command");
	
	public static boolean chatRelationColor = true;
	
	public static boolean lava_restrictToTowns = false;
	public static boolean lava_disableFlow = false;

	
	public Settings(Plugin plugin, String filename) {
		super(plugin, filename);
	}
	
	public static boolean isTownWorld(Location location) {
		return isTownWorld(location.getWorld());
	}
	
	public static boolean isTownWorld(Block block) {
		return isTownWorld(block.getWorld());
	}
	
	public static boolean isTownWorld(World world) {
		return world.getName().equals(townsWorld);
	}
	
	public static boolean isTownWorld(String worldName) {
		return worldName.equals(townsWorld);
	}

	@Override
	public void init() throws YamlerConfigurationException {
		super.init();
		
		checkFormula(Settings.economy_townTaxFormula_plots);
		checkFormula(Settings.economy_townTaxFormula_outposts);
		checkFormula(Settings.economy_townTaxFormula_residents);
		checkFormula(Settings.town_valueFormula);
	}
	
	private void checkFormula(String expression) throws YamlerConfigurationException {
		try {
			int[] testValues = {0, 1, 10, 100};
			for (int testValue : testValues) {
				Map<String, Number> variables = Maps.newHashMap();
				variables.put("plots", testValue);
				variables.put("outposts", testValue);
				variables.put("residents", testValue);
				variables.put("allies", testValue);
				MathExpression.eval(expression, variables);
			}
		} catch (MathParseException ex) {
			throw new YamlerConfigurationException("Formula non valida in config.yml: " + expression, ex);
		}
	}
	
}
