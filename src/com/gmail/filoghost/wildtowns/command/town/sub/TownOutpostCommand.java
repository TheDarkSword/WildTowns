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

public class TownOutpostCommand extends SubCommand {

	public TownOutpostCommand() {
		super("outpost");
		setRequiredRank(TownRank.ASSISTANT);
		setDescription("Conquista il plot cui ti trovi,", "un avamposto lontano dalla home.");
	}
	
	@Override
	public int getCost() {
		return Settings.economy_outpost;
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		CommandValidate.isTrue(town.getPlotsCount() < town.getSizeData().getMaxPlots(), "La città ha raggiunto il numero massimo di plot.");
		
		// Richiesta di conferma
		Location location = TownConfirmCommand.checkConfirm(player, this, args, Lang.format(Lang.thisCommandWillCost, "{money}", Lang.formatMoney(getCost())));
		Chunk chunk = location.getChunk();
		
		ExtraValidator.checkTownWorldLocation(location);
		ChunkCoords coords = ChunkCoords.of(location);
		
		CommandValidate.isTrue(!coords.isColony(), "Non puoi conquistare plot all'interno di una colonia.");
		
		Plot oldPlot = WTManager.getPlot(coords);
		if (oldPlot != null) {
			throw new ExecuteException(town.hasPlot(oldPlot) ? Lang.plotAlreadyOwned : Lang.plotBelongsAnotherTown);
		}
		CommandValidate.isTrue(!town.isAdjacentHomeGroupPlot(coords), "Questo plot è vicino al gruppo di plot che contiene la home, usa il comando \"/" + label + " claim\"");
		CommandValidate.isTrue(!WorldGuardBridge.isOverlappingRegions(chunk), Lang.claimOverlapsWGRegion);
		CommandValidate.isTrue(!Utils.overlapsWorldborder(chunk), Lang.claimOverlapsBorder);
		CommandValidate.isTrue(!Utils.hasNearbyDifferentTown(town, coords, Settings.town_minDistanceChunk_outpost), Lang.format(Lang.distanceTooShort, "{chunks}", Settings.town_minDistanceChunk_outpost));
		CommandValidate.isTrue(!Utils.hasNonResidentsInside(town, chunk), Lang.nonResidentsInsideClaim);
		
		ExtraValidator.takeBankMoney(town, Settings.economy_outpost);
		town.addPlot(coords);
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.format(Lang.youClaimedOutpost, "{money}", Lang.formatMoney(Settings.economy_outpost)));
		});
	}


}
