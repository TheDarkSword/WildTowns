package com.gmail.filoghost.wildtowns.util;

import java.util.Collection;
import java.util.Map;

public class Validate {

	public static void error(String msg) {
		throw new IllegalArgumentException(msg);
	}
	
	public static void notNull(Object o, String msg) {
		if (o == null) {
			throw new IllegalArgumentException(msg);
		}
	}
	
	public static void notNullOrEmpty(String s, String msg) {
		if (s == null || s.isEmpty()) {
			throw new IllegalArgumentException(msg);
		}
	}
	
	public static void notNullOrEmpty(Collection<?> coll, String msg) {
		if (coll == null || coll.isEmpty()) {
			throw new IllegalArgumentException(msg);
		}
	}
	
	public static void notNullOrEmpty(Map<?, ?> map, String msg) {
		if (map == null || map.isEmpty()) {
			throw new IllegalArgumentException(msg);
		}
	}
	
	public static void isTrue(boolean b, String msg) {
		if (!b) {
			throw new IllegalArgumentException(msg);
		}
	}

	
}
