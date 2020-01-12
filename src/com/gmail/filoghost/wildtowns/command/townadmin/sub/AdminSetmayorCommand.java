package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import org.bukkit.command.CommandSender;
import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import wild.api.command.CommandFramework.CommandValidate;

public class AdminSetmayorCommand extends SubCommand {

	public AdminSetmayorCommand() {
		super("setmayor");
		setMinArgs(2);
		setUsage("<città> <giocatore>");
		setDescription("Imposta il sindaco di una città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Town targetTown = WTManager.getTown(args[0]);
		CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
		
		Resident targetResident = WTManager.getOfflineResident(args[1]);
		CommandValidate.isTrue(targetResident != null && targetTown.hasResident(targetResident), "Quel giocatore non è in quella città.");
		CommandValidate.isTrue(!targetTown.isMayor(targetResident), "Quel giocatore è già sindaco.");
		
		targetTown.setMayor(targetResident);

		ExtraValidator.trySaveAsync(targetTown, sender, () -> {
			sender.sendMessage(Lang.color_highlight + "Il sindaco di " + targetTown + " è ora " + targetResident);
		});
	}


}
