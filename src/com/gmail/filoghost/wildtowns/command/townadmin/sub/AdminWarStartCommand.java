package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.object.base.War;

import wild.api.command.CommandFramework.CommandValidate;

public class AdminWarStartCommand extends SubCommand {
	
	private static final Pattern FORUM_THREAD_PATTERN = Pattern.compile("(https://forum.wildadventure.it/threads/)[^/]*?([0-9]+)/.*".replace("/", "\\/"), Pattern.CASE_INSENSITIVE);
			

	public AdminWarStartCommand() {
		super("war start");
		setMinArgs(3);
		setUsage("<cittàInAttacco> <cittàInDifesa> <link>");
		setDescription("Imposta lo stato di guerra per una città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Town attackerTown = WTManager.getTown(args[0]);
		Town defenderTown = WTManager.getTown(args[1]);
		CommandValidate.notNull(attackerTown, Lang.specifiedTownNotExist);
		CommandValidate.notNull(defenderTown, Lang.specifiedTownNotExist);
		
		CommandValidate.isTrue(attackerTown != defenderTown, "Le città specificate devono essere diverse!");
		
		String fullLink = args[2];
		Matcher matcher = FORUM_THREAD_PATTERN.matcher(fullLink);
		CommandValidate.isTrue(matcher.matches(), "Formato link non valido, deve essere una discussione del forum.");
		
		String shortLink = matcher.group(1) + matcher.group(2);
		
		CommandValidate.isTrue(!attackerTown.isUnderProtection(), "La città attaccante è sotto protezione.");
		CommandValidate.isTrue(!defenderTown.isUnderProtection(), "La città in difesa è sotto protezione.");
		
		CommandValidate.isTrue(attackerTown.getWar() == null, "La città attaccante è già in guerra.");
		CommandValidate.isTrue(defenderTown.getWar() == null, "La città in difesa è già in guerra.");
		
		attackerTown.setWar(new War(true, defenderTown.getName(), shortLink));
		defenderTown.setWar(new War(false, attackerTown.getName(), shortLink));
		
		ExtraValidator.trySaveAsync(attackerTown, sender, null);
		ExtraValidator.trySaveAsync(defenderTown, sender, null);
		
		sender.sendMessage(Lang.color_highlight + "Hai impostato " + attackerTown + " in guerra con " + defenderTown + ".");
	}


}
