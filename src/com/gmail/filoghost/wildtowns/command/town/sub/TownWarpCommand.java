package com.gmail.filoghost.wildtowns.command.town.sub;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework.CommandValidate;

import com.gmail.filoghost.wildtowns.bridge.EssentialsBridge;
import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.Utils;

public class TownWarpCommand extends SubCommand {

	public TownWarpCommand() {
		super("warp");
		setUsage("[nome]");
		setDescription("Raggiungi un teletrasporto all'interno della città.", "Se non viene specificato il nome elenca i possibili teletrasporti.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		if (args.length > 0) {
			String warpName = args[0];
			
			Location warp = town.getWarp(warpName);
			CommandValidate.notNull(warp, Lang.format(Lang.townWarpNotFound, "{warp}", warpName));
			CommandValidate.isTrue(town.hasPlot(ChunkCoords.of(warp)), "Il teletrasporto non è all'interno della città.");
			
			if (EssentialsBridge.hasEssentials()) {
				try {
					EssentialsBridge.teleportWithCooldown(player, warp);
				} catch (Exception e) {
					player.sendMessage(e.getMessage());
					return;
				}
			} else {
				player.teleport(warp);
			}
			sender.sendMessage(Lang.format(Lang.teleportingTownWarp, "{warp}", warpName));
			
		} else {
			Set<String> warps = town.getWarps();
			CommandValidate.isTrue(!warps.isEmpty(), "Non è ancora stato impostato alcun teletrasporto.");
			sender.sendMessage(Lang.format(Lang.townWarpsList, "{warps}", Utils.joinColors(warps, ", ")));
		}
	}

}
