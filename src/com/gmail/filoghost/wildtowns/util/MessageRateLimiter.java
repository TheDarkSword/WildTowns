package com.gmail.filoghost.wildtowns.util;

import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import wild.api.scheduler.Cooldowns;

public class MessageRateLimiter {
	
	private static Map<Player, Cooldowns<String>> cooldowns = Maps.newHashMap();
	
	public static void remove(Player player) {
		cooldowns.remove(player);
	}
	
	public static void sendMessage(Player player, String message, long cooldownMillis) {
		Cooldowns<String> playerCooldowns = cooldowns.get(player);
		if (playerCooldowns == null) {
			playerCooldowns = new Cooldowns<>();
			cooldowns.put(player, playerCooldowns);
		}
		
		if (!playerCooldowns.isCooldown(message)) {
			playerCooldowns.setCooldown(message, cooldownMillis);
			player.sendMessage(message);
		}
	}

}
