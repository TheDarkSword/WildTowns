package com.gmail.filoghost.wildtowns.bridge;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.User;
import com.gmail.filoghost.wildtowns.Perms;
import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

import net.ess3.api.events.SetHomeEvent;
import net.ess3.api.events.TeleportHomeEvent;
import net.md_5.bungee.api.ChatColor;

public class EssentialsBridge {
	
	private static Essentials essentials;

	public static boolean setup() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("Essentials");

        if (plugin != null) {
        	essentials = (Essentials) plugin;
        	
        	Bukkit.getPluginManager().registerEvents(new Listener() {
        		
        		@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
        		public void onTeleportHome(TeleportHomeEvent event) {
        			if (!canHaveHome(event.getPlayer(), event.getDestination())) {
        				event.setCancelled(true);
        				event.getPlayer().sendMessage(ChatColor.RED + "La tua home deve essere nella tua città, se è nel mondo Towny. Usa /sethome per re-impostarla.");
        			}
        		}
        		
        		@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
        		public void onSetHome(SetHomeEvent event) {
        			if (!canHaveHome(event.getPlayer(), event.getLocation())) {
        				event.setCancelled(true);
        				event.getPlayer().sendMessage(ChatColor.RED + "Puoi impostare la home solo all'interno della tua città, se sei nel mondo Towny.");
        			}
        		}
        		
        	}, WildTowns.getInstance());
        	
            return true;
        } else {
        	return false;
        }
    }
	
	private static boolean canHaveHome(Player player, Location where) {
		if (!Settings.isTownWorld(where)) {
			return true;
		}
		
		if (player.hasPermission(Perms.ESSENTIALS_HOME_BYPASS)) {
			return true;
		}
		
		Resident resident = WTManager.getOnlineResident(player);
		if (resident.getTown() == null) {
			return false; // Chi non ha città non può settare la home
		}
		
		Plot plot = WTManager.getPlot(ChunkCoords.of(where));
		if (plot == null) {
			return false; // Non deve essere fuori città
		}
		
		return resident.getTown() == plot.getTown();
	}
	
	public static boolean hasEssentials() {
		return essentials != null;
	}
	
	public static void teleportWithCooldown(Player who, Location destination) throws Exception {
		User user = essentials.getUser(who);
		Teleport teleport = user.getTeleport();
		teleport.cooldown(true); // Cause an essentials exception if in cooldown
		teleport.teleport(destination, null, TeleportCause.PLUGIN);
	}

}
