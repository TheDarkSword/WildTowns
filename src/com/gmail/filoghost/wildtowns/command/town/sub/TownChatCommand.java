package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.ChatManager;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import wild.api.command.CommandFramework.CommandValidate;

public class TownChatCommand extends SubCommand {
	
	public TownChatCommand() {
		super("chat");
		setUsage("[messaggio]");
		setDescription("Manda un messaggio nella chat cittadina.", "Se non viene inserito un messaggio, cambia la", "chat tra modalità pubblica e cittadina.", "Alias comando: /townChat, /tc");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		if (args.length == 0) {
			resident.setChatMode(resident.getChatMode().cycle());
			player.sendMessage(Lang.format(Lang.chatMode, "{chat}", resident.getChatMode().getUserFriendlyName()));
		} else {
			ChatManager.sendTownChatMessage(player, resident, town, String.join(" ", args));
		}
	}

}
