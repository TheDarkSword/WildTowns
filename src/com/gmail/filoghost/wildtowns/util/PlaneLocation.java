package com.gmail.filoghost.wildtowns.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlaneLocation {
	
	private int x, z;
	
	public PlaneLocation duplicate(int addX, int addZ) {
		return new PlaneLocation(x + addX, z + addZ);
	}

}
