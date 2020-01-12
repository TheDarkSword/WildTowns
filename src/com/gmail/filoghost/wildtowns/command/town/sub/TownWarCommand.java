package com.gmail.filoghost.wildtowns.command.town.sub;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.command.CommandSender;

import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.TownValueManager;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import wild.api.command.CommandFramework.CommandValidate;

public class TownWarCommand extends SubCommand {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy 'ore' HH:mm");
	
	
	public TownWarCommand() {
		super("war");
		setRequiredRank(TownRank.ASSISTANT);
		setMinArgs(1);
		setUsage("<città>");
		setDescription("Mostra le informazioni di guerra di una città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Town targetTown = WTManager.getTown(args[0]);
		CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
		
		sender.sendMessage(Lang.color_highlight + "Informazioni di guerra della città " + targetTown + ":");
		sender.sendMessage(Lang.color_main + "Data attuale: " + DATE_FORMAT.format(new Date()));
		sender.sendMessage(Lang.color_main + "Bottino massimo richiedibile: " + Lang.formatMoney(TownValueManager.getValue(targetTown)));
		sender.sendMessage("");
		sender.sendMessage(Lang.color_main + "Maggiori informazioni: forum.WildAdventure.it/forums/guerre");
	}

}
