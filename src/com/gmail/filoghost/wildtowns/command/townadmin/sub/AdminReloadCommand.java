package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import net.md_5.bungee.api.ChatColor;

public class AdminReloadCommand extends SubCommand {

	public AdminReloadCommand() {
		super("reload");
		setDescription("Ricarica il plugin.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {

		try {
			new Settings(WildTowns.getInstance(), "config.yml").init();
		} catch (Exception ex) {
			WildTowns.logError(Level.SEVERE, "Couldn't reload config.yml", ex);
			sender.sendMessage(ChatColor.RED + "Impossibile ricaricare config.yml");
		}
		
		try {
			new Lang(WildTowns.getInstance(), "lang.yml").init();
		} catch (Exception ex) {
			WildTowns.logError(Level.SEVERE, "Couldn't reload lang.yml", ex);
			sender.sendMessage(ChatColor.RED + "Impossibile ricaricare lang.yml");
		}
		
		for (Town town : WTManager.getTowns()) {
			town.updateTaxesCost();
		}
		
		sender.sendMessage(ChatColor.GREEN + "Configurazioni ricaricate!");
	}

}
