package com.gmail.filoghost.wildtowns.command;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.util.Utils;
import com.google.common.collect.Lists;

import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import wild.api.chat.ChatBuilder;
import wild.api.command.CommandFramework.CommandValidate;

public class HelpSubCommand extends SubCommand {
	
	private SubCommandFramework commandGroup;

	public HelpSubCommand(@NonNull SubCommandFramework commandGroup) {
		super("help");
		setUsage("[pagina]");
		setMinArgs(0);
		setDescription("Informazioni sui comandi.");
		setHideFromHelp(true);
		this.commandGroup = commandGroup;
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		int page = 1;
		if (args.length > 0) {
			page = CommandValidate.getPositiveIntegerNotZero(args[0]);
		}
		
		List<SubCommand> commandsList = commandGroup.getListedSubCommands();
		
		int pageSize = 9;
		int pagesAmount = (int) Math.ceil((double) commandsList.size() / pageSize);
		int startIndex = (page - 1) * pageSize;
		int toIndex = Math.min(startIndex + pageSize, commandsList.size());
		
		CommandValidate.isTrue(startIndex < commandsList.size(), "Pagina non esistente.");
		
		sender.sendMessage(Lang.format(Lang.format_helpHeader, "{page}", page, "{pages}", pagesAmount));
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			for (int i = startIndex; i < toIndex; i++) {
				SubCommand sub = commandsList.get(i);
				sendCommandHelpLine(sender, label, sub);
			}
			
			player.sendMessage(Lang.chatColor_neutral + "Passa il mouse sopra i comandi per più dettagli");
			if (page < pagesAmount) {
				player.sendMessage(Lang.chatColor_neutral + "Scrivi \"/" + commandGroup.getLabel() + " " + this.getName() + " " + (page+1) + "\" per la pagina successiva");
			}
			player.sendMessage("");
			
		} else {
			// Per la console, ad esempio
			for (int i = startIndex; i < toIndex; i++) {
				SubCommand sub = commandsList.get(i);
				
				sender.sendMessage(Lang.color_highlight + "/" + label + " " + sub.getName() + (sub.getUsage() != null ? " " + sub.getUsage() : ""));
				if (sub.getDescription() != null) {
					sender.sendMessage(Lang.color_main + " - "  + Utils.join(sub.getDescription(), " "));
				}
			}
		}
	}
	
	
	public static void sendCommandHelpLine(CommandSender sender, String label, SubCommand subCommand) {
		ChatBuilder message = new ChatBuilder("");
		
		List<String> tooltipLines = Lists.newArrayList();
		tooltipLines.add(ChatColor.WHITE + "/" + label + " " + subCommand.getName() + (subCommand.getUsage() != null ? " " + subCommand.getUsage() : ""));
		if (subCommand.getDescription() != null) {
			tooltipLines.addAll(subCommand.getDescription());
		}
		if (subCommand.getCost() > 0) {
			tooltipLines.add("Costo: " + ChatColor.GREEN + Lang.formatMoney(subCommand.getCost()));
		}
		if (subCommand.getRequiredRank() != null) {
			tooltipLines.add("Rango Richiesto: " + ChatColor.RED + subCommand.getRequiredRank().getUserFriendlyName());
		}
		
		message.tooltip(Lang.chatColor_neutral, Utils.join(tooltipLines, "\n"));
		
		if (subCommand.getRequiredRank() != null) {
			message.append("[" + StringUtils.capitalize(subCommand.getRequiredRank().getUserFriendlyName()) + "] ").color(ChatColor.DARK_GRAY);
		}
		message.append("/" + label + " " + subCommand.getName()).color(ChatColor.AQUA);
		if (subCommand.getUsage() != null) {
			message.append(" " + subCommand.getUsage()).color(ChatColor.DARK_AQUA);
		}
		
		message.send(sender);
	}

}
