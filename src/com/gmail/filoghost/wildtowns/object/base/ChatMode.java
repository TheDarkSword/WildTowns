package com.gmail.filoghost.wildtowns.object.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ChatMode {

	PUBLIC("pubblica"),
	TOWN("cittadina");
	
	@Getter private final String userFriendlyName;
	
	public ChatMode cycle() {
		int thisIndex = 0;
		
		for (int i = 0; i < values().length; i++) {
			if (values()[i] == this) {
				thisIndex = i;
				break;
			}
		}
		
		if (thisIndex >= values().length - 1) {
			return values()[0];
		} else {
			return values()[thisIndex + 1];
		}
	}
	
}
