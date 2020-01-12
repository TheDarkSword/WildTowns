package com.gmail.filoghost.wildtowns.command.town.sub;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.command.ExtraValidator;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.gui.map.FacingDirection;
import com.gmail.filoghost.wildtowns.gui.map.MapGenerator;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import wild.api.chat.ChatBuilder;
import wild.api.command.CommandFramework.CommandValidate;

public class TownMapCommand extends SubCommand {
	
	public TownMapCommand() {
		super("map");
		setDescription("Mappa delle città intorno a te.", "Alias comando: /map");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Resident resident = WTManager.getOnlineResident(player);
		Location playerLocation = ExtraValidator.getTownWorldLocation(player);
		ChunkCoords center = ChunkCoords.of(playerLocation);
		
		List<ChatBuilder> mapLines = MapGenerator.generateMap(resident.getTown(), center, 10, 6, FacingDirection.fromYaw(playerLocation.getYaw()));
		for (ChatBuilder mapLine : mapLines) {
			mapLine.append("   ", FormatRetention.NONE).color(ChatColor.GRAY);
		}

		mapLines.get(1).append(MapGenerator.SYMBOL_TOWN + " = La tua città").color(MapGenerator.COLOR_OWN_TOWN);
		mapLines.get(2).append(MapGenerator.SYMBOL_TOWN + " = Città alleate").color(MapGenerator.COLOR_ALLY);
		mapLines.get(3).append(MapGenerator.SYMBOL_TOWN + " = Città nemiche").color(MapGenerator.COLOR_ENEMY);
		mapLines.get(4).append(MapGenerator.SYMBOL_TOWN + " = Altre città").color(MapGenerator.COLOR_OTHER_TOWNS);
		
		mapLines.get(6).append("Passa il mouse sopra").color(ChatColor.GRAY);
		mapLines.get(7).append("la mappa per vedere").color(ChatColor.GRAY);
		mapLines.get(8).append("i nomi delle città").color(ChatColor.GRAY);
		
		player.sendMessage(Lang.format_townMapHeader);
		for (ChatBuilder line : mapLines) {
			line.send(player);
		}
	}

}
