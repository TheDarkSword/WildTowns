package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import wild.api.command.CommandFramework.CommandValidate;

public class TownPlotRemoveownerCommand extends SubCommand {

	public TownPlotRemoveownerCommand() {
		super("plot removeOwner");
		setRequiredRank(TownRank.ASSISTANT);
		setDescription("Rimuove il proprietario di un plot.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		Plot plot = town.getPlot(ExtraValidator.getTownWorldChunkCoords(player));
		CommandValidate.notNull(plot, "Questo plot non fa parte della tua città.");
		
		Resident oldOwner = plot.getOwner();
		CommandValidate.notNull(oldOwner, "Questo plot non ha un proprietario.");
		
		plot.setOwner(null);
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.format(Lang.plotOwnerRemoved, "{owner}", oldOwner));
		});
	}


}
