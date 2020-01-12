package com.gmail.filoghost.wildtowns;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.wildtowns.bridge.DynmapBridge;
import com.gmail.filoghost.wildtowns.bridge.EssentialsBridge;
import com.gmail.filoghost.wildtowns.bridge.WorldGuardBridge;
import com.gmail.filoghost.wildtowns.command.aliases.MapAliasCommand;
import com.gmail.filoghost.wildtowns.command.aliases.TownChatAliasCommand;
import com.gmail.filoghost.wildtowns.command.town.TownCommand;
import com.gmail.filoghost.wildtowns.command.townadmin.AdminCommand;
import com.gmail.filoghost.wildtowns.disk.ColoniesConfig;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.disk.PluginConfig;
import com.gmail.filoghost.wildtowns.disk.Settings;
import com.gmail.filoghost.wildtowns.listener.ChatListener;
import com.gmail.filoghost.wildtowns.listener.ChunkMoveListener;
import com.gmail.filoghost.wildtowns.listener.JoinQuitListener;
import com.gmail.filoghost.wildtowns.listener.protection.ProtectionCommandListener;
import com.gmail.filoghost.wildtowns.listener.protection.ProtectionEntityListener;
import com.gmail.filoghost.wildtowns.listener.protection.ProtectionInteractListener;
import com.gmail.filoghost.wildtowns.listener.protection.ProtectionInventoryListener;
import com.gmail.filoghost.wildtowns.listener.protection.ProtectionLiquidListener;
import com.gmail.filoghost.wildtowns.listener.protection.ProtectionPistonListener;
import com.gmail.filoghost.wildtowns.listener.protection.ProtectionPvPListener;
import com.gmail.filoghost.wildtowns.listener.protection.ProtectionWorldListener;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.timer.TaxesTimer;
import com.gmail.filoghost.wildtowns.timer.UpdateTownValuesTimer;
import com.google.common.base.Stopwatch;
import lombok.Getter;
import wild.api.util.FileLogger;

/**
 * Ordine caricamento: Residenti (non hanno città associate) -> Città
 *
 */
public class WildTowns extends JavaPlugin {
	
	@Getter	private static WildTowns instance;
	private static File townsFolder, deletedTownsFolder, residentsFolder, coloniesFile;
	private static FileLogger errorLogger, deleteLogger;
	
	@Getter private TaxesTimer taxesTimer;
	@Getter private TownCommand townCommand;

	@Override
	public void onEnable() {
		if (!Bukkit.getPluginManager().isPluginEnabled("WildCommons")) {
			fatalError("Richiesto WildCommons!", null);
			return;
		}
		
		instance = this;
		
		if (WorldGuardBridge.setup()) {
			getLogger().info("Plugin interfacciato correttamente con WorldGuard!");
		}
		
		if (EssentialsBridge.setup()) {
			getLogger().info("Plugin interfacciato correttamente con Essentials!");
		}
		
		if (DynmapBridge.setup()) {
			getLogger().info("Plugin interfacciato correttamente con Dynmap!");
		}
		
		System.out.println("========== Caricamento WildTowns ==========");
		Stopwatch stopWatch = Stopwatch.createStarted();
		
		try {
			new Settings(this, "config.yml").init();
		} catch (Exception ex) {
			fatalError("Impossibile caricare config.yml!", ex);
			return;
		}
		
		try {
			new Lang(this, "lang.yml").init();
		} catch (Exception ex) {
			fatalError("Impossibile caricare lang.yml!", ex);
			return;
		}
		
		townsFolder = new File(getDataFolder(), "towns");
		deletedTownsFolder = new File(getDataFolder(), "deleted");
		residentsFolder = new File(getDataFolder(), "residents");
		coloniesFile = new File(getDataFolder(), "colonies.yml");
		
		errorLogger = new FileLogger(this, "errors.log");
		deleteLogger = new FileLogger(this, "delete.log");
		
		if (!townsFolder.isDirectory()) {
			townsFolder.mkdirs();
		}
		if (!deletedTownsFolder.isDirectory()) {
			deletedTownsFolder.mkdirs();
		}
		if (!residentsFolder.isDirectory()) {
			residentsFolder.mkdirs();
		}
		
		// Caricamento colonie
		System.out.println("Caricamento colonie...");
		try {
			ColoniesConfig.load();
		} catch (Exception ex) {
			fatalError("Impossibile caricare il file delle colonie", ex);
			return;
		}
		
		// Caricamento residenti
		System.out.println("Caricamento residenti...");
		for (File residentFile : residentsFolder.listFiles()) {
			if (residentFile.isFile() && residentFile.getName().toLowerCase().endsWith(".yml")) {
				
				try {
					PluginConfig config = new PluginConfig(this, residentFile);
					config.load();
					Resident resident = new Resident(config);
					
					if (!cutYmlExtension(residentFile.getName()).equals(resident.getUUID().toString())) {
						throw new IllegalStateException("L'UUID salvato del residente è diverso dal nome del file! File: " + residentFile.getName());
					}
					
					WTManager.register(resident);
					
				} catch (Exception ex) {
					fatalError("Impossibile caricare il file di un residente: " + residentFile.getName(), ex);
					return;
				}
			}
		}
		for (Resident resident : WTManager.getOfflineResidents()) {
			try {
				resident.setupFriends();
			} catch (Exception ex) {
				fatalError("Impossibile caricare gli amici di un cittadino: " + resident, ex);
				return;
			}
		}
		System.out.println("Caricamento residenti completato!");
		
		// Caricamento città
		System.out.println("Caricamento città...");
		for (File townFile : townsFolder.listFiles()) {
			if (townFile.isFile() && townFile.getName().toLowerCase().endsWith(".yml")) {
				
				try {
					PluginConfig config = new PluginConfig(this, townFile);
					config.load();
					Town town = new Town(config);
					
					if (!cutYmlExtension(townFile.getName()).equalsIgnoreCase(Integer.toString(town.getID()))) {
						throw new IllegalStateException("L'ID salvato della città è diverso dal nome del file! File: " + townFile.getName());
					}
					
					WTManager.register(town);
					
				} catch (Exception ex) {
					fatalError("Impossibile caricare il file di una città: " + townFile.getName(), ex);
					return;
				}
			}
		}
		for (Town town : WTManager.getTowns()) {
			try {
				town.setupRelations();
			} catch (Exception ex) {
				fatalError("Impossibile caricare gli alleati o i nemici di una città: " + town, ex);
				return;
			}
		}
		System.out.println("Caricamento città completato!");
		
		try {
			WTManager.consistencyCheck();
		} catch (Exception ex) {
			fatalError("Errore di consistenza!", ex);
			return;
		}
		
		try {
			taxesTimer = new TaxesTimer(this, new File(getDataFolder(), "taxes.yml"));
			taxesTimer.startNewTask();
		} catch (Exception ex) {
			fatalError("Impossibile caricare taxes.yml!", ex);
			return;
		}
		new UpdateTownValuesTimer(this, TimeUnit.MINUTES, 30).startNewTask();
		
		
		Bukkit.getPluginManager().registerEvents(new JoinQuitListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChunkMoveListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		
		// Protezioni
		Bukkit.getPluginManager().registerEvents(new ProtectionWorldListener(), this);
		Bukkit.getPluginManager().registerEvents(new ProtectionEntityListener(), this);
		Bukkit.getPluginManager().registerEvents(new ProtectionInteractListener(), this);
		Bukkit.getPluginManager().registerEvents(new ProtectionInventoryListener(), this);
		Bukkit.getPluginManager().registerEvents(new ProtectionPistonListener(), this);
		Bukkit.getPluginManager().registerEvents(new ProtectionLiquidListener(), this);
		Bukkit.getPluginManager().registerEvents(new ProtectionPvPListener(), this);
		Bukkit.getPluginManager().registerEvents(new ProtectionCommandListener(), this);
		
		townCommand = new TownCommand(this, "town", "towns", "t");
		new AdminCommand(this, "townadmin", "ta");
		
		new TownChatAliasCommand(this, "townchat", "tc");
		new MapAliasCommand(this, "map");

		
		stopWatch.stop();
		System.out.println("Abilitato in " + stopWatch.elapsed(TimeUnit.MILLISECONDS) + " ms.");
		System.out.println("===========================================");
	}
	
	public static void logDelete(String message) {
		deleteLogger.log(message);
	}
	
	public static void logError(Level logLevel, String message, Throwable t) {
		instance.getLogger().log(logLevel, message, t);
		errorLogger.log("[" + logLevel.getName() + "] " + message, t);
	}
	
	public static void logError(Level logLevel, String message) {
		logError(logLevel, message, null);
	}
	
	public void fatalError(String message, Exception ex) {
		if (ex != null) {
			ex.printStackTrace();
		}
		consoleRedError(message);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) { }
		setEnabled(false);
		Bukkit.shutdown();
	}
	
	
	public void consoleRedError(String message) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + this.getName() + "] " + message);
	}
	
	
	private String cutYmlExtension(String s) {
		return s.substring(0, s.length() - 4);
	}
	
	public static File getTownFile(int townID) {
		return new File(townsFolder, townID + ".yml");
	}
	
	public static File getDeletedTownFile(String townName) {
		return new File(deletedTownsFolder, townName.toLowerCase() + " - " + new SimpleDateFormat("dd.MM.yyyy - hh.mm").format(new Date()) + ".yml");
	}
	
	public static File getResidentFile(UUID residentUUID) {
		return new File(residentsFolder, residentUUID.toString() + ".yml");
	}
	
	public static File getColoniesFile() {
		return coloniesFile;
	}
	
}
