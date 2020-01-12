package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import wild.api.command.CommandFramework.CommandValidate;

public class TownAcceptCommand extends SubCommand {

	public TownAcceptCommand() {
		super("accept");
		setMinArgs(1);
		setUsage("<città>");
		setDescription("Accetta l'invito di una città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = WTManager.getTown(args[0]);
		
		CommandValidate.notNull(town, "Quella città non esiste.");
		CommandValidate.isTrue(resident.getTown() == null, "Sei già in una città.");
		CommandValidate.isTrue(town.hasValidInvite(resident), "Quella città non ti ha invitato oppure l'invito è scaduto.");
		
		town.removeInvite(resident);
		town.addResident(resident);

		ExtraValidator.trySaveAsync(town, sender, () -> {
			town.broadcastOnlineResidents(Lang.format(Lang.newTownResident, "{player}", player.getName()));
		});
	}


}
