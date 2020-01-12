package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import org.bukkit.command.CommandSender;
import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import wild.api.command.CommandFramework.CommandValidate;

public class AdminAddmoneyCommand extends SubCommand {

	public AdminAddmoneyCommand() {
		super("addmoney");
		setMinArgs(2);
		setUsage("<città> <soldi>");
		setDescription("Aggiunge soldi nella banca di una città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Town targetTown = WTManager.getTown(args[0]);
		CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
		
		int money = CommandValidate.getPositiveIntegerNotZero(args[1]);
		targetTown.addMoney(money);
		
		ExtraValidator.trySaveAsync(targetTown, sender, () -> {
			sender.sendMessage(Lang.color_highlight + "Hai aggiunto " + Lang.formatMoney(money) + " nella banca di " + targetTown);
		});
	}


}
