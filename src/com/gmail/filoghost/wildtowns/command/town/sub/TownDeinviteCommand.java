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

public class TownDeinviteCommand extends SubCommand {

	public TownDeinviteCommand() {
		super("deinvite");
		setRequiredRank(TownRank.RECRUITER);
		setMinArgs(1);
		setUsage("<giocatore>");
		setDescription("Rimuove l'invito di un giocatore per entrare nella tua città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		Resident targetResident = WTManager.getOfflineResident(args[0]);
		
		CommandValidate.isTrue(targetResident != null && town.hasValidInvite(targetResident), "Quel giocatore non è stato invitato oppure l'invito è scaduto.");
		town.removeInvite(targetResident);
		sender.sendMessage(Lang.format(Lang.removeInvite, "{invited}", targetResident));
	}


}
