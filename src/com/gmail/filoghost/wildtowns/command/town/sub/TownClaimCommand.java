package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.bridge.WorldGuardBridge;
import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.Utils;

import wild.api.command.CommandFramework.CommandValidate;
import wild.api.command.CommandFramework.ExecuteException;

public class TownClaimCommand extends SubCommand {

	public TownClaimCommand() {
		super("claim");
		setDescription("Conquista il plot cui ti trovi.");
		setRequiredRank(TownRank.ASSISTANT);
	}
	
	@Override
	public int getCost() {
		return Settings.economy_claim;
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		CommandValidate.isTrue(town.getPlotsCount() < town.getSizeData().getMaxPlots(), "La città ha raggiunto il numero massimo di plot.");
		
		Location location = ExtraValidator.getTownWorldLocation(player);
		Chunk chunk = location.getChunk();
		ChunkCoords coords = ChunkCoords.of(chunk);
		
		CommandValidate.isTrue(!coords.isColony(), "Non puoi conquistare plot all'interno di una colonia.");
		
		Plot oldPlot = WTManager.getPlot(coords);
		if (oldPlot != null) {
			throw new ExecuteException(town.hasPlot(oldPlot) ? Lang.plotAlreadyOwned : Lang.plotBelongsAnotherTown);
		}
		CommandValidate.isTrue(town.isAdjacentHomeGroupPlot(coords), "Il plot deve essere adiacente al gruppo di plot che contiene la home. Per conquistare territori lontani vedi il comando \"/" + label + " outpost\"");
		CommandValidate.isTrue(!WorldGuardBridge.isOverlappingRegions(chunk), Lang.claimOverlapsWGRegion);
		CommandValidate.isTrue(!Utils.overlapsWorldborder(chunk), Lang.claimOverlapsBorder);
		CommandValidate.isTrue(!Utils.hasNearbyDifferentTown(town, coords, Settings.town_minDistanceChunk_claim), Lang.format(Lang.distanceTooShort, "{chunks}", Settings.town_minDistanceChunk_claim));
		CommandValidate.isTrue(!Utils.hasNonResidentsInside(town, chunk), Lang.nonResidentsInsideClaim);
		
		ExtraValidator.takeBankMoney(town, Settings.economy_claim);
		town.addPlot(coords);
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.format(Lang.youClaimedPlot, "{money}", Lang.formatMoney(Settings.economy_claim)));
		});
	}


}
