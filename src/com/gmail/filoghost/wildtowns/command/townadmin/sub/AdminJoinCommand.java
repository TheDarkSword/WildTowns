package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import org.bukkit.command.CommandSender;
import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import wild.api.command.CommandFramework.CommandValidate;

public class AdminJoinCommand extends SubCommand {

	public AdminJoinCommand() {
		super("join");
		setMinArgs(1);
		setUsage("<città>");
		setDescription("Entra in una città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Town targetTown = WTManager.getTown(args[0]);
		CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
		
		Resident targetResident = ExtraValidator.getResidentSender(sender);
		CommandValidate.isTrue(targetResident.getTown() == null, "Sei già dentro una città.");

		targetTown.addResident(targetResident);

		ExtraValidator.trySaveAsync(targetTown, sender, () -> {
			sender.sendMessage(Lang.color_highlight + "Sei entrato in " + targetTown);
		});
	}


}
