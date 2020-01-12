package com.gmail.filoghost.wildtowns.object.base;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.bridge.DynmapBridge;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.MapConfiguration;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.Invite;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.TownSizeManager;
import com.gmail.filoghost.wildtowns.object.TownSizeManager.TownSizeData;
import com.gmail.filoghost.wildtowns.object.TownValueManager;
import com.gmail.filoghost.wildtowns.util.Validate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import wild.api.math.MathExpression;
import wild.api.math.MathExpression.MathParseException;
import wild.api.util.CaseInsensitiveMap;
import wild.api.util.LocationSerializer;
import wild.api.util.LocationSerializer.WorldNotFoundException;

public final class Town extends Saveable {
	
	private final int id;
	
	@Getter private String name;
	@Getter protected Resident mayor; // È comunque nella lista residenti
	protected final Map<Resident, ResidentTownData> residentsMap;
	protected Map<ChunkCoords, Plot> plotsMap;
	private Set<ChunkCoords> homeGroupPlots;
	private Map<String, Location> warpsMap;
	protected Set<Town> allies;
	protected Set<Town> enemies;
	@Setter @Getter private String joinNotice;
	@Setter @Getter private War war;

	@Getter private long moneyBank;
	@Getter private List<TaxCost> taxesCostsPartials;
	@Getter private long taxesCostsTotal;
	@Getter private final long creationTimestamp;
	private long protectedUntilTimestamp;
	@Getter private Location home;
	private Map<Resident, Invite> invites;
	@Getter private TownSizeData sizeData;
	
	@Getter private int plotTax;
	@Getter private int residentTax;
	@Setter @Getter private long lastTaxEarnings;
	
	private List<Integer> tempAlliesIDs;
	private List<Integer> tempEnemiesIDs;
	
	
	// Quando si crea una città
	public Town(int id, @NonNull String name, @NonNull Resident mayor) {
		Validate.isTrue(WTManager.getTown(id) == null, "Town with same ID already exists");
		Validate.isTrue(WTManager.getTown(name) == null, "Town with same name already exists");
		Validate.isTrue(mayor.getTown() == null, "Mayor already has a town");
		
		this.id = id;
		this.name = name;
		this.residentsMap = Maps.newHashMap();
		this.plotsMap = Maps.newHashMap();
		this.homeGroupPlots = Sets.newHashSet();
		this.warpsMap = new CaseInsensitiveMap<>();
		this.invites = Maps.newHashMap();
		this.allies = Sets.newHashSet();
		this.enemies = Sets.newHashSet();
		
		this.creationTimestamp = System.currentTimeMillis();
		
		addResident(mayor);
		setMayor(mayor);
	}
	
	public int getID() {
		return id;
	}
	
	public boolean hasResident(@NonNull Resident resident) {
		return resident.town == this;
	}
	
	public boolean hasPlot(@NonNull Plot plot) {
		return plot.town == this;
	}
	
	public boolean hasPlot(@NonNull ChunkCoords chunkCoords) {
		return plotsMap.containsKey(chunkCoords);
	}
	
	public boolean isAdjacentHomeGroupPlot(ChunkCoords coords) {
		for (ChunkCoords homeGroupChunk : homeGroupPlots) {
			if (homeGroupChunk.isAdjacent(coords)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isHomeGroupPlot(ChunkCoords coords) {
		return homeGroupPlots.contains(coords);
	}
	
	public int getHomeGroupPlotsCount() {
		return homeGroupPlots.size();
	}
	
	private void getAdjacentPlotsGroup(Collection<ChunkCoords> collector, ChunkCoords initialChunk, ChunkCoords[] allChunks, boolean[] checkedChunks) {
		// Questo metodo funziona come un'onda "ricorsiva", in cui ogni plot controlla i suoi vicini e così via
		
		for (int i = 0; i < allChunks.length; i++) {
			if (checkedChunks[i]) {
				continue; // I chunk con la bitmask sono esclusi perché già controllati
			}
			
			ChunkCoords otherChunk = allChunks[i];
			
			if (!otherChunk.isAdjacent(initialChunk)) {
				continue; // Controlla solo i blocchi vicino al plot passato come parametro
			}
			
			checkedChunks[i] = true; // A questo punto imposta la bitmask
			collector.add(otherChunk);
			
			if (otherChunk.equals(initialChunk)) {
				continue; // È questo stesso blocco, ed è stato già controllato per forza all'inizio del metodo. Succede alla prima chiamata (nelle successiva origin ha già la bitmask corretta)
			}
			
			getAdjacentPlotsGroup(collector, otherChunk, allChunks, checkedChunks);
		}
	}

	public void setUnderProtection(TimeUnit timeUnit, int value) {
		Validate.isTrue(value >= 0, "Value cannot be negative");
		protectedUntilTimestamp = System.currentTimeMillis() + timeUnit.toMillis(value);
	}
	
	public boolean isUnderProtection() {
		return System.currentTimeMillis() <= protectedUntilTimestamp;
	}
	
	public int getUnderProtectionDays() {
		long diff = protectedUntilTimestamp - System.currentTimeMillis();
		if (diff < 0) {
			return 0;
		}
		
		return (int) Math.ceil((double) diff / (double) TimeUnit.DAYS.toMillis(1));
	}
	
	public void broadcastOnlineResidents(String message) {
		broadcastOnlineResidents(message, null);
	}
	
	public void broadcastOnlineResidents(String message, TownRank rank) {
		message = Lang.format(Lang.format_townBroadcast, "{town}", this.getName()) + message;
		for (Resident resident : residentsMap.keySet()) {
			if (rank == null || hasRank(resident, rank)) {
				resident.tellIfOnline(message);
			}
		}
	}

	public Plot addPlot(@NonNull ChunkCoords coords) {
		return addPlot(coords, false);
	}
	
	public Plot addPlot(@NonNull ChunkCoords coords, boolean ignoreMaxPlots) {
		Validate.isTrue(!plotsMap.containsKey(coords),  "Plot " + coords + " already added");
		if (!ignoreMaxPlots) {
			Validate.isTrue(plotsMap.size() < sizeData.getMaxPlots(), "Plots amount exceed max");
		}
		
		Plot newPlot = new Plot(this, coords);
		WTManager.register(newPlot);
		plotsMap.put(newPlot.getChunkCoords(), newPlot);
		onTopologyChange();
		return newPlot;
	}
	
	public void removePlot(@NonNull ChunkCoords coords) {
		Plot removedPlot = plotsMap.remove(coords);
		Validate.notNull(removedPlot, "Plot " + coords + " cannot be removed, not found");
		WTManager.unregister(removedPlot);
		onTopologyChange();
	}
	
	public void addInvite(@NonNull Resident resident, long inviteValidForMillis) {
		Validate.isTrue(resident.town == null, "Resident " + resident + " already has a town");
		invites.put(resident, new Invite(inviteValidForMillis));
	}
	
	public void removeInvite(@NonNull Resident resident) {
		Validate.isTrue(resident.town == null, "Resident " + resident + " already has a town");
		Validate.isTrue(invites.containsKey(resident), "Resident" + resident + " has not been invited");
		invites.remove(resident);
	}
	
	public boolean hasValidInvite(@NonNull Resident resident) {
		Validate.isTrue(resident.town == null, "Resident " + resident + " already has a town");
		Invite invite = invites.get(resident);
		if (invite != null) {
			return !invite.isExpired();
		} else {
			return false;
		}
	}
	
	public void addResident(@NonNull Resident resident) {
		Validate.isTrue(resident.town == null, "Resident " + resident + " already has a town");
		Validate.isTrue(!residentsMap.containsKey(resident), "Resident to add " + resident + " is already a resident in " + this);
		
		// Ordine: in questo modo residentsMap è sempre consistente per tutto il tempo
		resident.town = this;
		residentsMap.put(resident, new ResidentTownData());
		onResidentJoinQuit();
	}	
	
	public void removeResident(@NonNull Resident resident, boolean lockPlots) {
		Validate.isTrue(resident.town == this, "Resident " + resident + " already has a different or is without town");
		Validate.isTrue(residentsMap.containsKey(resident), "Resident to remove " + resident + " is not a resident in " + this);
		Validate.isTrue(mayor != resident, "Cannot kick mayor");
		
		// Se è proprietario di qualche plot tornano liberi
		for (Plot plot : plotsMap.values()) {
			if (resident == plot.getOwner()) {
				plot.setOwner(null);
				if (lockPlots) {
					plot.setSetting(PlotSetting.LOCKED, true);
				}
			}
		}
		
		// Ordine: in questo modo residentsMap è sempre consistente per tutto il tempo
		residentsMap.remove(resident);
		resident.town = null;
		onResidentJoinQuit();
	}
	
	public void renameTo(@NonNull String newName) {
		if (!this.name.equalsIgnoreCase(newName)) {
			WTManager.rename(this, this.name, newName);
		}
		this.name = newName;
	}
	
	public void delete() throws IOException {
		File townFile = WildTowns.getTownFile(this.id);
		
		try {
			Files.copy(townFile, WildTowns.getDeletedTownFile(name));
		} catch (Throwable t) {
			WildTowns.logError(Level.WARNING, "Could not copy town file to deleted folder", t);
		}
		
		if (!townFile.delete()) {
			throw new IOException("Could not delete town file for " + this); // Prima di iniziare a rimuovere i dati dalla memoria rimuoviamo quelli da disco
		}
		
		mayor = null;
		for (Resident resident : residentsMap.keySet()) {
			resident.town = null;
		}
		residentsMap.clear();
		for (Plot plot : plotsMap.values()) {
			if (plot.getOwner() != null) {
				plot.setOwner(null);
			}
			WTManager.unregister(plot);
		}
		plotsMap.clear();
		
		for (Town other : WTManager.getTowns()) {
			if (other != this && other.hasOnewayAlly(this)) {
				other.removeOnewayAlly(this);
				other.trySaveAsync(null, null);
			}
			if (other != this && other.hasEnemy(this)) {
				other.removeEnemy(this);
				other.trySaveAsync(null, null);
			}
		}
		
		WTManager.unregister(this);
		DynmapBridge.updateTownHome(this, null);
		TownValueManager.delete(this);
	}
	
	public Collection<Plot> getPlots() {
		return Collections.unmodifiableCollection(plotsMap.values());
	}
	
	public Plot getPlot(@NonNull ChunkCoords coords) {
		return plotsMap.get(coords);
	}
	
	public boolean hasRank(@NonNull Resident resident, @NonNull TownRank rank) {
		ResidentTownData residentData = getResidentData(resident);
		
		if (mayor == resident) {
			return true; // Il sindaco ha tutti i ranghi possibili
		}
		
		TownRank ownedRank = residentData.getRank();
		return ownedRank != null && ownedRank.getInheritedRanks().contains(rank);
	}
	
	public boolean hasDirectRank(@NonNull Resident resident, TownRank rank) {
		ResidentTownData residentData = getResidentData(resident);
		
		if (rank == TownRank.MAYOR) {
			return mayor == resident;
		} else {
			return residentData.getRank() == rank;
		}
	}
	
	public TownRank getRank(@NonNull Resident resident) {
		ResidentTownData residentData = getResidentData(resident);
		
		if (mayor == resident) {
			return TownRank.MAYOR;
		} else {
			return residentData.getRank();
		}
	}
	
	public void setRank(@NonNull Resident resident, TownRank rank) {
		ResidentTownData residentData = getResidentData(resident);
		Validate.isTrue(mayor != resident, "Cannot set rank to mayor");
		
		residentData.setRank(rank);
	}
	
	public Collection<Resident> getResidentsByRank(TownRank rank) {
		if (rank == TownRank.MAYOR) {
			return Lists.newArrayList(mayor);
		}
		
		List<Resident> matches = Lists.newArrayList();
		for (Entry<Resident, ResidentTownData> entry : residentsMap.entrySet()) {
			if (entry.getKey() != mayor && entry.getValue().getRank() == rank) {
				matches.add(entry.getKey());
			}
		}
		return matches;
	}
	
	public ResidentTownData getResidentData(@NonNull Resident resident) {
		ResidentTownData residentData = residentsMap.get(resident);
		Validate.notNull(residentData, "Resident " + resident + " is not in this town");
		return residentData;
	}
	
	public void setHome(@NonNull Location location) {
		Validate.isTrue(Settings.isTownWorld(location), "Home must be in town world");
		this.home = location;
		DynmapBridge.updateTownHome(this, location);
		onTopologyChange();
	}
	
	public boolean hasOnewayAlly(@NonNull Town other) {
		return allies.contains(other);
	}
	
	public Collection<Town> getOnewayAllies() {
		return Collections.unmodifiableSet(allies);
	}
	
	public Collection<Town> getReciprocalAllies() {
		return Collections2.filter(allies, (other) -> {
			return isReciprocalAlly(other);
		});
	}
	
	public void removeOnewayAlly(@NonNull Town other) {
		Validate.isTrue(this != other, "Self reference");
		Validate.isTrue(allies.contains(other), "The town " + other + " is not in the allies of " + this);
		allies.remove(other);
	}
	
	public void addOnewayAlly(@NonNull Town other) {
		Validate.isTrue(this != other, "Self reference");
		Validate.isTrue(!allies.contains(other), "The town " + other + " is already in the allies of " + this);
		allies.add(other);
	}
	
	public boolean isReciprocalAlly(@NonNull Town other) {
		Validate.isTrue(this != other, "Self reference");
		return this.allies.contains(other) && other.allies.contains(this);
	}
	
	public boolean hasEnemy(@NonNull Town other) {
		return enemies.contains(other);
	}
	
	public Collection<Town> getEnemies() {
		return Collections.unmodifiableSet(enemies);
	}
	
	public void removeEnemy(@NonNull Town other) {
		Validate.isTrue(this != other, "Self reference");
		Validate.isTrue(enemies.contains(other), "The town " + other + " is not in the enemies of " + this);
		enemies.remove(other);
	}
	
	public void addEnemy(@NonNull Town other) {
		Validate.isTrue(this != other, "Self reference");
		Validate.isTrue(!enemies.contains(other), "The town " + other + " is already in the enemies of " + this);
		enemies.add(other);
	}
	
	public void setMayor(@NonNull Resident resident) {
		Validate.isTrue(residentsMap.containsKey(resident), "Mayor " + resident + " is not a resident in " + this);
		mayor = resident;
		getResidentData(resident).setRank(null);
	}
	
	public boolean isMayor(@NonNull Resident resident) {
		Validate.isTrue(residentsMap.containsKey(resident), "Resident " + resident + " is not in " + this);
		return mayor == resident;
	}
	
	public void setMoney(long money) {
		Validate.isTrue(money >= 0, "Money must be positive or zero");
		moneyBank = money;
	}
	
	public void addMoney(long money) {
		Validate.isTrue(money > 0, "Money must be positive");
		moneyBank = Math.addExact(moneyBank, money); // Exception on overflow
	}
	
	public void removeMoney(long money) {
		Validate.isTrue(money > 0, "Money must be positive");
		Validate.isTrue(moneyBank >= money, "Town can't afford money");
		this.moneyBank -= money;
	}
	
	public int getPlotsCount(@NonNull Resident resident) {
		int count = 0;
		
		for (Plot plot : plotsMap.values()) {
			if (resident == plot.getOwner()) {
				count++;
			}
		}
		
		return count;
	}
	
	public void setPlotTax(int costPerPlot) {
		Validate.isTrue(costPerPlot >= 0, "Cost must be zero or positive");
		this.plotTax = costPerPlot;
	}
	
	public void setResidentTax(int costPerResident) {
		Validate.isTrue(costPerResident >= 0, "Cost must be zero or positive");
		this.residentTax = costPerResident;
	}
	
	public void setWarp(String name, @NonNull Location warp) {
		Validate.isTrue(Settings.isTownWorld(warp), "Warp must be in town world");
		warpsMap.put(name, warp);
	}
	
	public boolean hasWarp(String name) {
		return warpsMap.containsKey(name);
	}
	
	public boolean removeWarp(String name) {
		return warpsMap.remove(name) != null;
	}
	
	public Location getWarp(String name) {
		return warpsMap.get(name);
	}
	
	public Set<String> getWarps() {
		return warpsMap.keySet();
	}
	
	public int getWarpsCount() {
		return warpsMap.size();
	}
	
	public Collection<Resident> getResidents() {
		return Collections.unmodifiableSet(residentsMap.keySet());
	}
	
	public int getResidentsCount() {
		return residentsMap.size();
	}
	
	public int getPlotsCount() {
		return plotsMap.size();
	}
	
	public String getNameAndTitle() {
		return name + " (" + sizeData.getTownTitle() + ")";
	}
	
	public boolean hasWar() {
		return war != null;
	}
	
	private void onTopologyChange() {
		updateHomePlotsGroup();
		updateTaxesCost();
	}
	
	private void onResidentJoinQuit() {
		sizeData = TownSizeManager.findTownSizeData(residentsMap.size());
		updateTaxesCost();
	}
	
	private void updateHomePlotsGroup() {
		homeGroupPlots = Sets.newHashSet();
		
		if (home == null) {
			WildTowns.logError(Level.WARNING, "Couldn't update home plots group because home was null");
			return;
		}
		
		ChunkCoords homeCoords = ChunkCoords.of(home);
		
		Collection<ChunkCoords> plotChunks = plotsMap.keySet();
		ChunkCoords[] allChunks = plotChunks.toArray(new ChunkCoords[plotChunks.size()]);
		boolean[] checkedChunks = new boolean[allChunks.length];
		
		getAdjacentPlotsGroup(homeGroupPlots, homeCoords, allChunks, checkedChunks);
	}
	
	public void updateTaxesCost() {
		int totalPlots = getPlotsCount();
		int outpostPlots = totalPlots - getHomeGroupPlotsCount();
		int residents = getResidentsCount();
		
		taxesCostsPartials = Lists.newArrayList();
		addTaxesPartial(taxesCostsPartials, TaxCause.PLOTS, Settings.economy_townTaxFormula_plots, "plots", totalPlots);
		addTaxesPartial(taxesCostsPartials, TaxCause.OUTPOSTS, Settings.economy_townTaxFormula_outposts, "outposts", outpostPlots);
		addTaxesPartial(taxesCostsPartials, TaxCause.RESIDENTS, Settings.economy_townTaxFormula_residents, "residents", residents);
		taxesCostsTotal = 0;
		for (TaxCost taxCost : taxesCostsPartials) {
			taxesCostsTotal += taxCost.getValue();
		}
	}
	
	private void addTaxesPartial(List<TaxCost> taxesCostsPartials, TaxCause taxCause, String expression, String variableName, int variableValue) {
		long result;
		
		try {
			Map<String, Number> variables = Maps.newHashMap();
			variables.put(variableName, variableValue);
			result = (long) MathExpression.eval(expression, variables);
			if (result < 0) {
				WildTowns.logError(Level.SEVERE, "Taxes caused by " + taxCause.name().toLowerCase() + " are negative for town " + this + ".");
				result = 0;
			}
		} catch (MathParseException e) {
			WildTowns.logError(Level.SEVERE, "Could calculate taxes caused by " + taxCause.name().toLowerCase() + " for town " + this + ".", e);
			result = 0;
		}
		
		taxesCostsPartials.add(new TaxCost(taxCause, result));
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	
	
	
	
	
	
	/*
	 *  Caricamenti e salvataggi
	 */
	
	public Town(@NonNull YamlConfiguration config) {
		UUID mayorUUID = UUID.fromString(config.getString(Nodes.MAYOR));
		
		this.id = config.getInt(Nodes.ID);
		this.name = config.getString(Nodes.NAME);
		this.residentsMap = Maps.newHashMap();
		this.plotsMap = Maps.newHashMap();
		this.warpsMap = new CaseInsensitiveMap<>();
		this.invites = Maps.newHashMap();
		this.allies = Sets.newHashSet();
		this.enemies = Sets.newHashSet();
		
		String homeString = config.getString(Nodes.HOME);
		if (homeString != null) {
			try {
				setHome(LocationSerializer.fromString(homeString));
			} catch (ParseException e) {
				Validate.error("Cannot parse home location of " + this + ": " + e.getMessage());
			} catch (WorldNotFoundException e) {
				Validate.error("Cannot find world of home location of " + this + ".");
			}
		}

		List<Map<?, ?>> serializedResidents = config.getMapList(Nodes.RESIDENTS);
		if (serializedResidents != null) {
			for (Map<?, ?> serializedResident : serializedResidents) {
				MapConfiguration residentConfig = new MapConfiguration(serializedResident);
				Validate.isTrue(residentConfig.isSet(ResidentNodes.UUID), "Resident section must have uuid");
				
				UUID residentUUID = UUID.fromString(residentConfig.getString(ResidentNodes.UUID));
				Resident resident = WTManager.getOfflineResident(residentUUID);
				Validate.notNull(resident, "Resident " + residentUUID + " was not found as resident");
				resident.town = this;
				
				ResidentTownData residentTownData = new ResidentTownData();
				String rankString = residentConfig.getString(ResidentNodes.RANK);
				if (rankString != null) {
					TownRank rank = TownRank.match(rankString); // Il nome dell'enum, non il nome user-friendly
					if (rank != null) {
						residentTownData.setRank(rank);
					} else {
						WildTowns.logError(Level.WARNING, "Could not find rank " + rankString + " in town " + this);
					}
				}
				
				residentsMap.put(resident, residentTownData);
			}
		}
		
		Resident mayor = WTManager.getOfflineResident(mayorUUID);
		Validate.notNull(mayor, "Mayor was not found as resident");
		setMayor(mayor);
		
		Validate.notNullOrEmpty(name, "Name cannot be null or empty");
		Validate.notNullOrEmpty(residentsMap, "Residents cannot be null or empty");
		
		this.moneyBank = config.getLong(Nodes.MONEY);
		this.plotTax = config.getInt(Nodes.TAX_PLOT);
		this.residentTax = config.getInt(Nodes.TAX_RESIDENT);
		this.lastTaxEarnings = config.getLong(Nodes.TAX_LAST_EARNINGS);
		this.joinNotice = config.getString(Nodes.JOIN_NOTICE);
		
		ConfigurationSection warSection = config.getConfigurationSection(Nodes.WAR);
		if (warSection != null) {
			this.war = new War(warSection.getBoolean(WarNodes.IS_ATTACKER), warSection.getString(WarNodes.OTHER_TOWN), warSection.getString(WarNodes.LINK));
		}

		this.creationTimestamp = config.getLong(Nodes.CREATION_TIMESTAMP);
		this.protectedUntilTimestamp = config.getLong(Nodes.PROTECTED_UNTIL_TIMESTAMP);
		this.tempAlliesIDs = config.getIntegerList(Nodes.ALLIES);
		this.tempEnemiesIDs = config.getIntegerList(Nodes.ENEMIES);
		
		// Plots
		List<Map<?, ?>> serializedPlots = config.getMapList(Nodes.PLOTS);
		if (serializedPlots != null) {
			for (Map<?, ?> serializedPlot : serializedPlots) {
				MapConfiguration plotConfig = new MapConfiguration(serializedPlot);
				Validate.isTrue(plotConfig.isSet(PlotNodes.X) && plotConfig.isSet(PlotNodes.Z), "Plot section must have x and z set");
				
				Plot plot = new Plot(this, ChunkCoords.of(plotConfig.getInt(PlotNodes.X), plotConfig.getInt(PlotNodes.Z)));
				
				if (plotConfig.isString(PlotNodes.OWNER)) {
					UUID ownerUUID = UUID.fromString(plotConfig.getString(PlotNodes.OWNER));
					Resident owner = WTManager.getOfflineResident(ownerUUID);
					Validate.notNull(owner, "Plot owner not found as resident");
					plot.setOwner(owner);
				}
				
				if (plotConfig.isSet(PlotNodes.FORSALE_PRICE)) { // Se impostato, è in vendita
					plot.setForSale(plotConfig.getInt(PlotNodes.FORSALE_PRICE));
				}

				String description = plotConfig.getString(PlotNodes.DESCRIPTION);
				if (description != null) {
					plot.setDescription(description);
				}
				
				ConfigurationSection flagsSection = plotConfig.getConfigurationSection(PlotNodes.FLAGS);
				if (flagsSection != null) {
					for (PlotSetting plotSetting : PlotSetting.values()) {
						plot.setSetting(plotSetting, flagsSection.getBoolean(plotSetting.getConfigName()));
					}
				}
				
				WTManager.register(plot);
				plotsMap.put(plot.getChunkCoords(), plot);
			}
		}
		
		// Warps
		ConfigurationSection warpsSection = config.getConfigurationSection(Nodes.WARPS);
		if (warpsSection != null) {
			for (String warpName : warpsSection.getKeys(false)) {
				try {
					Location location = LocationSerializer.fromString(warpsSection.getString(warpName));
					setWarp(warpName, location);
				} catch (ParseException e) {
					Validate.error("Cannot parse warp location (" + warpName + ") of " + this + ": " + e.getMessage());
				} catch (WorldNotFoundException e) {
					Validate.error("Cannot find world of warp location (" + warpName + ") of " + this + ".");
				}
			}
		}
		
		// Importante oppure la città non avrà un titolo
		onTopologyChange();
		onResidentJoinQuit();
	}

	public void setupRelations() {
		if (tempAlliesIDs != null) {
			for (Integer tempAllyID : tempAlliesIDs) {
				Town ally = WTManager.getTown(tempAllyID);
				Validate.notNull(ally, "Ally town " + tempAllyID + " not found for " + this);
				addOnewayAlly(ally);
			}
			tempAlliesIDs = null;
		}
		if (tempEnemiesIDs != null) {
			for (Integer tempEnemyID : tempEnemiesIDs) {
				Town enemy = WTManager.getTown(tempEnemyID);
				Validate.notNull(enemy, "Enemy town " + tempEnemyID + " not found for " + this);
				addEnemy(enemy);
			}
			tempEnemiesIDs = null;
		}
	}
	
	@Override
	public void save() throws IOException {
		YamlConfiguration config = new YamlConfiguration();
		config.set(Nodes.ID, this.id);
		config.set(Nodes.NAME, this.name);
		config.set(Nodes.MAYOR, mayor.getUUID().toString());
		config.set(Nodes.MONEY, moneyBank);
		config.set(Nodes.JOIN_NOTICE, joinNotice);
		config.set(Nodes.CREATION_TIMESTAMP, creationTimestamp);
		config.set(Nodes.PROTECTED_UNTIL_TIMESTAMP, protectedUntilTimestamp);
		config.set(Nodes.TAX_PLOT, plotTax);
		config.set(Nodes.TAX_RESIDENT, residentTax);
		config.set(Nodes.TAX_LAST_EARNINGS, lastTaxEarnings);
		
		if (war != null) {
			ConfigurationSection warSection = config.createSection(Nodes.WAR);
			warSection.set(WarNodes.IS_ATTACKER, war.isAttacker());
			warSection.set(WarNodes.OTHER_TOWN, war.getOtherTown());
			warSection.set(WarNodes.LINK, war.getLink());
		}
		
		if (home != null) {
			config.set(Nodes.HOME, LocationSerializer.toString(home));
		}

		List<Integer> serializedAllies = Lists.newArrayList();
		for (Town ally : allies) {
			serializedAllies.add(ally.getID());
		}
		config.set(Nodes.ALLIES, serializedAllies);
		
		List<Integer> serializedEnemies = Lists.newArrayList();
		for (Town enemy : enemies) {
			serializedEnemies.add(enemy.getID());
		}
		config.set(Nodes.ENEMIES, serializedEnemies);
		
		List<YamlConfiguration> serializedResidents = Lists.newArrayList();
		for (Entry<Resident, ResidentTownData> entry : residentsMap.entrySet()) {
			YamlConfiguration residentSection = new YamlConfiguration();
			ResidentTownData residentData = entry.getValue();
			residentSection.set(ResidentNodes.UUID, entry.getKey().getUUID().toString());
			if (residentData.getRank() != null) {
				residentSection.set(ResidentNodes.RANK, residentData.getRank().name().toLowerCase()); // Il nome dell'enum, non il nome user-friendly
			}

			serializedResidents.add(residentSection);
		}
		config.set(Nodes.RESIDENTS, serializedResidents);
		
		
		List<YamlConfiguration> serializedPlots = Lists.newArrayList();
		for (Plot plot : plotsMap.values()) {
			YamlConfiguration plotSection = new YamlConfiguration();
			plotSection.set(PlotNodes.X, plot.getChunkCoords().getX());
			plotSection.set(PlotNodes.Z, plot.getChunkCoords().getZ());
			if (plot.getOwner() != null) {
				plotSection.set(PlotNodes.OWNER, plot.getOwner().getUUID().toString());
			}
			if (plot.isForSale()) {
				plotSection.set(PlotNodes.FORSALE_PRICE, plot.getPrice());
			}
			if (plot.getDescription() != null) {
				plotSection.set(PlotNodes.DESCRIPTION, plot.getDescription());
			}
			
			ConfigurationSection flagsSection = plotSection.createSection(PlotNodes.FLAGS);
			for (PlotSetting plotSetting : PlotSetting.values()) {
				boolean value = plot.getSetting(plotSetting);
				if (value != plotSetting.getDefaultValue()) {
					flagsSection.set(plotSetting.getConfigName(), value); // Salva solo se diverso da default
				}
			}

			serializedPlots.add(plotSection);
		}
		config.set(Nodes.PLOTS, serializedPlots);
		
		ConfigurationSection warpsSection = config.createSection(Nodes.WARPS);
		for (Entry<String, Location> warpEntry : warpsMap.entrySet()) {
			warpsSection.set(warpEntry.getKey(), LocationSerializer.toString(warpEntry.getValue()));
		}
		
		config.save(WildTowns.getTownFile(this.id));
	}
	
	private static class Nodes {

		private static final String
			ID = "id",
			NAME = "name",
			MAYOR = "mayor",
			RESIDENTS = "residents",
			PLOTS = "plots",
			WARPS = "warps",
			MONEY = "money",
			JOIN_NOTICE = "join-notice",
			WAR = "war",
			CREATION_TIMESTAMP = "creation-timestamp",
			PROTECTED_UNTIL_TIMESTAMP = "protected-until-timestamp",
			TAX_PLOT = "tax-plot",
			TAX_RESIDENT = "tax-resident",
			TAX_LAST_EARNINGS = "tax-last-earnings",
			ALLIES = "allies",
			ENEMIES = "enemies",
			HOME = "home";
			
	}
	
	private static class PlotNodes {
		
		private static final String
			X = "x",
			Z = "z",
			OWNER = "owner",
			FORSALE_PRICE = "price",
			DESCRIPTION = "description",
			FLAGS = "flags";
		
	}
	
	private static class ResidentNodes {
		
		private static final String
			UUID = "uuid",
			RANK = "rank";
		
	}
	
	private static class WarNodes {
		
		private static final String
			IS_ATTACKER = "attacker",
			OTHER_TOWN = "other-town",
			LINK = "link";
		
	}

}
