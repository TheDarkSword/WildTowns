package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import wild.api.command.CommandFramework.CommandValidate;

public class TownTaxPlotCommand extends SubCommand {

	public TownTaxPlotCommand() {
		super("tax plot");
		setRequiredRank(TownRank.MAYOR);
		setMinArgs(1);
		setUsage("<soldi>");
		setDescription("Imposta una tassa giornaliera su ogni plot.", ChatColor.YELLOW + "NOTA: " + ChatColor.GRAY + "Il sindaco è esente dalle tasse.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		int tax = CommandValidate.getPositiveInteger(args[0]);
		CommandValidate.isTrue(tax <= Settings.economy_maxTaxPerPlot, "La tassa giornaliera sui plot non deve superare " + Lang.formatMoney(Settings.economy_maxTaxPerPlot) + ".");
		
		town.setPlotTax(tax);
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.format(Lang.plotTaxSet, "{money}", Lang.formatMoney(tax)));
		});
	}


}
