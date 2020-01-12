package com.gmail.filoghost.wildtowns.object.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PlotSetting {
	
	//								Require no owner
	ALLOW_USE("use", 				true),
	ALLOW_CONTAINERS("containers",	true),
	ALLOW_BUILD("build", 			true),
	LOCKED("locked", 				false),
	PVP("pvp", 						true);
	
	@Getter private String configName;
	@Getter private boolean requireNoOwner;
	
	public boolean getDefaultValue() {
		return false;
	}
	
}
