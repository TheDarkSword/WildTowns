package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework.CommandValidate;
import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class TownAllyRemoveCommand extends SubCommand {

	public TownAllyRemoveCommand() {
		super("ally remove");
		setRequiredRank(TownRank.MAYOR);
		setMinArgs(1);
		setUsage("<città>");
		setDescription("Rimuovi una città dagli alleati.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		Town targetTown = WTManager.getTown(args[0]);
		CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
		CommandValidate.isTrue(town != targetTown, "Devi specificare una città diversa dalla tua.");
		CommandValidate.isTrue(town.hasOnewayAlly(targetTown), "La città specificata non è alleata.");
			
		town.removeOnewayAlly(targetTown);

		ExtraValidator.trySaveAsync(town, sender, () -> {
			if (targetTown.hasOnewayAlly(town)) {
				// L'alleanza allora è stata infranta
				town.broadcastOnlineResidents(Lang.format(Lang.removedAlly, "{ally}", targetTown));
				targetTown.broadcastOnlineResidents(Lang.format(Lang.yourTownRemovedAsAlly, "{ally}", town));
			} else {
				sender.sendMessage(Lang.format(Lang.onewayAllyRemoved, "{ally}", targetTown));
			}
		});
	}


}
