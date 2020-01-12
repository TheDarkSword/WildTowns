package com.gmail.filoghost.wildtowns.command;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.util.Utils;
import com.google.common.collect.Lists;

import lombok.Getter;
import wild.api.command.CommandFramework;

public abstract class SubCommandFramework extends CommandFramework {
	
	@Getter	private List<SubCommand> allSubCommands;
	@Getter	private List<SubCommand> listedSubCommands;
	private String permissionBase;

	public SubCommandFramework(JavaPlugin plugin, String permissionBase, String label, String... aliases) {
		super(plugin, label, aliases);
		this.permissionBase = permissionBase;
		this.allSubCommands = Lists.newArrayList();
		this.listedSubCommands = Lists.newArrayList();
	}
	
	public String getLabel() {
		return label;
	}
	
	protected void setSubCommands(SubCommand... subCommands) {
		for (int i = 0; i < subCommands.length; i++) {
			for (int j = i + 1; j < subCommands.length; j++) {
				if (subCommands[i].getName().equals(subCommands[j].getName())) {
					throw new IllegalArgumentException("repeated sub command " + subCommands[i].getName());
				}
			}
		}
		this.allSubCommands = Lists.newArrayList(subCommands);
		this.listedSubCommands = this.allSubCommands.stream().filter(sub -> !sub.isHideFromHelp()).collect(Collectors.toList());
	}
	
	protected abstract void sendCommandHelp(CommandSender sender);
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		if (args.length == 0) {
			sendCommandHelp(sender);
			return;
		}
		
		int maxMatchingElements = 0;
		
		for (SubCommand subCommand : allSubCommands) {
			String[] nameParts = subCommand.getNameParts();
			int matchingElements = countMatchingElements(nameParts, args);
			
			if (matchingElements == nameParts.length) {
				CommandValidate.isTrue(sender.hasPermission(permissionBase + "." + subCommand.getName()), "Non hai il permesso per questo comando.");
				
				if (subCommand.getRequiredRank() != null) {
					Resident resident = ExtraValidator.getResidentSender(sender);
					Town town = ExtraValidator.getRequiredTown(resident);
					
					CommandValidate.isTrue(town.hasRank(resident, subCommand.getRequiredRank()), "Devi avere il rango di " + subCommand.getRequiredRank() + " per questo comando.");
				}
				
				String[] subCommandArgs = Utils.removeFirstElements(args, matchingElements);
				CommandValidate.minLength(subCommandArgs, subCommand.getMinArgs(), "Utilizzo comando: /" + label + " " + subCommand.getName() + " " + subCommand.getUsage());
				
				try {
					subCommand.execute(sender, label, subCommandArgs);
				} catch (ExecuteException e) {
					if (e.getMessage() != null) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
					}
				} catch (Throwable e) {
					e.printStackTrace();
					sender.sendMessage(Lang.internalError);
				}
				return;
				
			} else {
				maxMatchingElements = Math.max(matchingElements, maxMatchingElements);
			}
		}
		
		if (maxMatchingElements > 0) {
			if (args.length > maxMatchingElements) {
				// È stato inserito un sottocomando sbagliato, ci sono degli elementi che non corrispondono
				sender.sendMessage(ChatColor.RED + "Sottocomando sconosciuto. Sottocomandi simili:");
			} else {
				// È stato omessa una parte del sottocomando
				sender.sendMessage(ChatColor.RED + "Sottocomandi:");
			}
			
			for (SubCommand subCommand : listedSubCommands) {
				if (countMatchingElements(subCommand.getNameParts(), args) >= maxMatchingElements) {
					HelpSubCommand.sendCommandHelpLine(sender, label, subCommand);
				}
			}
			
		} else {
			sender.sendMessage(ChatColor.RED + "Sottocomando sconosciuto. Lista comandi: /" + label + " help");
		}
	}
	
	/**
	 * Restituisce il numero di elementi corrispondenti consecutivi a partire dall'inizio tra name e args.
	 * Match completo se returnValue = name.length;
	 */
	private int countMatchingElements(String[] name, String[] args) {
		int maxIndex = Math.min(name.length - 1, args.length - 1);
		
		int count = 0;
		for (int i = 0; i <= maxIndex; i++) {
			if (name[i].equalsIgnoreCase(args[i])) {
				count++;
			} else {
				break;
			}
		}
		
		return count;
	}


}
