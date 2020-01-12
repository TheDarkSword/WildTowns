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

public class TownPlotDescCommand extends SubCommand {

	public TownPlotDescCommand() {
		super("plot desc");
		setRequiredRank(TownRank.ASSISTANT);
		setMinArgs(0);
		setUsage("[descrizione]");
		setDescription("Modifica la descrizione di un plot.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		Plot plot = town.getPlot(ExtraValidator.getTownWorldChunkCoords(player));
		CommandValidate.notNull(plot, "Questo plot non fa parte della tua città.");

		String description = args.length > 0 ? String.join(" ", args) : null;
		if (description != null) {
			CommandValidate.isTrue(description.length() <= 25, "La descrizione deve avere una lunghezza massima di 25 caratteri.");
		}
		CommandValidate.isTrue(plot.getOwner() == null, "Puoi cambiare la descrizione solo nei plot liberi (senza proprietario).");
		plot.setDescription(description);
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			if (description != null) {
				sender.sendMessage(Lang.format(Lang.descriptionSet, "{description}", description));
			} else {
				sender.sendMessage(Lang.descriptionCleared);
			}
		});
	}

}
