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

public class TownTaxResidentCommand extends SubCommand {

	public TownTaxResidentCommand() {
		super("tax resident");
		setRequiredRank(TownRank.MAYOR);
		setMinArgs(1);
		setUsage("<soldi>");
		setDescription("Imposta una tassa giornaliera su ogni cittadino.", ChatColor.YELLOW + "NOTA:" + ChatColor.GRAY + " Il sindaco è esente dalle tasse.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		int tax = CommandValidate.getPositiveInteger(args[0]);
		CommandValidate.isTrue(tax <= Settings.economy_maxTaxPerResident, "La tassa giornaliera non può superare " + Lang.formatMoney(Settings.economy_maxTaxPerResident) + ".");
		town.setResidentTax(tax);
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.format(Lang.residentTaxSet, "{money}", Lang.formatMoney(tax)));
		});
	}


}
