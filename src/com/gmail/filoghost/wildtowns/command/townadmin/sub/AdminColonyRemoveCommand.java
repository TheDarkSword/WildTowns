package com.gmail.filoghost.wildtowns.command.townadmin.sub;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.ColoniesConfig;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import wild.api.command.CommandFramework.CommandValidate;

public class AdminColonyRemoveCommand extends SubCommand {
	
	public AdminColonyRemoveCommand() {
		super("colony remove");
		setMinArgs(0);
		setDescription("Rimuove il plot attuale come colonia.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		
		Location location = ExtraValidator.getTownWorldLocation(player);
		ChunkCoords coords = ChunkCoords.of(location);
		
		CommandValidate.isTrue(ColoniesConfig.isColonyChunk(coords), "Questo plot non è impostato come colonia.");
		
		ColoniesConfig.removeColonyChunk(coords);		
		
		sender.sendMessage(Lang.color_highlight + "Hai rimosso questo plot come colonia.");
		
		try {
			ColoniesConfig.save();
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage(ChatColor.RED + "Errore interno durante il salvataggio della lista delle colonie.");
		}
	}


}
