package com.gmail.filoghost.wildtowns.object;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.gmail.filoghost.wildtowns.util.Utils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Getter;

public enum TownRank {
	
	// IMPORTANTE: non cambiare i nomi, vengono usati per i config
	BUILDER(1, "costruttore", "costruttori"),
	RECRUITER(2, "diplomatico", "diplomatici"),
	ASSISTANT(3, "assistente", "assistenti", BUILDER, RECRUITER),
	MAYOR(4, "sindaco", "sindaci", ASSISTANT);
	
	@Getter private final int prestige;
	@Getter private final String userFriendlyName, plural;
	@Getter private final Collection<TownRank> inheritedRanks;
	
	@Getter private static final TownRank[] valuesNoMayor = ArrayUtils.removeElement(values(), TownRank.MAYOR);
	@Getter private static final TownRank[] prestigeOrder = Utils.sortReturn(Lists.newArrayList(values()), new PrestigeComparator()).toArray(new TownRank[0]);
	@Getter private static final TownRank[] prestigeOrderNoMayor = ArrayUtils.removeElement(prestigeOrder, TownRank.MAYOR);

	private TownRank(int prestige, String userFriendlyName, String plural, TownRank... subRanks) {
		this.prestige = prestige;
		this.userFriendlyName = userFriendlyName;
		this.plural = plural;
		
		Set<TownRank> inheritedRanks = Sets.newHashSet();
		inheritedRanks.add(this); // Contiene il rango stesso
		if (subRanks != null) {
			for (TownRank subRank : subRanks) {
				subRank.addSubRanks(inheritedRanks);
			}
		}
		this.inheritedRanks = Collections.unmodifiableSet(inheritedRanks);
	}
	
	private void addSubRanks(Set<TownRank> set) {
		if (set.add(this)) { // Il rango non c'era e dobbiamo aggiungere quelli sotto
			for (TownRank inheritedRank : inheritedRanks) {
				inheritedRank.addSubRanks(set);
			}
		}
	}
	
	public boolean isStrictlyHigherThan(TownRank thanRank) {
		if (thanRank == null) {
			return true;
		}

		return this != thanRank && this.getInheritedRanks().contains(thanRank);
	}
	
	public static TownRank match(String what) {
		what = what.toLowerCase();
		for (TownRank rank : values()) {
			if (rank.name().toLowerCase().equals(what)) {
				return rank;
			}
		}
		return null;
	}
	
	public static TownRank matchUserFriendly(String what) {
		what = what.toLowerCase();
		for (TownRank rank : values()) {
			if (rank.userFriendlyName.toLowerCase().equals(what)) {
				return rank;
			}
		}
		return null;
	}
	
	private static class PrestigeComparator implements Comparator<TownRank> {

		@Override
		public int compare(TownRank o1, TownRank o2) {
			return (o1.prestige > o2.prestige) ? -1 : 1;
		}
			
	}
	
	@Override
	public String toString() {
		return userFriendlyName;
	}
	
}
