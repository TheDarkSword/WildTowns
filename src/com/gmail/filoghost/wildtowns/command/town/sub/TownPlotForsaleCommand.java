package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework.CommandValidate;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class TownPlotForsaleCommand extends SubCommand {

	public TownPlotForsaleCommand() {
		super("plot forSale");
		setRequiredRank(TownRank.ASSISTANT);
		setMinArgs(1);
		setUsage("<prezzo>");
		setDescription("Metti in vendita un plot.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		Plot plot = town.getPlot(ExtraValidator.getTownWorldChunkCoords(player));
		CommandValidate.notNull(plot, "Questo plot non fa parte della tua città.");
		CommandValidate.isTrue(plot.getOwner() == null, "Non puoi impostare un prezzo, il plot ha già un proprietario.");
		
		int price = CommandValidate.getPositiveInteger(args[0]);
		CommandValidate.isTrue(price <= Settings.economy_maxPlotPrice, "Il prezzo massimo per un plot è " + Lang.formatMoney(Settings.economy_maxPlotPrice) + ".");
		
		plot.setForSale(price);
		
		// TODO riservare il plot per qualcuno
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.format(Lang.plotForSale, "{money}", Lang.formatMoney(price)));
		});
	}

}
