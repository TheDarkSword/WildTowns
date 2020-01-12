package com.gmail.filoghost.wildtowns.object.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TaxCost {
	
	private final TaxCause cause;
	private final long value;

}
