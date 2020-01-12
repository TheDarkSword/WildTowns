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

public class TownNoticeCommand extends SubCommand {

	public TownNoticeCommand() {
		super("notice");
		setRequiredRank(TownRank.ASSISTANT);
		setMinArgs(1);
		setUsage("<off|messaggio>");
		setDescription("Imposta un annuncio che viene visualizzato quando", "i cittadini entrano nella modalità.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		String joinNotice = String.join(" ", args);
		String confirmMessage;
		
		if (joinNotice.equalsIgnoreCase("off")) {
			town.setJoinNotice(null);
			confirmMessage = Lang.joinNoticeRemoved;
		} else {
			town.setJoinNotice(joinNotice);
			confirmMessage = Lang.joinNoticeSet;
		}

		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(confirmMessage);
		});
	}

}
