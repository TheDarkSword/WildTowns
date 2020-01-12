package com.gmail.filoghost.wildtowns.object.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class War {
	
	private boolean isAttacker;
	@NonNull private String otherTown;
	@NonNull private String link;

}
