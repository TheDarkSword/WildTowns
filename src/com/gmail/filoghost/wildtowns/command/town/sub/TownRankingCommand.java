package com.gmail.filoghost.wildtowns.command.town.sub;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.TownValueManager;
import com.gmail.filoghost.wildtowns.object.base.Town;
import wild.api.command.CommandFramework.CommandValidate;

public class TownRankingCommand extends SubCommand {

	public TownRankingCommand() {
		super("ranking");
		setUsage("[pagina]");
		setDescription("Mostra la classifica delle città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		int page = args.length > 0 ? CommandValidate.getPositiveIntegerNotZero(args[0]) : 1;
		int page0 = page - 1;
		
		int index0 = page0 * 10;
		
		List<Entry<Town, Long>> pageTowns = TownValueManager.getTop(index0, index0 + 10);
		CommandValidate.isTrue(!pageTowns.isEmpty(), "Pagina non trovata.");
		
		sender.sendMessage(Lang.format(Lang.format_topHeader, "{page}", page));
		for (Entry<Town, Long> entry : pageTowns) {
			sender.sendMessage(Lang.color_main + (++index0) + ") " + Lang.color_highlight + entry.getKey() + Lang.color_main + " (" + entry.getKey().getResidentsCount() + " cittadini)");
		}
		sender.sendMessage("");
	}

}
