package com.gmail.filoghost.wildtowns.command.town.sub;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.google.common.collect.Maps;

import lombok.Getter;
import wild.api.command.CommandFramework.CommandValidate;
import wild.api.scheduler.Cooldowns;
import wild.api.util.UnitFormatter;

public class TownFriendAddCommand extends SubCommand {
	
	private static final long ADD_COOLDOWN_MILLIS = TimeUnit.MINUTES.toMillis(10);
	
	@Getter private static Map<Player, Cooldowns<UUID>> friendAddCooldowns = Maps.newHashMap();

	public TownFriendAddCommand() {
		super("friend add");
		setMinArgs(1);
		setUsage("<giocatore>");
		setDescription("Aggiungi un giocatore alla lista amici.", "Gli amici possono costruire nei plot dove sei proprietario", "se appartengono alla tua stessa città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		
		Resident targetResident = WTManager.getOfflineResident(args[0]);
		CommandValidate.notNull(targetResident, Lang.residentNotFound);
		CommandValidate.isTrue(resident != targetResident, Lang.cantUseCommandOnSelf);
		CommandValidate.isTrue(!resident.hasFriend(targetResident), "Quel giocatore è già nella lista amici.");
		CommandValidate.isTrue(targetResident.getFriendsCount() < Settings.town_friendsLimit, "Puoi avere massimo " + Settings.town_friendsLimit + " amici.");
		
		Cooldowns<UUID> playerFriendCooldowns = friendAddCooldowns.get(player);
		if (playerFriendCooldowns == null) {
			playerFriendCooldowns = new Cooldowns<>();
			friendAddCooldowns.put(player, playerFriendCooldowns);
		}
		
		long remainingCooldown = playerFriendCooldowns.getRemainingCooldown(targetResident.getUUID());
		CommandValidate.isTrue(remainingCooldown <= 0, targetResident + " è già stato aggiunto agli amici recentemente. Attendi " + UnitFormatter.formatMinutesOrSeconds((int) remainingCooldown / 1000) + " prima di aggiungerlo di nuovo.");
		playerFriendCooldowns.setCooldown(targetResident.getUUID(), ADD_COOLDOWN_MILLIS);
		
		resident.addFriend(targetResident);
		
		ExtraValidator.trySaveAsync(resident, sender, () -> {
			sender.sendMessage(Lang.format(Lang.friendAdded, "{friend}", targetResident));
			targetResident.tellIfOnline(Lang.format(Lang.youHaveBeenAddedAsFriend, "{name}", resident));
		});
	}


}
