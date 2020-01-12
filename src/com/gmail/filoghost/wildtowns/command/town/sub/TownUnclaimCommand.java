package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework.CommandValidate;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class TownUnclaimCommand extends SubCommand {

	public TownUnclaimCommand() {
		super("unclaim");
		setRequiredRank(TownRank.ASSISTANT);
		setDescription("Elimina il plot cui ti trovi.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		ChunkCoords coords = ExtraValidator.getTownWorldChunkCoords(player);
		CommandValidate.notNull(town.hasPlot(coords), "La città non possiede questo plot.");
		
		CommandValidate.isTrue(!coords.isColony(), "Non puoi eliminare plot all'interno di una colonia.");
		
		CommandValidate.isTrue(town.getPlotsCount() > 1, "La città deve possedere almeno un plot.");
		CommandValidate.isTrue(town.getHome() == null || !coords.containsLocation(town.getHome()), "Questo plot contiene la home, devi prima spostarla.");
		town.removePlot(coords);

		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.plotUnclaimed);
		});
	}


}
