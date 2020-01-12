package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import wild.api.command.CommandFramework.CommandValidate;

public class TownSetmayorCommand extends SubCommand {

	public TownSetmayorCommand() {
		super("setMayor");
		setRequiredRank(TownRank.MAYOR);
		setMinArgs(1);
		setUsage("<giocatore>");
		setDescription("Trasferisci la carica di sindaco della città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		TownConfirmCommand.checkConfirm(player, this, args, "L'operazione non potrà essere annullata.");
		
		Resident targetResident = WTManager.getOfflineResident(args[0]);
		CommandValidate.isTrue(targetResident != null && town.hasResident(targetResident), "Quel giocatore non è in questa città.");
		CommandValidate.isTrue(resident != targetResident, Lang.cantUseCommandOnSelf);
		
		town.setMayor(targetResident);

		ExtraValidator.trySaveAsync(town, sender, () -> {
			town.broadcastOnlineResidents(Lang.format(Lang.newMayor, "{old}", resident, "{new}", targetResident));
		});
	}

}
