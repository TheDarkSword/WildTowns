package com.gmail.filoghost.wildtowns.command.town.sub;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework.CommandValidate;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class TownDeleteCommand extends SubCommand {

	public TownDeleteCommand() {
		super("delete");
		setRequiredRank(TownRank.MAYOR);
		setDescription("Cancella la città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		CommandValidate.isTrue(args.length == 0, "Utilizzo comando: /" + label + " delete");
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		TownConfirmCommand.checkConfirm(player, this, args, "La città verrà eliminata e non potrà essere recuperata.");
		
		try {
			town.delete();
			Bukkit.broadcastMessage(Lang.format(Lang.townDeletedCommand, "{town}", town.getName(), "{player}", player.getName()));
			WildTowns.logDelete("La città " + town + " è stata cancellata dal sindaco " + resident + ".");
		} catch (IOException e) {
			WildTowns.logError(Level.SEVERE, "Could not delete file for town " + town.getName(), e);
			sender.sendMessage(Lang.internalError);
		}
	}


}
