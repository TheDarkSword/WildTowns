package com.gmail.filoghost.wildtowns.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.filoghost.wildtowns.command.town.sub.TownFriendAddCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminBypassCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.object.base.War;
import com.gmail.filoghost.wildtowns.util.MessageRateLimiter;
import com.gmail.filoghost.wildtowns.util.Utils;

public class JoinQuitListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Resident resident = WTManager.getOfflineResident(player.getName());
		
		if (resident == null) {
			resident = new Resident(player.getUniqueId());
			WTManager.register(resident);
			resident.trySaveAsync(null, null);
		}
		
		WTManager.getOnlineResidentsMap().put(player, resident);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onJoinHighest(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Town town = WTManager.getOnlineResident(player).getTown();
		
		if (town != null) {
			if (town.getJoinNotice() != null) {
				player.sendMessage(Lang.format(Lang.format_joinNotice, "{message}", town.getJoinNotice()));
			}
			
			if (town.getWar() != null) {
				War war = town.getWar();
				
				if (war.isAttacker()) {
					player.sendMessage(Lang.format(Lang.format_newWarTo, "{town}", town, "{to}", war.getOtherTown(), "{link}", war.getLink()));
				} else {
					player.sendMessage(Lang.format(Lang.format_newWarFrom, "{town}", town, "{from}", war.getOtherTown(), "{link}", war.getLink()));
				}
			}
		
			long maxTaxPayTimes = town.getMoneyBank() / town.getTaxesCostsTotal();
			if (maxTaxPayTimes == 0) {
				player.sendMessage(Lang.format(Lang.townWillBeDeletedTaxes, "{time}", Settings.taxes_collectHour));
			} else if (maxTaxPayTimes <= Settings.taxes_warnOnPossibleCollectAmountsLessEqualThan) {
				player.sendMessage(ChatColor.RED + "La tua città ha abbastanza soldi per pagare le tasse solo per " + Utils.getCorrectForm(maxTaxPayTimes, "un altro giorno", "altri " + maxTaxPayTimes + " giorni") + ".");
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Resident resident = WTManager.getOnlineResidentsMap().remove(event.getPlayer()); // Non serve salvare
		resident.setChatMode(null); // Reset chat mode
		AdminBypassCommand.removeBypass(event.getPlayer());
		TownFriendAddCommand.getFriendAddCooldowns().remove(event.getPlayer());
		MessageRateLimiter.remove(event.getPlayer());
	}

}
