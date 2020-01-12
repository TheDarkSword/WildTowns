package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import wild.api.chat.ChatBuilder;
import wild.api.command.CommandFramework.CommandValidate;

public class TownShowCommand extends SubCommand {

	public TownShowCommand() {
		super("show");
		setMinArgs(1);
		setUsage("<giocatore>");
		setDescription("Mostra le informazioni su un giocatore.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Resident targetResident = WTManager.getOfflineResident(args[0]);
		CommandValidate.notNull(targetResident, Lang.residentNotFound);
		CommandValidate.notNull(targetResident.getTown(), "Quel giocatore non fa parte di nessuna città.");
	
		TownRank rank = targetResident.getTown().getRank(targetResident);
		new ChatBuilder(targetResident.toString()).color(Lang.chatColor_highlight)
			.append(" è ").color(Lang.chatColor_main)
			.append(rank != null ? rank.getUserFriendlyName() : "cittadino").color(Lang.chatColor_highlight)
			.append(" nella città ").color(Lang.chatColor_main)
			.append(targetResident.getTown().getName()).color(Lang.chatColor_highlight)
			.append(" (").color(Lang.chatColor_neutral)
			.append("info").underlined(true)
				.tooltip(Lang.chatColor_neutral, "Clicca per mostrare le informazioni sulla città.")
				.runCommand("/" + label + " info " + targetResident.getTown())
			.append(")", FormatRetention.NONE).color(Lang.chatColor_neutral)
			.append(".").color(Lang.chatColor_main)
			.send(sender);
	}


}
