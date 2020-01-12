package com.gmail.filoghost.wildtowns.gui.plot;

import org.bukkit.ChatColor;
import lombok.Getter;
import wild.api.menu.Icon;

public class PlotSettingIconHolder {
	
	@Getter
	private boolean active, locked;
	
	private boolean needsRefresh;

	private final Icon statusIcon;
	
	public PlotSettingIconHolder(Icon statusIcon) {
		this.statusIcon = statusIcon;
		this.needsRefresh = true;
	}

	public PlotSettingIconHolder setActive(boolean active) {
		if (this.active != active) {
			this.active = active;
			needsRefresh = true;
		}
		return this;
	}

	public PlotSettingIconHolder setLocked(boolean locked) {
		if (this.locked != locked) {
			this.locked = locked;
			needsRefresh = true;
		}
		return this;
	}
	
	public void refreshIcon() {
		if (needsRefresh) {
			needsRefresh = false;
			ChatColor color = active ? (locked ? ChatColor.DARK_GREEN : ChatColor.GREEN) : (locked ? ChatColor.DARK_RED : ChatColor.RED);
			statusIcon.setName(color + (active ? "On" : "Off") + (locked ? " (Impostazione bloccata)" : ""));
			statusIcon.setDataValue((short) (active ? 5 : 14));
		}
	}
	
}
