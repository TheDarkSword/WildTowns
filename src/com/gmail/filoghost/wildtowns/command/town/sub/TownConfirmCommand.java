package com.gmail.filoghost.wildtowns.command.town.sub;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import wild.api.chat.ChatBuilder;
import wild.api.command.CommandFramework.CommandValidate;
import wild.api.command.CommandFramework.ExecuteException;

public class TownConfirmCommand extends SubCommand {
	
	private static Map<Player, Confirmation> confirmMap;

	public TownConfirmCommand() {
		super("confirm");
		setMinArgs(1);
		setUsage("<comando>");
		setDescription("Conferma una richiesta.");
		setHideFromHelp(true);
		
		confirmMap = Maps.newHashMap();
		Bukkit.getPluginManager().registerEvents(new Listener() {
			
			@EventHandler
			public void onQuit(PlayerQuitEvent event) {
				confirmMap.remove(event.getPlayer());
			}
			
		}, WildTowns.getInstance());
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Confirmation confirmation = confirmMap.get(player);
		String confirmedSubCommandWithArgs = String.join(" ", args);
		
		CommandValidate.isTrue(confirmation != null && !confirmation.isExpired() && confirmation.isSameCommand(confirmedSubCommandWithArgs), "Non hai nessuna richiesta di conferma in sospeso, oppure è scaduta.");
		
		confirmation.setDispatchingCommand(true);
		player.chat("/" + label + " " + confirmation.getSubLabelWithArgs()); // In questo modo appare sulla console e nei log
		confirmation.setDispatchingCommand(false);
		confirmMap.remove(player);
	}
	
	
	// Viene chiamato nei vari sottocomandi, quando vengono eseguite da dispatchCommand di questa classe
	// Restituisce la posizione originale del comando
	public static Location checkConfirm(Player player, SubCommand subCommand, String[] args, String message) {

		Confirmation confirmation = confirmMap.get(player);
		if (confirmation != null && confirmation.isDispatchingCommand()) { // È true solo quando viene eseguito player.chat()
			// Ok, prosegui
			return confirmation.getOriginalLocation();
		} else {
			// Il comando è stato eseguito manualmente, serve sempre conferma (sovrascrive la vecchia)
			String subLabelWithArgs = subCommand.getName() + (args.length > 0 ? " " + String.join(" ", args) : "");
			confirmation = new Confirmation(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10), subLabelWithArgs, player.getLocation());
			confirmMap.put(player, confirmation);
			player.sendMessage(Lang.format_confirm_header);
			player.sendMessage(Lang.format(Lang.format_confirm_description, "{text}", message));
			new ChatBuilder(Lang.format_confirm_buttonPrefix).append(Lang.format_confirm_buttonText).applyStylesFromString(Lang.format_confirm_buttonStyle).tooltip(Lang.chatColor_neutral, Lang.format_confirm_buttonTooltip).runCommand("/t confirm " + confirmation.getSubLabelWithArgs()).send(player);
			player.sendMessage(Lang.format_confirm_footer);
			throw new ExecuteException(null);
		}
	}
	

	@Getter
	private static class Confirmation {
		
		// Non si può mettere un runnable perché magari le condizioni del giocatore sono cambiate, e non verrebbero controllate.
		// Bisogna per forza eseguire di nuovo il comando.
		
		private long expiration;
		private SubCommand subCommand;
		private String subLabelWithArgs;
		private Location originalLocation;
		@Setter private boolean dispatchingCommand;
		
		public Confirmation(long expiration, String subLabelWithArgs, Location location) {
			this.expiration = expiration;
			this.subLabelWithArgs = subLabelWithArgs;
			this.originalLocation = location;
		}
		
		public boolean isSameCommand(String subLabelWithArgs) {
			return this.subLabelWithArgs.equals(subLabelWithArgs);
		}
		
		public boolean isExpired() {
			return System.currentTimeMillis() > expiration;
		}
		
	}

}
