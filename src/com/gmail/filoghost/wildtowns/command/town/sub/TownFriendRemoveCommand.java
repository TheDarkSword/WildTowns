package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import wild.api.command.CommandFramework.CommandValidate;

public class TownFriendRemoveCommand extends SubCommand {

	public TownFriendRemoveCommand() {
		super("friend remove");
		setMinArgs(1);
		setUsage("<giocatore>");
		setDescription("Rimuovi un giocatore dalla lista amici.", "Gli amici possono costruire nei plot dove sei proprietario", "se appartengono alla tua stessa città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		
		Resident targetResident = WTManager.getOfflineResident(args[0]);
		CommandValidate.notNull(targetResident, Lang.residentNotFound);
		CommandValidate.isTrue(resident.hasFriend(targetResident), "Quel giocatore non è nella lista amici.");
		
		resident.removeFriend(targetResident);
		
		ExtraValidator.trySaveAsync(resident, sender, () -> {
			sender.sendMessage(Lang.format(Lang.friendRemoved, "{friend}", targetResident));
			targetResident.tellIfOnline(Lang.format(Lang.youHaveBeenRemovedAsFriend, "{name}", resident));
		});
	}


}
