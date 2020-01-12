package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework.CommandValidate;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class TownPlotAbandonCommand extends SubCommand {

	public TownPlotAbandonCommand() {
		super("plot abandon");
		setDescription("Abbandona il plot nel quale ti trovi.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		Location location = TownConfirmCommand.checkConfirm(player, this, args, "L'operazione non può essere annullata.");
		ExtraValidator.checkTownWorldLocation(location);
		
		Plot plot = town.getPlot(ChunkCoords.of(location));
		CommandValidate.notNull(plot, "Questo plot non fa parte della tua città.");
		CommandValidate.isTrue(plot.getOwner() == resident, "Non sei il proprietario di questo plot.");
		
		plot.setOwner(null);
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.plotAbandoned);
		});
	}

}
