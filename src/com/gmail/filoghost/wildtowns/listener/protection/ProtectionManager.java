package com.gmail.filoghost.wildtowns.listener.protection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminBypassCommand;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.PlotSetting;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.google.common.collect.Maps;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ProtectionManager {
	
	@AllArgsConstructor
	@Getter
	public static enum Action {
		BUILD ("modificare"),
		USE ("utilizzare questo"),
		OPEN_CONTAINER ("aprire questo");
		
		private String verb;
	}
	
	
	/**
	 * Cancella un evento se è all'interno di una città.
	 */
	public static boolean cancelInsideTown(Cancellable event, Block block) {
		if (Settings.isTownWorld(block)) {
			if (isChunkProtected(ChunkCoords.of(block))) {
				event.setCancelled(true);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Elimina dalla collection tutti i blocchi appartenenti a una città o colonia.
	 */
	public static void filterTownBlocks(Collection<Block> blocks) {
		Map<ChunkCoords, Boolean> protectedChunks = Maps.newHashMap(); // Il valore boolean indica se i blocchi vanni protetti (true) o meno (false)
		
		for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext();) {
			Block block = (Block) iterator.next();
			ChunkCoords coords = ChunkCoords.of(block);
			
			Boolean protect = protectedChunks.get(coords);
			if (protect == null) {
				protectedChunks.put(coords, isChunkProtected(coords));
			}
			
			if (protect) {
				iterator.remove();
			}
		}
	}
	
	public static boolean isChunkProtected(ChunkCoords coords) {
		return coords.isColony() || WTManager.getPlot(coords) != null;
	}
	

	/**
	 * Controlla se un blocco può moversi da un plot a un altro.
	 */
	public static boolean canBlockMove(ChunkCoords fromChunk, ChunkCoords toChunk) {
		Plot fromPlot = WTManager.getPlot(fromChunk);
		Plot toPlot = WTManager.getPlot(toChunk);
		
		if (toPlot == null) {
			// Se il plot del blocco mosso è nella wilderness, sempre autorizzato (a meno che non sia una colonia)
			if (toChunk.isColony()) {
				return false;
			}
			
			return true;
		} else {
			// Nota: a questo punto toPlot appartiene a una città per certo
			
			if (fromPlot == null) {
				// Ci si sta muovendo da wilderness a città, non va bene
				return false;
			} else {
				// Se è da città a città, deve essere la stessa
				if (fromPlot.getTown() != toPlot.getTown()) {
					return false;
				}
				
				if (fromPlot.getOwner() == toPlot.getOwner()) {
					// Stesso proprietario o entrambi null
					return true;
				} else {
					return false;
				}
			}
		}
	}
	
	
	/**
	 * Processa un evento.
	 * @return true se è tutto ok, false se viene cancellato
	 */
	public static boolean processAction(Cancellable event, Player source, Action action, Plot plot, ChunkCoords coords, boolean silent) {
		if (event.isCancelled()) {
			return false;
		}
		
		String errorMessage = getActionErrorMessage(source, action, plot, coords);
		if (errorMessage != null) {
			event.setCancelled(true);
			if (!silent) {
				source.sendMessage(ChatColor.RED + errorMessage);
			}
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean processAction(Cancellable event, Player source, Action action, ChunkCoords position) {
		return processAction(event, source, action, position, false);
	}
	
	public static boolean processAction(Cancellable event, Player source, Action action, ChunkCoords position, boolean silent) {
		return processAction(event, source, action, WTManager.getPlot(position), position, silent);
	}
	
	public static boolean processAction(Cancellable event, Player source, Action action, Location location) {
		return processAction(event, source, action, location, false);
	}
	
	public static boolean processAction(Cancellable event, Player source, Action action, Location location, boolean silent) {
		ChunkCoords coords = ChunkCoords.of(location);
		return processAction(event, source, action, WTManager.getPlot(coords), coords, silent);
	}
	
	public static boolean processAction(Cancellable event, Player source, Action action, Block block) {
		return processAction(event, source, action, block, false);
	}
	
	public static boolean processAction(Cancellable event, Player source, Action action, Block block, boolean silent) {
		ChunkCoords coords = ChunkCoords.of(block);
		return processAction(event, source, action, WTManager.getPlot(coords), coords, silent);
	}
	
	/**
	 * @return null se è tutto ok e si può modificare, altrimenti il messaggio d'errore.
	 */
	public static String getActionErrorMessage(Player source, Action action, Plot plot, ChunkCoords coords) {
		if (plot == null && !coords.isColony()) {
			return null;
		}
		
		if (AdminBypassCommand.hasBypass(source)) {
			return null;
		}
		
		if (plot == null && coords.isColony()) {
			return "Non puoi " + action.getVerb() + " in questa colonia.";
		}
		
		Resident resident = WTManager.getOnlineResident(source);
		
		if (resident.getTown() != plot.getTown()) {
			return "Non puoi " + action.getVerb() + " in questa città.";
		}
		
		Resident owner = plot.getOwner();
		
		if (owner != null) {
			if (owner == resident || owner.hasFriend(resident)) {
				return null; // Il proprietario e i suoi amici possono fare tutto, anche quando è bloccato
			}
		}
		
		if (plot.getSetting(PlotSetting.LOCKED)) {
			// Gli assistenti possono fare tutto in tutti i plot, non serve controllare altre cose sotto.
			// A questo punto si decide già se resident può costruire o meno, a prescindere dalle flag
			if (resident.getTown().hasRank(resident, TownRank.ASSISTANT)) {
				return null;
			} else {
				return "Questo plot è protetto.";
			}
		}

		if (owner == null) {
			// Se non c'è un proprietario, si possono controllare le flag per la costruzione
			boolean allowBuild = plot.getSetting(PlotSetting.ALLOW_BUILD);
			if ((action == Action.BUILD && allowBuild)
				|| (action == Action.USE && (plot.getSetting(PlotSetting.ALLOW_USE) || allowBuild))
				|| (action == Action.OPEN_CONTAINER && (plot.getSetting(PlotSetting.ALLOW_CONTAINERS) || allowBuild))) {
				
				return null; // Se ci sono le flag e l'azione corrisponde, è sempre autorizzata
			}
		}
		
		// Altrimenti se non ci sono flag esplicite si controlla il rango
		if (resident.getTown().hasRank(resident, TownRank.BUILDER)) {
			return null;
		} else {
			return "Non puoi " + action.getVerb() + " qui.";
		}
	}

}
