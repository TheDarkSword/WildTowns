package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import org.bukkit.command.CommandSender;
import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import wild.api.command.CommandFramework.CommandValidate;

public class AdminRenameCommand extends SubCommand {

	public AdminRenameCommand() {
		super("rename");
		setMinArgs(2);
		setUsage("<città> <nuovoNome>");
		setDescription("Cambia il nome di una città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Town targetTown = WTManager.getTown(args[0]);
		CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
		
		String newName = args[1];
		if (!targetTown.getName().equalsIgnoreCase(newName)) {
			CommandValidate.isTrue(WTManager.getTown(newName) == null, "Esiste già un'altra città con quel nome.");
		}
		
		targetTown.renameTo(newName);

		ExtraValidator.trySaveAsync(targetTown, sender, () -> {
			sender.sendMessage(Lang.color_highlight + "La città ora si chiama " + newName);
		});
	}


}
