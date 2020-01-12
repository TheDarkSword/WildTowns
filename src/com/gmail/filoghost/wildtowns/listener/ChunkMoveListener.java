package com.gmail.filoghost.wildtowns.listener;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.PlotSetting;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import wild.api.WildCommons;
import wild.api.chat.ChatBuilder;

public class ChunkMoveListener implements Listener {
	
	private boolean isSameChunk(Location from, Location to) {
		return from.getBlockX() >> 4 == to.getBlockX() >> 4 && from.getBlockZ() >> 4 == to.getBlockZ() >> 4 && from.getWorld() == to.getWorld();
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent event) {
		onMove(event); // Richiama lo stesso handler usato per il movimento
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (isSameChunk(event.getFrom(), event.getTo())) {
				return; // Stesso plot, stesso mondo
		}

		if (!Settings.isTownWorld(event.getTo())) {
			return; // Non sta andando nel mondo towns
		}
		
		Plot fromPlot = Settings.isTownWorld(event.getFrom()) ? WTManager.getPlot(ChunkCoords.of(event.getFrom())) : null;
		Plot toPlot = WTManager.getPlot(ChunkCoords.of(event.getTo()));
		
		DetailsLevel detailsLevel;
		
		if (toPlot != null) {
			// Da (città | wilderness) a città
			Resident walker = WTManager.getOnlineResident(event.getPlayer());
			if (walker == null) {
				return; // Non è ancora stato caricato il giocatore
			}
			
			if (walker.getTown() != null && walker.getTown() == toPlot.getTown()) {
				detailsLevel = DetailsLevel.ALL;
			} else if (walker.getTown() != null && walker.getTown().isReciprocalAlly(toPlot.getTown())) {
				detailsLevel = DetailsLevel.PVP_ONLY;
			} else {
				detailsLevel = DetailsLevel.MINIMAL;
			}
			
		} else {
			// Da (città | wilderness) a wilderness
			if (fromPlot == null) {
				return; // Da wilderness a wilderness, è inutile proseguire
			}
			
			detailsLevel = DetailsLevel.MINIMAL;
		}
		
		
		
		if (detailsLevel == DetailsLevel.ALL) {
			if (fromPlot != null &&	toPlot != null &&											// Se i plot hanno una città;
				fromPlot.getTown() == toPlot.getTown() &&										// Se la città è uguale;
				Objects.equals(fromPlot.getOwner(), toPlot.getOwner()) && 						// Se il proprietario è lo stesso;
				Objects.equals(fromPlot.getDescription(), toPlot.getDescription()) &&			// Se la descrizione è la stessa;
				fromPlot.getSetting(PlotSetting.PVP) == toPlot.getSetting(PlotSetting.PVP) && 	// Se le impostazioni PvP sono uguali;
				!toPlot.isForSale()) {															// Se il plot non è in vendita;
					return; // Non notificare nulla.
				}
			
		} else if (detailsLevel == DetailsLevel.PVP_ONLY) {
			if (fromPlot != null &&	toPlot != null &&											// Se i plot hanno una città;
				fromPlot.getTown() == toPlot.getTown() &&										// Se la città è uguale;
				fromPlot.getSetting(PlotSetting.PVP) == toPlot.getSetting(PlotSetting.PVP)) { 	// Se le impostazioni PvP sono uguali;
					return; // Non notificare nulla.
			}
			
		} else if (detailsLevel == DetailsLevel.MINIMAL) {
			if (fromPlot != null &&	toPlot != null &&											// Se i plot hanno una città;
				fromPlot.getTown() == toPlot.getTown()) {										// Se la città è uguale;
					return; // Non notificare nulla.
			}
		}
		

		ChatBuilder message;
		String title;
		String subtitle;
			
		if (toPlot == null) {
			message = new ChatBuilder(Lang.format_plotInfoPrefix + Lang.format_wildernessName).color(Lang.chatColor_plotInfoWilderness);
			title = Lang.color_plotInfoWilderness + Lang.format_wildernessName;
			subtitle = null;
			
		} else {
			message = new ChatBuilder(Lang.format_plotInfoPrefix + toPlot.getTown()).color(Lang.chatColor_plotInfoTown);
			title = Lang.color_plotInfoTown + toPlot.getTown();
			subtitle = Lang.color_neutral + (toPlot.isHomeGroup() ? toPlot.getTown().getSizeData().getTownTitle() : "Avamposto");
			
			if (toPlot.isHomeGroup()) {
				if (toPlot.getTown().isUnderProtection()) {
					message.append(" ");
					Utils.addUnderProtectionMessage(message, toPlot.getTown(), ChatColor.GRAY);
				}
			} else {
				message.append(" (Avamposto)").color(Lang.chatColor_neutral);
			}			
			
			if (toPlot.getTown().getWar() != null) {
				message.append(" ");
				Utils.addWarMessage(message, toPlot.getTown().getWar(), ChatColor.GRAY);
			}
			
			if (detailsLevel == DetailsLevel.PVP_ONLY || detailsLevel == DetailsLevel.ALL) {
				if (toPlot.getSetting(PlotSetting.PVP)) {
					addPlotInfoSeparator(message);
					message.append("PvP abilitato").color(ChatColor.DARK_RED);
				}
			}
			
			if (detailsLevel == DetailsLevel.ALL) {
				addPlotInfoSeparator(message);
				if (toPlot.getOwner() != null) {
					message.append("Plot di " + toPlot.getOwner()).color(Lang.chatColor_plotInfoElement);
				} else {
					message.append(toPlot.getDescription() != null ? toPlot.getDescription() : "Plot libero").color(Lang.chatColor_plotInfoElement);
				}
				
				if (toPlot.isForSale()) {
					addPlotInfoSeparator(message);
					message.append("In vendita (" + Lang.formatMoney(toPlot.getPrice()) + ")").color(Lang.chatColor_plotInfoElement);
				}
			}
		}

		if (fromPlot == null || toPlot == null || fromPlot.getTown() != toPlot.getTown()) { // Se si è passati da wilderness a città o viceversa, o da una città all'altra
			WildCommons.sendTitle(event.getPlayer(), 10, 40, 10, title, subtitle);
		}
		message.send(event.getPlayer());
	}
	
	static enum DetailsLevel {
		
		MINIMAL, PVP_ONLY, ALL;
		
	}

	private void addPlotInfoSeparator(ChatBuilder message) {
		message.append(Lang.format_plotInfoSeparator, FormatRetention.NONE).color(Lang.chatColor_plotInfoSeparator);
	}
	
}
