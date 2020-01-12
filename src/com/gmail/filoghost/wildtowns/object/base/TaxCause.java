package com.gmail.filoghost.wildtowns.object.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TaxCause {

	PLOTS ("al numero di plot totali"),
	OUTPOSTS ("al numero di avamposti"),
	RESIDENTS ("al numero di cittadini");
	
	@Getter private final String dueToWhat;
	
}
