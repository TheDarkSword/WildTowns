package com.gmail.filoghost.wildtowns.command.townadmin;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.wildtowns.Perms;
import com.gmail.filoghost.wildtowns.command.HelpSubCommand;
import com.gmail.filoghost.wildtowns.command.SubCommandFramework;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminAddmoneyCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminBypassCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminColonyAddCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminColonyRemoveCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminDebugCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminHomeCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminJoinCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminProtectionCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminReloadCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminRenameCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminSetclaimCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminSetmayorCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminTakemoneyCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminUnclaimCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminWarInfoCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminWarListCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminWarStartCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminWarStopCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.sub.AdminWarpCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;

public class AdminCommand extends SubCommandFramework {
	

	public AdminCommand(JavaPlugin plugin, String label, String... aliases) {
		super(plugin, Perms.COMMAND_TOWNADMIN, label, aliases);
		
		setSubCommands(
			new HelpSubCommand(this),
			new AdminHomeCommand(),
			new AdminWarpCommand(),
			new AdminBypassCommand(),
			new AdminUnclaimCommand(),
			new AdminSetclaimCommand(),
			new AdminProtectionCommand(),
			new AdminJoinCommand(),
			new AdminSetmayorCommand(),
			new AdminRenameCommand(),
			new AdminAddmoneyCommand(),
			new AdminTakemoneyCommand(),
			new AdminColonyAddCommand(),
			new AdminColonyRemoveCommand(),
			new AdminWarStartCommand(),
			new AdminWarStopCommand(),
			new AdminWarInfoCommand(),
			new AdminWarListCommand(),
			new AdminReloadCommand(),
			new AdminDebugCommand()
		);
	}

	@Override
	public void sendCommandHelp(CommandSender sender) {
		sender.sendMessage(Lang.color_main + "Questo è il comando amministrativo.");
		sender.sendMessage(Lang.color_main + "Per una lista dei comandi, scrivi " + Lang.color_highlight + "/" + label + " help");
	}
	
}
