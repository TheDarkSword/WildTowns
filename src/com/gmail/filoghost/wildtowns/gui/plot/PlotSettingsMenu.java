package com.gmail.filoghost.wildtowns.gui.plot;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.PlotSetting;
import wild.api.menu.ClickHandler;
import wild.api.menu.Icon;
import wild.api.menu.IconBuilder;
import wild.api.menu.IconMenu;
import wild.api.sound.EasySound;

public class PlotSettingsMenu extends IconMenu {

	private Plot plot;
	private boolean needSave;

	private PlotSettingIconHolder
		allowBuildIcon,
		allowContainersIcon,
		allowUseIcon,
		lockedIcon,
		pvpIcon;
	
	public PlotSettingsMenu(Plot plot) {
		super("Impostazioni plot", 4);
		this.plot = plot;
		
		allowBuildIcon = setSettingIcon(PlotSetting.ALLOW_BUILD, 1, 2, Material.IRON_PICKAXE, "Autorizza costruzioni",
				"Consenti a tutti i cittadini di costruire.");
		
		allowContainersIcon = setSettingIcon(PlotSetting.ALLOW_CONTAINERS, 3, 2, Material.CHEST, "Autorizza apertura inventari",
				"Consenti a tutti i cittadini di aprire gli inventari",
				"condivisi: casse, fornaci, fari, etc.",
				"",
				ChatColor.YELLOW + "NOTA:" + ChatColor.GRAY + " Gli inventari condivisi sono quelli in cui",
				"tutti i giocatori vedono lo stesso contenuto o le",
				"stesse impostazioni (fari). Sono quindi esclusi",
				"banchi da lavoro, casse ender, incudini e tavoli",
				"per incantesimi.");
		
		allowUseIcon = setSettingIcon(PlotSetting.ALLOW_USE, 5, 2, Material.DARK_OAK_DOOR_ITEM, "Autorizza uso blocchi",
				"Consenti a tutti i cittadini di interagire con",
				"porte, botole, cancelletti, etc.",
				"",
				ChatColor.YELLOW + "NOTA:" + ChatColor.GRAY + " I pulsanti di legno e le pedane possono",
				"essere attivati da tutti i giocatori e dalle entità.");
		
		lockedIcon = setSettingIcon(PlotSetting.LOCKED, 7, 2, Material.BARRIER, "Protezione",
				"Previene la modifica da parte dei builder.",
				"Il sindaco, gli assistenti e il proprietario (se",
				"presente) possono continuare a costruire.",
				"",
				ChatColor.YELLOW + "NOTA:" + ChatColor.GRAY + " L'opzione viene abilitata automaticamente",
				"quando il proprietario viene cacciato dalla città.");
		
		pvpIcon = setSettingIcon(PlotSetting.PVP, 9, 2, Material.DIAMOND_SWORD, "Autorizza PvP",
				"Consenti ai cittadini di combattere fra loro.");
		
		for (PlotSetting plotSetting : PlotSetting.values()) {
			updateSettingIcon(plotSetting, plot.getSetting(plotSetting)); // Refresh di ogni icona
		}
		
		refresh();
	}
	
	@Override
	public void onClose(Player player) {
		if (needSave) {
			needSave = false;
			plot.getTown().trySaveAsync(() -> player.sendMessage(Lang.plotSettingsSaved), () -> player.sendMessage(Lang.internalError));
		}
	}

	
	private PlotSettingIconHolder setSettingIcon(PlotSetting plotSetting, int x, int y, Material material, String name, String... description) {

		ClickHandler clickHandler = (clicker) -> {
			
			boolean newState = !plot.getSetting(plotSetting);

			if (plot.canChangeSettingSendMessage(clicker, plotSetting, newState)) {
				needSave = true;
				plot.setSetting(plotSetting, newState);
				EasySound.quickPlay(clicker, Sound.UI_BUTTON_CLICK);
			}
		};
		
		
		for (int i = 0; i < description.length; i++) {
			description[i] = ChatColor.GRAY + description[i];
		}
		
		setIcon(x, y, new IconBuilder(material).name(ChatColor.WHITE + name).lore(description).clickHandler(clickHandler).build());
		
		Icon toggleIcon = new IconBuilder(Material.STAINED_GLASS_PANE).clickHandler(clickHandler).build();
		setIcon(x, y + 1, toggleIcon);
		
		return new PlotSettingIconHolder(toggleIcon);
	}
	
	
	private PlotSettingIconHolder getSettingIcon(PlotSetting plotSetting) {
		switch (plotSetting) {
			case ALLOW_BUILD: 		return allowBuildIcon;
			case ALLOW_CONTAINERS: 	return allowContainersIcon;
			case ALLOW_USE: 		return allowUseIcon;
			case LOCKED:			return lockedIcon;
			case PVP: 				return pvpIcon;
		}
		
		throw new IllegalArgumentException("Unhandled icon for " + plotSetting);
	}
	
	public void updateSettingIcon(PlotSetting setting, boolean newState) {
		getSettingIcon(setting).setActive(newState).refreshIcon();
	}
	
	public void updateSettingIcon(PlotSetting setting, boolean newState, boolean locked) {
		getSettingIcon(setting).setActive(newState).setLocked(locked).refreshIcon();
	}


}
