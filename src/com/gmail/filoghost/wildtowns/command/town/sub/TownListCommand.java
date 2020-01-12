package com.gmail.filoghost.wildtowns.command.town.sub;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.TownValueManager;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.Utils;
import com.google.common.collect.Lists;

import net.md_5.bungee.api.ChatColor;

public class TownListCommand extends SubCommand {

	public TownListCommand() {
		super("list");
		setDescription("Elenco completo di tutte le città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		List<Town> towns = Lists.newArrayList(WTManager.getTowns());
		Collections.sort(towns, (town1, town2) -> {
			long diff = TownValueManager.getValue(town2) - TownValueManager.getValue(town1);
			if (diff == 0) {
				diff = town1.getCreationTimestamp() - town2.getCreationTimestamp();
			}
			if (diff == 0) {
				diff = town2.getID() - town1.getID();
			}
			return Utils.signum(diff);
		});
		
		sender.sendMessage(Lang.color_main + "Lista città (" + towns.size() + "): " + (towns.isEmpty() ? ChatColor.DARK_GRAY + "-" : Utils.joinColors(towns, ", ")));
	}

}
