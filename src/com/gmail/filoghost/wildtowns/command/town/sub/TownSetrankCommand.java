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
import com.gmail.filoghost.wildtowns.util.Utils;

import wild.api.command.CommandFramework.CommandValidate;

public class TownSetrankCommand extends SubCommand {

	public TownSetrankCommand() {
		super("setRank");
		setRequiredRank(TownRank.ASSISTANT);
		setMinArgs(2);
		setUsage("<giocatore> <rango>");
		setDescription("Aggiungi/rimuovi un rango da un cittadino.", "Lista ranghi: " + Utils.join(TownRank.getValuesNoMayor(), ", ") + ", cittadino");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		TownRank executorRank = town.getRank(resident);
		
		TownRank newRank;
		if (args[1].equalsIgnoreCase("cittadino")) {
			newRank = null;
		} else {
			newRank = TownRank.matchUserFriendly(args[1]);
			CommandValidate.notNull(newRank, "Ranghi validi: " + "cittadino, " + Utils.join(TownRank.getValuesNoMayor(), ", "));
			CommandValidate.isTrue(newRank != TownRank.MAYOR, "Per cambiare sindaco usa il comando /" + label + " setMayor");
			CommandValidate.isTrue(executorRank.isStrictlyHigherThan(newRank), "Puoi impostare solo ranghi inferiori al tuo.");
		}
		
		Resident targetResident = WTManager.getOfflineResident(args[0]);
		CommandValidate.isTrue(targetResident != null && town.hasResident(targetResident), "Quel giocatore non è in questa città.");
		CommandValidate.isTrue(resident != targetResident, Lang.cantUseCommandOnSelf);
		CommandValidate.isTrue(!town.isMayor(targetResident), "Il sindaco non può cambiare rango.");
		
		TownRank targetRank = town.getRank(targetResident);
		CommandValidate.isTrue(executorRank.isStrictlyHigherThan(targetRank), "Non puoi utilizzare questo comando su un giocatore con rango pari o superiore al tuo.");
		CommandValidate.isTrue(!town.hasDirectRank(targetResident, newRank), targetResident + " ha già quel rango.");
		
		town.setRank(targetResident, newRank);
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			town.broadcastOnlineResidents(Lang.format(Lang.rankSet, "{player}", player.getName(), "{rank}", newRank != null ? newRank.getUserFriendlyName() : "cittadino", "{ranked}", targetResident));
		});
	}

}
