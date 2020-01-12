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

public class AdminColonyAddCommand extends SubCommand {
	
	public AdminColonyAddCommand() {
		super("colony add");
		setMinArgs(0);
		setDescription("Imposta il plot attuale come colonia.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = CommandValidate.getPlayerSender(sender);
		
		Location location = ExtraValidator.getTownWorldLocation(player);
		ChunkCoords coords = ChunkCoords.of(location);
		
		CommandValidate.isTrue(!ColoniesConfig.isColonyChunk(coords), "Questo plot è già impostato come colonia.");
		
		ColoniesConfig.setColonyChunk(coords);		
		
		sender.sendMessage(Lang.color_highlight + "Hai impostato questo plot come colonia.");
		
		try {
			ColoniesConfig.save();
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage(ChatColor.RED + "Errore interno durante il salvataggio della lista delle colonie.");
		}
	}


}
