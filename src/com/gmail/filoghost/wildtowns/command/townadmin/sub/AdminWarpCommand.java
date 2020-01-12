package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.Utils;

import wild.api.command.CommandFramework.CommandValidate;

public class AdminWarpCommand extends SubCommand {

	public AdminWarpCommand() {
		super("warp");
		setMinArgs(1);
		setUsage("<città> [warp]");
		setDescription("Teletrasportati a un warp di una città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		Town targetTown = WTManager.getTown(args[0]);
		CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
		
		if (args.length > 1) {
			String warpName = args[1];
			Location warp = targetTown.getWarp(warpName);
			CommandValidate.notNull(warp, Lang.format(Lang.townWarpNotFound, "{warp}", warpName));
			player.teleport(warp);
			sender.sendMessage(Lang.format(Lang.teleportingTownWarp, "{warp}", warpName));
		} else {
			Set<String> warps = targetTown.getWarps();
			CommandValidate.isTrue(!warps.isEmpty(), "Non è ancora stato impostato alcun teletrasporto.");
			sender.sendMessage(Lang.format(Lang.townWarpsList, "{warps}", Utils.joinColors(warps, ", ")));
		}
	}


}
