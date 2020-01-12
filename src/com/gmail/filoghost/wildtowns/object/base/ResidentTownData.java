package com.gmail.filoghost.wildtowns.object.base;

import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.util.Validate;

public class ResidentTownData {
	
	private TownRank rank;
	
	protected void setRank(TownRank rank) {
		Validate.isTrue(rank != TownRank.MAYOR, "Can't set MAYOR as rank");
		this.rank = rank;
	}

	protected TownRank getRank() {
		return rank;
	}

}
