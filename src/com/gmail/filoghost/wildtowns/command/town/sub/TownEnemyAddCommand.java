package com.gmail.filoghost.wildtowns.command.town.sub;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import com.google.common.collect.Maps;

import wild.api.command.CommandFramework.CommandValidate;
import wild.api.scheduler.Cooldowns;
import wild.api.util.UnitFormatter;

public class TownEnemyAddCommand extends SubCommand {
	
	private static final long ADD_COOLDOWN_MILLIS = TimeUnit.MINUTES.toMillis(10);
	
	private Map<String, Cooldowns<String>> enemyAddCooldowns = Maps.newHashMap();

	public TownEnemyAddCommand() {
		super("enemy add");
		setRequiredRank(TownRank.MAYOR);
		setMinArgs(1);
		setUsage("<città>");
		setDescription("Aggiungi una città come nemica.", "Le città nemiche appaiono di un colore diverso sulla mappa.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Town town = ExtraValidator.getRequiredTown(resident);
		
		Town targetTown = WTManager.getTown(args[0]);
		CommandValidate.notNull(targetTown, Lang.specifiedTownNotExist);
		CommandValidate.isTrue(town != targetTown, "Devi specificare una città diversa dalla tua.");
		CommandValidate.isTrue(!town.hasEnemy(targetTown), "La città è già stata aggiunta ai nemici.");
		CommandValidate.isTrue(!town.hasOnewayAlly(targetTown), "La città è alleata, deve essere prima rimossa dagli alleati.");
		CommandValidate.isTrue(town.getEnemies().size() < Settings.town_enemyLimit, "La città può avere massimo " + Settings.town_enemyLimit + " città nemiche.");
		
		Cooldowns<String> townEnemyCooldowns = enemyAddCooldowns.get(town.getName());
		if (townEnemyCooldowns == null) {
			townEnemyCooldowns = new Cooldowns<>();
			enemyAddCooldowns.put(town.getName(), townEnemyCooldowns);
		}
		
		long remainingCooldown = townEnemyCooldowns.getRemainingCooldown(targetTown.getName());
		CommandValidate.isTrue(remainingCooldown <= 0, "La città " + targetTown + " è già stata dichiarata nemica recentemente. Attendi " + UnitFormatter.formatMinutesOrSeconds((int) remainingCooldown / 1000) + " prima di aggiungerla di nuovo.");
		townEnemyCooldowns.setCooldown(targetTown.getName(), ADD_COOLDOWN_MILLIS);
		
		town.addEnemy(targetTown);

		ExtraValidator.trySaveAsync(town, sender, () -> {
			town.broadcastOnlineResidents(Lang.format(Lang.newEnemy, "{enemy}", targetTown));
			targetTown.broadcastOnlineResidents(Lang.format(Lang.yourTownAddedAsEnemy, "{enemy}", town)); // Notifica i cittadini dell'altra città se online
		});
	}


}
