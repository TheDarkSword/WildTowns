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

public class TownSethomeCommand extends SubCommand {

	public TownSethomeCommand() {
		super("setHome");
		setRequiredRank(TownRank.MAYOR);
		setDescription("Imposta la home della città.");
	}
	
	@Override
	public int getCost() {
		return Settings.economy_sethome;
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		// Richiesta di conferma
		Location location = TownConfirmCommand.checkConfirm(player, this, args, Lang.format(Lang.thisCommandWillCost, "{money}", Lang.formatMoney(getCost())));
		
		ExtraValidator.checkTownWorldLocation(location);
		ChunkCoords coords = ChunkCoords.of(location);
		CommandValidate.isTrue(town.hasPlot(coords), "Non puoi impostare la home fuori dalla città.");
		CommandValidate.isTrue(town.isHomeGroupPlot(coords), "La home non può essere spostata in un avamposto.");
		
		ExtraValidator.takeBankMoney(town, Settings.economy_sethome);
		town.setHome(location);

		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.format(Lang.townHomeSet, "{money}", Lang.formatMoney(Settings.economy_sethome)));
		});
	}


}
