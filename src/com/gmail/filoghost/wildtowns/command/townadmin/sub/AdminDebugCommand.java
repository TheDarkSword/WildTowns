package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import org.bukkit.command.CommandSender;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import wild.api.command.CommandFramework.CommandValidate;

public class AdminDebugCommand extends SubCommand {

	public AdminDebugCommand() {
		super("debug");
		setMinArgs(0);
		setDescription("Informazioni di debug.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {

		if (args.length > 0 && args[0].equalsIgnoreCase("taxes")) {
			sender.sendMessage("Raccolta tasse!");
			WildTowns.getInstance().getTaxesTimer().collectTaxes();
			return;
		}
		
		if (args.length > 0 && args[0].equalsIgnoreCase("outpost")) {
			CommandValidate.minLength(args, 2, "outpost <città>");
			Town town = WTManager.getTown(args[1]);
			CommandValidate.notNull(town, Lang.specifiedTownNotExist);
			ChunkCoords coords = ExtraValidator.getTownWorldChunkCoords(CommandValidate.getPlayerSender(sender));
			sender.sendMessage("Plot del gruppo home: " + (town.isHomeGroupPlot(coords) ? "si" : "no"));
			sender.sendMessage("Plot vicino al gruppo home: " + (town.isAdjacentHomeGroupPlot(coords) ? "si" : "no"));
			return;
		}
		
		if (args.length > 0 && args[0].equalsIgnoreCase("town")) {
			CommandValidate.minLength(args, 2, "town <città>");
			Town town = WTManager.getTown(args[1]);
			CommandValidate.notNull(town, Lang.specifiedTownNotExist);
			sender.sendMessage("Plot totali: " + town.getPlotsCount());			
			sender.sendMessage("Plot del gruppo home: " + town.getHomeGroupPlotsCount());
			sender.sendMessage("Plot outpost: " + (town.getPlotsCount() - town.getHomeGroupPlotsCount()));
			return;
		}
		
		sender.sendMessage(Lang.color_main + "Città totali: " + Lang.color_highlight + WTManager.getTowns().size());
		sender.sendMessage(Lang.color_main + "Plot totali: " + Lang.color_highlight + WTManager.getPlots().size());
		sender.sendMessage(Lang.color_main + "Residenti totali: " + Lang.color_highlight + WTManager.getOfflineResidents().size());
		sender.sendMessage(Lang.color_main + "Residenti caricati (online): " + Lang.color_highlight + WTManager.getOnlineResidents().size());
	}

}
