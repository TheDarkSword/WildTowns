package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework.CommandValidate;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class TownSetwarpCommand extends SubCommand {

	public TownSetwarpCommand() {
		super("setWarp");
		setRequiredRank(TownRank.ASSISTANT);
		setMinArgs(1);
		setUsage("<nome>");
		setDescription("Imposta un teletrasporto all'interno della città.");
	}
	
	@Override
	public int getCost() {
		return Settings.economy_warp;
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		String warpName = args[0];
		
		CommandValidate.isTrue(warpName.matches("[a-zA-Z0-9]+"), "Il nome del teletrasporto deve essere alfanumerico.");
		CommandValidate.isTrue(warpName.length() >= 3, "Il nome deve avere una lunghezza minima di 3 lettere.");
		CommandValidate.isTrue(warpName.length() <= 12, "Il nome deve avere una lunghezza massima di 12 lettere.");
		
		// Richiesta di conferma
		Location location = TownConfirmCommand.checkConfirm(player, this, args, Lang.format(Lang.thisCommandWillCost, "{money}", Lang.formatMoney(getCost())));
		
		ExtraValidator.checkTownWorldLocation(location);
		CommandValidate.isTrue(town.hasPlot(ChunkCoords.of(location)), "Non puoi impostare un teletrasporto fuori dalla città.");
		
		// Controlla se non si sta modificando un warp esistente (e quindi non servirebbe controllare il limite)
		if (!town.hasWarp(warpName)) {
			CommandValidate.isTrue(town.getWarpsCount() < Settings.town_warpsLimit, "La città può avere massimo " + Settings.town_warpsLimit + " teletrasporti.");
		}

		ExtraValidator.takeBankMoney(town, Settings.economy_warp);
		town.setWarp(args[0], location);

		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.format(Lang.townWarpSet, "{warp}", warpName, "{money}", Lang.formatMoney(Settings.economy_warp)));
		});
	}

}
