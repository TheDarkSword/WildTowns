package com.gmail.filoghost.wildtowns.gui.map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FacingDirection {

	NORTH		("⬆", "ˏ", "ˌˈ"),
	NORTH_EAST	("⬈", "ˈ", "ˌˈ"),
	EAST		("➡", "", "ˌ"),
	SOUTH_EAST	("⬊", "ˌ", "ˈˌ"),
	SOUTH		("⬇", "ˊ", "ˈˌ"),
	SOUTH_WEST	("⬋", "ˌˈ", "ˌ"),
	WEST		("⬅", "ˈ", "ˈ"),
	NORTH_WEST	("⬉", "ˈˌ", "ˈ");
	
	@Getter private final String arrow;
	@Getter private final String paddingBefore, paddingAfter;
	
	public boolean isDiagonal() {
		switch (this) {
			case NORTH_EAST:
			case NORTH_WEST:
			case SOUTH_EAST:
			case SOUTH_WEST:
				return true;
			default:
				return false;
		}
	}
	
	public static FacingDirection fromYaw(double yaw) {
		double degrees = yaw;
		while (degrees < 0) {
			degrees += 360.0;
		}
		
		degrees = degrees % 360;
		
		if (0 <= degrees && degrees < 22.5) {
			return SOUTH;
		} else if (22.5 <= degrees && degrees < 67.5) {
			return SOUTH_WEST;
		} else if (67.5 <= degrees && degrees < 112.5) {
			return WEST;
		} else if (112.5 <= degrees && degrees < 157.5) {
			return NORTH_WEST;
		} else if (157.5 <= degrees && degrees < 202.5) {
			return NORTH;
		} else if (202.5 <= degrees && degrees < 247.5) {
			return NORTH_EAST;
		} else if (247.5 <= degrees && degrees < 292.5) {
			return EAST;
		} else if (292.5 <= degrees && degrees < 337.5) {
			return SOUTH_EAST;
		} else if (337.5 <= degrees && degrees < 360.0) {
			return SOUTH;
		} else {
			throw new IllegalArgumentException("degrees not between 0 and 360: " + degrees);
		}
	}
}