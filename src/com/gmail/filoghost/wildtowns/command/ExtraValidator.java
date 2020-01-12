package com.gmail.filoghost.wildtowns.command;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Saveable;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.Validate;

import lombok.NonNull;
import wild.api.bridges.EconomyBridge;
import wild.api.command.CommandFramework.CommandValidate;
import wild.api.command.CommandFramework.ExecuteException;

public class ExtraValidator {
	
	public static Resident getResidentSender(@NonNull CommandSender sender) {
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		return resident;
	}
	
	public static Town getRequiredTown(@NonNull Resident resident) {
		CommandValidate.notNull(resident.getTown(), Lang.youDontHaveTown);
		return resident.getTown();
	}
	
	public static Location getTownWorldLocation(@NonNull Player player) {
		Location loc = player.getLocation();
		checkTownWorldLocation(loc);
		return loc;
	}
	
	public static void checkTownWorldLocation(@NonNull Location location) {
		CommandValidate.isTrue(Settings.isTownWorld(location), "Non sei nel mondo delle città.");
	}
	
	public static ChunkCoords getTownWorldChunkCoords(Player player) {
		return ChunkCoords.of(getTownWorldLocation(player));
	}
	
	public static Town checkTownRank(@NonNull Resident resident, @NonNull TownRank rank, String message) {
		Town town = resident.getTown();
		Validate.notNull(town, "Resident " + resident + " has no town");
		if (!town.hasRank(resident, rank)) {
			throw new ExecuteException(message);
		}
		return town;
	}
	
	public static void takeBankMoney(@NonNull Town town, long money) {
		if (town.getMoneyBank() < money) {
			throw new ExecuteException("La città non possiede abbastanza fondi (servono " + Lang.formatMoney(money) + ").");
		} else {
			town.removeMoney(money);
		}
	}
	
	public static void takePlayerActionMoney(@NonNull Player player, int money, String action) {
		if (money == 0) {
			return;
		}
		
		if (!EconomyBridge.hasMoney(player, money)) {
			throw new ExecuteException(Lang.format(Lang.noMoney, "{money}", Lang.formatMoney(money), "{action}", action));
		}
		
		boolean result = EconomyBridge.takeMoney(player, money);
		if (!result) {
			throw new ExecuteException("Impossibile prelevare i soldi. Contatta lo staff.");
		}
	}
	
	public static void trySaveAsync(Saveable saveable, CommandSender reportErrorsTo, Runnable onSuccess) {
		saveable.trySaveAsync(onSuccess, () -> {
			reportErrorsTo.sendMessage(Lang.internalError);
		});
	}

}
