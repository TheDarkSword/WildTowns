package com.gmail.filoghost.wildtowns.command.town.sub;

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

public class TownHomeCommand extends SubCommand {

	public TownHomeCommand() {
		super("home");
		setUsage("[città]");
		setDescription("Torna allo spawn della tua città o di una città alleata.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		Town targetTown = town;
		
		if (args.length > 0) {
			targetTown = WTManager.getTown(args[0]);
			CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
			
			CommandValidate.isTrue(town == targetTown || town.isReciprocalAlly(targetTown), "La tua città e " + targetTown + " non sono reciprocamente alleate.");
		}
		
		Location homeLocation = targetTown.getHome();
		CommandValidate.notNull(homeLocation, "La città non possiede una home.");
		CommandValidate.isTrue(targetTown.hasPlot(ChunkCoords.of(homeLocation)), "La home non è all'interno della città.");

		if (EssentialsBridge.hasEssentials()) {
			try {
				EssentialsBridge.teleportWithCooldown(player, homeLocation);
			} catch (Exception e) {
				player.sendMessage(e.getMessage());
				return;
			}
		} else {
			player.teleport(homeLocation);
		}
		sender.sendMessage(Lang.format(Lang.teleportingTownHome, "{town}", targetTown));
	}


}
