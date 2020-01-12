package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.bridges.EconomyBridge;
import wild.api.command.CommandFramework.CommandValidate;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class TownWithdrawCommand extends SubCommand {

	public TownWithdrawCommand() {
		super("withdraw");
		setRequiredRank(TownRank.MAYOR);
		setMinArgs(1);
		setUsage("<soldi>");
		setDescription("Ritira soldi dalla banca della tua città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		int money = CommandValidate.getPositiveIntegerNotZero(args[0]);
		
		CommandValidate.isTrue(town.getMoneyBank() >= money, "La tua città non possiede quella cifra.");
		town.removeMoney(money);
		EconomyBridge.giveMoney(player, money);
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.format(Lang.moneyWithdrawn, "{money}", Lang.formatMoney(money)));
		});
	}


}
