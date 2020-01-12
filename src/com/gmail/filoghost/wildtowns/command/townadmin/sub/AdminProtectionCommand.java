package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import java.util.concurrent.TimeUnit;
import org.bukkit.command.CommandSender;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import wild.api.command.CommandFramework.CommandValidate;

public class AdminProtectionCommand extends SubCommand {
	

	public AdminProtectionCommand() {
		super("protection");
		setMinArgs(2);
		setUsage("<città> <giorni>");
		setDescription("Imposta la protezione di una città in giorni (0 per disattivare).");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Town targetTown = WTManager.getTown(args[0]);
		int days = CommandValidate.getPositiveInteger(args[1]);
		
		CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
		CommandValidate.isTrue(targetTown.getWar() == null, "La città è attualmente in guerra.");
		
		targetTown.setUnderProtection(TimeUnit.DAYS, days);
		
		ExtraValidator.trySaveAsync(targetTown, sender, () -> {
			if (days == 0) {
				sender.sendMessage(Lang.color_highlight + "Hai rimosso la protezione a " + targetTown + ".");
			} else {
				sender.sendMessage(Lang.color_highlight + "Hai impostato " + days + " giorni di protezione a " + targetTown + ".");
			}
		});
	}


}
