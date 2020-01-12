package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework.CommandValidate;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class TownPlotNotforsaleCommand extends SubCommand {

	public TownPlotNotforsaleCommand() {
		super("plot notForSale");
		setRequiredRank(TownRank.ASSISTANT);
		setDescription("Rimuovi dalla vendita un plot.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		Plot plot = town.getPlot(ExtraValidator.getTownWorldChunkCoords(player));
		CommandValidate.notNull(plot, "Questo plot non fa parte della tua città.");
		CommandValidate.isTrue(plot.isForSale(), "Questo plot non è in vendita.");
		
		plot.removeForSale();
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.plotNotForSale);
		});
	}

}
