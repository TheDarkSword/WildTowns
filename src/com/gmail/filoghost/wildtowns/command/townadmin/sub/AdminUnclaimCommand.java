package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import wild.api.command.CommandFramework.CommandValidate;

public class AdminUnclaimCommand extends SubCommand {

	public AdminUnclaimCommand() {
		super("unclaim");
		setMinArgs(0);
		setDescription("Rimuove il plot su cui ti trovi dalla città che lo possiede.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		Location location = ExtraValidator.getTownWorldLocation(player);
		ChunkCoords coords = ChunkCoords.of(location);
		Plot plot = WTManager.getPlot(coords);
		
		CommandValidate.notNull(plot, "Non c'è nessun plot alla tua posizione.");
		plot.getTown().removePlot(coords);

		ExtraValidator.trySaveAsync(plot.getTown(), sender, () -> {
			player.sendMessage(Lang.color_highlight + "Plot rimosso alla città " + plot.getTown());
		});
	}


}
