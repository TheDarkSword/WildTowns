package com.gmail.filoghost.wildtowns.gui.map;

import java.util.List;

import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import wild.api.chat.ChatBuilder;

public class MapGenerator {

	public static final ChatColor
		COLOR_WILDERNESS = 		ChatColor.DARK_GRAY,
		COLOR_OWN_TOWN = 		ChatColor.GREEN,
		COLOR_ALLY = 			ChatColor.LIGHT_PURPLE,
		COLOR_ENEMY = 			ChatColor.RED,
		COLOR_OTHER_TOWNS = 	ChatColor.WHITE,
		COLOR_UNOWNED_COLONY = 	ChatColor.YELLOW;
	
	public static final String
		SYMBOL_WILDERNESS = 	"▒",
		SYMBOL_TOWN = 			"█",
		SYMBOL_UNOWNED_COLONY = "█",
		SYMBOL_FOR_SALE =		"";
	

	public static List<ChatBuilder> generateMap(Town viewerTown, ChunkCoords center, int widthX, int widthZ, FacingDirection direction) {
		// Ordine di analisi dei chunk (Nord ^)
		// Guardando a nord (^), si guarda verso Z negative
		// Guardando a ovest (<), si guarda verso X negative
		// 1 - 2 - 3
		// 4 - 5 - 6
		// 7 - 8 - 9 ...
		
		int startX = center.getX() - widthX;
		int endX = center.getX() + widthX;
		int startZ = center.getZ() - widthZ;
		int endZ = center.getZ() + widthZ;
		
		List<ChatBuilder> outputLines = Lists.newArrayList();
		
		for (int z = startZ; z <= endZ; z++) {
			ChatBuilder line = new ChatBuilder("");
			List<String> lastHoverTooltip = null;

			for (int x = startX; x <= endX; x++) {
				ChunkCoords coords = ChunkCoords.of(x, z);
				Plot plot = WTManager.getPlot(coords);
				
				ChatColor color;
				String symbol;
				List<String> hoverTooltip = Lists.newArrayList();
				
				if (plot == null) {
					if (coords.isColony()) {
						color = COLOR_UNOWNED_COLONY;
						symbol = SYMBOL_UNOWNED_COLONY;
						hoverTooltip.add(Lang.color_neutral + "(Colonia non occupata)");
					} else {
						color = COLOR_WILDERNESS;
						symbol = SYMBOL_WILDERNESS;
					}
				} else {
					if (plot.getTown() == viewerTown) {
						color = COLOR_OWN_TOWN;
					} else if (viewerTown != null && viewerTown.isReciprocalAlly(plot.getTown())) {
						color = COLOR_ALLY;
					} else if (viewerTown != null && viewerTown.hasEnemy(plot.getTown())) {
						color = COLOR_ENEMY;
					} else {
						color = COLOR_OTHER_TOWNS;
					}
					symbol = SYMBOL_TOWN;
					hoverTooltip.add(Lang.color_neutral + "Città: " + color + plot.getTown());
					
					if (coords.isColony()) {
						hoverTooltip.add(Lang.color_neutral + "(Colonia)");
					}
					
					if (plot.getTown() == viewerTown) {
						// Maggiori informazioni per i cittadini
						if (plot.getOwner() != null) {
							hoverTooltip.add(Lang.color_neutral + "Proprietario: " + ChatColor.WHITE + plot.getOwner());
						}
						if (plot.isForSale()) {
							hoverTooltip.add(Lang.color_neutral + "In vendita: " + ChatColor.YELLOW + Lang.formatMoney(plot.getPrice()));
						}
					}
				}
				
				String textPiece;
				if (x == center.getX() && z == center.getZ()) {
					textPiece = ChatColor.BLACK + direction.getPaddingBefore() + (color == ChatColor.DARK_GRAY ? ChatColor.GRAY : color) + direction.getArrow() + ChatColor.BLACK + direction.getPaddingAfter();
				} else {
					textPiece = color + symbol;
				}
				
				line.append(textPiece);
				if (lastHoverTooltip == null || !hoverTooltip.equals(lastHoverTooltip)) { // Imposta solo se diverso da quello prima
					if (!hoverTooltip.isEmpty()) {
						line.tooltip(Lang.chatColor_neutral, String.join("\n", hoverTooltip));
					} else {
						line.retain(FormatRetention.FORMATTING); // Rimuove il precedente
					}
					lastHoverTooltip = hoverTooltip;
				}
			}
			
			outputLines.add(line);
		}
		
		return outputLines;
	}

}
