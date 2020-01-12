package com.gmail.filoghost.wildtowns.object.base;

import java.lang.ref.WeakReference;

import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.gui.plot.PlotSettingsMenu;
import com.gmail.filoghost.wildtowns.object.ChunkCoords;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.util.Validate;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
public final class Plot {
	
	@Getter	protected final Town town;
	@Getter	private final ChunkCoords chunkCoords;
	@Getter private Resident owner; // Can be null
	@Getter private int price;
	@Getter private boolean forSale;
	@Getter private String description;
	
	private boolean allowContainers, allowUse, allowBuild, locked, pvp;
	
	private WeakReference<PlotSettingsMenu> settingsMenuWeakReference;

	public Plot(@NonNull Town town, @NonNull ChunkCoords chunkCoords) {
		this.town = town;
		this.chunkCoords = chunkCoords;
		for (PlotSetting setting : PlotSetting.values()) {
			setSetting(setting, setting.getDefaultValue());
		}
	}
	
	/**
	 * Resetta le flag ai valori di default
	 */
	public void setOwner(Resident owner) {
		Validate.isTrue(this.owner != owner, "Owner is already the same");
		removeForSale();
		this.description = null;
		if (owner != null) {
			// Se ora c'è un proprietario resetta tutte quelle flag che richiedevano che non ce ne fosse uno
			for (PlotSetting setting : PlotSetting.values()) {
				if (setting.isRequireNoOwner()) {
					setSetting(setting, setting.getDefaultValue());
				}
			}
		}
		this.owner = owner;
	}
	
	public void setForSale(int price) {
		Validate.isTrue(this.owner == null, "Can't set for sale with owner present");
		Validate.isTrue(price >= 0, "Price cannot be negative");
		this.price = price;
		this.forSale = true;
	}
	
	public void setDescription(String description) {
		Validate.isTrue(this.owner == null, "Can't set description with owner present");
		this.description = description;
	}
	
	public void removeForSale() {
		this.forSale = false;
		this.price = 0;
	}
	
	public boolean isHomeGroup() {
		return town.isHomeGroupPlot(chunkCoords);
	}

	public void openSettingsMenu(Player player) {
		PlotSettingsMenu menu = null;
		
		if (settingsMenuWeakReference != null) {
			menu = settingsMenuWeakReference.get();
		}
		
		if (menu == null) {
			menu = new PlotSettingsMenu(this);
			settingsMenuWeakReference = new WeakReference<>(menu);
		}
		
		menu.open(player);
	}
	
	public boolean canChangeSettingSendMessage(Player player, PlotSetting setting, boolean newState) {
		Resident resident = WTManager.getOnlineResident(player);
		
		if (resident.getTown() != this.getTown()) {
			player.sendMessage(Lang.color_error + "Non appartieni alla città che possiede questo plot.");
			return false;
		}
		
		if (!resident.getTown().hasRank(resident, TownRank.ASSISTANT)) {
			player.sendMessage(Lang.color_error + "Serve il rango di assistente o superiore per modificare le impostazioni.");
			return false;
		}
		
		if (this.getOwner() != null && setting.isRequireNoOwner()) {
			player.sendMessage(Lang.color_error + "Questa impostazione è disponibile solo nei plot liberi (senza proprietario).");
			return false;
		}
		
		if (setting == PlotSetting.ALLOW_BUILD || setting == PlotSetting.ALLOW_CONTAINERS || setting == PlotSetting.ALLOW_USE) {
			if (newState == true && locked) {
				player.sendMessage(Lang.color_error + "Non puoi abilitare questa impostazione, il plot è protetto.");
				return false;
			}
		}
		
		if (setting == PlotSetting.ALLOW_CONTAINERS || setting == PlotSetting.ALLOW_USE) {
			if (newState == false && allowBuild) {
				player.sendMessage(Lang.color_error + "Non puoi disabilitare questa impostazione, tutti possono costruire.");
				return false;
			}
		}

		return true;
	}
	
	public void setSetting(PlotSetting setting, boolean value) {
		switch (setting) {
			case ALLOW_BUILD:
				allowBuild = value;
				break;
			case ALLOW_CONTAINERS:
				allowContainers = value;
				break;
			case ALLOW_USE:
				allowUse = value;
				break;
			case LOCKED:
				locked = value;
				break;
			case PVP:
				pvp = value;
				break;
			default:
				throw new IllegalStateException("setting not handled: " + setting);
		}
		
		PlotSettingsMenu menu = settingsMenuWeakReference != null ? settingsMenuWeakReference.get() : null;
		
		if (menu != null) {
			menu.updateSettingIcon(setting, value);
		}
		
		if (setting == PlotSetting.LOCKED) {
			boolean lockBuildAndContainersAndUse;
			
			if (value == true) {
				allowBuild = false;
				allowContainers = false;
				allowUse = false;
				lockBuildAndContainersAndUse = true;
			} else {
				lockBuildAndContainersAndUse = false;
			}
			
			if (menu != null) {
				menu.updateSettingIcon(PlotSetting.ALLOW_BUILD, allowBuild, lockBuildAndContainersAndUse);
				menu.updateSettingIcon(PlotSetting.ALLOW_CONTAINERS, allowContainers, lockBuildAndContainersAndUse);
				menu.updateSettingIcon(PlotSetting.ALLOW_USE, allowUse, lockBuildAndContainersAndUse);
			}
			
		} else if (setting == PlotSetting.ALLOW_BUILD) {
			boolean lockContainersAndUse;
			
			if (value == true) {
				allowContainers = true;
				allowUse = true;
				lockContainersAndUse = true;
			} else {
				lockContainersAndUse = false;
			}
			
			if (menu != null) {
				menu.updateSettingIcon(PlotSetting.ALLOW_CONTAINERS, allowContainers, lockContainersAndUse);
				menu.updateSettingIcon(PlotSetting.ALLOW_USE, allowUse, lockContainersAndUse);
			}
		}
		
		if (menu != null) {
			menu.refresh();
		}
	}
	
	public boolean getSetting(PlotSetting setting) {
		switch (setting) {
			case ALLOW_BUILD:
				return allowBuild;
			case ALLOW_CONTAINERS:
				return allowContainers;
			case ALLOW_USE:
				return allowUse;
			case LOCKED:
				return locked;
			case PVP:
				return pvp;
			default:
				throw new IllegalStateException("setting not handled: " + setting);
		}
	}

}
