package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework.CommandValidate;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class TownDepositCommand extends SubCommand {

	public TownDepositCommand() {
		super("deposit");
		setMinArgs(1);
		setUsage("<soldi>");
		setDescription("Deposita soldi nella banca della tua città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		int money = CommandValidate.getPositiveIntegerNotZero(args[0]);
		
		ExtraValidator.takePlayerActionMoney(player, money, "depositare quella cifra");
		town.addMoney(money);
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.format(Lang.moneyDeposited, "{money}", Lang.formatMoney(money)));
		});
	}


}
