package com.gmail.filoghost.wildtowns.listener.protection;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.PlotSetting;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.MessageRateLimiter;
import com.gmail.filoghost.wildtowns.util.Utils;

public class ProtectionPvPListener implements Listener {
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			Player playerDefender = (Player) event.getEntity();
			Player playerAttacker = Utils.getRootPlayerAttacker(event.getCombuster());
			
			if (playerAttacker != null) {
				checkPvPEvent(event, playerAttacker, playerDefender, false);
			}
		}
	}
	
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			Player playerDefender = (Player) event.getEntity();
			Player playerAttacker = Utils.getRootPlayerAttacker(event.getDamager());
			
			if (playerAttacker != null) {
				boolean sendMessage = !(event.getDamager() instanceof Tameable);
				checkPvPEvent(event, playerAttacker, playerDefender, sendMessage);
				if (event.isCancelled() && event.getDamager() instanceof Projectile) {
					event.getDamager().remove(); // Disabilita le frecce che rimbalzano indietro
				}
			}
		}
	}
	
	
	private void checkPvPEvent(Cancellable event, Player playerAttacker, Player playerDefender, boolean sendMessage) {
		if (playerAttacker == playerDefender) {
			return; // Ci si può attaccare da soli
		}
		
		Resident defender = WTManager.getOnlineResident(playerDefender);
		Resident attacker = WTManager.getOnlineResident(playerAttacker);
		if (attacker == null) {
			attacker = WTManager.getOfflineResident(playerAttacker.getName()); // Può aver lanciato una freccia ed essere uscito
		}

		if (defender.getTown() == null) {
			return; // Il difensore non ha città
		}
		
		Location defenderLocation = playerDefender.getLocation();
		String defenderWorldName = defenderLocation.getWorld().getName();
		
		if (attacker.getTown() == defender.getTown() || (attacker.getTown() != null && attacker.getTown().isReciprocalAlly(defender.getTown()))) {

			if (Settings.pvpBetweenResidentsWorlds.contains(defenderWorldName)) {
				return;
			}
			
			if (Settings.isTownWorld(defenderWorldName)) {
				Plot defenderPlot = WTManager.getPlot(ChunkCoords.of(defenderLocation));
				if (defenderPlot != null && defenderPlot.getSetting(PlotSetting.PVP) && (defenderPlot.getTown() == defender.getTown() || defenderPlot.getTown() == attacker.getTown())) {

					Plot attackerPlot = WTManager.getPlot(ChunkCoords.of(playerAttacker.getLocation()));
					if (attackerPlot != null && attackerPlot.getSetting(PlotSetting.PVP) && (attackerPlot.getTown() == defender.getTown() || attackerPlot.getTown() == attacker.getTown())) {
						return; // Annulla la disabilitazione del pvp se entrambi sono in un plot dove è permesso, e i plot appartengono a una città di uno dei due combattenti
					}
				}
			}
			
			MessageRateLimiter.sendMessage(playerAttacker, Lang.color_error + "Non puoi attaccare i membri della tua città o gli alleati qui.", 1500);
			event.setCancelled(true); // Di default è disabilitato
			
		} else {
			if (Settings.isTownWorld(defenderWorldName) && defender.getTown().isUnderProtection()) {
				// La città del defender è sotto protezione, controlla se il defender è nella sua città

				Plot defenderPlot = defender.getTown().getPlot(ChunkCoords.of(defenderLocation));
				if (defenderPlot != null && defenderPlot.isHomeGroup()) {
					if (sendMessage) {
						MessageRateLimiter.sendMessage(playerAttacker, Lang.format(Lang.targetIsUnderProtection, "{player}", playerDefender.getName()), 1500);
					}
					event.setCancelled(true);
				}
			}
		}
	}

}
