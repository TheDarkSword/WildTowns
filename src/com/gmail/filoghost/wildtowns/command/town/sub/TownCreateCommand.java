package com.gmail.filoghost.wildtowns.command.town.sub;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework.CommandValidate;

import com.gmail.filoghost.wildtowns.bridge.WorldGuardBridge;
import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.Utils;

public class TownCreateCommand extends SubCommand {

	public TownCreateCommand() {
		super("create");
		setMinArgs(1);
		setUsage("<nome>");
		setDescription("Crea una nuova città.");
	}
	
	@Override
	public int getCost() {
		return Settings.economy_newTown;
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		String name = args[0];
		
		CommandValidate.isTrue(name.matches(Settings.town_name_regex), Lang.townNameInvalidFormat);
		CommandValidate.isTrue(name.length() >= Settings.town_name_minLength, "Il nome deve avere una lunghezza minima di " + Settings.town_name_minLength + " lettere.");
		CommandValidate.isTrue(name.length() <= Settings.town_name_maxLength, "Il nome deve avere una lunghezza massima di " + Settings.town_name_maxLength + " lettere.");
		
		CommandValidate.isTrue(resident.getTown() == null, Lang.youAlreadyHaveTown);
		CommandValidate.isTrue(WTManager.getTown(name) == null, "Esiste già una città con quel nome.");
		
		// Richiesta di conferma
		Location location = TownConfirmCommand.checkConfirm(player, this, args, "Verrà conquistato questo plot e impostata la home alla tua posizione. " + Lang.format(Lang.thisCommandWillCost, "{money}", Lang.formatMoney(getCost())));
		ExtraValidator.checkTownWorldLocation(location);
		Chunk chunk = location.getChunk();
		ChunkCoords coords = ChunkCoords.of(location);
		CommandValidate.isTrue(!coords.isColony(), "Questo plot è già occupato da una colonia.");
		CommandValidate.isTrue(WTManager.getPlot(coords) == null, "Questo plot è già occupato.");
		CommandValidate.isTrue(!WorldGuardBridge.isOverlappingRegions(chunk), Lang.claimOverlapsWGRegion);
		CommandValidate.isTrue(!Utils.overlapsWorldborder(chunk), Lang.claimOverlapsBorder);
		CommandValidate.isTrue(!Utils.hasNearbyDifferentTown(null, coords, Settings.town_minDistanceChunk_newTown), Lang.format(Lang.distanceTooShort, "{chunks}", Settings.town_minDistanceChunk_newTown));
		
		ExtraValidator.takePlayerActionMoney(player, Settings.economy_newTown, "creare una città");

		Town newTown = new Town(WTManager.getNextTownID(), name, resident);
		WTManager.register(newTown);
		newTown.setHome(location);
		newTown.addPlot(coords);
		newTown.setUnderProtection(TimeUnit.DAYS, Settings.newCityProtectionDays);
		newTown.setMoney(Settings.economy_newTownBank);
		
		ExtraValidator.trySaveAsync(newTown, sender, () -> {
			player.sendMessage(Lang.format(Lang.newTownCost, "{money}", Settings.economy_newTown));
			Bukkit.broadcastMessage(Lang.format(Lang.newTownBroadcast, "{player}", player.getName(), "{town}", name));
		});
	}



}
