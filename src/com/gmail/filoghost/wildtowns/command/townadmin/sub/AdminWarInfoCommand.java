package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import org.bukkit.command.CommandSender;

import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.TownValueManager;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import wild.api.command.CommandFramework.CommandValidate;

public class AdminWarInfoCommand extends SubCommand {

	public AdminWarInfoCommand() {
		super("war info");
		setMinArgs(1);
		setUsage("<città>");
		setDescription("Mostra le informazioni di guerra di una città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Town targetTown = WTManager.getTown(args[0]);
		CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
		
		sender.sendMessage(Lang.color_highlight + "Informazioni di guerra della città " + targetTown + ":");
		sender.sendMessage(Lang.color_main + "Bottino massimo richiedibile: " + Lang.formatMoney(TownValueManager.getValue(targetTown)));
	}


}
