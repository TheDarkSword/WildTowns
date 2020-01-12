package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.Bukkit;
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

public class TownInviteCommand extends SubCommand {

	public TownInviteCommand() {
		super("invite");
		setRequiredRank(TownRank.RECRUITER);
		setMinArgs(1);
		setUsage("<giocatore>");
		setDescription("Invita un giocatore nella tua città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);

		Player target = Bukkit.getPlayerExact(args[0]);
		CommandValidate.notNull(target, Lang.thatPlayerIsNotOnline);
		
		Resident targetResident = WTManager.getOnlineResident(target);
		CommandValidate.isTrue(resident != targetResident, Lang.cantUseCommandOnSelf);
		CommandValidate.isTrue(targetResident.getTown() != town, "Quel giocatore è già in questa città.");
		CommandValidate.isTrue(targetResident.getTown() == null, "Quel giocatore ha già una città.");
		CommandValidate.isTrue(!town.hasValidInvite(targetResident), "Quel giocatore è già stato invitato.");
		
		town.addInvite(targetResident, Settings.inviteExpirationSeconds * 1000);
		sender.sendMessage(Lang.format(Lang.youHaveInvited, "{invited}", target.getName()));
		target.sendMessage(Lang.format(Lang.youHaveBeenInvited, "{player}", player.getName(), "{town}", town, "{seconds}", Settings.inviteExpirationSeconds));
	}


}
