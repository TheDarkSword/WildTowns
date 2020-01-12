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

public class TownLeaveCommand extends SubCommand {

	public TownLeaveCommand() {
		super("leave");
		setDescription("Esci dalla tua città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		CommandValidate.isTrue(!town.isMayor(resident), "Il sindaco non può abbandonare la città, devi prima affidarla a qualcuno oppure cancellarla.");
		
		TownConfirmCommand.checkConfirm(player, this, args, "Non potrai più rientrare se non invitato.");

		town.removeResident(resident, false);
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			town.broadcastOnlineResidents(Lang.format(Lang.townLeave, "{player}", player.getName()));
			player.sendMessage(Lang.youLeftTown);
		});
	}


}
