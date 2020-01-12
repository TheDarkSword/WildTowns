package com.gmail.filoghost.wildtowns.command.town.sub;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.Utils;

import net.md_5.bungee.api.ChatColor;
import wild.api.command.CommandFramework.CommandValidate;

public class TownFriendListCommand extends SubCommand {

	public TownFriendListCommand() {
		super("friend list");
		setDescription("Lista degli amici.", "Gli amici possono costruire nei plot dove sei proprietario", "se appartengono alla tua stessa città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		
		Collection<Resident> friends = resident.getFriends();
		sender.sendMessage(Lang.color_main + "Lista amici (" + friends.size() + "): " + (friends.isEmpty() ? ChatColor.DARK_GRAY + "-" : Utils.joinColors(friends, ", ")));
	}


}
