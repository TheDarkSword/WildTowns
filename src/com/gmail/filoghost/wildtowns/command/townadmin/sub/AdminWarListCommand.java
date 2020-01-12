package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import org.bukkit.command.CommandSender;

import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class AdminWarListCommand extends SubCommand {

	public AdminWarListCommand() {
		super("war list");
		setDescription("Elenco delle guerre in in corso.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		sender.sendMessage(Lang.color_main + "Elenco delle città in guerra:");
		for (Town town : WTManager.getTowns()) {
			if (town.getWar() != null && town.getWar().isAttacker()) {
				sender.sendMessage(Lang.color_highlight + "- " + town + " in attacco contro " + town.getWar().getOtherTown());
			}
		}
	}


}
