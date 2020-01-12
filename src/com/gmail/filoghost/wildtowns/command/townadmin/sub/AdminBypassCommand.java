package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.Perms;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.google.common.collect.Sets;

import net.md_5.bungee.api.ChatColor;
import wild.api.command.CommandFramework.CommandValidate;

public class AdminBypassCommand extends SubCommand {
	
	private static Set<Player> bypassEnabled;
	private static String bypassPermission;

	public AdminBypassCommand() {
		super("bypass");
		setMinArgs(0);
		setDescription("Attiva/Disattiva il bypass della protezione in città.");
		bypassEnabled = Sets.newHashSet();
		bypassPermission = getPermission(Perms.COMMAND_TOWNADMIN);
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		
		if (bypassEnabled.contains(player)) {
			bypassEnabled.remove(player);
			player.sendMessage(ChatColor.YELLOW + "Modalità bypass " + ChatColor.RED + "DISATTIVATA" + ChatColor.YELLOW + ".");
		} else {
			bypassEnabled.add(player);
			player.sendMessage(ChatColor.YELLOW + "Modalità bypass " + ChatColor.GREEN + "ATTIVATA" + ChatColor.YELLOW + ".");
		}
	}
	
	public static boolean hasBypass(Player player) {
		return bypassEnabled.contains(player) && player.hasPermission(bypassPermission);
	}

	public static void removeBypass(Player player) {
		bypassEnabled.remove(player);
	}

}
