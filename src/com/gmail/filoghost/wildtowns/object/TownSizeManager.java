package com.gmail.filoghost.wildtowns.object;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.gmail.filoghost.wildtowns.util.Validate;
import com.google.common.collect.Lists;

public class TownSizeManager {
	
	private static List<TownSizeData> townData = Lists.newArrayList(
		//					Residenti	Nome città				Nome capo				Max Plots
		new TownSizeData(	1, 			"Accampamento", 		"Eremita", 				20			),
		new TownSizeData(	2, 			"Accampamento", 		"Leader", 				22			),
		new TownSizeData(	3, 			"Insediamento", 		"Leader", 				24			),
		new TownSizeData(	4, 			"Insediamento", 		"Leader", 				26			),
		new TownSizeData(	5, 			"Insediamento", 		"Leader", 				28			),
		new TownSizeData(	6, 			"Insediamento", 		"Leader", 				30			),
		new TownSizeData(	8, 			"Villaggio", 			"Capo Villaggio", 		35			),
		new TownSizeData(	10, 		"Villaggio", 			"Capo Villaggio", 		40			),
		new TownSizeData(	15, 		"Villaggio", 			"Capo Villaggio", 		50			),
		new TownSizeData(	20, 		"Paese", 				"Sindaco", 				60			),
		new TownSizeData(	25, 		"Paese", 				"Sindaco", 				70			),
		new TownSizeData(	30,			"Paese", 				"Sindaco", 				85			),
		new TownSizeData(	35,			"Cittadina", 			"Sindaco", 				100			),
		new TownSizeData(	40, 		"Cittadina", 			"Sindaco", 				120			),
		new TownSizeData(	45, 		"Cittadina", 			"Sindaco", 				140			),
		new TownSizeData(	50, 		"Città", 				"Sindaco", 				160			),
		new TownSizeData(	55, 		"Città", 				"Sindaco", 				180			),
		new TownSizeData(	60, 		"Città", 				"Sindaco", 				200			),
		new TownSizeData(	65, 		"Grande Città", 		"Sindaco", 				225			),
		new TownSizeData(	70, 		"Grande Città", 		"Sindaco", 				250			),
		new TownSizeData(	75,	 		"Grande Città", 		"Sindaco", 				275			),
		new TownSizeData(	80, 		"Metropoli", 			"Sindaco", 				300			),
		new TownSizeData(	85, 		"Metropoli", 			"Sindaco", 				325			),
		new TownSizeData(	90, 		"Metropoli", 			"Sindaco", 				350			),
		new TownSizeData(	95, 		"Metropoli", 			"Sindaco", 				375			),
		new TownSizeData(	100, 		"Metropoli", 			"Sindaco", 				400			)
	);

	public static TownSizeData findTownSizeData(int residents) {
		
		Validate.isTrue(residents > 0, "Less than 1 resident!?");
		
		TownSizeData bestMatch = null;
		
		for (TownSizeData data : townData) {
			if (residents >= data.getMinResidents()) { // Potrebbe sembrare meglio al contrario, ma in media le città avranno pochi giocatori
				bestMatch = data;
			} else {
				break;
			}
		}
		
		return bestMatch;
	}
	
	
	@AllArgsConstructor
	@Getter
	public static class TownSizeData {
		
		private int minResidents;
		private String townTitle;
		private String mayorTitle;
		private int maxPlots;
		
		@Override
		public String toString() {
			return "TownSizeData [minResidents=" + minResidents + ", townTitle=" + townTitle + ", mayorTitle=" + mayorTitle + ", maxPlots=" + maxPlots + "]";
		}
	}
	
}
