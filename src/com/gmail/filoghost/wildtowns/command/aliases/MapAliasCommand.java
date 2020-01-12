package com.gmail.filoghost.wildtowns.command.aliases;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.util.Utils;

import wild.api.command.CommandFramework;

public class MapAliasCommand extends CommandFramework {
	

	public MapAliasCommand(JavaPlugin plugin, String label, String... aliases) {
		super(plugin, label, aliases);
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		WildTowns.getInstance().getTownCommand().execute(sender, "town", Utils.insertFirstElement(args, "map"));
	}


}
