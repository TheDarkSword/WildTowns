package com.gmail.filoghost.wildtowns.command.town.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework.CommandValidate;
import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class TownAllyAddCommand extends SubCommand {

	public TownAllyAddCommand() {
		super("ally add");
		setRequiredRank(TownRank.MAYOR);
		setMinArgs(1);
		setUsage("<città>");
		setDescription("Aggiungi una città come alleata.", "Se l'alleanza è reciproca ciascun cittadino delle due città", "può visitare l'altra con il comando /t home <città>");
	}
	
	@Override
	public int getCost() {
		return Settings.economy_addAlly;
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		Town targetTown = WTManager.getTown(args[0]);
		CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
		CommandValidate.isTrue(town != targetTown, "Devi specificare una città diversa dalla tua.");
		CommandValidate.isTrue(!town.hasOnewayAlly(targetTown), "La città è già stata aggiunta come alleata" + (town.isReciprocalAlly(targetTown) ? "." : " (non confermata)."));
		CommandValidate.isTrue(!town.hasEnemy(targetTown), "La città è nemica, deve essere prima rimossa dai nemici.");
		CommandValidate.isTrue(town.getOnewayAllies().size() < Settings.town_allyLimit, "La città può avere massimo " + Settings.town_allyLimit + " città alleate.");
		
		TownConfirmCommand.checkConfirm(player, this, args, Lang.format(Lang.thisCommandWillCost, "{money}", Lang.formatMoney(getCost())));
	
		ExtraValidator.takeBankMoney(town, Settings.economy_addAlly);
		town.addOnewayAlly(targetTown);

		ExtraValidator.trySaveAsync(town, sender, () -> {
			if (town.isReciprocalAlly(targetTown)) {
				town.broadcastOnlineResidents(Lang.format(Lang.newAlly, "{ally}", targetTown));
				targetTown.broadcastOnlineResidents(Lang.format(Lang.newAlly, "{ally}", town));
			} else {
				sender.sendMessage(Lang.format(Lang.newAllyRequestSent, "{ally}", targetTown, "{from}", town));
				targetTown.broadcastOnlineResidents(Lang.format(Lang.newAllyRequestReceived, "{from}", town), TownRank.ASSISTANT); // Notifica il sindaco e gli assistenti dell'altra città se online
			}
		});
	}


}
