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

public class TownKickCommand extends SubCommand {

	public TownKickCommand() {
		super("kick");
		setRequiredRank(TownRank.RECRUITER);
		setMinArgs(1);
		setUsage("<giocatore>");
		setDescription("Caccia un giocatore dalla tua città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		Resident targetResident = WTManager.getOfflineResident(args[0]);
		CommandValidate.isTrue(targetResident != null && town.hasResident(targetResident), "Quel giocatore non fa parte della tua città.");
		CommandValidate.isTrue(resident != targetResident, Lang.cantUseCommandOnSelf);
		
		TownRank executorRank = town.getRank(resident);
		CommandValidate.isTrue(executorRank != null && executorRank.isStrictlyHigherThan(town.getRank(targetResident)), "Non puoi cacciare un giocatore con rango pari o superiore al tuo.");
		
		town.removeResident(targetResident, true);
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			town.broadcastOnlineResidents(Lang.format(Lang.townKick, "{player}", player.getName(), "{kicked}", targetResident));
			targetResident.tellIfOnline(Lang.format(Lang.youHaveBeenKicked, "{player}", player.getName()));
		});
	}


}
