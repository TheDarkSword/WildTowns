package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework.CommandValidate;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class TownDelwarpCommand extends SubCommand {

	public TownDelwarpCommand() {
		super("delWarp");
		setRequiredRank(TownRank.ASSISTANT);
		setMinArgs(1);
		setUsage("<nome>");
		setDescription("Elimina un teletrasporto all'interno della città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		String warpName = args[0];
		
		CommandValidate.isTrue(town.removeWarp(warpName), Lang.format(Lang.townWarpNotFound, "{warp}", warpName));
		
		ExtraValidator.trySaveAsync(town, sender, () -> {
			sender.sendMessage(Lang.format(Lang.townWarpRemoved, "{warp}", warpName));
		});
	}


}
