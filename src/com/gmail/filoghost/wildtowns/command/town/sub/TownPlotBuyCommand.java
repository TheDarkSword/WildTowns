package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework.CommandValidate;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class TownPlotBuyCommand extends SubCommand {

	public TownPlotBuyCommand() {
		super("plot buy");
		setDescription("Compra il plot nel quale ti trovi.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		Plot plot = town.getPlot(ExtraValidator.getTownWorldChunkCoords(player));
		CommandValidate.notNull(plot, "Questo plot non fa parte della tua città.");
		CommandValidate.isTrue(plot.isForSale(), "Questo plot non è in vendita.");
		
		int price = plot.getPrice();

		ExtraValidator.takePlayerActionMoney(player, price, "comprare questo plot");
		if (price > 0) {
			town.addMoney(price);
		}
		
		plot.setOwner(resident);

		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.format(Lang.plotBought, "{money}", Lang.formatMoney(price)));
		});
	}

}
