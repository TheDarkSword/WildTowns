package com.gmail.filoghost.wildtowns.command.town.sub;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.Utils;
import com.google.common.collect.Lists;

import wild.api.command.CommandFramework.CommandValidate;

public class TownOnlineCommand extends SubCommand {

	public TownOnlineCommand() {
		super("online");
		setDescription("Mostra i cittadini online nella tua città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		List<String> onlineResidents = Lists.newArrayList();
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (town.hasResident(WTManager.getOnlineResident(onlinePlayer))) {
				onlineResidents.add(onlinePlayer.getName());
			}
		}
		
		sender.sendMessage(Lang.color_main + "Cittadini online (" + onlineResidents.size() + "): " + Utils.joinColors(onlineResidents, ", "));
	}

}
