package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import org.bukkit.command.CommandSender;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import wild.api.command.CommandFramework.CommandValidate;

public class AdminWarStopCommand extends SubCommand {

	public AdminWarStopCommand() {
		super("war stop");
		setMinArgs(1);
		setUsage("<città>");
		setDescription("Rimuove lo stato di guerra per una città e l'eventuale rivale.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Town targetTown = WTManager.getTown(args[0]);
		CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
		
		CommandValidate.isTrue(targetTown.getWar() != null, "La città non è in guerra.");
		
		Town otherTown = WTManager.getTown(targetTown.getWar().getOtherTown());
		
		targetTown.setWar(null);
		ExtraValidator.trySaveAsync(targetTown, sender, () -> {
			
			if (otherTown != null) {
				otherTown.setWar(null);
				ExtraValidator.trySaveAsync(otherTown, sender, () -> {
					sender.sendMessage(Lang.color_highlight + "Hai disattivato la guerra tra " + targetTown + " e " + otherTown + ".");
				});
			} else {
				sender.sendMessage(Lang.color_highlight + "Hai disattivato la guerra per " + targetTown + ".");
			}
		});
	}


}
