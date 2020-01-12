package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import wild.api.command.CommandFramework.CommandValidate;

public class AdminSetclaimCommand extends SubCommand {

	public AdminSetclaimCommand() {
		super("setclaim");
		setMinArgs(1);
		setUsage("<città>");
		setDescription("Imposta l'appartenenza di questo plot.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		Town targetTown = WTManager.getTown(args[0]);
		CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
		
		Location location = ExtraValidator.getTownWorldLocation(player);
		ChunkCoords coords = ChunkCoords.of(location);
		Plot plot = WTManager.getPlot(coords);
		
		Town oldTown;
		
		if (plot != null) {
			oldTown = plot.getTown();
			CommandValidate.isTrue(oldTown != targetTown, "Questo plot appartiene già alla città " + oldTown + ".");
			oldTown.removePlot(coords);
		} else {
			oldTown = null;
		}
		
		targetTown.addPlot(coords, true);

		ExtraValidator.trySaveAsync(targetTown, sender, () -> {
			player.sendMessage(Lang.color_highlight + "Il plot ora appartiene alla città " + targetTown + (oldTown != null ? " (precedentemente della città " + oldTown + ")" : ""));
		});
		if (oldTown != null) {
			ExtraValidator.trySaveAsync(oldTown, sender, null);
		}
	}


}
