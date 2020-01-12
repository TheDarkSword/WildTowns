package com.gmail.filoghost.wildtowns.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;

import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.object.base.War;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import wild.api.chat.ChatBuilder;

public class Utils {
	
	
	private static final DecimalFormat THOUSANDS_SEPARATOR_FORMAT = new DecimalFormat("###,###", DecimalFormatSymbols.getInstance(Locale.ITALIAN));
	
	public static String[] removeFirstElements(String[] array, int n) {
		Validate.isTrue(array.length > 0, "Empty array");
		return Arrays.copyOfRange(array, n, array.length);
	}
	
	public static String[] insertFirstElement(String[] array, String firstElement) {
		String[] newArray = new String[array.length + 1];
		for (int i = 0; i < array.length; i++) {
			newArray[i + 1] = array[i];
		}
		newArray[0] = firstElement;
		return newArray;
	}
	
	
	public static int signum(long value) {
		if (value > 0) {
			return 1;
		} else if (value < 0) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public static <T> ChatBuilder formattedJoin(ChatBuilder builder, Collection<T> collection, ChatColor separatorColor, String separator, ElementFormatter<T> formatter) {
		if (builder == null) {
			builder = new ChatBuilder("");
		}
		
		boolean first = true;
		for (T element : collection) {
			if (first) {
				first = false;
			} else {
				builder.append(separator, FormatRetention.NONE).color(separatorColor); // Dal secondo in poi
			}
			formatter.append(builder, element);
		}
		
		return builder;
	}

	public static interface ElementFormatter<T> {
		
		public void append(ChatBuilder componentBuilder, T element);
		
	}
	
	
	public static void addUnderProtectionMessage(ChatBuilder message, Town town, ChatColor color) {
		int protectionDays = town.getUnderProtectionDays();
		
		addParenthesisDetail(
			message,
			"Protetta",
			"La città è nuova o ha recentemente combattuto in guerra\n" +
			"e non può essere attaccata all'interno dei propri\n" +
			"territori principali per " + protectionDays + (protectionDays == 1 ? " giorno." : " giorni.") + "\n" +
			"\n" +
			"Gli avamposti sono esclusi.",
			color,
			null);
	}
	
	public static void addWarMessage(ChatBuilder message, War war, ChatColor color) {
		addParenthesisDetail(
			message,
			"In Guerra",
			"La città è in guerra con " + war.getOtherTown() + ".\n" +
			"(Clicca per aprire il link)",
			color,
			war.getLink());
	}
	
	private static void addParenthesisDetail(ChatBuilder chatBuilder, String content, String tooltip, ChatColor color, String clickUrl) {
		chatBuilder.append("(").color(color);
		chatBuilder.append(content).underlined(true).tooltip(Lang.chatColor_neutral, tooltip);
		if (clickUrl != null) {
			chatBuilder.openUrl(clickUrl);
		}
		
		chatBuilder.append(")", FormatRetention.NONE).color(color);
	}
	
	
	public static Player getRootPlayerAttacker(Entity damager) {
		
		if (damager == null) {
			return null;
		}
		
		if (damager.getType() == EntityType.PLAYER) {
			return (Player) damager;
			
		} else if (damager instanceof Projectile) {
			Projectile projectileDamager = (Projectile) damager;
			if (projectileDamager.getShooter() instanceof Player) {
				return (Player) projectileDamager.getShooter();
			}
			
		} else if (damager instanceof Tameable) {
			Tameable tameableDamager = (Tameable) damager;
			if (tameableDamager.getOwner() instanceof Player) {
				return (Player) tameableDamager.getOwner();
			}
		}
		
		return null;
	}
	
	
	public static String getCorrectForm(Number quantity, String singular, String plural) {
		if (quantity.intValue() == 1) {
			return singular;
		} else {
			return plural;
		}
	}
	
	public static long secondsToTicks(long seconds) {
		return seconds * 20;
	}
	
	public static String joinColors(Iterable<?> parts, String separator) {
		return join(parts, separator, Lang.color_highlight, Lang.color_main);
	}
	
	public static String join(Object[] parts, String separator) {
		return join(Arrays.asList(parts), separator, null, null);
	}
	
	public static String join(Iterable<?> parts, String separator) {
		return join(parts, separator, null, null);
	}
	
	public static String join(Iterable<?> parts, String separator, String elementColor, String separatorColor) {
		StringBuilder builder = new StringBuilder();
		for (Object part : parts) {
			if (builder.length() > 0) {
				if (separatorColor != null) {
					builder.append(separatorColor);
				}
				builder.append(separator);
			}
			if (elementColor != null) {
				builder.append(elementColor);
			}
			builder.append(part);
		}
		return builder.toString();
	}
	
	public static boolean isNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}
	
	public static boolean isNullOrEmpty(Collection<?> coll) {
		return coll == null || coll.isEmpty();
	}
	
	public static String separateThousands(long i) {
		return THOUSANDS_SEPARATOR_FORMAT.format(i);
	}
	
	/**
	 * 0 = viene controllato solo start position
	 * 1 = vengono controllati tutti i chunk intorno (anche in diagonale)
	 * 2+ = cerchio normale
	 * 
	 * Gli outpost devono essere molto distanti dalle altre città, i claim meno.
	 * @param currentTown può essere null (viene considerata qualunque città)
	 */
	public static boolean hasNearbyDifferentTown(Town currentTown, ChunkCoords startPosition, int radius) {
		int radiusSquared = radius * radius;
		
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				if (x*x + z*z <= radiusSquared + 1) { // Approssimazione per eccesso del cerchio
					ChunkCoords coords = ChunkCoords.of(startPosition.getX() + x, startPosition.getZ() + z);
					Plot plot = WTManager.getPlot(coords);
					if (plot != null && plot.getTown() != currentTown) {
						return true;
					}
					if (plot == null && coords.isColony()) {
						return true;
					}
				}
				
			}
		}
		
		return false;
	}
	
	public static boolean overlapsWorldborder(Chunk chunk) {
		WorldBorder worldBorder = chunk.getWorld().getWorldBorder();
		Location center = worldBorder.getCenter();
		double radius = worldBorder.getSize() / 2;
		
		PlaneLocation corner1 = new PlaneLocation(chunk.getX() << 4, chunk.getZ() << 4);
		PlaneLocation corner2 = corner1.duplicate(16, 0);
		PlaneLocation corner3 = corner1.duplicate(0, 16);
		PlaneLocation corner4 = corner1.duplicate(16, 16);
		
		if (
			isOutsideWorldborder(center, radius, corner1) ||
			isOutsideWorldborder(center, radius, corner2) ||
			isOutsideWorldborder(center, radius, corner3) ||
			isOutsideWorldborder(center, radius, corner4)) {
				return true;
		} else {
				return false;
		}
	}
	
	private static boolean isOutsideWorldborder(Location center, double radius, PlaneLocation checkLocation) {
		return
			checkLocation.getX() > center.getX() + radius ||
			checkLocation.getX() < center.getX() - radius ||
			checkLocation.getZ() > center.getZ() + radius ||
			checkLocation.getZ() < center.getZ() - radius;
	}
	
	public static <T> List<T> sortReturn(List<T> collection, Comparator<? super T> comparator) {
		Collections.sort(collection, comparator);
		return collection;
	}

	public static <K> void increaseMapValue(Map<K, Integer> map, K key) {
		Integer oldValue = map.get(key);
		if (oldValue != null) {
			map.put(key, oldValue + 1);
		} else {
			map.put(key, 1);
		}
	}

	public static boolean hasNonResidentsInside(Town town, Chunk chunk) {
		for (Entity entity : chunk.getEntities()) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				if (player.getGameMode() == GameMode.SURVIVAL && !town.hasResident(WTManager.getOnlineResident(player))) {
					return true;
				}
				
			}
		}
		return false;
	}

}
