package com.gmail.filoghost.wildtowns.timer;

import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.PluginConfig;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.Utils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import wild.api.bridges.EconomyBridge;
import wild.api.scheduler.BukkitTimer;

public class TaxesTimer extends BukkitTimer {
	
	private static Clock italianClock = Clock.system(ZoneId.of("Europe/Rome"));
	
	private Plugin plugin;
	private File saveFile;
	private int lastTaxYear, lastTaxMonth, lastTaxDay;

	public TaxesTimer(Plugin plugin, File saveFile) throws IOException, InvalidConfigurationException {
		super(plugin, 0, Utils.secondsToTicks(30));
		this.plugin = plugin;
		this.saveFile = saveFile;
		
		PluginConfig config = new PluginConfig(plugin, saveFile);
		config.load();
		
		this.lastTaxYear = config.getInt("lastTax.year", 0);
		this.lastTaxMonth = config.getInt("lastTax.month", 0);
		this.lastTaxDay = config.getInt("lastTax.day", 0);
	}

	public void collectTaxes() {
		Collection<Town> towns = Lists.newArrayList(WTManager.getTowns()); // Non possiamo fare Town#delete() iterando perché viene rimossa
		
		for (Town town : towns) {
			try {
				
				int residentTax = town.getResidentTax();
				int plotTax = town.getPlotTax();

				Map<Resident, Integer> dueResidentAmounts = Maps.newHashMap();
				
				for (Resident resident : town.getResidents()) {
					if (town.isMayor(resident)) {
						continue; // Il sindaco non paga le tasse
					}
					
					int dueAmount = residentTax;
					if (plotTax > 0) {
						dueAmount += plotTax * town.getPlotsCount(resident);
					}
					
					dueResidentAmounts.put(resident, dueAmount);
				}
				
				Bukkit.getScheduler().runTaskAsynchronously(WildTowns.getInstance(), () -> {
					try {
						Collection<Resident> kickList = Lists.newArrayList();
						long townEarnings = 0;
						
						for (Entry<Resident, Integer> entry : dueResidentAmounts.entrySet()) {
							Resident resident = entry.getKey();
							Integer dueAmount = entry.getValue();
												
							if (EconomyBridge.takeMoney(resident.getUUID(), dueAmount)) {
								townEarnings += dueAmount;
							} else {
								kickList.add(resident);
							}
						}
						
						long finalTownEarnings = townEarnings;
						
						Bukkit.getScheduler().runTask(WildTowns.getInstance(), () -> {
							try {
								for (Resident kicked : kickList) {
									town.removeResident(kicked, true);
									town.broadcastOnlineResidents(Lang.format(Lang.playerKickedTaxes, "{kicked}", kicked));
									kicked.tellIfOnline(Lang.youHaveBeenKickedTaxes);
								}
								
								town.setLastTaxEarnings(finalTownEarnings);
								if (finalTownEarnings > 0) {
									town.addMoney(finalTownEarnings); // Aggiunge i soldi guadagnati con le tasse
								}
								
								long townTaxes = town.getTaxesCostsTotal();
								
								if (town.getMoneyBank() < townTaxes) {
									town.delete();
									Bukkit.broadcastMessage(Lang.format(Lang.townDeletedTaxes, "{town}", town));
									WildTowns.logDelete("La città " + town + " è stata cancellata per mancanza di fondi.");
								} else {
									town.removeMoney(townTaxes);
									town.trySaveAsync(null, null);
								}
							} catch (Throwable t) {
								handleTaxException(town, t);
							}
						});
					} catch (Throwable t) {
						handleTaxException(town, t);
					}
				});
				
			} catch (Throwable t) {
				handleTaxException(town, t);
			}
		}
		
		Bukkit.broadcastMessage(Lang.taxesCollected);
	}
	
	private void handleTaxException(Town town, Throwable t) {
		WildTowns.logError(Level.SEVERE, "Couldn't tax city " + town, t);
	}

	@Override
	public void run() {
		LocalDateTime date = LocalDateTime.now(italianClock);
		if (lastTaxYear != date.getYear() || lastTaxMonth != date.getMonthValue() || lastTaxDay != date.getDayOfMonth()) {
			if (date.getHour() >= Settings.taxes_collectHour) {
				// Bisogna attivare le tasse
				lastTaxYear = date.getYear();
				lastTaxMonth = date.getMonthValue();
				lastTaxDay = date.getDayOfMonth();
				
				PluginConfig config = new PluginConfig(plugin, saveFile);
				config.set("lastTax.year", lastTaxYear);
				config.set("lastTax.month", lastTaxMonth);
				config.set("lastTax.day", lastTaxDay);
				
				try {
					config.save();
					collectTaxes(); // Non riscuotere mai le tasse se il config non si è salvato, oppure al reload verranno di nuovo ritirate
				} catch (IOException e) {
					WildTowns.logError(Level.SEVERE, "Couldn't save taxes file", e);
				}
			}
		}
	}

}
